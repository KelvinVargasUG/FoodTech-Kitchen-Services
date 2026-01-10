package com.foodtech.kitchen.infrastructure.rest.dto;

//HUMAN REVIEW: DTO para respuestas de error estandarizadas. Mejora consistencia de API y facilita debugging.
//Usa String para timestamp para evitar problemas de serialización JSON con LocalDateTime.
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
