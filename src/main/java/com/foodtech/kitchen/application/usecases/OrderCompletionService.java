package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.TaskStatus;
public class OrderCompletionService {

    private final TaskRepository taskRepository;
    private final OrderRepository orderRepository;

    public OrderCompletionService(TaskRepository taskRepository, OrderRepository orderRepository) {
        this.taskRepository = taskRepository;
        this.orderRepository = orderRepository;
    }

    public void completeOrderIfReady(Long orderId) {
        long totalTasks = taskRepository.countByOrderId(orderId);
        if (totalTasks == 0) {
            return;
        }

        long completedTasks = taskRepository.countByOrderIdAndStatus(orderId, TaskStatus.COMPLETED);
        if (totalTasks != completedTasks) {
            return;
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.getStatus() == OrderStatus.COMPLETED) {
            return;
        }

        order.markCompleted();
        orderRepository.save(order);
    }
}
