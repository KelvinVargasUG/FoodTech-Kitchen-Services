package com.foodtech.kitchen.infrastructure.execution;

import com.foodtech.kitchen.application.exepcions.TaskNotFoundException;
import com.foodtech.kitchen.application.ports.out.CommandExecutor;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.application.usecases.OrderCompletionService;
import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.ports.out.AsyncCommandDispatcher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class ReactorAsyncCommandDispatcher implements AsyncCommandDispatcher {

    private final CommandExecutor commandExecutor;
    private final TaskRepository taskRepository;
    private final OrderCompletionService orderCompletionService;

    public ReactorAsyncCommandDispatcher(
            CommandExecutor commandExecutor,
            TaskRepository taskRepository,
            OrderCompletionService orderCompletionService
    ) {
        this.commandExecutor = commandExecutor;
        this.taskRepository = taskRepository;
        this.orderCompletionService = orderCompletionService;
    }

    @Override
    public void dispatch(Command command, Long taskId) {
        Mono.fromRunnable(() -> commandExecutor.execute(command))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(unused -> {
                    Task completedTask = taskRepository.findById(taskId)
                            .orElseThrow(() -> new TaskNotFoundException(taskId));
                    completedTask.complete();
                    taskRepository.save(completedTask);
                    orderCompletionService.completeOrderIfReady(completedTask.getOrderId());
                    System.out.println("[REACTOR] Task " + taskId + " completed");
                })
                .doOnError(error -> {
                    System.err.println("[REACTOR] Error in task " + taskId);
                    error.printStackTrace();
                })
                .subscribe();
    }
}
