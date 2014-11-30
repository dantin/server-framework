package com.demo2do.core.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CommonUtilTest {

    @Test
    public void testDeduplicateStringList() {
        List<String> list = new ArrayList<String>();
        list.add("one");
        list.add("two");
        list.add("two");
        list.add("three");

        List<String> result = CommonUtil.deduplicate(list);

        Assert.assertEquals(list.size() - 1, result.size());

        System.out.println();
        System.out.println("Test Deduplicate String list");
        System.out.println("original list:     " + list);
        System.out.println("after deduplicate: " + result);
    }

    @Test
    public void testDeduplicateClassList() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Integer.class);
        list.add(Long.class);
        list.add(Long.class);
        list.add(Double.class);

        List<Class<?>> result = CommonUtil.deduplicate(list);

        Assert.assertEquals(list.size() - 1, result.size());

        System.out.println();
        System.out.println("Test Deduplicate Class list");
        System.out.println("original list:     " + list);
        System.out.println("after deduplicate: " + result);
    }

    @Test
    public void testDeduplicatedMergeIntList() {
        List<Integer> list1 = new ArrayList<Integer>();
        list1.add(1);
        list1.add(2);
        list1.add(3);
        list1.add(3);
        List<Integer> list2 = new ArrayList<Integer>();
        list2.add(3);
        list2.add(3);
        list2.add(4);
        list2.add(5);

        List<Integer> result = CommonUtil.deduplicatedMerge(list1, list2);

        Assert.assertEquals(list1.size() + list2.size() - 3, result.size());

        System.out.println();
        System.out.println("Test Deduplicated Merge int list");
        System.out.println("original");
        System.out.println("list1:  " + list1);
        System.out.println("list2:  " + list2);
        System.out.println("after deduplicated merge int list");
        System.out.println("result: " + result);
    }
}
