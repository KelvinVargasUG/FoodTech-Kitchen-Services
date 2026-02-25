package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByEmailOrUsername(String identifier);
}
