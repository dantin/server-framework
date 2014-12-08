package com.cosmos.netty.action;

import java.lang.reflect.Method;

/**
 * 方法/Action映射对象
 *
 * @author David
 */
public class MethodBean {

    private Method method;

    private Object action;

    public MethodBean(Method method, Object action) {
        super();
        this.method = method;
        this.action = action;
    }

    public Method getMethod() {
        return method;
    }

    public Object getAction() {
        return action;
    }

}