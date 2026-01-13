package com.foodtech.kitchen.infrastructure.rest.dto;

//HUMAN REVIEW: DTO tipado para productos. Reemplaza Map<String, String> para mejor type safety y validación.
//Validación se maneja en OrderMapper para permitir que GlobalExceptionHandler capture errores correctamente.
public record ProductRequest(
    String name,
    String type
) {}
