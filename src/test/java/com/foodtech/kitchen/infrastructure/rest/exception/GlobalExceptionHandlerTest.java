package com.foodtech.kitchen.infrastructure.rest.exception;

import com.foodtech.kitchen.application.exepcions.DuplicateEmailException;
import com.foodtech.kitchen.application.exepcions.DuplicateUsernameException;
import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.exepcions.TaskNotFoundException;
import com.foodtech.kitchen.infrastructure.rest.dto.ErrorResponse;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("component")
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleOrderNotFoundException_returnsNotFound() {
        // Arrange
        OrderNotFoundException ex = new OrderNotFoundException(10L);

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleOrderNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Order not found", response.getBody().message());
        assertEquals(404, response.getBody().status());
    }

    @Test
    void handleTaskNotFoundException_returnsNotFound() {
        // Arrange
        TaskNotFoundException ex = new TaskNotFoundException(99L);

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleTaskNotFoundException(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task not found", response.getBody().message());
        assertEquals(404, response.getBody().status());
    }

    @Test
    void handleDuplicateEmailException_returnsConflict() {
        // Arrange
        DuplicateEmailException ex = new DuplicateEmailException("dup");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleDuplicateEmailException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Duplicate email", response.getBody().message());
        assertEquals(409, response.getBody().status());
    }

    @Test
    void handleDuplicateUsernameException_returnsConflict() {
        // Arrange
        DuplicateUsernameException ex = new DuplicateUsernameException("dup");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleDuplicateUsernameException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Duplicate username", response.getBody().message());
        assertEquals(409, response.getBody().status());
    }

    @Test
    void handleValidationException_returnsBadRequest() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("bad input");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().message());
        assertEquals(400, response.getBody().status());
    }

    @Test
    void handleIllegalStateException_returnsBadRequest() {
        // Arrange
        IllegalStateException ex = new IllegalStateException("bad state");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid state transition", response.getBody().message());
        assertEquals(400, response.getBody().status());
    }

    @Test
    void handleTypeMismatchException_formatsMessage() {
        // Arrange
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "BAD",
                String.class,
                "station",
                null,
                new IllegalArgumentException("bad")
        );

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatchException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid parameter type", response.getBody().message());
        assertEquals(400, response.getBody().status());
        assertNotNull(response.getBody().error());
    }

    @Test
    void handleMethodArgumentNotValidException_usesFirstFieldErrorMessage() {
        // Arrange
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "Email is required"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValidException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().message());
        assertEquals(400, response.getBody().status());
        assertEquals("Email is required", response.getBody().error());
    }

    @Test
    void handleHttpMessageNotReadableException_returnsBadRequest() {
        // Arrange
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("bad json", (Throwable) null);

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().message());
        assertEquals(400, response.getBody().status());
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        // Arrange
        Exception ex = new Exception("boom");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().error());
        assertEquals(500, response.getBody().status());
    }
}
