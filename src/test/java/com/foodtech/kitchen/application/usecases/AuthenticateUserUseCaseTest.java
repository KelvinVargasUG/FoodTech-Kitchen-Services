package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.out.TokenProvider;
import com.foodtech.kitchen.application.ports.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProvider tokenProvider;

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

        verify(tokenProvider, never()).generateToken(any());
    }
}
