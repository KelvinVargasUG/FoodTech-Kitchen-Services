package com.foodtech.kitchen.infrastructure.rest.dto;

import java.util.List;
import java.util.Map;

public record CreateOrderRequest(
    String tableNumber,
    List<Map<String, String>> products
) {}