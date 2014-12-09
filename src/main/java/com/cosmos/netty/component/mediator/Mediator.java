package com.cosmos.netty.component.mediator;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.netty.Setting;
import com.cosmos.netty.action.Action;
import com.cosmos.netty.action.BaseAction;
import com.cosmos.netty.action.MethodBean;
import com.google.protobuf.MessageLite;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 中介器基类
 *
 * @author David
 */
@Component
public abstract class Mediator {

    private static final Logger logger = LoggerFactory.getLogger(Mediator.class);

    @SuppressWarnings("unchecked")
    private static final List<Class<?>> METHOD_PARAMETER_TYPES = Arrays.asList(String.class, ChannelHandlerContext.class, MessageEvent.class);

    private static Mediator INSTANCE = null;

    private static Map<String, MethodBean> methods = new HashMap<String, MethodBean>();

    /**
     * 注册action
     *
     * @param baseAction Action类
     */
    public void register(BaseAction baseAction) {
        for (Method method : baseAction.getClass().getMethods()) {
            Action action = method.getAnnotation(Action.class);
            if (action != null && StringUtils.isNotBlank(action.name())) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length > METHOD_PARAMETER_TYPES.size() + 1) {
                    logger.error("register action error, the parameter's length should be less than 4, please check the parameter. method name: {}, action name: {}, action class: {}", method.getName(), action.name(), baseAction.getClass().getName());
                } else {
                    if (containsInvalidType(parameters)) {
                        logger.error("register action error, please check the parameter. method name: {}, action name: {}, action class: {}", method.getName(), action.name(), baseAction.getClass().getName());
                    } else {
                        methods.put(action.name(), new MethodBean(method, baseAction));
                        logger.warn("register action, action name: {}, method name: {}, action class: {}", action.name(), method.getName(), baseAction.getClass().getName());
                    }
                }
            }
        }
    }

    /**
     * 是否包含非法参数类型
     *
     * @param parameters 方法参数类型
     * @return 是否包含非法参数类型的判断结果
     */
    private boolean containsInvalidType(Class<?>[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return true;
        }

        // 业务参数只有一个
        int businessRequest = 0;
        for (Class<?> parameter : parameters) {
            if (!METHOD_PARAMETER_TYPES.contains(parameter)) {
                businessRequest++;
            }
        }

        return businessRequest > 1;
    }

    /**
     * 生成适配的中介器
     *
     * @return 处理指定协议的指定包里的中介器
     */
    public static synchronized Mediator getInstance() {
        if (Setting.EXTENDED_MEDIATOR_CLASS == null) {
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
     * 执行业务逻辑
     *
     * @param args 方法参数
     *             第一个参数request：请求对象(Object)
     *             第二个参数ip：客户端ip(String)
     *             第三个参数ctx：ProtocolHandler中messageReceived方法传过来的ChannelHandlerContext对象
     *             第四个参数e：ProtocolHandler中messageReceived方法传过来的MessageEvent对象
     *             <p/>
     *             说明：Action的业务方法参数必须为这四个中的一个
     * @return 返回对象
     */
    public Object execute(Object... args) {
        long start = System.currentTimeMillis();

        if (args == null || args.length == 0) {
            logger.error("the execute method parameter is blank");
            return null;
        }

        //第一个参数是请求object
        String cmd = getActionKeyByRequest(args[0]);
        if (StringUtils.isBlank(cmd)) {
            logger.error("the cmd is null, please check request");
            return null;
        }

        MethodBean methodBean = methods.get(cmd);
        if (methodBean == null) {
            logger.error("action not found, please check request");
            return null;
        }

        Object response = null;
        try {
            Method method = methodBean.getMethod();
            Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length == 1) {
                response = methodBean.getMethod().invoke(
                        methodBean.getAction(),
                        args[getArgsIndex(parameters[0])]);
            } else if (parameters.length == 2) {
                response = methodBean.getMethod().invoke(
                        methodBean.getAction(),
                        args[getArgsIndex(parameters[0])],
                        args[getArgsIndex(parameters[1])]);

            } else if (parameters.length == 3) {
                response = methodBean.getMethod().invoke(
                        methodBean.getAction(),
                        args[getArgsIndex(parameters[0])],
                        args[getArgsIndex(parameters[1])],
                        args[getArgsIndex(parameters[2])]);
            } else if (parameters.length == 4) {
                response = methodBean.getMethod().invoke(
                        methodBean.getAction(),
                        args[getArgsIndex(parameters[0])],
                        args[getArgsIndex(parameters[1])],
                        args[getArgsIndex(parameters[2])],
                        args[getArgsIndex(parameters[3])]);
            } else {
                logger.error("action: {}, method: {}, parameter error", methodBean.getAction().getClass().getName(), method.getName());
            }

            logger.warn("execute elapsed time: {} ms, action: {}, class: {}, method: {}",
                    System.currentTimeMillis() - start, cmd, methodBean.getAction().getClass().getName(),
                    methodBean.getMethod().getName());

            return response;
        } catch (Exception e) {
            logger.error("execute action error, action: {}, exception: {}", cmd, e);
        }

        return response;
    }

    private int getArgsIndex(Class<?> clazz) {
        if (clazz == String.class) {
            return 1;
        } else if (clazz == ChannelHandlerContext.class) {
            return 2;
        } else if (clazz == MessageEvent.class) {
            return 3;
        } else {
            return 0;
        }
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
