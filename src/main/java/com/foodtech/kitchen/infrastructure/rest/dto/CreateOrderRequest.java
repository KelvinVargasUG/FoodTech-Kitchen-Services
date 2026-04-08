package com.foodtech.kitchen.infrastructure.rest.dto;

import java.util.List;

public record CreateOrderRequest(
    String tableNumber,
    String customerName,
    String customerEmail,
    List<ProductRequest> products
) {}
