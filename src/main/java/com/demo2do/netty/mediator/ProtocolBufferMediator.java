package com.demo2do.netty.mediator;

import com.demo2do.core.exception.BusinessException;
import com.demo2do.core.utils.ClassUtil;
import com.google.protobuf.MessageLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Protocol Buffer中介器
 */
public abstract class ProtocolBufferMediator extends AbstractMediator {

    private static final Logger logger = LoggerFactory.getLogger(ProtocolBufferMediator.class);

    /**
     *
     * @param basePackage
     */
    public static Class<?> getExtendedMediatorClass(String basePackage) {
        // 判断有没有类继承ProtocolBufferMediator类
        List<Class<?>> classes = null;
        try {
            classes = ClassUtil.getAllSubClass(basePackage, ProtocolBufferMediator.class);

            if (classes != null && !classes.isEmpty()) {
                return classes.get(0);
            }
        } catch (IOException e) {
            logger.error("errors when search sub-class of {}", ProtocolBufferMediator.class.getName());
            throw new BusinessException("exceptions when scan sub-class", e);
        }
        logger.error("no class extends {} under package {}", ProtocolBufferMediator.class.getName(), basePackage);
        throw new BusinessException("no sub-class found");
    }

    public abstract MessageLite getPbInstance();

    public abstract String getActionKeyByRequest(Object request);
}
