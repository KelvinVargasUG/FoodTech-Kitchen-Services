package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.TokenProvider;
import com.foodtech.kitchen.application.ports.out.UserRepository;
import com.foodtech.kitchen.domain.model.User;

public class AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public AuthenticateUserUseCase(UserRepository userRepository, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    public String execute(String identifier, String password) {
        User user = userRepository.findByEmailOrUsername(identifier)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return null;
    }
}
