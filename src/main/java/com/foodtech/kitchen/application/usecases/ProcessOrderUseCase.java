package com.foodtech.kitchen.application.usecases;

import com.foodtech.kitchen.application.ports.in.ProcessOrderPort;
import com.foodtech.kitchen.application.ports.out.CommandExecutor;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.domain.commands.Command;
import com.foodtech.kitchen.domain.model.Order;
import com.foodtech.kitchen.domain.model.Task;
import com.foodtech.kitchen.domain.services.CommandFactory;
import com.foodtech.kitchen.domain.services.TaskDecomposer;

import java.util.List;

//HUMAN REVIEW: Implementé el flujo completo Command Pattern que faltaba.
//Ahora después de crear tareas, se crean Commands usando CommandFactory
//y se ejecutan con CommandExecutor. Esto cumple el patrón Command correctamente.
public class ProcessOrderUseCase implements ProcessOrderPort {
    
    private final TaskDecomposer taskDecomposer;
    private final TaskRepository taskRepository;
    private final CommandFactory commandFactory;
    private final CommandExecutor commandExecutor;

    public ProcessOrderUseCase(
        TaskDecomposer taskDecomposer,
        TaskRepository taskRepository,
        CommandFactory commandFactory,
        CommandExecutor commandExecutor
    ) {
        this.taskDecomposer = taskDecomposer;
        this.taskRepository = taskRepository;
        this.commandFactory = commandFactory;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public List<Task> execute(Order order) {
        // 1. Descomponer orden en tareas
        List<Task> tasks = taskDecomposer.decompose(order);
        
        // 2. Guardar tareas en repositorio
        taskRepository.saveAll(tasks);
        
        // 3. Crear comandos a partir de las tareas
        List<Command> commands = tasks.stream()
            .map(task -> commandFactory.createCommand(task.getStation(), task.getProducts()))
            .toList();
        
        // 4. Ejecutar todos los comandos
        commandExecutor.executeAll(commands);
        
        return tasks;
    }
}