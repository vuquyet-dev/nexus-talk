package com.product.nexustalk.user.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 200)
        String avatarUrl,

        @Size(max = 2000)
        String bio
) {
}

