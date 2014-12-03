package com.cosmos.core.utils;

import com.cosmos.netty.mediator.AbstractMediator;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;

public class ClassUtilsTest extends TestCase{

    public void testGetAllSubClass() {
        List<Class<?>> classes = null;
        try {
            classes = ClassUtils.getAllSubClass("com.demo2do", AbstractMediator.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(classes);
        Assert.assertEquals(0, classes.size());
    }

    public void testGetAllClass() {
        List<Class<?>> classes = null;
        try {
            classes = ClassUtils.getAllClass("com.demo2do.server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(classes);
        Assert.assertEquals(2, classes.size());

        for(Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }
}
