package com.cosmos.app.mediator;

import com.cosmos.netty.mediator.ProtocolBufferMediator;
import com.cosmos.protocol.EchoProtocol.EchoPb;
import com.google.protobuf.MessageLite;

/**
 * 针对应用的自定义中介器
 *
 * @author David
 */
public class EchoMediator extends ProtocolBufferMediator {

    @Override
    public String getActionKeyByRequest(Object request) {
        EchoPb protocolModel = (EchoPb) request;
        EchoPb.ActionType type = protocolModel.getActionType();
        return type.name();
    }

    @Override
    public MessageLite getPbInstance() {
        return EchoPb.getDefaultInstance();
    }
}
