package com.product.nexustalk.user.service;

import com.product.nexustalk.user.dto.UpdateProfileRequest;
import com.product.nexustalk.user.dto.UserResponse;

public interface UserService {
    UserResponse getById(Long id);

    UserResponse getMe(Long meId);

    UserResponse updateMe(Long meId, UpdateProfileRequest request);

    void follow(Long meId, Long targetUserId);

    void unfollow(Long meId, Long targetUserId);

    void block(Long meId, Long targetUserId);

    void unblock(Long meId, Long targetUserId);
}

