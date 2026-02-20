package com.foodtech.kitchen.infrastructure.rest.dto;

import java.time.LocalDateTime;

public record CompletedOrderResponse(
        Long orderId,
        String tableNumber,
        LocalDateTime completedAt,
        Integer totalItems,
        Long totalPreparationTime
) {
}
