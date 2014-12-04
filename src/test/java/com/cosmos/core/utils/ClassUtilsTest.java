package com.cosmos.core.utils;

import com.cosmos.netty.mediator.AbstractMediator;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;

public class ClassUtilsTest extends TestCase{

    public void testGetAllSubClass() {
        List<Class<? extends AbstractMediator>> classes = null;
        try {
            classes = ClassUtils.getAllSubClass("com.cosmos", AbstractMediator.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(classes);
        Assert.assertEquals(2, classes.size());

        System.out.println();
        System.out.println("List sub-class of " + AbstractMediator.class.getName());
        for(Class<? extends AbstractMediator> clazz : classes) {
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
