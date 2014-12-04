package com.cosmos.netty;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.netty.mediator.AbstractMediator;
import com.cosmos.netty.pipeline.ProtocolBufferPipelineFactory;
import com.cosmos.server.Protocol;
import com.cosmos.server.Setting;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Netty服务启动类
 */
public class Server implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private ChannelFactory channelFactory;

    private ChannelGroup channels;

    @Autowired
    private Setting setting;

    /**
     * 启动Netty服务
     */
    private void start() {
        logger.warn("{} staring...", setting.getServerName());

        // 判断配置的协议是否支持
        if (!setting.isSupportedProtocol()) {
            logger.error("{} started failure! Protocol should be {}, but it is '{}' in server.properties", setting.getServerName(), setting.getSupportedProtocols(), setting.getProtocol().getCode());
            System.exit(1);
        }

        // 根据协议扫描目标包里的中介器
        AbstractMediator mediator = AbstractMediator.getInstance(setting.getProtocol(), setting.getBasePackage());
        if (mediator == null) {
            logger.error("no mediator created for protocol {}, {} started failure!", setting.getProtocol().getCode(), setting.getServerName());
            System.exit(1);
        }

        // 若使用Protocol Buffer协议，则判断自定义的Protocol Buffer协议是否实现
        if (setting.getProtocol() == Protocol.PROTOBUF && mediator.getPbInstance() == null) {
            logger.error("{} started failure! the method getPbInstance() in {} returns null", setting.getServerName(), mediator.getClass().getName());
            System.exit(1);
        }

        // 如果心跳开关打开，扫描并设置心跳handler类
        if(setting.isHeartBeatOn()) {
            try {
                ComponentFactory.scanHeartBeatHandlerClass(setting);
            } catch (BusinessException e) {
                logger.error("{} started failure! heart beat handler scanning exception", setting.getServerName());
                System.exit(1);
            }
        }

        // 扫描并设置业务处理handler类
        ComponentFactory.scanProtocolHandlerClass(setting);

        /**
         * 把共享的ExecutionHandler实例放在业务逻辑handler之前即可，注意ExecutionHandler一定要在不同的pipeline之间共享。
         * 它的作用是自动从ExecutionHandler自己管理的一个线程池中拿出一个线程来处理排在它后面的业务逻辑handler。
         * 而worker线程在经过ExecutionHandler后就结束了，它会被ChannelFactory的worker线程池所回收。
         *
         * 它的构造方法是ExecutionHandler(Executor executor)，很显然executor就是ExecutionHandler内部管理的线程池了。
         * Netty额外给我们提供了两种线程池：
         * MemoryAwareThreadPoolExecutor
         * OrderedMemoryAwareThreadPoolExecutor
         * 它们都在org.jboss.netty.handler.execution 包下。
         * MemoryAwareThreadPoolExecutor确保jvm不会因为过多的线程而导致内存溢出错误，
         * OrderedMemoryAwareThreadPoolExecutor是前一个线程池的子类，除了保证没有内存溢出之外，
         * 还可以保证channel event的处理次序。
         */

        this.channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        // Netty服务启动触发器
        ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);

        // 获得业务线程池模型
        MemoryAwareThreadPoolExecutor workThreadPoolexecutor = null;
        try {
            workThreadPoolexecutor = ComponentFactory.getThreadPoolExecutorInstance(setting);
            logger.warn("work execution thread pool class: {}, thread pool size: {}", workThreadPoolexecutor.getClass().getName(), setting.getExecutionThreadPoolSize());
        } catch (BusinessException e) {
            logger.error("{} started failure! create thread pool executor exception", setting.getServerName());
            System.exit(1);
        }

        /**
         * 为了提高并发数，一般通过ExecutionHandler线程池来异步处理ChannelHandler链（worker线程在经过ExecutionHandler后就结束了，
         * 它会被ChannelFactory的worker线程池所回收）。
         */
        ExecutionHandler executionHandler = new ExecutionHandler(workThreadPoolexecutor);

        ProtocolBufferPipelineFactory protocolBufferPipelineFactory = ComponentFactory.getServerPipelineFactoryInstance(executionHandler, setting);

        bootstrap.setPipelineFactory(protocolBufferPipelineFactory);
        /**禁用纳格算法，将数据立即发送出去。纳格算法是以减少封包传送量来增进TCP/IP网络的效能,
         * 前面的child前缀必须要加上，用来指明这个参数将被应用到接收到的Channels，而不是设置的ServerSocketChannel
         */
        bootstrap.setOption("child.tcpNoDelay", true);
        /**
         * keepalive不是说TCP的常连接，当我们作为服务端，一个客户端连接上来，如果设置了keeplive为true，
         * 当对方没有发送任何数据过来，超过一个时间(看系统内核参数配置)，那么我们这边会发送一个ack探测包发到
         * 对方，探测双方的TCP/IP连接是否有效(对方可能断点，断网)，在Linux好像这个时间是75秒。如果不设置，
         * 那么客户端宕机时，服务器永远也不知道客户端宕机了，仍然保存这个失效的连接。
         */
        bootstrap.setOption("child.keepAlive", true);


        this.channels = new DefaultChannelGroup(setting.getServerName());

        Channel channel = bootstrap.bind(new InetSocketAddress(setting.getListenPort()));
        this.channels.add(channel);

        logger.warn("heartbeat switch: {}", setting.isHeartBeatOn() ? "on" : "off");
        logger.warn("listen port: {}, protocol: {}", setting.getListenPort(), setting.getProtocol().getCode());
        logger.warn("{} started successfully!", setting.getServerName());
    }

    /**
     * 停止Netty服务
     */
    private void stop() {
        logger.warn("{} stopping...", setting.getServerName());

        if (channels != null) {
            ChannelGroupFuture future = channels.close();
            future.awaitUninterruptibly();
        }

        if (channelFactory != null) {
            channelFactory.releaseExternalResources();
        }

        logger.warn("{} stopped successfully!", setting.getServerName());
    }

    @Override
    public void destroy() throws Exception {
        stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
