# SYSTEM_MAP

## 1) System Overview
FoodTech Kitchen Service is a Spring Boot backend that manages kitchen orders by decomposing them into station-specific tasks. It exposes REST endpoints for order intake, task tracking, and authentication, persists data via JPA, and uses a command execution model to simulate task preparation.

## 2) Architectural Style
Hexagonal (Ports and Adapters) / Clean Architecture with three main layers:
- Domain: core business rules and models, framework-free.
- Application: use cases and ports (in/out), orchestration only.
- Infrastructure: adapters for REST, persistence, security, serialization, and async execution.

Reference: [docs/core/ARCHITECTURE_CONTEXT.md](ARCHITECTURE_CONTEXT.md)

## 3) Modules and Responsibilities
- Domain (src/main/java/com/foodtech/kitchen/domain)
  - Models: Order, Task, Product, Station, enums and status.
  - Services: Task decomposition, validation, command selection, status calculation.
  - Commands: preparation commands for each station.
- Application (src/main/java/com/foodtech/kitchen/application)
  - Use cases: order processing, task preparation, status retrieval, invoice requests, user auth.
  - Ports: in/out interfaces for persistence, security, serialization, and command execution.
  - Outbox: event persistence model for external integration.
- Infrastructure (src/main/java/com/foodtech/kitchen/infrastructure)
  - REST controllers and DTO mappers.
  - JPA adapters, repositories, and entity mappers.
  - Security configuration and JWT adapters.
  - Async dispatcher (Reactor) and transactional wrappers.

## 4) Main Application Entry Point
- KitchenServiceApplication (src/main/java/com/foodtech/kitchen/KitchenServiceApplication.java)

## 5) Domain Model Overview
Core entities and enums:
- Order: table, products, status; transitions CREATED -> IN_PROGRESS -> COMPLETED -> INVOICED.
- Task: station-specific work unit; transitions PENDING -> IN_PREPARATION -> COMPLETED.
- Product: name and type.
- Station: BAR, HOT_KITCHEN, COLD_KITCHEN.
- ProductType: maps to Station.

Reference: [docs/core/DOMAIN_MAP.md](DOMAIN_MAP.md)

## 6) Use Cases / Application Layer
Key use cases and services:
- ProcessOrderUseCase: persist order, decompose into tasks, save tasks.
- GetTasksByStationUseCase: list tasks by station and optional status.
- StartTaskPreparationUseCase: start task, update order, dispatch command async.
- GetOrderStatusUseCase: compute order status from tasks and persisted status.
- GetCompletedOrdersUseCase: list completed orders (view model).
- RequestOrderInvoiceUseCase: create outbox event and mark order invoiced.
- RegisterUserUseCase / AuthenticateUserUseCase: user registration and login.
- OrderCompletionService: mark order completed when all tasks done.

Ports:
- Input ports in application/ports/in.
- Output ports in application/ports/out.

## 7) Infrastructure Adapters
- REST: controllers, DTOs, mappers, and exception handler.
- Persistence: JPA repositories, adapters, entity mappers, and entities.
- Security: JWT token generation/validation and filter.
- Execution: ReactorAsyncCommandDispatcher and SyncCommandExecutor.
- Serialization: JacksonPayloadSerializer.
- Transactional wrappers: Transactional* ports in infrastructure/transactional.

## 8) REST Controllers
- OrderController: /api/orders (create, status, completed, invoice).
- TaskController: /api/tasks (by station, start preparation).
- AuthController: /api/auth (register, login).
- GlobalExceptionHandler: centralized exception mapping.

## 9) Integration Points
- Database via Spring Data JPA (PostgreSQL in main config, H2 for tests).
- JWT authentication for /api/** endpoints.
- Outbox event persistence for invoice requests (no publisher observed).
- Async command execution via Reactor dispatcher.

## 10) External Systems
- PostgreSQL database (configured in application.yaml).
- Invoice processing system is implied by outbox event type, but no publisher is implemented.

## 11) Test Coverage Strategy
- JUnit 5 with Spring Boot test utilities.
- Integration tests for REST and security.
- Jacoco configured with 0.75 minimum coverage threshold.
- Test configuration uses application-test.yaml with H2.

## 12) Build System
- Gradle 8.5, Java 17 toolchain.
- Spring Boot 3.2.1 and dependency management plugin.
- Jacoco reports and coverage verification in build.gradle.

## 13) Deployment Model
- Docker multi-stage build produces a runnable jar.
- Runtime uses Eclipse Temurin JRE 17 on port 8080.

## 14) Known Risks
- Duplicate bean risk: ReactorAsyncCommandDispatcher is a @Component and also constructed in ApplicationConfig.
- Outbox is persist-only; no publishing worker is present.
- Security config is permissive and intended for development.

## 15) Technical Debt Candidates
- application/exepcions package typo (spelling) could propagate confusion.
- Async dispatcher is fire-and-forget with minimal error handling.
- No outbox publisher; integration delivery is incomplete.
