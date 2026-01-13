package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.in.ProcessOrderPort;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.services.TaskDecomposer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessOrderUseCase implements ProcessOrderPort {

    private final OrderRepository orderRepository;
    private final TaskDecomposer taskDecomposer;
    private final TaskRepository taskRepository;

    public ProcessOrderUseCase(
            OrderRepository orderRepository,
            TaskDecomposer taskDecomposer,
            TaskRepository taskRepository
    ) {
        this.orderRepository = orderRepository;
        this.taskDecomposer = taskDecomposer;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> execute(Order order) {
        Order savedOrder = orderRepository.save(order);
        List<Task> tasks = taskDecomposer.decompose(savedOrder);
        taskRepository.saveAll(tasks);

        return tasks;
    }
}