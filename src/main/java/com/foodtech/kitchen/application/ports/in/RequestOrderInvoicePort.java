package com.foodtech.kitchen.application.ports.in;

public interface RequestOrderInvoicePort {
    void execute(Long orderId);
}
