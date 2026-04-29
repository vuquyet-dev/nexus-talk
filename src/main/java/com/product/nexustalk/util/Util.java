package com.product.nexustalk.util;

import com.product.nexustalk.user.dto.UserResponse;
import com.product.nexustalk.user.entity.User;

public class Util {
    private static Util instance;

    public static synchronized Util getInstance()
    {
        if (instance == null)
        {
            instance = new Util();
        }
        return instance;
    }
    private Util() {
    }

    public UserResponse toUser(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getRole(),
                user.getStatus(),
                user.getLastActiveAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
