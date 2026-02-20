package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.exepcions.TaskNotFoundException;
import com.foodtech.kitchen.application.ports.in.StartTaskPreparationPort;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.ports.out.AsyncCommandDispatcher;
import com.foodtech.kitchen.domain.services.CommandFactory;
public class StartTaskPreparationUseCase implements StartTaskPreparationPort {

    private final TaskRepository taskRepository;
    private final OrderRepository orderRepository;
    private final CommandFactory commandFactory;
    private final AsyncCommandDispatcher asyncCommandDispatcher;

    public StartTaskPreparationUseCase(
            TaskRepository taskRepository,
            OrderRepository orderRepository,
            CommandFactory commandFactory,
            AsyncCommandDispatcher asyncCommandDispatcher
    ) {
        this.taskRepository = taskRepository;
        this.orderRepository = orderRepository;
        this.commandFactory = commandFactory;
        this.asyncCommandDispatcher = asyncCommandDispatcher;
    }

    @Override
    public Task execute(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        task.start();
        Task savedTask = taskRepository.save(task);
        updateOrderInProgress(savedTask.getOrderId());

        Command command = commandFactory.createCommand(
                savedTask.getStation(),
                savedTask.getProducts()
        );
        asyncCommandDispatcher.dispatch(command, taskId);

        return savedTask;
    }

    private void updateOrderInProgress(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markInProgress();
        orderRepository.save(order);
    }
}