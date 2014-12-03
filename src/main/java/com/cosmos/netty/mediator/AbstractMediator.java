package com.cosmos.netty.mediator;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.netty.Protocol;
import com.cosmos.netty.Setting;
import com.google.protobuf.MessageLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 中介器基类
 */
@Component
public abstract class AbstractMediator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMediator.class);

    private static AbstractMediator INSTANCE = null;

    public static Class<?> EXTENDED_MEDIATOR_CLASS = null;

    //private static Map<String, MethodBean> methods = new HashMap<String, MethodBean>();

    /**
     * 根据协议扫描包里适配的中介器
     *
     * @return 处理指定协议的指定包里的中介器
     */
    public static AbstractMediator getInstance() {
        return getInstance(Protocol.PROTOBUF, Setting.DEFAULT_PACKAGE);
    }

    /**
     * 根据协议扫描包里适配的中介器
     *
     * @param protocol    协议
     * @param basePackage 目标包的根
     * @return 处理指定协议的指定包里的中介器
     */
    public static synchronized AbstractMediator getInstance(final Protocol protocol, final String basePackage) {
        try {
            if (INSTANCE == null) {
                switch (protocol) {
                    case PROTOBUF:
                        EXTENDED_MEDIATOR_CLASS = ProtocolBufferMediator.getExtendedMediatorClass(basePackage);
                        INSTANCE = (AbstractMediator) EXTENDED_MEDIATOR_CLASS.newInstance();
                        break;
                    default:
                        throw new BusinessException("no mediator found! current protocol: " + protocol.getCode());
                }
            }
        } catch (BusinessException e) {
            logger.error("internal error: {}", e.getMessage());
        } catch (InstantiationException e) {
            logger.error("error instantiation mediator!");
        } catch (IllegalAccessException e) {
            logger.error("illegal access when creating mediator!");
        }

        return INSTANCE;
    }

    /**
     *
     * @param args
     * @return
     */
    public Object execute(Object... args) {
        return null;
    }

    /**
     * 获得ProtoBuffer实例，用于ChannelPipeline的Protocol Buffer协议解析链,子类可以重写此方法
     *
     * @return MessageLite, e.g. return SamplePb.getDefaultInstance();
     */
    public abstract MessageLite getPbInstance();

    /**
     * 根据请求获取对应的命令，用于action分发，子类可以重写此方法
     *
     * @param request   请求对象
     * @return Action名称
     */
    public abstract String getActionKeyByRequest(Object request);
}
