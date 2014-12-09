package com.cosmos.app.action;

import com.cosmos.netty.action.BaseAction;
import com.cosmos.protocol.EchoProtocol.EchoPb;
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
}
