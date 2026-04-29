package com.product.nexustalk.auth.security;

import com.product.nexustalk.user.entity.UserRole;

public record AuthUserPrincipal(
        Long userId,
        String username,
        UserRole role
) {
}

