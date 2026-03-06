# Engineering Principles

## SOLID
- Single Responsibility: each class has one clear reason to change.
- Open/Closed: extend behavior via new types, avoid modifying stable logic.
- Liskov Substitution: interface implementations must be interchangeable.
- Interface Segregation: keep interfaces small and focused.
- Dependency Inversion: depend on abstractions, not concrete frameworks.

## Clean Code
- Prefer small functions with explicit names.
- Keep control flow simple and avoid deep nesting.
- Avoid hidden side effects and implicit dependencies.

## Hexagonal Architecture
- Dependencies flow inward: infrastructure -> application -> domain.
- Use ports for all external interactions.
- Keep framework concerns in infrastructure only.

## Testability
- Prefer constructor injection and pure Java use cases.
- Unit tests should mock ports and avoid Spring contexts.
- Integration tests validate wiring, persistence, and security.

## Documentation Standards
- Canonical engineering docs are in English.
- Workshop evidence may remain in Spanish.
- Prefer links to canonical sources over duplicated text.
- Keep documentation aligned with docs/core/DOCS_INDEX.md.
