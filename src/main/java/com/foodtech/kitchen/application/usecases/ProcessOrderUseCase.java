package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.in.ProcessOrderPort;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.services.TaskDecomposer;

import java.util.List;

public class ProcessOrderUseCase implements ProcessOrderPort {
    
    private final TaskDecomposer taskDecomposer;
    private final TaskRepository taskRepository;

    public ProcessOrderUseCase(TaskDecomposer taskDecomposer, TaskRepository taskRepository) {
        this.taskDecomposer = taskDecomposer;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> execute(Order order) {
        // Descomponer orden en tareas
        List<Task> tasks = taskDecomposer.decompose(order);
        
        // Guardar tareas
        taskRepository.saveAll(tasks);
        
        return tasks;
    }
}