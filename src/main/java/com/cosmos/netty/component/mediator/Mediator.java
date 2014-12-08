package com.cosmos.netty.component.mediator;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.netty.Setting;
import com.google.protobuf.MessageLite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 中介器基类
 */
@Component
public abstract class Mediator {

    private static final Logger logger = LoggerFactory.getLogger(Mediator.class);

    private static Mediator INSTANCE = null;

    //private static Map<String, MethodBean> methods = new HashMap<String, MethodBean>();


    /**
     * 生成适配的中介器
     *
     * @return 处理指定协议的指定包里的中介器
     */
    public static synchronized Mediator getInstance() {
        if(Setting.EXTENDED_MEDIATOR_CLASS == null) {
            throw new BusinessException("mediator class NOT found");
        }
        if (INSTANCE == null) {
            try {
                INSTANCE = Setting.EXTENDED_MEDIATOR_CLASS.newInstance();
            } catch (BusinessException e) {
                logger.error("internal error: {}", e.getMessage());
            } catch (InstantiationException e) {
                logger.error("error instantiation mediator!");
            } catch (IllegalAccessException e) {
                logger.error("illegal access when creating mediator!");
            }

            if (INSTANCE == null) {
                throw new BusinessException("mediator creation failed");
            }
        }

        return INSTANCE;
    }

    /**
     * @param args
     * @return
     */
    public Object execute(Object... args) {
        return null;
    }

    /**
     * 获得协议实例，用于ChannelPipeline的Protocol Buffer协议解析链,子类可以重写此方法
     *
     * @return MessageLite, e.g. return SamplePb.getDefaultInstance();
     */
    public abstract MessageLite getProtocolInstance();

    /**
     * 根据请求获取对应的命令，用于action分发，子类可以重写此方法
     *
     * @param request 请求对象
     * @return Action名称
     */
    public abstract String getActionKeyByRequest(Object request);

}
