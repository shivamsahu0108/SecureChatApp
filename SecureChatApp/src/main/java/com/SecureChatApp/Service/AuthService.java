package com.SecureChatApp.Service;


import com.SecureChatApp.Dto.Request.LoginRequest;
import com.SecureChatApp.Dto.Request.RegisterRequest;
import com.SecureChatApp.Dto.Response.AuthResponse;
import com.SecureChatApp.Dto.Response.UserResponse;
import com.SecureChatApp.Security.RequestContext;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request, RequestContext ctx);

    AuthResponse refresh(String refreshToken, RequestContext ctx);

    UserResponse me(Long userId);
}
