package com.cosmos.netty.component;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.netty.handler.ProtocolHandler;
import com.cosmos.netty.pipeline.ProtocolBufferPipelineFactory;
import com.cosmos.server.Protocol;
import com.cosmos.server.Setting;
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

import java.util.List;

/**
 * Netty组件工厂类
 *
 * @author David
 */
public class NettyComponentFactory {

    private static final Logger logger = LoggerFactory.getLogger(NettyComponentFactory.class);

    private List<ProtocolComponent> protocolComponentScanner;

    private static final String ORDERED_MEMORY_AWARE_THREAD_POOL_EXECUTOR = "OrderedMemoryAwareThreadPoolExecutor";

    private static final String MEMORY_AWARE_THREAD_POOL_EXECUTOR = "MemoryAwareThreadPoolExecutor";

    @Autowired
    private Setting setting;

    @Autowired
    private CommonComponent<IdleStateAwareChannelHandler> heartBeatScanner;

    @Autowired
    private CommonComponent<ProtocolHandler> protocolHandlerScanner;

    /**
     * @param protocolComponentScanner the protocolComponents to set
     */
    public void setProtocolComponentScanner(List<ProtocolComponent> protocolComponentScanner) {
        this.protocolComponentScanner = protocolComponentScanner;
    }

    /**
     * 获取Mediator实现类
     *
     * @param protocol 协议
     * @return 相应协议的Mediator实现类
     */
    public Class<?> getMediatorClass(Protocol protocol) {
        if(this.protocolComponentScanner != null) {
            for (ProtocolComponent protocolComponent : this.protocolComponentScanner) {
                if (protocolComponent.isSupported(protocol)) {
                    return protocolComponent.getComponentClass();
                }
            }
        }

        throw new BusinessException(protocol.getCode() + " NOT supported!");
    }

    /**
     * 获取心跳实现类
     *
     * @return 心跳实现类
     */
    public Class<IdleStateAwareChannelHandler> getHeartBeatClass() {
        return heartBeatScanner.getComponentClass();
    }

    /**
     * 获取业务处理实现类
     *
     * @return 业务处理实现类
     */
    public Class<ProtocolHandler> getProtocolHandlerClass() {
        return protocolHandlerScanner.getComponentClass();
    }

    /**
     * 根据配置初始化业务线程池模型
     *
     * @return 业务线程池模型
     */
    public ChannelPipelineFactory getChannelPipelineFactory() {
        // 获得业务线程池模型
        MemoryAwareThreadPoolExecutor workThreadPoolexecutor;

        if(StringUtils.isNotBlank(setting.getExecutionThreadPoolClass())) {
            if(StringUtils.equals(ORDERED_MEMORY_AWARE_THREAD_POOL_EXECUTOR, setting.getExecutionThreadPoolClass())) {
                workThreadPoolexecutor = new OrderedMemoryAwareThreadPoolExecutor(setting.getExecutionThreadPoolSize(), setting.getExecutionThreadPoolMaxChannelMemorySize(), setting.getExecutionThreadPoolMaxTotalMemorySize());
            } else if(StringUtils.equals(MEMORY_AWARE_THREAD_POOL_EXECUTOR, setting.getExecutionThreadPoolClass())) {
                workThreadPoolexecutor = new MemoryAwareThreadPoolExecutor(setting.getExecutionThreadPoolSize(), setting.getExecutionThreadPoolMaxChannelMemorySize(), setting.getExecutionThreadPoolMaxTotalMemorySize());
            } else {
                throw new BusinessException("thread pool executor class is NOT supported");
            }
        } else {
            throw new BusinessException("thread pool executor class is NOT set");
        }

        logger.warn("work execution thread pool class: {}, thread pool size: {}", workThreadPoolexecutor.getClass().getName(), setting.getExecutionThreadPoolSize());

        /**
         * 为了提高并发数，一般通过ExecutionHandler线程池来异步处理ChannelHandler链（worker线程在经过ExecutionHandler后就结束了，
         * 它会被ChannelFactory的worker线程池所回收）。
         */
        ExecutionHandler executionHandler = new ExecutionHandler(workThreadPoolexecutor);

        if (setting.isHeartBeatOn()) {
            return new ProtocolBufferPipelineFactory(setting, executionHandler, new HashedWheelTimer());
        } else {
            return new ProtocolBufferPipelineFactory(setting, executionHandler);
        }
    }
}
