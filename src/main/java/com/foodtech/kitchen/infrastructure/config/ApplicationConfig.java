package com.foodtech.kitchen.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodtech.kitchen.application.ports.in.BulkUploadProductsPort;
import com.foodtech.kitchen.application.ports.in.ChangeProductStatusPort;
import com.foodtech.kitchen.application.ports.in.CreateProductPort;
import com.foodtech.kitchen.application.ports.in.DeleteOrderPort;
import com.foodtech.kitchen.application.ports.in.GetActiveProductsPort;
import com.foodtech.kitchen.application.ports.in.GetCompletedOrdersPort;
import com.foodtech.kitchen.application.ports.in.GetOrderStatusPort;
import com.foodtech.kitchen.application.ports.in.GetTasksByStationPort;
import com.foodtech.kitchen.application.ports.in.ProcessOrderPort;
import com.foodtech.kitchen.application.ports.in.RequestOrderInvoicePort;
import com.foodtech.kitchen.application.ports.in.StartTaskPreparationPort;
import com.foodtech.kitchen.application.ports.in.UpdateProductPort;
import com.foodtech.kitchen.application.ports.out.CommandExecutor;
import com.foodtech.kitchen.application.ports.out.OrderRepository;
import com.foodtech.kitchen.application.ports.out.PasswordHasher;
import com.foodtech.kitchen.application.ports.out.PayloadSerializer;
import com.foodtech.kitchen.application.ports.out.ProductRepository;
import com.foodtech.kitchen.application.ports.out.TaskRepository;
import com.foodtech.kitchen.application.ports.out.TokenGenerator;
import com.foodtech.kitchen.application.ports.out.UploadSessionRepository;
import com.foodtech.kitchen.application.ports.out.UserRepository;
import com.foodtech.kitchen.application.usecases.AuthenticateUserUseCase;
import com.foodtech.kitchen.application.usecases.BulkUploadProductsUseCase;
import com.foodtech.kitchen.application.usecases.ChangeProductStatusUseCase;
import com.foodtech.kitchen.application.usecases.CreateProductUseCase;
import com.foodtech.kitchen.application.usecases.DeleteOrderUseCase;
import com.foodtech.kitchen.application.usecases.GetActiveProductsUseCase;
import com.foodtech.kitchen.application.usecases.GetCompletedOrdersUseCase;
import com.foodtech.kitchen.application.usecases.GetOrderStatusUseCase;
import com.foodtech.kitchen.application.usecases.GetTasksByStationUseCase;
import com.foodtech.kitchen.application.usecases.ProcessOrderUseCase;
import com.foodtech.kitchen.application.usecases.RegisterUserUseCase;
import com.foodtech.kitchen.application.usecases.StartTaskPreparationUseCase;
import com.foodtech.kitchen.application.usecases.InvoicePayloadBuilder;
import com.foodtech.kitchen.application.usecases.OrderCompletionService;
import com.foodtech.kitchen.application.usecases.RequestOrderInvoiceUseCase;
import com.foodtech.kitchen.application.usecases.UpdateProductUseCase;
import com.foodtech.kitchen.domain.ports.out.AsyncCommandDispatcher;
import com.foodtech.kitchen.domain.services.CommandFactory;
import com.foodtech.kitchen.domain.services.CommandStrategy;
import com.foodtech.kitchen.domain.services.OrderStatusCalculator;
import com.foodtech.kitchen.domain.services.OrderValidator;
import com.foodtech.kitchen.domain.services.PrepareColdDishStrategy;
import com.foodtech.kitchen.domain.services.PrepareDrinkStrategy;
import com.foodtech.kitchen.domain.services.PrepareHotDishStrategy;
import com.foodtech.kitchen.domain.services.TaskDecomposer;
import com.foodtech.kitchen.domain.services.TaskFactory;
import com.foodtech.kitchen.infrastructure.execution.ReactorAsyncCommandDispatcher;
import com.foodtech.kitchen.infrastructure.security.BCryptPasswordHasher;
import com.foodtech.kitchen.infrastructure.security.JwtTokenValidator;
import com.foodtech.kitchen.infrastructure.security.JwtTokenGenerator;
import com.foodtech.kitchen.infrastructure.serialization.JacksonPayloadSerializer;
import com.foodtech.kitchen.infrastructure.transactional.TransactionalOrderCompletionService;
import com.foodtech.kitchen.infrastructure.transactional.TransactionalDeleteOrderPort;
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
    public DeleteOrderUseCase deleteOrderUseCase(
            OrderRepository orderRepository,
            TaskRepository taskRepository
    ) {
        return new DeleteOrderUseCase(orderRepository, taskRepository);
    }

    @Bean
    public DeleteOrderPort deleteOrderPort(DeleteOrderUseCase deleteOrderUseCase) {
        return new TransactionalDeleteOrderPort(deleteOrderUseCase);
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

    @Bean
    public JwtTokenValidator jwtTokenValidator(
            @Value("${jwt.secret}") String secret,
            Clock clock
    ) {
        return new JwtTokenValidator(secret, clock);
    }

    @Bean
    public CreateProductPort createProductPort(ProductRepository productRepository) {
        return new CreateProductUseCase(productRepository);
    }

    @Bean
    public GetActiveProductsPort getActiveProductsPort(ProductRepository productRepository) {
        return new GetActiveProductsUseCase(productRepository);
    }

    @Bean
    public ChangeProductStatusPort changeProductStatusPort(ProductRepository productRepository) {
        return new ChangeProductStatusUseCase(productRepository);
    }

    @Bean
    public UpdateProductPort updateProductPort(ProductRepository productRepository) {
        return new UpdateProductUseCase(productRepository);
    }

    @Bean
    public BulkUploadProductsPort bulkUploadProductsPort(
            UploadSessionRepository uploadSessionRepository,
            ProductRepository productRepository,
            @Value("${upload.storage.base-path:/tmp/foodtech/uploads}") String storageBasePath,
            @Value("${upload.max-file-size-bytes:10485760}") long maxFileSizeBytes
    ) {
        return new BulkUploadProductsUseCase(
            uploadSessionRepository, productRepository, storageBasePath, maxFileSizeBytes);
    }
}
