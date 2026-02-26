package com.foodtech.kitchen.infrastructure.rest.dto;

public record LoginRequest(
    String identifier,
    String password
) {}
