package com.product.nexustalk.common.api;

import java.time.Instant;

public record BaseResponse(
        int status,
        String code,
        String message,
        Instant timestamp
) {
    public static BaseResponse of(int status, String code, String message) {
        return new BaseResponse(status, code, message, Instant.now());
    }
}

