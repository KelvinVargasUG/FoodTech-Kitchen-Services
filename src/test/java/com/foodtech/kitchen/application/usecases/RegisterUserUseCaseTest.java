package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.domain.model.User;
import com.foodtech.kitchen.domain.model.UserStatus;
import com.foodtech.kitchen.application.ports.out.PasswordHasher;
import com.foodtech.kitchen.application.ports.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    void registerUser_withUniqueUsernameEmail_andValidPassword_savesUserAndReturnsActive() {
        String username = "jdoe";
        String email = "jdoe@example.com";
        String password = "abc123";

        when(passwordHasher.hash(password)).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = registerUserUseCase.execute(username, email, password);

        verify(passwordHasher).hash(password);
        verify(userRepository).save(any(User.class));
        assertNotNull(result);
        assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

    @Test
    void registerUser_withInvalidEmail_throwsExceptionAndDoesNotPersist() {
        String username = "jdoe";
        String email = "correo-invalido";
        String password = "abc123";

        assertThrows(IllegalArgumentException.class,
                () -> registerUserUseCase.execute(username, email, password));

        verify(passwordHasher, never()).hash(password);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_withWeakPassword_throwsExceptionAndDoesNotPersist() {
        String username = "jdoe";
        String email = "jdoe@example.com";
        String password = "abcdef";

        assertThrows(IllegalArgumentException.class,
                () -> registerUserUseCase.execute(username, email, password));

        verify(passwordHasher, never()).hash(password);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_withDuplicateEmail_throwsExceptionAndDoesNotPersist() {
        String username = "user1";
        String email = "test@mail.com";
        String password = "abc123";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> registerUserUseCase.execute(username, email, password));

        verify(passwordHasher, never()).hash(any());
        verify(userRepository, never()).save(any());
    }
}
