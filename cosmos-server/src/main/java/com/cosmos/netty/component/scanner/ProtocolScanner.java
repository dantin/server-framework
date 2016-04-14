package com.cosmos.netty.component.scanner;

import com.cosmos.netty.Protocol;

/**
 * 协议组件
 *
 * @author David
 */
public interface ProtocolScanner<T> extends ComponentScanner<T> {

    /**
     * 判断组件是否支持
     *
     * @param protocol 协议
     * @return 判断结果
     */
    public boolean isSupported(Protocol protocol);

}
