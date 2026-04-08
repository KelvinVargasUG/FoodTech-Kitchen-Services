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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import com.foodtech.kitchen.application.exepcions.FileSizeLimitExceededException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("component")
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleOrderNotFoundException_returnsNotFound() {

        OrderNotFoundException ex = new OrderNotFoundException(10L);

        ResponseEntity<ErrorResponse> response = handler.handleOrderNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Order not found", response.getBody().message());
        assertEquals(404, response.getBody().status());
    }

    @Test
    void handleTaskNotFoundException_returnsNotFound() {

        TaskNotFoundException ex = new TaskNotFoundException(99L);

        ResponseEntity<ErrorResponse> response = handler.handleTaskNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task not found", response.getBody().message());
        assertEquals(404, response.getBody().status());
    }

    @Test
    void handleDuplicateEmailException_returnsConflict() {

        DuplicateEmailException ex = new DuplicateEmailException("dup");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateEmailException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Duplicate email", response.getBody().message());
        assertEquals(409, response.getBody().status());
    }

    @Test
    void handleDuplicateUsernameException_returnsConflict() {

        DuplicateUsernameException ex = new DuplicateUsernameException("dup");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateUsernameException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Duplicate username", response.getBody().message());
        assertEquals(409, response.getBody().status());
    }

    @Test
    void handleValidationException_returnsBadRequest() {

        IllegalArgumentException ex = new IllegalArgumentException("bad input");

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().message());
        assertEquals(400, response.getBody().status());
    }

    @Test
    void handleIllegalStateException_returnsBadRequest() {

        IllegalStateException ex = new IllegalStateException("bad state");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid state transition", response.getBody().message());
        assertEquals(400, response.getBody().status());
    }

    @Test
    void handleTypeMismatchException_formatsMessage() {

        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "BAD",
                String.class,
                "station",
                null,
                new IllegalArgumentException("bad")
        );

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatchException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid parameter type", response.getBody().message());
        assertEquals(400, response.getBody().status());
        assertNotNull(response.getBody().error());
    }

    @Test
    void handleMethodArgumentNotValidException_usesFirstFieldErrorMessage() {

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "Email is required"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().message());
        assertEquals(400, response.getBody().status());
        assertEquals("Email is required", response.getBody().error());
    }

    @Test
    void handleHttpMessageNotReadableException_returnsBadRequest() {

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("bad json", (Throwable) null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadableException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().message());
        assertEquals(400, response.getBody().status());
    }

    @Test
    void handleFileSizeLimitExceededException_returnsPayloadTooLarge() {

        long actualBytes = 12L * 1024 * 1024;  
        long maxBytes    = 10L * 1024 * 1024;  
        FileSizeLimitExceededException ex = new FileSizeLimitExceededException(actualBytes, maxBytes);

        ResponseEntity<ErrorResponse> response =
                handler.handleFileSizeLimitExceededException(ex);

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(413, response.getBody().status());
        assertEquals("El archivo supera el tamaño máximo permitido (10 MB)",
                response.getBody().message());
    }

    @Test
    void handleMaxUploadSizeExceededException_returnsPayloadTooLarge() {

        MaxUploadSizeExceededException ex =
                new MaxUploadSizeExceededException(10 * 1024 * 1024L);

        ResponseEntity<ErrorResponse> response =
                handler.handleMaxUploadSizeExceededException(ex);

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(413, response.getBody().status());
        assertEquals("El archivo supera el tamaño máximo permitido (10 MB)",
                response.getBody().message());
    }

    @Test
    void handleGenericException_returnsInternalServerError() {

        Exception ex = new Exception("boom");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().error());
        assertEquals(500, response.getBody().status());
    }
}
