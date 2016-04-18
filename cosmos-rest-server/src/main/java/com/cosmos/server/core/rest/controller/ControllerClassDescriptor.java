package com.cosmos.server.core.rest.controller;

/**
 * Controller Class Descriptor.
 *
 * @author BSD
 */
public class ControllerClassDescriptor {

    private Class<?> clazz;

    public ControllerClassDescriptor(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
