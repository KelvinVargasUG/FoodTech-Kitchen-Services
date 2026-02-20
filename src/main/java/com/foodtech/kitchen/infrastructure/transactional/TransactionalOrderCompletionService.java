package com.foodtech.kitchen.infrastructure.transactional;

import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.application.usecases.OrderCompletionService;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalOrderCompletionService extends OrderCompletionService {

    public TransactionalOrderCompletionService(TaskRepository taskRepository, OrderRepository orderRepository) {
        super(taskRepository, orderRepository);
    }

    @Override
    @Transactional
    public void completeOrderIfReady(Long orderId) {
        super.completeOrderIfReady(orderId);
    }
}
