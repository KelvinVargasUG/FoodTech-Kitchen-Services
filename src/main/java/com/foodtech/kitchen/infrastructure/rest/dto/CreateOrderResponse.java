package com.foodtech.kitchen.infrastructure.rest.dto;

public record CreateOrderResponse(
    String tableNumber,
    Integer tasksCreated,
    String message
) {}