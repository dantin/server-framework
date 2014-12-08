package com.cosmos.netty.component.scanner;

import com.cosmos.core.utils.ClassUtils;
import com.cosmos.netty.component.ComponentScanner;
import com.cosmos.netty.handler.ProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

/**
 * Protocol Handler扫描器
 *
 * @author David
 */
public class ProtocolHandlerScanner implements ComponentScanner<ProtocolHandler> {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolHandlerScanner.class);

    @Value("#{server['base.package']}")
    private String basePackage;

    /**
     * 扫描Protocol Handler实现类
     *
     * @return Protocol Handler实现类
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public Class<ProtocolHandler> getComponentClass() {
        try {
            List<Class<? extends ProtocolHandler>> classes = ClassUtils.getAllSubClass(basePackage, ProtocolHandler.class);

            if (!classes.isEmpty()) {
                if (classes.size() > 1) {
                    logger.warn("found multiple protocol handler, use the first one");
                }
                return (Class<ProtocolHandler>) classes.get(0);
            }
            logger.warn("no protocol handler found, use default");
        } catch (IOException e) {
            logger.warn("exception when scanning heart beat class, use default");
        }
        return ProtocolHandler.class;
    }
}
