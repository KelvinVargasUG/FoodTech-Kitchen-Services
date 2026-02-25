package com.foodtech.kitchen.domain.model;

import java.time.LocalDateTime;

public class User {
    private final Long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final UserStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastLoginAt;

    public User(String username, String email, String passwordHash, UserStatus status) {
        this(null, username, email, passwordHash, status, LocalDateTime.now(), null);
    }

    public User(Long id,
                String username,
                String email,
                String passwordHash,
                UserStatus status,
                LocalDateTime createdAt,
                LocalDateTime lastLoginAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
