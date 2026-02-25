package com.foodtech.kitchen.application.ports.out;

import com.foodtech.kitchen.domain.model.User;

public interface TokenProvider {
    String generateToken(User user);
}
