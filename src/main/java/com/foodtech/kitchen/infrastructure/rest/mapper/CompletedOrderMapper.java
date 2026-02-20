package com.foodtech.kitchen.infrastructure.rest.mapper;

import com.foodtech.kitchen.application.usecases.dto.CompletedOrderView;
import com.foodtech.kitchen.infrastructure.rest.dto.CompletedOrderResponse;

import java.util.List;
import java.util.stream.Collectors;

public class CompletedOrderMapper {

    private CompletedOrderMapper() {
    }

    public static CompletedOrderResponse toResponse(CompletedOrderView view) {
        return new CompletedOrderResponse(
                view.orderId(),
                view.tableNumber(),
                view.completedAt(),
                view.totalItems(),
                view.totalPreparationTime()
        );
    }

    public static List<CompletedOrderResponse> toResponseList(List<CompletedOrderView> views) {
        return views.stream()
                .map(CompletedOrderMapper::toResponse)
                .collect(Collectors.toList());
    }
}
