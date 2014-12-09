package com.cosmos.app.service;

import com.cosmos.app.entity.protocol.request.AuthenticationRequest;
import com.cosmos.app.entity.protocol.response.AuthenticationResponse;

/**
 * 用户Service
 *
 * @author David
 */
public interface UserService {

    public AuthenticationResponse login(AuthenticationRequest request);

}
