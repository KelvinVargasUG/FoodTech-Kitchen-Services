package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.PasswordHasher;
import com.foodtech.kitchen.application.ports.out.UserRepository;
import com.foodtech.kitchen.domain.model.User;
import com.foodtech.kitchen.domain.model.UserStatus;

public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(String username, String email, String rawPassword) {
        String passwordHash = passwordHasher.hash(rawPassword);
        User user = new User(username, email, passwordHash, UserStatus.ACTIVE);
        return userRepository.save(user);
    }
}
