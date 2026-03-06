# Architecture Rules Summary

Derived from docs/core/ARCHITECTURE_CONTEXT.md, .spec/constitution.md, and docs/core/SYSTEM_MAP.md.

## Architecture Style
- Hexagonal (Ports and Adapters) / Clean Architecture.
- Three layers: domain, application, infrastructure.

## Layer Responsibilities
- Domain: core business models, services, commands, and rules. No framework dependencies.
- Application: use cases and ports; orchestration only. No framework dependencies.
- Infrastructure: adapters for REST, persistence, security, serialization, async execution, and wiring.

## Allowed Dependencies
- Domain: Java standard library and domain packages only.
- Application: domain + application ports + Java standard library.
- Infrastructure: domain + application + framework libraries (Spring, JPA, Reactor, Jackson).

## Port and Adapter Rules
- Input ports live in src/main/java/com/foodtech/kitchen/application/ports/in.
- Output ports live in src/main/java/com/foodtech/kitchen/application/ports/out unless the domain requires a direct port.
- Controllers depend on input ports only, never on repositories.
- Adapters implement output ports and live under infrastructure/*.

## Transaction Boundaries
- Transactions are enforced in infrastructure via transactional wrapper ports.
- Application use cases remain framework-free and are wired in ApplicationConfig.

## Async Execution Rules
- Async dispatch is implemented in infrastructure (ReactorAsyncCommandDispatcher).
- Domain and application layers must not import Reactor types.
- Async failures must not leak framework details into application or domain.

## Wiring Rules
- Avoid duplicate bean registration (either @Bean or @Component, not both).
- Keep framework annotations out of application and domain layers.
