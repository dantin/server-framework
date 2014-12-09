package com.cosmos.app.action;

import com.cosmos.core.exception.ErrorCode;
import com.cosmos.netty.action.BaseAction;
import com.cosmos.protocol.EchoProtocol.EchoPb;
import com.cosmos.protocol.common.CommonPb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 业务控制器基类
 *
 * @author David
 */
public class GeneralAction extends BaseAction {

    protected static final Logger logger = LoggerFactory.getLogger(GeneralAction.class);

    protected EchoPb writelog(EchoPb request, EchoPb.Builder response) {
        logger.warn("\n*****request：*****\n" + request.toString());
        logger.warn("\n*****response：*****\n" + response.build().toString());
        return response.build();
    }

    protected CommonPb.ResponseStatus createCommonStatus(ErrorCode errorCode) {
        CommonPb.ResponseStatus.Builder status = CommonPb.ResponseStatus.newBuilder();
        status.setCode(errorCode.getError().code());
        status.setDescription(errorCode.getError().message());

        return status.build();
    }
}
