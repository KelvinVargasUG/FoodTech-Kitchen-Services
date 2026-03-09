package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.PasswordHasher;
import com.foodtech.kitchen.application.ports.out.TokenGenerator;
import com.foodtech.kitchen.application.ports.out.UserRepository;
import com.foodtech.kitchen.domain.model.User;
import com.foodtech.kitchen.domain.model.UserStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Test
    void authenticateUser_whenUserNotFound_throwsException() {
        String identifier = "unknown@mail.com";
        String password = "abc123";

        when(userRepository.findByEmailOrUsername(identifier))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authenticateUserUseCase.execute(identifier, password));

        verify(tokenGenerator, never()).generateToken(any());
    }

    @Test
    void authenticateUser_whenPasswordIsIncorrect_throwsException() {
        String identifier = "user@mail.com";
        String rawPassword = "wrong123";
        String storedHash = "hashedPassword";

        User user = new User(
            1L,
            "username",
            identifier,
            storedHash,
            UserStatus.ACTIVE,
            LocalDateTime.now(),
            null
        );

        when(userRepository.findByEmailOrUsername(identifier))
            .thenReturn(Optional.of(user));

        when(passwordHasher.matches(rawPassword, storedHash))
            .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> authenticateUserUseCase.execute(identifier, rawPassword));

        verify(tokenGenerator, never()).generateToken(any());
    }

        @Test
        void authenticateUser_whenUserIsInactive_throwsException() {
        String identifier = "user@mail.com";
        String rawPassword = "abc123";
        String storedHash = "hashedPassword";

        User user = new User(
            1L,
            "username",
            identifier,
            storedHash,
            UserStatus.INACTIVE,
            LocalDateTime.now(),
            null
        );

        when(userRepository.findByEmailOrUsername(identifier))
            .thenReturn(Optional.of(user));

        when(passwordHasher.matches(rawPassword, storedHash))
            .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
            () -> authenticateUserUseCase.execute(identifier, rawPassword));

        verify(tokenGenerator, never()).generateToken(any());
        }

        @Test
        void authenticateUser_whenCredentialsAreValid_returnsToken() {
        String identifier = "user@mail.com";
        String rawPassword = "abc123";
        String storedHash = "hashedPassword";
        String expectedToken = "jwt-token";

        User user = new User(
            1L,
            "username",
            identifier,
            storedHash,
            UserStatus.ACTIVE,
            LocalDateTime.now(),
            null
        );

        when(userRepository.findByEmailOrUsername(identifier))
            .thenReturn(Optional.of(user));

        when(passwordHasher.matches(rawPassword, storedHash))
            .thenReturn(true);

        when(tokenGenerator.generateToken(user.getUsername()))
            .thenReturn(expectedToken);

        String result = authenticateUserUseCase.execute(identifier, rawPassword);

        assertEquals(expectedToken, result);

        verify(tokenGenerator).generateToken(user.getUsername());
        }
}
