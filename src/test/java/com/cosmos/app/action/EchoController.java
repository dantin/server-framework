package com.cosmos.app.action;

import com.cosmos.app.entity.protocol.RequestFactory;
import com.cosmos.app.entity.protocol.request.AuthenticationRequest;
import com.cosmos.app.entity.protocol.response.AuthenticationResponse;
import com.cosmos.app.service.UserService;
import com.cosmos.core.exception.BusinessException;
import com.cosmos.netty.action.Action;
import com.cosmos.protocol.EchoProtocol.EchoPb;
import com.cosmos.protocol.user.UserServicePb.LoginResponse;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * 示例控制器
 */
@Controller
public class EchoController extends GeneralAction {

    @Resource
    private UserService userService;

    @Action(name = "LOGIN")
    public EchoPb login(EchoPb request, String ip) {
        EchoPb.Builder response = EchoPb.newBuilder();
        LoginResponse.Builder loginResponse = null;

        try {
            //获取请求对象
            AuthenticationRequest authenticationRequest = RequestFactory.parseRequest(AuthenticationRequest.class, request);

            AuthenticationResponse authenticationResponse = userService.login(authenticationRequest);

            //设置响应对象
            loginResponse = authenticationResponse.marshal();
            //loginResponse.setResponseStatus(createCommonStatus(ErrorCode.success_0));
        } catch (BusinessException e) {

        }

        return null;
    }
}
