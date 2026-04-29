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

@Entity
@Table(
        name = "user_blocks",
        uniqueConstraints = @UniqueConstraint(name = "uq_block", columnNames = {"BLOCKER_ID", "BLOCKED_ID"})
)
public class UserBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "BLOCKER_ID", nullable = false)
    private User blocker;

    @ManyToOne(optional = false)
    @JoinColumn(name = "BLOCKED_ID", nullable = false)
    private User blocked;

    public UserBlock() {
    }

    public UserBlock(User blocker, User blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }

    public Long getId() {
        return id;
    }

    public User getBlocker() {
        return blocker;
    }

    public User getBlocked() {
        return blocked;
    }
}

