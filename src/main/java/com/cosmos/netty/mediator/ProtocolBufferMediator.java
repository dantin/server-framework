package com.cosmos.netty.mediator;

import com.cosmos.core.exception.BusinessException;

/**
 * Protocol Buffer中介器
 *
 * @author David
 */
public abstract class ProtocolBufferMediator extends Mediator {

    /**
     * 默认构造函数
     *
     * 若使用Protocol Buffer协议，则需要判断自定义的Protocol Buffer协议是否实现
     */
    public ProtocolBufferMediator() {
        if (this.getPbInstance() == null) {
            throw new BusinessException("the method getPbInstance() returns null in protocol buffer mediator");
        }
    }

}