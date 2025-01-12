package com.bolt.services;

import com.bolt.controller.auth.LoginRequest;
import com.bolt.controller.auth.RegisterRequest;

public interface IAuthenticationService {
    void register(RegisterRequest request);
    void authenticate (LoginRequest request);

}
