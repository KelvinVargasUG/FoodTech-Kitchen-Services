package com.foodtech.kitchen.infrastructure.rest.dto;

public record ProductRequest(
    String name,
    String type,
    int price
) {}
