package com.demo2do.core.utils;

import com.demo2do.netty.mediation.Mediator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ClassUtilTest {

    @Test
    public void testGetAllSubClass() {
        List<Class<?>> classes = ClassUtil.getAllSubClass("com.demo2do", Mediator.class);
        Assert.assertNotNull(classes);
        Assert.assertEquals(0, classes.size());
    }

    @Test
    public void testGetAllClass() {
        List<Class<?>> classes = null;
        try {
            classes = ClassUtil.getAllClass("com.demo2do.server");
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
