package com.product.nexustalk.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        @Size(max = 100)
        String login,

        @NotBlank
        @Size(min = 8, max = 200)
        String password
) {
}

