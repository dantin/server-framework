package com.cosmos.app.entity.protocol.response;

import com.cosmos.app.entity.protocol.BaseProtocol;
import com.cosmos.app.entity.protocol.Marshaller;
import com.cosmos.app.entity.protocol.user.User;
import com.cosmos.protocol.user.UserServicePb;
import com.cosmos.protocol.user.UserServicePb.LoginResponse;

/**
 * Created by david on 12/8/14.
 */
public class AuthenticationResponse extends BaseProtocol implements Marshaller<LoginResponse.Builder> {

    private Long id;

    private String token;

    private String username;

    private String name;

    public AuthenticationResponse(User user) {
        this.id = user.getId();
        this.token = user.getToken();
        this.username = user.getUsername();
        this.name = user.getProfile().getName();
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    @Override
    public LoginResponse.Builder marshal() {
        LoginResponse.Builder response = LoginResponse.newBuilder();

        response.setToken(this.getToken());
        response.setUser(builderIdAndName(this.getId(), this.getName()));

        return response;
    }
}
