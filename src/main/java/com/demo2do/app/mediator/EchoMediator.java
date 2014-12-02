package com.demo2do.app.mediator;

import com.demo2do.entity.protocol.proto.Demo2doPb.SampleAppPb;
import com.demo2do.netty.mediator.ProtocolBufferMediator;
import com.google.protobuf.MessageLite;

/**
 * 针对应用的自定义中介器
 *
 * @author David
 */
public class EchoMediator extends ProtocolBufferMediator {

    @Override
    public String getActionKeyByRequest(Object request) {
        SampleAppPb protocolModel = (SampleAppPb) request;
        SampleAppPb.ActionType type = protocolModel.getActionType();
        return type.name();
    }

    @Override
    public MessageLite getPbInstance() {
        return SampleAppPb.getDefaultInstance();
    }
}
