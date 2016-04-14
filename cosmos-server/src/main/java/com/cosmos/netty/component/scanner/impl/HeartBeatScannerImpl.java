package com.cosmos.netty.component.scanner.impl;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.core.utils.ClassUtils;
import com.cosmos.netty.component.scanner.ComponentScanner;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

/**
 * Heart Beat扫描器
 *
 * @author David
 */
public class HeartBeatScannerImpl implements ComponentScanner<IdleStateAwareChannelHandler> {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatScannerImpl.class);

    private static final Class<IdleStateAwareChannelHandler> BASE_CLASS = IdleStateAwareChannelHandler.class;

    @Value("#{server['base.package']}")
    private String basePackage;

    /**
     * 扫描继承IdleStateAwareChannelHandler的心跳类
     *
     * @return 继承IdleStateAwareChannelHandler的心跳类
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public Class<IdleStateAwareChannelHandler> getComponentClass() {
        try {
            List<Class<? extends IdleStateAwareChannelHandler>> classes = ClassUtils.getAllSubClass(basePackage, BASE_CLASS);

            if (classes.isEmpty()) {
                throw new BusinessException("heart beat class not found");
            } else if (classes.size() > 1) {
                logger.warn("found multiple heart beat class, use the first one");
            }
            return (Class<IdleStateAwareChannelHandler>) classes.get(0);
        } catch (IOException e) {
            throw new BusinessException("exception when scanning heart beat class");
        }
    }
}
