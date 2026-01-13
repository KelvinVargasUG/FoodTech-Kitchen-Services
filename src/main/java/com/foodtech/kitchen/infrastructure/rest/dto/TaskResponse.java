package com.foodtech.kitchen.infrastructure.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record TaskResponse(
    Long id,
    Long orderId,
    String station,
    String tableNumber,
    List<Map<String, String>> products,
    LocalDateTime createdAt,
    String status,
    LocalDateTime startedAt,
    LocalDateTime completedAt
) {}