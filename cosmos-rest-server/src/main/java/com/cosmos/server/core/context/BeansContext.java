package com.cosmos.server.core.context;

/**
 * Beans context container similar to Spring application context.
 *
 * @author BSD
 */
public interface BeansContext {

    /**
     * Return the bean instance that uniquely matches the given object type, if any.
     *
     * @param requiredType type the bean must match; can be an interface or superclass, {@code null} is disallowed.
     * @param <T> type
     * @return an instance of the bean matching the required type
     */
    <T> T getBean(Class<T> requiredType);
}
