package com.foodtech.kitchen.infrastructure.rest.dto;

public record RegisterRequest(
    String username,
    String email,
    String password
) {}
