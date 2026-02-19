package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.ports.in.GetOrderStatusPort;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.model.TaskStatus;
import com.foodtech.kitchen.domain.services.OrderStatusCalculator;

import java.util.List;

public class GetOrderStatusUseCase implements GetOrderStatusPort {

    private final TaskRepository taskRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusCalculator orderStatusCalculator;

    public GetOrderStatusUseCase(TaskRepository taskRepository,
                                 OrderRepository orderRepository,
                                 OrderStatusCalculator orderStatusCalculator) {
        this.taskRepository = taskRepository;
        this.orderRepository = orderRepository;
        this.orderStatusCalculator = orderStatusCalculator;
    }

    @Override
    public TaskStatus execute(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        List<Task> tasks = taskRepository.findByOrderId(orderId);

        // Validación de aplicación: el orderId debe existir
        if (tasks.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }

        // Delegar lógica de negocio al dominio
        TaskStatus taskDerivedStatus = orderStatusCalculator.calculateOrderStatus(tasks);
        TaskStatus persistedStatus = orderStatusCalculator.calculateOrderStatus(order.getStatus(), tasks);

        if (persistedStatus == taskDerivedStatus) {
            return persistedStatus;
        }

        return taskDerivedStatus;
    }
}