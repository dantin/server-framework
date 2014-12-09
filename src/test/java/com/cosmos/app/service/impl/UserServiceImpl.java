package com.cosmos.app.service.impl;

import com.cosmos.app.entity.protocol.request.AuthenticationRequest;
import com.cosmos.app.entity.protocol.response.AuthenticationResponse;
import com.cosmos.app.entity.protocol.user.User;
import com.cosmos.app.entity.protocol.user.UserProfile;
import com.cosmos.app.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 *
 * @author David
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        User mock = new User();
        mock.setId(1L);
        mock.setUsername(request.getUsername());
        UserProfile mockProfile = new UserProfile();
        mockProfile.setName("Mock");
        mock.setProfile(mockProfile);
        mock.setToken("111");
        return new AuthenticationResponse(mock);
    }
}
