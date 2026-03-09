package com.foodtech.kitchen.integration;

import com.foodtech.kitchen.application.ports.out.UserRepository;
import com.foodtech.kitchen.application.usecases.RegisterUserUseCase;
import com.foodtech.kitchen.domain.model.User;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
class RegisterUserIntegrationTest {

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_persistsUserInDatabase() {
        String username = "integrationUser";
        String email = "integration@mail.com";
        String password = "abc123";

        User user = registerUserUseCase.execute(username, email, password);

        assertNotNull(user.getId());

        Optional<User> found = userRepository.findByEmailOrUsername(email);
        assertTrue(found.isPresent());
        assertEquals(username, found.get().getUsername());
    }
}
