package com.product.nexustalk.user.repository;

import com.product.nexustalk.user.entity.User;
import com.product.nexustalk.user.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    boolean existsByBlockerAndBlocked(User blocker, User blocked);

    void deleteByBlockerAndBlocked(User blocker, User blocked);
}

