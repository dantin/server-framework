package com.cosmos.server;

import com.cosmos.netty.handler.ProtocolHandler;
import com.cosmos.server.Protocol;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

/**
 * 配置参数
 */
public class Setting {

    private static final List<String> SUPPORTED_PROTOCOLS = Arrays.asList(Protocol.PROTOBUF.getCode());

    public static final String DEFAULT_PACKAGE = "com.cosmos";

    private static final int MB = 1024 * 1024;

    /**
     * 心跳处理类
     */
    private Class<IdleStateAwareChannelHandler> heartBeatClass;

    /**
     * 业务handler处理类
     */
    private Class<ProtocolHandler> businessHandlerClass;

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
     * 扫描包目录
     */
    @Value("#{server['base.package']}")
    private String basePackage;

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

    private static final int DEFAULT_EXECUTION_THREADPOOL_MAXCHANNELMEMORYSIZE = 0;

    /**
     * 业务处理线程池个数(活动线程的最大数量),worker execution threads
     */
    @Value("#{server['execution.threadpool.size']}")
    private String executionThreadPoolSize;

    private static final int DEFAULT_EXECUTION_THREADPOOL_SIZE = 16;
    /**
     * 该线程池队事件的内存总大小，单位MB，默认0禁用
     */
    @Value("#{server['execution.threadpool.maxTotalMemorySize']}")
    private String executionThreadPoolMaxTotalMemorySize;

    private static final int DEFAULT_EXECUTION_THREADPOOL_MAXTOTALMEMORYSIZE = 0;

    @Value("#{server['heartbeat.readerIdleTimeMillis']}")
    private String heartBeatReaderIdleTime;

    private static final int DEFAULT_HEART_BEAT_READ_IDLE_TIME = 10;

    @Value("#{server['heartbeat.writerIdleTimeMillis']}")
    private String heartBeatWriterIdleTime;

    private static final int DEFAULT_HEART_BEAT_WRITE_IDLE_TIME = 10;

    @Value("#{server['heartbeat.allIdleTimeMillis']}")
    private String heartBeatAllIdleTime;

    private static final int DEFAULT_HEART_BEAT_ALL_IDLE_TIME = 0;

    @Value("#{server['listen.port']}")
    private String listenPort;

    private static final int DEFAULT_LISTEN_PORT = 20330;

    private Setting() {
    }

    public void setHeartBeatClass(Class<IdleStateAwareChannelHandler> heartBeatClass) {
        this.heartBeatClass = heartBeatClass;
    }

    public void setBusinessHandlerClass(Class<ProtocolHandler> businessHandlerClass) {
        this.businessHandlerClass = businessHandlerClass;
    }

    public boolean isSupportedProtocol() {
        for (String supportedProtocol : SUPPORTED_PROTOCOLS) {
            if (StringUtils.equals(supportedProtocol, protocolType)) {
                return true;
            }
        }
        return false;
    }

    public String getSupportedProtocols() {
        return StringUtils.join(SUPPORTED_PROTOCOLS, " or ");
    }

    public String getServerName() {
        return serverName;
    }

    public Protocol getProtocol() {
        for(Protocol protocol : Protocol.values()) {
            if(StringUtils.equals(protocolType, protocol.getCode())) {
                return protocol;
            }
        }
        return Protocol.UNKNOWN;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public boolean isHeartBeatOn() {
        return StringUtils.equals("on", heartBeatSwitch);
    }

    public String getExecutionThreadPoolClass() {
        return executionThreadPoolClass;
    }

    public int getExecutionThreadPoolMaxChannelMemorySize() {
        try {
            return Integer.parseInt(executionThreadPoolMaxChannelMemorySize) * MB;
        } catch (RuntimeException e) {
            return DEFAULT_EXECUTION_THREADPOOL_MAXCHANNELMEMORYSIZE * MB;
        }
    }

    public int getExecutionThreadPoolSize() {
        try {
            return Integer.parseInt(executionThreadPoolSize);
        } catch (RuntimeException e) {
            return DEFAULT_EXECUTION_THREADPOOL_SIZE;
        }
    }

    public int getExecutionThreadPoolMaxTotalMemorySize() {
        try {
            return Integer.parseInt(executionThreadPoolMaxTotalMemorySize) * MB;
        } catch (RuntimeException e) {
            return DEFAULT_EXECUTION_THREADPOOL_MAXTOTALMEMORYSIZE * MB;
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

    public Class<IdleStateAwareChannelHandler> getHeartBeatClass() {
        return heartBeatClass;
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
