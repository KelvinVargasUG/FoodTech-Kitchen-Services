package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.PasswordHasher;
import com.foodtech.kitchen.application.ports.out.TokenProvider;
import com.foodtech.kitchen.application.ports.out.UserRepository;
import com.foodtech.kitchen.domain.model.User;

public class AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordHasher passwordHasher;

    public AuthenticateUserUseCase(UserRepository userRepository,
                                   TokenProvider tokenProvider,
                                   PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordHasher = passwordHasher;
    }

    public String execute(String identifier, String password) {
        User user = userRepository.findByEmailOrUsername(identifier)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return null;
    }
}
