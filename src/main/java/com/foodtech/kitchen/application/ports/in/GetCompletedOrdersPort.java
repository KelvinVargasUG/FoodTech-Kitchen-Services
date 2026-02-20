package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.application.usecases.dto.CompletedOrderView;

import java.util.List;

public interface GetCompletedOrdersPort {
    List<CompletedOrderView> execute();
}
