package com.demo2do.core.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CommonUtilTest {

    @Test
    public void testDeduplicate() {
        List<String> list = new ArrayList<String>();
        list.add("one");
        list.add("two");
        list.add("two");
        list.add("three");
        Assert.assertEquals("deduplicate 'two'", list.size() - 1, CommonUtil.deduplicate(list).size());
        for (String element : CommonUtil.deduplicate(list)) {
            System.out.println(element);
        }
    }

    @Test
    public void testDeduplicatedMerge() {
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
        Assert.assertEquals(list1.size() + list2.size() - 3, CommonUtil.deduplicatedMerge(list1, list2).size());
        for (Integer element : CommonUtil.deduplicatedMerge(list1, list2)) {
            System.out.println(element);
        }
    }
}
