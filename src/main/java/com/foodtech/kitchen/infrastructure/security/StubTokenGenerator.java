package com.foodtech.kitchen.infrastructure.security;

import com.foodtech.kitchen.application.ports.out.TokenGenerator;

public class StubTokenGenerator implements TokenGenerator {
    @Override
    public String generateToken(String username) {
        return "stub-token-for-" + username;
    }
}
