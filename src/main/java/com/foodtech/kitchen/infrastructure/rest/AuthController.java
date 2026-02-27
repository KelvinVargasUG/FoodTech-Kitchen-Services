package com.foodtech.kitchen.infrastructure.rest;

import com.foodtech.kitchen.application.usecases.AuthenticateUserUseCase;
import com.foodtech.kitchen.application.usecases.RegisterUserUseCase;
import com.foodtech.kitchen.infrastructure.rest.dto.LoginRequest;
import com.foodtech.kitchen.infrastructure.rest.dto.LoginResponse;
import com.foodtech.kitchen.infrastructure.rest.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          AuthenticateUserUseCase authenticateUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        // ⚕️ HUMAN CHECK: Exception mapping is delegated to GlobalExceptionHandler.
        // Controller must not swallow domain/application exceptions.
        registerUserUseCase.execute(request.username(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authenticateUserUseCase.execute(request.identifier(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }

}
