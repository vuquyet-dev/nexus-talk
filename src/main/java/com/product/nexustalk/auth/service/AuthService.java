package com.product.nexustalk.auth.service;

import com.product.nexustalk.auth.dto.LoginRequest;
import com.product.nexustalk.auth.dto.RegisterRequest;
import com.product.nexustalk.auth.dto.TokenResponse;

public interface AuthService {
    TokenResponse register(RegisterRequest request, String userAgent);

    TokenResponse login(LoginRequest request, String userAgent);

    TokenResponse refresh(String refreshToken, String userAgent);

    void logout(String refreshToken);
}

