package com.demo2do.core.utils;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonUtilTest extends TestCase {

    private List<String> strings;

    private List<Class<?>> classes;

    private List<Integer> iList1;

    private List<Integer> iList2;

    @Override
    public void setUp() {
        this.strings = Arrays.asList("one", "two", "two", "three");
        this.classes = Arrays.asList(new Class<?>[]{Integer.class, Long.class, Long.class, Double.class});
        this.iList1 = Arrays.asList(1, 2, 3, 3);
        this.iList2 = Arrays.asList(3, 3, 4, 5);
    }

    @Override
    public void tearDown() {
        // do nothings here
    }

    public void testDeduplicate() {
        List<String> deduplicatedStrings = CommonUtil.deduplicate(strings);

        Assert.assertEquals(strings.size() - 1, deduplicatedStrings.size());

        System.out.println();
        System.out.println("Test Deduplicate String list");
        System.out.println("original list:     " + strings);
        System.out.println("after deduplicate: " + deduplicatedStrings);

        List<Class<?>> deduplicatedClasses = CommonUtil.deduplicate(classes);

        Assert.assertEquals(classes.size() - 1, deduplicatedClasses.size());

        System.out.println();
        System.out.println("Test Deduplicate Class list");
        System.out.println("original list:     " + classes);
        System.out.println("after deduplicate: " + deduplicatedClasses);
    }

    public void testDeduplicatedMerge() {
        List<Integer> mergedIntegerList = CommonUtil.deduplicatedMerge(iList1, iList2);

        Assert.assertEquals(iList1.size() + iList2.size() - 3, mergedIntegerList.size());

        System.out.println();
        System.out.println("Test Deduplicated Merge int list");
        System.out.println("original");
        System.out.println("list1:  " + iList1);
        System.out.println("list2:  " + iList2);
        System.out.println("after deduplicated merge int list");
        System.out.println("result: " + mergedIntegerList);
    }
}
