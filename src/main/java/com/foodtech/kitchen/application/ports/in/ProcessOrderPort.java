package com.foodtech.kitchen.application.ports.in;

import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;

import java.util.List;

public interface ProcessOrderPort {
    List<Task> execute(Order order);
}