package com.cosmos.netty.component;

import com.cosmos.server.Protocol;

/**
 * 协议组件
 *
 * @author David
 */
public interface ProtocolComponent<T> extends CommonComponent<T> {

    /**
     * 判断组件是否支持
     *
     * @param protocol 协议
     * @return 判断结果
     */
    public boolean isSupported(Protocol protocol);


}
