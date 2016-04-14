package com.cosmos.netty.component.scanner.impl;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.core.utils.ClassUtils;
import com.cosmos.netty.component.scanner.ProtocolScanner;
import com.cosmos.netty.component.mediator.ProtocolBufferMediator;
import com.cosmos.netty.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

/**
 * Protocol Buffer Mediator扫描器
 *
 * @author David
 */
public class ProtocolBufferMediatorScannerImpl implements ProtocolScanner<ProtocolBufferMediator> {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolScanner.class);

    private static final Protocol PROTOCOL_BUFFER = Protocol.PROTOBUF;

    @Value("#{server['base.package']}")
    private String basePackage;

    @Override
    public boolean isSupported(Protocol protocol) {
        return PROTOCOL_BUFFER == protocol;
    }

    /**
     * 扫描Protocol Buffer Mediator的实现类
     *
     * @return Protocol Buffer Mediator的实现类
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public Class<ProtocolBufferMediator> getComponentClass() {
        // 判断有没有类继承ProtocolBufferMediator类
        try {
            List<Class<? extends ProtocolBufferMediator>> classes = ClassUtils.getAllSubClass(basePackage, ProtocolBufferMediator.class);

            if (classes != null && !classes.isEmpty()) {
                return (Class<ProtocolBufferMediator>) classes.get(0);
            }
        } catch (IOException e) {
            logger.error("errors when search sub-class of {}", ProtocolBufferMediator.class.getName());
            throw new BusinessException("exceptions when scan sub-class", e);
        }
        logger.error("no class extends {} under package {}", ProtocolBufferMediator.class.getName(), basePackage);
        throw new BusinessException("no sub-class found");
    }
}
