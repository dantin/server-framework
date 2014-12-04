package com.cosmos.netty;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.core.utils.ClassUtils;
import com.cosmos.netty.handler.ProtocolHandler;
import com.cosmos.netty.pipeline.ProtocolBufferPipelineFactory;
import com.cosmos.server.Setting;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Netty Component工厂类
 *
 * @author David
 */
public abstract class ComponentFactory {

    private static final Logger logger = LoggerFactory.getLogger(ComponentFactory.class);

    private static final String ORDERED_MEMORY_AWARE_THREAD_POOL_EXECUTOR = "OrderedMemoryAwareThreadPoolExecutor";

    private static final String MEMORY_AWARE_THREAD_POOL_EXECUTOR = "MemoryAwareThreadPoolExecutor";

    /**
     * 获取并设置心跳handler类
     *
     * @param setting 全局配置类
     */
    public static void scanHeartBeatHandlerClass(Setting setting) {
        // 扫描继承IdleStateAwareChannelHandler的心跳类
        try {
            List<Class<? extends IdleStateAwareChannelHandler>> classes = ClassUtils.getAllSubClass(setting.getBasePackage(), IdleStateAwareChannelHandler.class);

            if (classes.isEmpty()) {
                throw new BusinessException("heart beat class not found");
            } else if (classes.size() > 1) {
                logger.warn("found multiple heart beat class, use the first one");
            }
            setting.setHeartBeatClass((Class<IdleStateAwareChannelHandler>)classes.get(0));
        } catch (IOException e) {
            logger.warn("exception when scanning heart beat class");
            throw new BusinessException(e);
        }
    }

    /**
     * 获取并设置Protocol handler类
     *
     * @param setting 全局配置类
     */
    public static void scanProtocolHandlerClass(Setting setting) {
        // 扫描继承IdleStateAwareChannelHandler的心跳类
        try {
            List<Class<? extends ProtocolHandler>> classes = ClassUtils.getAllSubClass(setting.getBasePackage(), ProtocolHandler.class);

            if (!classes.isEmpty()) {
                if (classes.size() > 1) {
                    logger.warn("found multiple protocol handler, use the first one");
                }
                setting.setBusinessHandlerClass((Class<ProtocolHandler>) classes.get(0));
            }
        } catch (IOException e) {
            logger.warn("exception when scanning heart beat class, use the default");
            setting.setBusinessHandlerClass(ProtocolHandler.class);
        }
    }

    /**
     * 根据配置初始化业务线程池模型
     *
     * @param setting 全局配置类
     * @return 业务线程池模型
     */
    public static MemoryAwareThreadPoolExecutor getThreadPoolExecutorInstance(final Setting setting) {

        if(StringUtils.isNotBlank(setting.getExecutionThreadPoolClass())) {
            if(StringUtils.equals(ORDERED_MEMORY_AWARE_THREAD_POOL_EXECUTOR, setting.getExecutionThreadPoolClass())) {
                return new OrderedMemoryAwareThreadPoolExecutor(setting.getExecutionThreadPoolSize(), setting.getExecutionThreadPoolMaxChannelMemorySize(), setting.getExecutionThreadPoolMaxTotalMemorySize());
            } else if(StringUtils.equals(MEMORY_AWARE_THREAD_POOL_EXECUTOR, setting.getExecutionThreadPoolClass())) {
                return new MemoryAwareThreadPoolExecutor(setting.getExecutionThreadPoolSize(), setting.getExecutionThreadPoolMaxChannelMemorySize(), setting.getExecutionThreadPoolMaxTotalMemorySize());
            } else {
                throw new BusinessException("thread pool executor class is NOT supported");
            }
        } else {
            throw new BusinessException("thread pool executor class is NOT set");
        }
    }

    /**
     *
     * @param executionHandler
     * @param setting
     * @return
     */
    public static ProtocolBufferPipelineFactory getServerPipelineFactoryInstance(ExecutionHandler executionHandler, final Setting setting) {
        if (setting.isHeartBeatOn()) {
            return new ProtocolBufferPipelineFactory(setting, executionHandler, new HashedWheelTimer());
        } else {
            return new ProtocolBufferPipelineFactory(setting, executionHandler);
        }
    }
}
