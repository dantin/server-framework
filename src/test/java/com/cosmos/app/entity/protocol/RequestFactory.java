package com.cosmos.app.entity.protocol;

import com.cosmos.app.entity.protocol.request.BaseRequest;
import com.cosmos.core.exception.BusinessException;
import com.cosmos.protocol.EchoProtocol.EchoPb;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 请求工厂对象
 *
 * @author David
 */
public abstract class RequestFactory {

    /**
     * 将Protocol Buffer对象解析成业务对象
     *
     * @param clazz 目标对象类型
     * @param request Protobuf 对象
     * @param <T> 类型
     * @return 解析后的业务对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseRequest(Class<? extends BaseRequest> clazz, EchoPb request) {
        if(request == null) {
            throw new BusinessException("request is NULL");
        }

        try {
            Constructor constructor = clazz.getDeclaredConstructor(new Class[]{EchoPb.class});
            constructor.setAccessible(true);
            return (T) constructor.newInstance(request);
        } catch (NoSuchMethodException e) {
            throw new BusinessException("request constructor NOT found");
        } catch (InvocationTargetException e) {
            throw new BusinessException("request parsing exception");
        } catch (InstantiationException e) {
            throw new BusinessException("request parsing exception");
        } catch (IllegalAccessException e) {
            throw new BusinessException("request parsing exception");
        }
    }
}
