# Specification Constitution

This document defines the governing rules for spec-driven development in this repository. It is the single source of truth for project governance and AI-first workflow rules.

## Architecture Principles
- The authoritative architecture rules live in [docs/core/ARCHITECTURE_CONTEXT.md](../docs/core/ARCHITECTURE_CONTEXT.md).
- Domain and application layers must remain framework-free.
- All new work must respect current hexagonal boundaries and port placement rules.

## Development Workflow
- Work proceeds in sequence: spec -> plan -> tasks -> implementation.
- Each feature or change must start with a spec folder under .spec/specs/.
- Implementation must reference an approved spec and plan.
- Changes must be small, reviewable, and reversible.

## Testing Policy
- Tests are required for behavior changes in domain or application layers.
- Integration tests are required for REST, persistence, or security changes.
- Run ./gradlew test and ensure Jacoco coverage passes the configured threshold.

## AI-First Workflow
- Verify before modification. Do not assume files, classes, or dependencies.
- Design before implementation. Start with spec and plan.
- If an architectural exception is required, document it in [docs/core/ARCHITECTURE_CONTEXT.md](../docs/core/ARCHITECTURE_CONTEXT.md) before proceeding.
- Use [.ai/bootstrap.md](../.ai/bootstrap.md) and [.ai/orchestrator.md](../.ai/orchestrator.md) for agent startup and coordination.
- Use [.spec/index.md](./index.md) for the knowledge map and navigation.

## Documentation Structure
- [docs/core/](../docs/core/) is the canonical governance and architecture source.
- [docs/workshops/](../docs/workshops/) is historical workshop evidence only.
- [.spec/](./) holds specifications, plans, and tasks for changes.
- [.ai/](../.ai/) holds AI agent operating procedures and role definitions.

## Documentation Language Policy
- Canonical engineering documentation must be in English.
- Workshop deliverables may remain in Spanish when required for course or reporting needs.

## Git Strategy
- Follow [docs/core/GIT_STRATEGY.md](../docs/core/GIT_STRATEGY.md) for branch and commit conventions.
- Keep documentation changes separate from runtime code changes.

## Code Review Policy
- All changes to develop must be reviewed before merge.
- Reviews must confirm compliance with docs/core/ARCHITECTURE_CONTEXT.md and this constitution.
