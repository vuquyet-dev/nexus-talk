package com.product.nexustalk.auth.service;

import com.product.nexustalk.auth.dto.LoginRequest;
import com.product.nexustalk.auth.dto.RegisterRequest;
import com.product.nexustalk.auth.dto.TokenResponse;
import com.product.nexustalk.auth.entity.RefreshToken;
import com.product.nexustalk.auth.exception.InvalidCredentialsException;
import com.product.nexustalk.auth.exception.InvalidRefreshTokenException;
import com.product.nexustalk.auth.repository.RefreshTokenRepository;
import com.product.nexustalk.user.entity.User;
import com.product.nexustalk.user.exception.DuplicateUserException;
import com.product.nexustalk.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public TokenResponse register(RegisterRequest request, String userAgent) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateUserException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateUserException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);

        return issueTokens(saved, userAgent);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request, String userAgent) {
        Optional<User> byEmail = userRepository.findByEmail(request.login());
        User user = byEmail.orElseGet(() -> userRepository.findByUsername(request.login()).orElse(null));
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return issueTokens(user, userAgent);
    }

    @Override
    public TokenResponse refresh(String refreshToken, String userAgent) {
        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken).orElseThrow(InvalidRefreshTokenException::new);
        if (rt.isRevoked() || rt.isExpired(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException();
        }
        String expectedUaHash = rt.getUserAgentHash();
        if (expectedUaHash != null && !expectedUaHash.equals(hashUserAgent(userAgent))) {
            throw new InvalidRefreshTokenException();
        }

        // Rotate refresh token
        rt.revokeNow();
        String newRefresh = randomToken();
        RefreshToken rotated = new RefreshToken(rt.getUser(), newRefresh, expectedUaHash, LocalDateTime.now().plus(REFRESH_TOKEN_TTL));
        refreshTokenRepository.save(rotated);

        String access = jwtService.issueAccessToken(rt.getUser());
        return TokenResponse.bearer(access, newRefresh, JwtService.ACCESS_TOKEN_TTL.toSeconds());
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    private TokenResponse issueTokens(User user, String userAgent) {
        String access = jwtService.issueAccessToken(user);
        String refresh = randomToken();
        String uaHash = hashUserAgent(userAgent);
        RefreshToken rt = new RefreshToken(user, refresh, uaHash, LocalDateTime.now().plus(REFRESH_TOKEN_TTL));
        refreshTokenRepository.save(rt);
        return TokenResponse.bearer(access, refresh, JwtService.ACCESS_TOKEN_TTL.toSeconds());
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private static String hashUserAgent(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(userAgent.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return null;
        }
    }
}

