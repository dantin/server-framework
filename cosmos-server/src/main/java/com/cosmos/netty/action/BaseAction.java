package com.cosmos.netty.action;

import com.cosmos.netty.component.mediator.Mediator;

/**
 * 中介模式中业务请求动作跳转调度基类
 *
 * @author David
 */
public abstract class BaseAction {

    public BaseAction() {
        Mediator.getInstance().register(this);
    }
}
