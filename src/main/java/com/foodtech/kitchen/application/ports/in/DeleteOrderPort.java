package com.foodtech.kitchen.application.ports.in;

public interface DeleteOrderPort {
    void execute(Long orderId);
}
