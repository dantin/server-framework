package com.cosmos.netty;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.netty.component.NettyComponentFactory;
import com.cosmos.netty.mediator.Mediator;
import com.cosmos.server.Setting;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
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

    @Autowired
    private NettyComponentFactory nettyComponentFactory;

    /**
     * 启动Netty服务
     */
    private void start() {
        logger.warn("{} staring...", setting.getServerName());

        try {
            // 根据配置文件扫描各组件实现类
            nettyComponentFactory.scanComponents();

            // 生成中介器
            Mediator.getInstance();

            // 启动并配置Netty服务
            this.channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
            ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);

            bootstrap.setPipelineFactory(nettyComponentFactory.getChannelPipelineFactory());
            /**
             * 禁用纳格算法，将数据立即发送出去。
             * 纳格算法是以减少封包传送量来增进TCP/IP网络的效能，前面的child前缀必须要加上，用来指明
             * 这个参数将被应用到接收到的Channels，而不是设置的ServerSocketChannel
             */
            bootstrap.setOption("child.tcpNoDelay", true);
            /**
             * keepalive不是说TCP的常连接，当我们作为服务端，一个客户端连接上来，如果设置了keeplive为true，
             * 当对方没有发送任何数据过来，超过一个时间(看系统内核参数配置)，那么我们这边会发送一个ack探测包发到
             * 对方，探测双方的TCP/IP连接是否有效(对方可能断点，断网)，在Linux好像这个时间是75秒。
             * 如果不设置，那么客户端宕机时，服务器永远也不知道客户端宕机了，仍然保存这个失效的连接。
             */
            bootstrap.setOption("child.keepAlive", true);

            // 侦听目标端口
            this.channels = new DefaultChannelGroup(setting.getServerName());
            Channel channel = bootstrap.bind(new InetSocketAddress(setting.getListenPort()));
            this.channels.add(channel);

            logger.warn("heartbeat switch: {}", setting.isHeartBeatOn() ? "on" : "off");
            logger.warn("listen port: {}, protocol: {}", setting.getListenPort(), setting.getProtocol().getCode());
            logger.warn("{} started successfully!", setting.getServerName());
        } catch (BusinessException e) {
            logger.error("{}, {} started failure!", e.getMessage(), setting.getServerName());
            System.exit(1);
        }
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
