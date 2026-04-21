package com.product.nexustalk.user.repository;

import com.product.nexustalk.user.entity.User;
import com.product.nexustalk.user.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);

    void deleteByFollowerAndFollowing(User follower, User following);
}

