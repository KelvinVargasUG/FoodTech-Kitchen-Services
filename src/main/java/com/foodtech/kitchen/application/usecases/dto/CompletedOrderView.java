package com.foodtech.kitchen.application.usecases.dto;

import java.time.LocalDateTime;

public record CompletedOrderView(
        Long orderId,
        String tableNumber,
        LocalDateTime completedAt,
        Integer totalItems,
        Long totalPreparationTime
) {
}
