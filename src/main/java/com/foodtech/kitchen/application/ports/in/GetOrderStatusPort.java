package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.domain.model.TaskStatus;

public interface GetOrderStatusPort {
    TaskStatus execute(Long orderId);
}
