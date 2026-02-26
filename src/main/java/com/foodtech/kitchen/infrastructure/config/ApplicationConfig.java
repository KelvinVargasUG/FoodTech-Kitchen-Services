package com.foodtech.kitchen.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodtech.kitchen.application.ports.in.*;
import com.foodtech.kitchen.application.ports.out.CommandExecutor;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.PasswordHasher;
import com.foodtech.kitchen.application.ports.out.PayloadSerializer;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.application.ports.out.TokenGenerator;
import com.foodtech.kitchen.application.ports.out.UserRepository;
import com.foodtech.kitchen.application.usecases.*;
import com.foodtech.kitchen.domain.ports.out.AsyncCommandDispatcher;
import com.foodtech.kitchen.domain.services.*;
import com.foodtech.kitchen.infrastructure.execution.ReactorAsyncCommandDispatcher;
import com.foodtech.kitchen.infrastructure.security.BCryptPasswordHasher;
import com.foodtech.kitchen.infrastructure.security.JwtTokenGenerator;
import com.foodtech.kitchen.infrastructure.serialization.JacksonPayloadSerializer;
import com.foodtech.kitchen.infrastructure.transactional.TransactionalOrderCompletionService;
import com.foodtech.kitchen.infrastructure.transactional.TransactionalProcessOrderPort;
import com.foodtech.kitchen.infrastructure.transactional.TransactionalRequestOrderInvoicePort;
import com.foodtech.kitchen.infrastructure.transactional.TransactionalStartTaskPreparationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.time.Clock;
import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public PayloadSerializer payloadSerializer(ObjectMapper objectMapper) {
        return new JacksonPayloadSerializer(objectMapper);
    }

    @Bean
    public InvoicePayloadBuilder invoicePayloadBuilder(PayloadSerializer payloadSerializer) {
        return new InvoicePayloadBuilder(payloadSerializer);
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
    public PrepareDrinkStrategy prepareDrinkStrategy() {
        return new PrepareDrinkStrategy();
    }

    @Bean
    public PrepareHotDishStrategy prepareHotDishStrategy() {
        return new PrepareHotDishStrategy();
    }

    @Bean
    public PrepareColdDishStrategy prepareColdDishStrategy() {
        return new PrepareColdDishStrategy();
    }

    @Bean
    public CommandFactory commandFactory(List<CommandStrategy> strategies) {
        return new CommandFactory(strategies);
    }

    @Bean
    public TaskDecomposer taskDecomposer(
            OrderValidator orderValidator,
            TaskFactory taskFactory
    ) {
        return new TaskDecomposer(orderValidator, taskFactory);
    }

    @Bean
    public OrderStatusCalculator orderStatusCalculator() {
        return new OrderStatusCalculator();
    }

    @Bean
    public ProcessOrderUseCase processOrderUseCase(
            OrderRepository orderRepository,
            TaskDecomposer taskDecomposer,
            TaskRepository taskRepository
    ) {
        return new ProcessOrderUseCase(orderRepository, taskDecomposer, taskRepository);
    }

    @Bean
    public ProcessOrderPort processOrderPort(ProcessOrderUseCase processOrderUseCase) {
        return new TransactionalProcessOrderPort(processOrderUseCase);
    }

    @Bean
    public OrderCompletionService orderCompletionService(
            TaskRepository taskRepository,
            OrderRepository orderRepository
    ) {
        return new TransactionalOrderCompletionService(taskRepository, orderRepository);
    }

    @Bean
    public StartTaskPreparationUseCase startTaskPreparationUseCase(
            TaskRepository taskRepository,
            OrderRepository orderRepository,
            CommandFactory commandFactory,
            AsyncCommandDispatcher asyncCommandDispatcher
    ) {
        return new StartTaskPreparationUseCase(
                taskRepository,
                orderRepository,
                commandFactory,
                asyncCommandDispatcher
        );
    }

    @Bean
    public StartTaskPreparationPort startTaskPreparationPort(StartTaskPreparationUseCase startTaskPreparationUseCase) {
        return new TransactionalStartTaskPreparationPort(startTaskPreparationUseCase);
    }

    @Bean
    public AsyncCommandDispatcher asyncCommandDispatcher(
            CommandExecutor commandExecutor,
            TaskRepository taskRepository,
            OrderCompletionService orderCompletionService
    ) {
        return new ReactorAsyncCommandDispatcher(
                commandExecutor,
                taskRepository,
                orderCompletionService
        );
    }

    @Bean
    public GetTasksByStationUseCase getTasksByStationUseCase(
            TaskRepository taskRepository
    ) {
        return new GetTasksByStationUseCase(taskRepository);
    }

    @Bean
    public GetTasksByStationPort getTasksByStationPort(GetTasksByStationUseCase getTasksByStationUseCase) {
        return getTasksByStationUseCase;
    }

    @Bean
    public GetOrderStatusUseCase getOrderStatusUseCase(
            TaskRepository taskRepository,
            OrderRepository orderRepository,
            OrderStatusCalculator orderStatusCalculator
    ) {
        return new GetOrderStatusUseCase(taskRepository, orderRepository, orderStatusCalculator);
    }

    @Bean
    public GetOrderStatusPort getOrderStatusPort(GetOrderStatusUseCase getOrderStatusUseCase) {
        return getOrderStatusUseCase;
    }

    @Bean
    public GetCompletedOrdersUseCase getCompletedOrdersUseCase(
            OrderRepository orderRepository,
            TaskRepository taskRepository
    ) {
        return new GetCompletedOrdersUseCase(orderRepository, taskRepository);
    }

    @Bean
    public GetCompletedOrdersPort getCompletedOrdersPort(GetCompletedOrdersUseCase getCompletedOrdersUseCase) {
        return getCompletedOrdersUseCase;
    }

    @Bean
    public RequestOrderInvoiceUseCase requestOrderInvoiceUseCase(
            OrderRepository orderRepository,
            com.foodtech.kitchen.application.ports.out.OutboxEventRepository outboxEventRepository,
            InvoicePayloadBuilder payloadBuilder
    ) {
        return new RequestOrderInvoiceUseCase(orderRepository, outboxEventRepository, payloadBuilder);
    }

    @Bean
    public RequestOrderInvoicePort requestOrderInvoicePort(RequestOrderInvoiceUseCase requestOrderInvoiceUseCase) {
        return new TransactionalRequestOrderInvoicePort(requestOrderInvoiceUseCase);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordHasher passwordHasher
    ) {
        return new RegisterUserUseCase(userRepository, passwordHasher);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            UserRepository userRepository,
            TokenGenerator tokenGenerator,
            PasswordHasher passwordHasher
    ) {
        return new AuthenticateUserUseCase(userRepository, tokenGenerator, passwordHasher);
    }

    @Bean
    public PasswordHasher passwordHasher() {
        return new BCryptPasswordHasher();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public TokenGenerator tokenGenerator(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expirationSeconds}") long expirationSeconds,
            Clock clock
    ) {
        return new JwtTokenGenerator(secret, expirationSeconds, clock);
    }
}