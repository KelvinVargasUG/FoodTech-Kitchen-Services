package com.foodtech.kitchen.infrastructure.config;

import com.foodtech.kitchen.application.ports.out.PasswordHasher;
import com.foodtech.kitchen.application.ports.out.UserRepository;
import com.foodtech.kitchen.domain.model.User;
import com.foodtech.kitchen.domain.model.UserStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestAuthDataConfig {

    @Bean
    public CommandLineRunner seedAuthUser(UserRepository userRepository, PasswordHasher passwordHasher) {
        return args -> {
            String email = "jdoe@example.com";
            if (userRepository.existsByEmail(email)) {
                return;
            }
            String passwordHash = passwordHasher.hash("abc123");
            User user = new User("jdoe", email, passwordHash, UserStatus.ACTIVE);
            userRepository.save(user);
        };
    }
}
