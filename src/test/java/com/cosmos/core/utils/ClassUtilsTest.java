package com.cosmos.core.utils;

import com.cosmos.netty.mediator.Mediator;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;

public class ClassUtilsTest extends TestCase{

    public void testGetAllSubClass() {
        List<Class<? extends Mediator>> classes = null;
        try {
            classes = ClassUtils.getAllSubClass("com.cosmos", Mediator.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(classes);
        Assert.assertEquals(2, classes.size());

        System.out.println();
        System.out.println("List sub-class of " + Mediator.class.getName());
        for(Class<? extends Mediator> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }

    public void testGetAllClass() {
        List<Class<?>> classes = null;
        try {
            classes = ClassUtils.getAllClass("com.cosmos.server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(classes);
        Assert.assertEquals(4, classes.size());

        System.out.println();
        System.out.println("List all classes under com.cosmos.server");
        for(Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }
}
