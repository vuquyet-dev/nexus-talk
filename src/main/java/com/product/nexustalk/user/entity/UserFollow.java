package com.product.nexustalk.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_follows",
        uniqueConstraints = @UniqueConstraint(name = "uq_follow", columnNames = {"FOLLOWER_ID", "FOLLOWING_ID"})
)
public class UserFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "FOLLOWER_ID", nullable = false)
    private User follower;

    @ManyToOne(optional = false)
    @JoinColumn(name = "FOLLOWING_ID", nullable = false)
    private User following;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    public UserFollow() {
    }

    public UserFollow(User follower, User following) {
        this.follower = follower;
        this.following = following;
    }

    public Long getId() {
        return id;
    }

    public User getFollower() {
        return follower;
    }

    public User getFollowing() {
        return following;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

