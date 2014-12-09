package com.cosmos.app.entity.protocol.request;

import com.cosmos.core.exception.BusinessException;
import com.cosmos.core.exception.ErrorCode;
import com.cosmos.protocol.EchoProtocol.EchoPb;
import com.cosmos.protocol.user.UserServicePb.LoginRequest;
import com.google.common.base.Preconditions;

/**
 * 登录请求对象
 *
 * @author David
 */
public class AuthenticationRequest extends BaseRequest {

    private String username;

    private String password;

    protected AuthenticationRequest(EchoPb pb) {
        try {
            Preconditions.checkArgument(pb.hasUserService());
            Preconditions.checkArgument(pb.getUserService().hasLoginRequest());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("missing required request part", ErrorCode.param_1021);
        }

        LoginRequest request = pb.getUserService().getLoginRequest();

        try {
            Preconditions.checkArgument(request.hasAccount());
            Preconditions.checkArgument(request.hasPassword());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("missing required parameter", ErrorCode.param_1021);
        }

        this.username = request.getAccount();
        this.password = request.getPassword();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
