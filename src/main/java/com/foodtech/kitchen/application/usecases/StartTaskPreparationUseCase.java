package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.exepcions.TaskNotFoundException;
import com.foodtech.kitchen.application.ports.in.StartTaskPreparationPort;
import com.foodtech.kitchen.application.ports.out.CommandExecutor;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.OrderStatus;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.model.TaskStatus;
import com.foodtech.kitchen.domain.services.CommandFactory;
import com.foodtech.kitchen.domain.services.OrderStatusCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class StartTaskPreparationUseCase implements StartTaskPreparationPort {

    private final TaskRepository taskRepository;
    private final OrderRepository orderRepository;
    private final CommandFactory commandFactory;
    private final CommandExecutor commandExecutor;
    private final OrderStatusCalculator orderStatusCalculator;

    public StartTaskPreparationUseCase(
            TaskRepository taskRepository,
            OrderRepository orderRepository,
            CommandFactory commandFactory,
            CommandExecutor commandExecutor,
            OrderStatusCalculator orderStatusCalculator
    ) {
        this.taskRepository = taskRepository;
        this.orderRepository = orderRepository;
        this.commandFactory = commandFactory;
        this.commandExecutor = commandExecutor;
        this.orderStatusCalculator = orderStatusCalculator;
    }

    @Override
    @Transactional
    public Task execute(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        task.start();
        Task savedTask = taskRepository.save(task);
        updateOrderInProgress(savedTask.getOrderId());

        // Ejecutar comando asíncronamente
        Mono.fromRunnable(() -> {
                    Command command = commandFactory.createCommand(
                            savedTask.getStation(),
                            savedTask.getProducts()
                    );
                    commandExecutor.execute(command);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(unused -> {
                    Task completedTask = taskRepository.findById(taskId)
                            .orElseThrow(() -> new TaskNotFoundException(taskId));
                    completedTask.complete();
                    taskRepository.save(completedTask);
                    updateOrderIfCompleted(completedTask.getOrderId());
                    System.out.println("✅ [REACTOR] Task " + taskId + " completed");
                })
                .doOnError(error -> {
                    System.err.println("❌ [REACTOR] Error in task " + taskId);
                    error.printStackTrace();
                })
                .subscribe();

        return savedTask;
    }

    private void updateOrderInProgress(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.markInProgress();
        orderRepository.save(order);
    }

    private void updateOrderIfCompleted(Long orderId) {
        List<Task> tasks = taskRepository.findByOrderId(orderId);
        if (tasks.isEmpty()) {
            return;
        }

        TaskStatus status = orderStatusCalculator.calculateOrderStatus(tasks);
        if (status != TaskStatus.COMPLETED) {
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