package com.cosmos.netty.component;

/**
 * Netty组件通用接口
 *
 * @author David
 */
public interface ComponentScanner<T> {

    /**
     * 返回组件实现类
     *
     * @return 组件实现类类名
     */
    public Class<T> getComponentClass();

}
