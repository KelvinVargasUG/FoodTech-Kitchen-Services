package com.foodtech.kitchen.infrastructure.rest.dto;

import java.util.List;

//HUMAN REVIEW: Reemplazado List<Map<String, String>> por List<ProductRequest> para mejor type safety.
public record CreateOrderRequest(
    String tableNumber,
    List<ProductRequest> products
) {}