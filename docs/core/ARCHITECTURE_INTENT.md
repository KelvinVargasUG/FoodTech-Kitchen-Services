# ARCHITECTURE_INTENT

This document captures architectural intent, rationale, and future direction. Current-state facts live in [docs/core/ARCHITECTURE_CONTEXT.md](ARCHITECTURE_CONTEXT.md) and [docs/core/SYSTEM_MAP.md](SYSTEM_MAP.md).

## Clean Architecture Intent
- domain: core business rules, entities, value objects, and domain services.
- application: use cases and ports (in/out).
- infrastructure: adapters (web, persistence, messaging) and framework wiring.

## Import Rules (Intent)
- domain must not import Spring, Jackson, Reactor, or JPA.
- application must not depend on infrastructure.
- infrastructure may depend on all layers.

## Required Patterns (Intent)
- Repository pattern via output ports in application/ports/out.
- Strategy pattern for variable behavior instead of large switch-case blocks.

## Architectural Decision Placeholder
// ARCHITECTURE_DECISION:
This interface exists to decouple persistence and enable mocking in unit tests.

## Why We Refactor (Intent)
Refactors must reinforce layer boundaries, reduce framework coupling in core logic, and improve unit testability without changing production behavior.
