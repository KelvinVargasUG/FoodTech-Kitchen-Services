package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.ports.in.DeleteOrderPort;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;

public class DeleteOrderUseCase implements DeleteOrderPort {

    private final OrderRepository orderRepository;
    private final TaskRepository taskRepository;

    public DeleteOrderUseCase(OrderRepository orderRepository, TaskRepository taskRepository) {
        this.orderRepository = orderRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute(Long orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        taskRepository.deleteByOrderId(orderId);
        orderRepository.deleteById(orderId);
    }
}
