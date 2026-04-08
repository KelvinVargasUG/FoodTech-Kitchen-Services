# Test Plan: DevOps + Testing Pipeline (JWT Feature)

Date: 2026-03-06
Scope: Workshop Week 3 (Mid Level) testing strategy for the JWT authentication feature and pipeline readiness.

## 1) Testing Strategy Overview
This plan defines a CI-friendly testing strategy for the existing Spring Boot service. It formalizes test classification, tagging, and execution order to support an improved DevOps pipeline. The strategy is grounded in the JWT authentication feature implemented in the previous workshop and aligns with the repository governance in .spec and .ai.

Primary goals:
- Preserve fast feedback with unit tests.
- Separate component and integration tests for clarity and parallel execution.
- Add a blackbox test to validate external behavior of the JWT feature.
- Provide deterministic, reproducible test runs for CI.

## 2) Testing Pyramid for This Project
- Unit tests (base): fast, isolated, no Spring context.
- Component tests (middle): narrow Spring slices or focused wiring tests; no external systems.
- Integration tests (upper-middle): full Spring context, H2 persistence, REST endpoints.
- Blackbox tests (top): API-level tests against a running service, no internal knowledge.

Target distribution (guideline, not strict):
- 70% unit
- 20% component
- 9% integration
- 1% blackbox

## 3) Test Classification Rules

Unit tests
- No Spring context or container.
- Mock ports and infrastructure.
- Focus on domain rules and use case orchestration.
- Example scope: JWT token generation rules and validation logic in isolation.

Component tests
- Limited Spring Boot context (slices) or focused adapter tests.
- Validate infrastructure adapters or REST controllers in isolation with mocks for ports.
- No external services; H2 allowed only if the component under test requires persistence wiring.

Integration tests
- Full Spring Boot context with H2 test profile.
- Validate end-to-end application flows within the process (REST + persistence + security).
- Examples: register/login flows and protected endpoint access.

Blackbox tests
- Run against the service as a running process (local or CI), using only HTTP APIs.
- Do not access internal classes or database directly.
- Validate externally visible behavior and contract.

## 4) JUnit Tagging Strategy
Tag tests for CI separation and reporting:
- @Tag("unit") for unit tests
- @Tag("component") for component tests
- @Tag("integration") for integration tests
- @Tag("blackbox") for blackbox tests

Execution rules:
- Default CI: unit + component on every PR.
- Integration tests on PRs to develop/main and nightly.
- Blackbox tests on release candidates or pre-merge to main.

## 5) Blackbox Test Candidate (JWT Feature)
Candidate: JWT login and access flow via public endpoints.

Scenario:
1) POST /api/auth/register with valid payload -> 201.
2) POST /api/auth/login with valid credentials -> 200 + token.
3) GET /api/orders or protected endpoint with Authorization: Bearer <token> -> 200.

Rationale:
- Validates the JWT feature without internal knowledge.
- Confirms security filter, token issuance, and endpoint protection.

## 6) Test Data Strategy
- Use H2 for integration tests with the test profile.
- Each test should create its own data, avoiding shared mutable state.
- Use deterministic inputs; avoid time-based or random assertions unless fixed clocks are injected.
- For blackbox tests, seed data via API calls only.

## 7) CI Test Execution Strategy
Planned execution order:
1) unit tests (fast feedback)
2) component tests (adapter and wiring validation)
3) integration tests (full context)
4) blackbox tests (service-level, optional in PRs)

Artifacts:
- JUnit XML reports for each test level.
- JaCoCo XML + HTML coverage for unit/component/integration.
- Test logs per job to support evaluation evidence.

## 8) Application of the Seven Testing Principles
1) Testing shows presence of defects: focus on critical JWT flows and failure modes.
2) Exhaustive testing is impossible: prioritize JWT auth and protected endpoint access.
3) Early testing saves time: unit and component tests run on every PR.
4) Defect clustering: security and auth logic is higher risk; higher test density.
5) Pesticide paradox: rotate and expand scenarios (invalid tokens, expired tokens).
6) Testing is context dependent: align with Spring Boot service and JWT requirements.
7) Absence of errors is a fallacy: require behavioral coverage beyond green builds.

## References
- .spec/specs/devops-testing-pipeline/spec.md
- docs/workshops/week2/TESTING_STRATEGY.md
- docs/core/GIT_STRATEGY.md
- .spec/constitution.md
