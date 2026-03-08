# Derived Requirements for Authentication Feature

## Feature Scope
Derived from [docs/workshops/week2/TESTING_STRATEGY.md](../week2/TESTING_STRATEGY.md). The feature covers JWT authentication with user registration, login by username or email, token issuance and validation (including expiration and malformed tokens), and access control to protected endpoints based on token presence and validity. Role-based authorization, refresh tokens, and other security hardening are explicitly out of scope in the strategy.

## Functional Requirements
REQ-AUTH-001
Users can register with valid email, username, and password.

REQ-AUTH-002
Registration rejects duplicate email addresses.

REQ-AUTH-003
Registration rejects duplicate usernames.

REQ-AUTH-004
Registration rejects invalid email formats.

REQ-AUTH-005
Registration rejects weak passwords (less than 6 characters, missing number, or missing letter).

REQ-AUTH-006
Users can log in using a username with valid credentials.

REQ-AUTH-007
Users can log in using an email with valid credentials.

REQ-AUTH-008
Login rejects invalid credentials.

REQ-AUTH-009
Login rejects inactive users.

REQ-AUTH-010
A JWT token is generated after successful authentication.

REQ-AUTH-011
Token validation detects expiration.

REQ-AUTH-012
Token validation rejects malformed tokens.

REQ-AUTH-013
Protected endpoints deny access when the token is missing.

REQ-AUTH-014
Protected endpoints allow access when the token is valid.

## Acceptance Criteria
AC-AUTH-REG-001
Given a new user with valid email, username, and password
When the user submits registration data
Then the account is created successfully.

AC-AUTH-REG-002
Given a user already exists with the same email
When a new registration is submitted
Then the registration is rejected due to duplicate email.

AC-AUTH-REG-003
Given a user already exists with the same username
When a new registration is submitted
Then the registration is rejected due to duplicate username.

AC-AUTH-REG-004
Given a new user with an invalid email format
When the user submits registration data
Then the registration is rejected for invalid email.

AC-AUTH-REG-005
Given a new user with a weak password (less than 6 characters, missing number, or missing letter)
When the user submits registration data
Then the registration is rejected for weak password.

AC-AUTH-LOGIN-001
Given a registered active user
When the user logs in with a valid username and password
Then authentication succeeds and a JWT token is returned.

AC-AUTH-LOGIN-002
Given a registered active user
When the user logs in with a valid email and password
Then authentication succeeds and a JWT token is returned.

AC-AUTH-LOGIN-003
Given a registered active user
When the user logs in with an invalid password
Then authentication is rejected.

AC-AUTH-LOGIN-004
Given a registered inactive user
When the user attempts to log in with valid credentials
Then authentication is rejected.

AC-AUTH-TOKEN-001
Given a successful authentication
When the system issues a token
Then the JWT is generated and can be validated.

AC-AUTH-TOKEN-002
Given an expired token
When the token is validated
Then validation fails due to expiration.

AC-AUTH-TOKEN-003
Given a malformed token
When the token is validated
Then validation fails due to token format.

AC-AUTH-PROTECT-001
Given a protected endpoint
When a request is made without a token
Then access is denied.

AC-AUTH-PROTECT-002
Given a protected endpoint
When a request is made with a valid token
Then access is allowed.

## Mapping to Test Levels
| Requirement | Strategy Scenario(s) | Acceptance Criteria | Test Level | Technique |
| --- | --- | --- | --- | --- |
| REQ-AUTH-001 | Register success | AC-AUTH-REG-001 | Integration Test | Black Box |
| REQ-AUTH-002 | Register duplicate email | AC-AUTH-REG-002 | Integration Test | Black Box |
| REQ-AUTH-003 | Register duplicate username | AC-AUTH-REG-003 | Integration Test | Black Box |
| REQ-AUTH-004 | Invalid email format | AC-AUTH-REG-004 | Component Test | White Box |
| REQ-AUTH-005 | Weak password (menos de 6, sin numero, sin letra) | AC-AUTH-REG-005 | Component Test | White Box |
| REQ-AUTH-006 | Login success (username) | AC-AUTH-LOGIN-001 | Integration Test | Black Box |
| REQ-AUTH-007 | Login success (email) | AC-AUTH-LOGIN-002 | Integration Test | Black Box |
| REQ-AUTH-008 | Login wrong password | AC-AUTH-LOGIN-003 | Component Test | White Box |
| REQ-AUTH-009 | Login inactive user | AC-AUTH-LOGIN-004 | Component Test | White Box |
| REQ-AUTH-010 | Token generation | AC-AUTH-TOKEN-001 | Component Test | White Box |
| REQ-AUTH-011 | Token expiration validation | AC-AUTH-TOKEN-002 | Component Test | White Box |
| REQ-AUTH-012 | Token malformed | AC-AUTH-TOKEN-003 | Component Test | White Box |
| REQ-AUTH-013 | Protected endpoint without token | AC-AUTH-PROTECT-001 | Integration Test | Black Box |
| REQ-AUTH-014 | Protected endpoint with valid token | AC-AUTH-PROTECT-002 | Integration Test | Black Box |
