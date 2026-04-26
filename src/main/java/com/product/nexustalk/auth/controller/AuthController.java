package com.product.nexustalk.auth.controller;

import com.product.nexustalk.auth.dto.LoginRequest;
import com.product.nexustalk.auth.dto.LogoutRequest;
import com.product.nexustalk.auth.dto.RefreshRequest;
import com.product.nexustalk.auth.dto.RegisterRequest;
import com.product.nexustalk.auth.dto.TokenResponse;
import com.product.nexustalk.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register(@Valid @RequestBody RegisterRequest request, HttpServletRequest http) {
        return authService.register(request, http.getHeader("User-Agent"));
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        return authService.login(request, http.getHeader("User-Agent"));
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request, HttpServletRequest http) {
        return authService.refresh(request.refreshToken(), http.getHeader("User-Agent"));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.refreshToken());
    }

    @GetMapping("/oauth2/google")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void oauth2Google() {
        // TODO: implement spring-security oauth2-client flow as in the document.
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void forgotPassword() {
        // TODO: implement email reset flow (Spring Mail) as in the document.
    }
}

