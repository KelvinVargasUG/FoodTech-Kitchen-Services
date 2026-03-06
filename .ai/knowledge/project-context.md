# Project Context

## Purpose
FoodTech Kitchen Service is a Spring Boot backend that manages kitchen orders by decomposing them into station-specific tasks and tracking preparation status.

## Domain Overview
- Orders contain products and move through CREATED, IN_PROGRESS, COMPLETED, and INVOICED states.
- Tasks represent station-specific preparation work and move through PENDING, IN_PREPARATION, and COMPLETED states.
- Stations: BAR, HOT_KITCHEN, COLD_KITCHEN.

## Main Components
- REST controllers for orders, tasks, and authentication.
- Application use cases and ports for orchestration.
- Domain services and commands for task creation and preparation simulation.
- Infrastructure adapters for JPA persistence, JWT security, Reactor async execution, and Jackson serialization.

## External Integrations
- PostgreSQL for runtime persistence.
- H2 for test persistence.
- JWT authentication using JJWT.
- Postman collection in FoodTech_v2.json for API testing.
- Outbox event persistence for invoice requests (publisher not implemented).

## Important Constraints
- Do not modify runtime source code unless explicitly required.
- Follow governance in .spec/constitution.md and architecture rules in docs/core/ARCHITECTURE_CONTEXT.md.
- Prefer documentation links over duplication.
