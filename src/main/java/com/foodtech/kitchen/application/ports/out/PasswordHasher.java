package com.foodtech.kitchen.application.ports.out;

public interface PasswordHasher {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String storedHash);
}
