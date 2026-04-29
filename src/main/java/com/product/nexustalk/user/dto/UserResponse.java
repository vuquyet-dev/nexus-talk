package com.product.nexustalk.user.dto;

import com.product.nexustalk.user.entity.UserRole;
import com.product.nexustalk.user.entity.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String avatarUrl,
        String bio,
        UserRole role,
        UserStatus status,
        LocalDateTime lastActiveAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

