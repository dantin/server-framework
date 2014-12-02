package com.demo2do.core.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 常用工具类
 *
 * @author David
 */
public abstract class CommonUtils {

    /**
     * 去重列表
     *
     * @param list 原始列表
     * @return 去重后的列表
     */
    public static <T> List<T> deduplicate(final List<T> list) {
        if (list == null || list.size() == 0) {
            return new ArrayList<T>();
        }

        Set<T> set = new HashSet<T>();
        for (T element : list) {
            if (!set.contains(element)) {
                set.add(element);
            }
        }

        return new ArrayList<T>(set);
    }

    /**
     * 合并并去重两个列表
     *
     * @param one     列表一
     * @param another 列表二
     * @return 去重后的列表
     */
    public static <T> List<T> deduplicatedMerge(final List<T> one, final List<T> another) {
        if (one == null && another == null) {
            return null;
        }
        if (one != null && another == null) {
            return CommonUtils.deduplicate(one);
        }
        if (one == null) {
            return CommonUtils.deduplicate(another);
        }

        Set<T> set = new HashSet<T>(another);
        set.removeAll(one);

        List<T> result = new ArrayList<T>(CommonUtils.deduplicate(one));
        result.addAll(set);
        return result;
    }
}
