package com.cosmos.netty;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.netty.component.handler.ProtocolHandler;
import com.cosmos.netty.component.mediator.Mediator;
import com.cosmos.netty.component.pipeline.ProtocolBufferPipelineFactory;
import com.cosmos.netty.component.scanner.ComponentScanner;
import com.cosmos.netty.component.scanner.ProtocolScanner;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * 配置参数
 */
public class Setting {

    private static final Logger logger = LoggerFactory.getLogger(Setting.class);

    private static final int MB = 1024 * 1024;

    public static Class<? extends Mediator> EXTENDED_MEDIATOR_CLASS = null;

    @Autowired
    private ComponentScanner<IdleStateAwareChannelHandler> heartBeatScanner;

    @Autowired
    private ComponentScanner<ProtocolHandler> protocolHandlerScanner;

    /**
     * 心跳处理类
     */
    private Class<IdleStateAwareChannelHandler> heartBeatClass;

    /**
     * 业务handler处理类
     */
    private Class<ProtocolHandler> businessHandlerClass;

    private List<ProtocolScanner> protocolComponentScanners;

    /**
     * 服务名
     */
    @Value("#{server['instance.name']}")
    private String serverName;

    /**
     * 协议类型
     */
    @Value("#{server['protocol.type']}")
    private String protocolType;

    /**
     * 心跳开关
     */
    @Value("#{server['heartbeat.switch']}")
    private String heartBeatSwitch;

    /**
     * ExecutionHandler的线程池模型
     */
    @Value("#{server['execution.threadpool.class']}")
    private String executionThreadPoolClass;

    /**
     * 每个通道排队事件的内存总大小，单位MB，默认0禁用
     */
    @Value("#{server['execution.threadpool.maxChannelMemorySize']}")
    private String executionThreadPoolMaxChannelMemorySize;

    /**
     * 业务处理线程池个数(活动线程的最大数量),worker execution threads
     */
    @Value("#{server['execution.threadpool.size']}")
    private String executionThreadPoolSize;

    /**
     * 该线程池队事件的内存总大小，单位MB，默认0禁用
     */
    @Value("#{server['execution.threadpool.maxTotalMemorySize']}")
    private String executionThreadPoolMaxTotalMemorySize;

    @Value("#{server['heartbeat.readerIdleTimeMillis']}")
    private String heartBeatReaderIdleTime;

    @Value("#{server['heartbeat.writerIdleTimeMillis']}")
    private String heartBeatWriterIdleTime;

    @Value("#{server['heartbeat.allIdleTimeMillis']}")
    private String heartBeatAllIdleTime;

    @Value("#{server['listen.port']}")
    private String listenPort;

    private static final int DEFAULT_EXECUTION_THREAD_POOL_SIZE = 16;
    private static final int DEFAULT_EXECUTION_THREAD_POOL_MAX_TOTAL_MEMORY_SIZE = 0;
    private static final int DEFAULT_EXECUTION_THREAD_POOL_MAX_CHANNEL_MEMORY_SIZE = 0;
    private static final int DEFAULT_HEART_BEAT_READ_IDLE_TIME = 10;
    private static final int DEFAULT_HEART_BEAT_WRITE_IDLE_TIME = 10;
    private static final int DEFAULT_HEART_BEAT_ALL_IDLE_TIME = 0;
    private static final int DEFAULT_LISTEN_PORT = 20330;
    private static final String ORDERED_MEMORY_AWARE_THREAD_POOL_EXECUTOR = "OrderedMemoryAwareThreadPoolExecutor";
    private static final String MEMORY_AWARE_THREAD_POOL_EXECUTOR = "MemoryAwareThreadPoolExecutor";

    private Setting() {
    }

    /**
     * @param protocolComponentScanners the protocolComponents to set
     */
    public void setProtocolComponentScanners(List<ProtocolScanner> protocolComponentScanners) {
        this.protocolComponentScanners = protocolComponentScanners;
    }

    /**
     * 根据配置文件扫描各组件实现类
     */
    @SuppressWarnings("unchecked")
    public void scanComponents() {
        // 根据协议扫描目标包里的中介器
        Setting.EXTENDED_MEDIATOR_CLASS = (Class<? extends Mediator>) this.getMediatorClass(this.getProtocol());

        // 如果心跳开关打开，扫描并设置心跳处理类
        if (this.isHeartBeatOn()) {
            this.heartBeatClass = heartBeatScanner.getComponentClass();
        }

        // 扫描并设置业务处理类
        this.businessHandlerClass = protocolHandlerScanner.getComponentClass();
    }

    /**
     * 获取Mediator实现类
     *
     * @param protocol 协议
     * @return 相应协议的Mediator实现类
     */
    private Class<?> getMediatorClass(Protocol protocol) {
        if (this.protocolComponentScanners != null) {
            for (ProtocolScanner protocolComponent : this.protocolComponentScanners) {
                if (protocolComponent.isSupported(protocol)) {
                    return protocolComponent.getComponentClass();
                }
            }
        } else {
            throw new BusinessException("procotol component scanners is null");
        }

        throw new BusinessException(protocol.getCode() + " NOT supported!");
    }

    /**
     * 获取心跳实现类
     *
     * @return 心跳实现类
     */
    public Class<IdleStateAwareChannelHandler> getHeartBeatClass() {
        return heartBeatClass;
    }

    /**
     * 根据配置初始化业务线程池模型
     *
     * @return 业务线程池模型
     */
    public ChannelPipelineFactory getChannelPipelineFactory() {
        // 获得业务线程池模型
        MemoryAwareThreadPoolExecutor workThreadPoolexecutor;

        if (StringUtils.isNotBlank(this.executionThreadPoolClass)) {
            if (StringUtils.equals(ORDERED_MEMORY_AWARE_THREAD_POOL_EXECUTOR, this.executionThreadPoolClass)) {
                workThreadPoolexecutor = new OrderedMemoryAwareThreadPoolExecutor(this.getExecutionThreadPoolSize(), this.getExecutionThreadPoolMaxChannelMemorySize(), this.getExecutionThreadPoolMaxTotalMemorySize());
            } else if (StringUtils.equals(MEMORY_AWARE_THREAD_POOL_EXECUTOR, this.executionThreadPoolClass)) {
                workThreadPoolexecutor = new MemoryAwareThreadPoolExecutor(this.getExecutionThreadPoolSize(), this.getExecutionThreadPoolMaxChannelMemorySize(), this.getExecutionThreadPoolMaxTotalMemorySize());
            } else {
                throw new BusinessException("thread pool executor class is NOT supported");
            }
        } else {
            throw new BusinessException("thread pool executor class is NOT set");
        }

        logger.warn("work execution thread pool class: {}, thread pool size: {}", workThreadPoolexecutor.getClass().getSimpleName(), this.getExecutionThreadPoolSize());

        /**
         * 为了提高并发数，一般通过ExecutionHandler线程池来异步处理ChannelHandler链（worker线程在经过ExecutionHandler后就结束了，
         * 它会被ChannelFactory的worker线程池所回收）。
         */
        ExecutionHandler executionHandler = new ExecutionHandler(workThreadPoolexecutor);

        if (this.isHeartBeatOn()) {
            return new ProtocolBufferPipelineFactory(this, executionHandler, new HashedWheelTimer());
        } else {
            return new ProtocolBufferPipelineFactory(this, executionHandler);
        }
    }


    public String getServerName() {
        return serverName;
    }

    public Protocol getProtocol() {
        for (Protocol protocol : Protocol.values()) {
            if (StringUtils.equals(protocolType, protocol.getCode())) {
                return protocol;
            }
        }
        return Protocol.UNKNOWN;
    }

    public boolean isHeartBeatOn() {
        return StringUtils.equals("on", heartBeatSwitch);
    }

    private int getExecutionThreadPoolMaxChannelMemorySize() {
        try {
            return Integer.parseInt(executionThreadPoolMaxChannelMemorySize) * MB;
        } catch (RuntimeException e) {
            return DEFAULT_EXECUTION_THREAD_POOL_MAX_CHANNEL_MEMORY_SIZE * MB;
        }
    }

    private int getExecutionThreadPoolSize() {
        try {
            return Integer.parseInt(executionThreadPoolSize);
        } catch (RuntimeException e) {
            return DEFAULT_EXECUTION_THREAD_POOL_SIZE;
        }
    }

    private int getExecutionThreadPoolMaxTotalMemorySize() {
        try {
            return Integer.parseInt(executionThreadPoolMaxTotalMemorySize) * MB;
        } catch (RuntimeException e) {
            return DEFAULT_EXECUTION_THREAD_POOL_MAX_TOTAL_MEMORY_SIZE * MB;
        }
    }

    public int getHeartBeatWriterIdleTime() {
        try {
            return Integer.parseInt(heartBeatWriterIdleTime);
        } catch (RuntimeException e) {
            return DEFAULT_HEART_BEAT_WRITE_IDLE_TIME;
        }
    }

    public int getHeartBeatReaderIdleTime() {
        try {
            return Integer.parseInt(heartBeatReaderIdleTime);
        } catch (RuntimeException e) {
            return DEFAULT_HEART_BEAT_READ_IDLE_TIME;
        }
    }

    public int getHeartBeatAllIdleTime() {
        try {
            return Integer.parseInt(heartBeatAllIdleTime);
        } catch (RuntimeException e) {
            return DEFAULT_HEART_BEAT_ALL_IDLE_TIME;
        }
    }

    public Class<ProtocolHandler> getBusinessHandlerClass() {
        return businessHandlerClass;
    }

    public int getListenPort() {
        try {
            return Integer.parseInt(listenPort);
        } catch (RuntimeException e) {
            return DEFAULT_LISTEN_PORT;
        }
    }
}
