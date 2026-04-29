package com.product.nexustalk.user.service;

import com.product.nexustalk.user.dto.UpdateProfileRequest;
import com.product.nexustalk.user.dto.UserResponse;
import com.product.nexustalk.user.entity.User;
import com.product.nexustalk.user.entity.UserBlock;
import com.product.nexustalk.user.entity.UserFollow;
import com.product.nexustalk.user.exception.UserNotFoundException;
import com.product.nexustalk.user.repository.UserBlockRepository;
import com.product.nexustalk.user.repository.UserFollowRepository;
import com.product.nexustalk.user.repository.UserRepository;
import jakarta.websocket.Decoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Base64;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final UserBlockRepository blockRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            UserFollowRepository followRepository,
            UserBlockRepository blockRepository
    ) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.blockRepository = blockRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMe(Long meId) {
        return getById(meId);
    }

    @Override
    public UserResponse updateMe(Long meId, UpdateProfileRequest request) {
        User user = userRepository.findById(meId).orElseThrow(() -> new UserNotFoundException(meId));
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl());
        }
        if (request.bio() != null) {
            user.setBio(request.bio());
        }
        return toResponse(user);
    }

    @Override
    public void follow(Long meId, Long targetUserId) {
        if (meId.equals(targetUserId)) {
            return;
        }
        User me = userRepository.findById(meId).orElseThrow(() -> new UserNotFoundException(meId));
        User target = userRepository.findById(targetUserId).orElseThrow(() -> new UserNotFoundException(targetUserId));
        if (followRepository.existsByFollowerAndFollowing(me, target)) {
            return;
        }
        followRepository.save(new UserFollow(me, target));
    }

    @Override
    public void unfollow(Long meId, Long targetUserId) {
        User me = userRepository.findById(meId).orElseThrow(() -> new UserNotFoundException(meId));
        User target = userRepository.findById(targetUserId).orElseThrow(() -> new UserNotFoundException(targetUserId));
        followRepository.deleteByFollowerAndFollowing(me, target);
    }

    @Override
    public void block(Long meId, Long targetUserId) {
        if (meId.equals(targetUserId)) {
            return;
        }
        User me = userRepository.findById(meId).orElseThrow(() -> new UserNotFoundException(meId));
        User target = userRepository.findById(targetUserId).orElseThrow(() -> new UserNotFoundException(targetUserId));
        if (blockRepository.existsByBlockerAndBlocked(me, target)) {
            return;
        }
        blockRepository.save(new UserBlock(me, target));
    }

    @Override
    public void unblock(Long meId, Long targetUserId) {
        User me = userRepository.findById(meId).orElseThrow(() -> new UserNotFoundException(meId));
        User target = userRepository.findById(targetUserId).orElseThrow(() -> new UserNotFoundException(targetUserId));
        blockRepository.deleteByBlockerAndBlocked(me, target);
    }

    private static UserResponse toResponse(User user) {
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

