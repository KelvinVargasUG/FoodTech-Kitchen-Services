# Authentication Feature - Test Plan

## 1. Introduction
This test plan defines how the JWT authentication feature is verified for the Week 3 workshop. It operationalizes the scenarios defined in [docs/workshops/week2/TESTING_STRATEGY.md](../week2/TESTING_STRATEGY.md) and the derived requirements in [docs/workshops/week3/REQUIREMENTS_DERIVED_FROM_STRATEGY.md](REQUIREMENTS_DERIVED_FROM_STRATEGY.md). The plan focuses on repeatable, CI-friendly verification of registration, login, token handling, and protected endpoint access.

## 2. Scope of Testing
### In Scope
- Registration with email, username, and password validation.
- Login using username or email.
- JWT token generation after successful authentication.
- Token validation, including expiration and malformed token handling.
- Access control for protected endpoints based on token presence and validity.

### Out of Scope
- Role-based authorization and permissions.
- Refresh tokens, rotation, or remember-me behavior.
- Token revocation or blacklist and server-side invalidation.
- Email verification, password recovery, or MFA/2FA.
- External identity providers (OAuth2, social login).
- Rate limiting, brute-force protection, or CAPTCHA.
- Security audit trails, telemetry, or compliance controls.
- Transport-level hardening outside the application code.

## 3. Testing Strategy (Multilevel)
### Component Tests
Component tests validate domain and application logic in isolation without Spring context. These tests focus on validation rules, user state handling, duplicate detection responses, and token generation and validation behavior. They run quickly, rely on mocks or fakes for ports, and provide white-box coverage.

### Integration Tests
Integration tests validate end-to-end behavior in-process with SpringBootTest and H2 in-memory persistence. These tests confirm request handling, persistence constraints, security filter behavior, and protected endpoint access using actual HTTP flows. They provide black-box verification of the externally observable behavior.

## 4. Testing Techniques
### White Box Testing
Applied to component tests for validation rules, duplicate checks, user status rules, and token parsing logic. White-box tests inspect internal decisions and error paths without external infrastructure.

### Black Box Testing
Applied to integration tests that exercise HTTP endpoints and security filters. These tests verify observable inputs and outputs without relying on internal implementation details.

## 5. Application of the 7 Testing Principles
- Testing shows presence of defects: component tests validate negative paths (invalid email, weak password, wrong password, inactive user) to reveal defect presence, not absence.
- Exhaustive testing is impossible: the plan targets a defined scenario set from the strategy rather than all possible input permutations.
- Early testing (shift left): component tests run early in CI for fast feedback before integration and container steps.
- Defect clustering: duplicate handling and credential validation are tested at both component and integration levels to focus on high-risk logic.
- Pesticide paradox: regression risk is reduced by running the same scenarios across multiple levels and by CI enforcement in [CI workflow](.github/workflows/ci.yml).
- Testing depends on context: the plan uses H2 for integration tests because the feature is validated as a self-contained service without external dependencies.
- Absence-of-errors fallacy: passing tests only show the authentication feature works for scoped requirements and does not imply full security hardening.

## 6. Traceability Matrix
| Requirement | Acceptance Criteria | Test Level | Test Type | Verification Method |
| --- | --- | --- | --- | --- |
| REQ-AUTH-001 | AC-AUTH-REG-001 | Component | White Box | Component test of registration use case |
| REQ-AUTH-001 | AC-AUTH-REG-001 | Integration | Black Box | SpringBootTest registration endpoint flow |
| REQ-AUTH-002 | AC-AUTH-REG-002 | Component | White Box | Duplicate email validation in use case |
| REQ-AUTH-002 | AC-AUTH-REG-002 | Integration | Black Box | Registration endpoint with existing email |
| REQ-AUTH-003 | AC-AUTH-REG-003 | Component | White Box | Duplicate username validation in use case |
| REQ-AUTH-003 | AC-AUTH-REG-003 | Integration | Black Box | Registration endpoint with existing username |
| REQ-AUTH-004 | AC-AUTH-REG-004 | Component | White Box | Email format validation rule test |
| REQ-AUTH-005 | AC-AUTH-REG-005 | Component | White Box | Password robustness rule test |
| REQ-AUTH-006 | AC-AUTH-LOGIN-001 | Component | White Box | Login use case with username |
| REQ-AUTH-006 | AC-AUTH-LOGIN-001 | Integration | Black Box | Login endpoint with username |
| REQ-AUTH-007 | AC-AUTH-LOGIN-002 | Component | White Box | Login use case with email |
| REQ-AUTH-007 | AC-AUTH-LOGIN-002 | Integration | Black Box | Login endpoint with email |
| REQ-AUTH-008 | AC-AUTH-LOGIN-003 | Component | White Box | Invalid password response in use case |
| REQ-AUTH-009 | AC-AUTH-LOGIN-004 | Component | White Box | Inactive user rejection rule test |
| REQ-AUTH-010 | AC-AUTH-TOKEN-001 | Component | White Box | Token generation and parse test |
| REQ-AUTH-011 | AC-AUTH-TOKEN-002 | Component | White Box | Token expiration validation test |
| REQ-AUTH-012 | AC-AUTH-TOKEN-003 | Component | White Box | Malformed token validation test |
| REQ-AUTH-013 | AC-AUTH-PROTECT-001 | Integration | Black Box | Protected endpoint without token |
| REQ-AUTH-014 | AC-AUTH-PROTECT-002 | Integration | Black Box | Protected endpoint with valid token |

## 7. Test Cases
### TC-AUTH-REG-001
- Requirement ID: REQ-AUTH-001
- Acceptance Criteria: AC-AUTH-REG-001
- Test Level: Integration
- Test Type: Black Box
- Preconditions: H2 test profile active; no existing user with the same email or username.
- Test Steps: Submit registration request with valid email, username, and password.
- Expected Result: Account is created successfully.

### TC-AUTH-REG-002
- Requirement ID: REQ-AUTH-002
- Acceptance Criteria: AC-AUTH-REG-002
- Test Level: Integration
- Test Type: Black Box
- Preconditions: Existing user with the same email.
- Test Steps: Submit registration request with duplicate email and a new username.
- Expected Result: Registration is rejected due to duplicate email.

### TC-AUTH-REG-003
- Requirement ID: REQ-AUTH-003
- Acceptance Criteria: AC-AUTH-REG-003
- Test Level: Integration
- Test Type: Black Box
- Preconditions: Existing user with the same username.
- Test Steps: Submit registration request with duplicate username and a new email.
- Expected Result: Registration is rejected due to duplicate username.

### TC-AUTH-REG-004
- Requirement ID: REQ-AUTH-004
- Acceptance Criteria: AC-AUTH-REG-004
- Test Level: Component
- Test Type: White Box
- Preconditions: None.
- Test Steps: Invoke registration validation with an invalid email format.
- Expected Result: Registration is rejected for invalid email.

### TC-AUTH-REG-005
- Requirement ID: REQ-AUTH-005
- Acceptance Criteria: AC-AUTH-REG-005
- Test Level: Component
- Test Type: White Box
- Preconditions: None.
- Test Steps: Invoke registration validation with a weak password.
- Expected Result: Registration is rejected for weak password.

### TC-AUTH-LOGIN-001
- Requirement ID: REQ-AUTH-006
- Acceptance Criteria: AC-AUTH-LOGIN-001
- Test Level: Integration
- Test Type: Black Box
- Preconditions: Registered active user with username and password.
- Test Steps: Submit login request using username and valid password.
- Expected Result: Authentication succeeds and JWT token is returned.

### TC-AUTH-LOGIN-002
- Requirement ID: REQ-AUTH-007
- Acceptance Criteria: AC-AUTH-LOGIN-002
- Test Level: Integration
- Test Type: Black Box
- Preconditions: Registered active user with email and password.
- Test Steps: Submit login request using email and valid password.
- Expected Result: Authentication succeeds and JWT token is returned.

### TC-AUTH-LOGIN-003
- Requirement ID: REQ-AUTH-008
- Acceptance Criteria: AC-AUTH-LOGIN-003
- Test Level: Component
- Test Type: White Box
- Preconditions: Registered active user.
- Test Steps: Attempt login with invalid password.
- Expected Result: Authentication is rejected.

### TC-AUTH-LOGIN-004
- Requirement ID: REQ-AUTH-009
- Acceptance Criteria: AC-AUTH-LOGIN-004
- Test Level: Component
- Test Type: White Box
- Preconditions: Registered inactive user.
- Test Steps: Attempt login with valid credentials.
- Expected Result: Authentication is rejected.

### TC-AUTH-TOKEN-001
- Requirement ID: REQ-AUTH-010
- Acceptance Criteria: AC-AUTH-TOKEN-001
- Test Level: Component
- Test Type: White Box
- Preconditions: Successful authentication result available.
- Test Steps: Generate JWT and validate it with the token validator.
- Expected Result: Token is generated and validated successfully.

### TC-AUTH-TOKEN-002
- Requirement ID: REQ-AUTH-011
- Acceptance Criteria: AC-AUTH-TOKEN-002
- Test Level: Component
- Test Type: White Box
- Preconditions: Expired JWT token available.
- Test Steps: Validate the expired token.
- Expected Result: Validation fails due to expiration.

### TC-AUTH-TOKEN-003
- Requirement ID: REQ-AUTH-012
- Acceptance Criteria: AC-AUTH-TOKEN-003
- Test Level: Component
- Test Type: White Box
- Preconditions: Malformed JWT token available.
- Test Steps: Validate the malformed token.
- Expected Result: Validation fails due to token format.

### TC-AUTH-PROTECT-001
- Requirement ID: REQ-AUTH-013
- Acceptance Criteria: AC-AUTH-PROTECT-001
- Test Level: Integration
- Test Type: Black Box
- Preconditions: Protected endpoint is deployed in the test context.
- Test Steps: Call protected endpoint without a token.
- Expected Result: Access is denied.

### TC-AUTH-PROTECT-002
- Requirement ID: REQ-AUTH-014
- Acceptance Criteria: AC-AUTH-PROTECT-002
- Test Level: Integration
- Test Type: Black Box
- Preconditions: Valid JWT token available from login.
- Test Steps: Call protected endpoint with the valid token.
- Expected Result: Access is allowed.

## 8. CI/CD Integration
The CI pipeline in [CI workflow](.github/workflows/ci.yml) enforces the testing strategy with separate jobs for unit tests, component tests, and integration tests. It also runs SAST with CodeQL, generates an SBOM, builds a Docker image, and performs a Trivy vulnerability scan and smoke test. These steps ensure the authentication feature remains stable, secure, and verifiable on every push and pull request.

## 9. Evidence of Execution
Evidence is collected from CI artifacts and job status:
- Green pipeline status showing all test jobs and the Docker build completed.
- Unit, component, and integration test reports uploaded as artifacts (unit-test-results, component-test-results, integration-test-results, test-reports).
- SBOM artifact from the CycloneDX report (sbom).
- Trivy vulnerability scan report artifact (trivy-security-report).
- Docker build and smoke test job logs verifying the container starts and responds.
