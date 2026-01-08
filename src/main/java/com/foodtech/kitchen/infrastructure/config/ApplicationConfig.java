package com.foodtech.kitchen.infrastructure.config;

import com.foodtech.kitchen.application.ports.in.ProcessOrderPort;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.application.usecases.ProcessOrderUseCase;
import com.foodtech.kitchen.domain.services.OrderValidator;
import com.foodtech.kitchen.domain.services.TaskDecomposer;
import com.foodtech.kitchen.domain.services.TaskFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//HUMAN REVIEW: Configuré beans para todas las clases de servicio separadas.
//Cada bean tiene una responsabilidad clara y se inyectan correctamente en TaskDecomposer.
//HUMAN REVIEW: Agregué bean ObjectMapper como singleton para inyectarlo en adapters.
//Cumple DIP: adapters no crean dependencias, las reciben por inyección.
@Configuration
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public OrderValidator orderValidator() {
        return new OrderValidator();
    }

    @Bean
    public TaskFactory taskFactory() {
        return new TaskFactory();
    }

    @Bean
    public com.foodtech.kitchen.domain.services.CommandFactory commandFactory() {
        return new com.foodtech.kitchen.domain.services.CommandFactory();
    }

    @Bean
    public TaskDecomposer taskDecomposer(OrderValidator orderValidator,
                                         TaskFactory taskFactory) {
        return new TaskDecomposer(orderValidator, taskFactory);
    }

    @Bean
    public ProcessOrderPort processOrderPort(
        TaskDecomposer taskDecomposer, 
        TaskRepository taskRepository,
        com.foodtech.kitchen.domain.services.CommandFactory commandFactory,
        com.foodtech.kitchen.application.ports.out.CommandExecutor commandExecutor
    ) {
        return new ProcessOrderUseCase(taskDecomposer, taskRepository, commandFactory, commandExecutor);
    }
}