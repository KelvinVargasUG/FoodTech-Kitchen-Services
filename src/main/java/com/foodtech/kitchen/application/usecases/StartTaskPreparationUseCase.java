package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.exepcions.OrderNotFoundException;
import com.foodtech.kitchen.application.exepcions.TaskNotFoundException;
import com.foodtech.kitchen.application.ports.in.StartTaskPreparationPort;
import com.foodtech.kitchen.application.ports.out.CommandExecutor;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.services.CommandFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Service
public class StartTaskPreparationUseCase implements StartTaskPreparationPort {

    private final TaskRepository taskRepository;
    private final OrderRepository orderRepository;
    private final CommandFactory commandFactory;
    private final CommandExecutor commandExecutor;
    private final OrderCompletionService orderCompletionService;

    public StartTaskPreparationUseCase(
            TaskRepository taskRepository,
            OrderRepository orderRepository,
            CommandFactory commandFactory,
            CommandExecutor commandExecutor,
            OrderCompletionService orderCompletionService
    ) {
        this.taskRepository = taskRepository;
        this.orderRepository = orderRepository;
        this.commandFactory = commandFactory;
        this.commandExecutor = commandExecutor;
        this.orderCompletionService = orderCompletionService;
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
                        orderCompletionService.completeOrderIfReady(completedTask.getOrderId());
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
}