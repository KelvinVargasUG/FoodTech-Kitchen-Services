package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.User;

public interface UserRepository {
    User save(User user);

    boolean existsByEmail(String email);
}
