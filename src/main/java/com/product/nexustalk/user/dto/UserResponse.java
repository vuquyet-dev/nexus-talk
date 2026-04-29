package com.product.nexustalk.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.product.nexustalk.user.entity.UserRole;
import com.product.nexustalk.user.entity.UserStatus;
import com.product.nexustalk.util.Util;

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
        @JsonFormat(pattern = Util.DATETIME_FORMAT)
        LocalDateTime createdAt,
        @JsonFormat(pattern = Util.DATETIME_FORMAT)
        LocalDateTime updatedAt
) {
}

