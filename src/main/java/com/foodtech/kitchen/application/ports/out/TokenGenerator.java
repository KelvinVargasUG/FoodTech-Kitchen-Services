package com.foodtech.kitchen.application.ports.out;

public interface TokenGenerator {
    String generateToken(String username);
}
