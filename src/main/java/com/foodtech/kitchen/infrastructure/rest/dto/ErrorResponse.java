package com.foodtech.kitchen.infrastructure.rest.dto;

public record ErrorResponse(
    String error,
    String message,
    String timestamp,
    int status
) {
    public ErrorResponse(String error, String message, int status) {
        this(error, message, java.time.LocalDateTime.now().toString(), status);
    }
}
