# Frameworks and Libraries

This file lists frameworks and libraries detected from build.gradle and the Gradle wrapper.

## Spring Boot
- Version: 3.2.1
- Docs: https://docs.spring.io/spring-boot/docs/3.2.1/reference/html/
- Usage: application framework for REST, security, data access, validation, and configuration.

## Spring Web (MVC)
- Version: managed by Spring Boot 3.2.1
- Docs: https://docs.spring.io/spring-framework/reference/web/webmvc.html
- Usage: REST controllers and HTTP endpoints.

## Spring Security
- Version: managed by Spring Boot 3.2.1
- Docs: https://docs.spring.io/spring-security/reference/
- Usage: stateless API security with JWT filter and endpoint protection.

## Spring Data JPA
- Version: managed by Spring Boot 3.2.1
- Docs: https://spring.io/projects/spring-data-jpa
- Usage: JPA repositories and persistence adapters.

## Spring Validation (Bean Validation)
- Version: managed by Spring Boot 3.2.1
- Docs: https://docs.spring.io/spring-framework/reference/core/validation/beanvalidation.html
- Usage: request validation for REST DTOs.

## Reactor Core
- Version: 3.6.0
- Docs: https://projectreactor.io/docs/core/release/reference/
- Usage: async command dispatch in infrastructure.

## Jackson Databind
- Version: managed by Spring Boot 3.2.1
- Docs: https://github.com/FasterXML/jackson
- Usage: JSON serialization via JacksonPayloadSerializer.

## JJWT (io.jsonwebtoken)
- Version: 0.11.5
- Docs: https://github.com/jwtk/jjwt
- Usage: JWT token generation and validation.

## PostgreSQL JDBC
- Version: managed by Spring Boot 3.2.1
- Docs: https://jdbc.postgresql.org/
- Usage: runtime database driver for the main profile.

## H2 Database
- Version: managed by Spring Boot 3.2.1
- Docs: https://h2database.com/html/main.html
- Usage: in-memory database for tests.

## Gradle
- Version: 8.5 (Gradle wrapper)
- Docs: https://docs.gradle.org/8.5/userguide/userguide.html
- Usage: build tool and test runner.

## JUnit 5
- Version: managed by Spring Boot 3.2.1
- Docs: https://junit.org/junit5/docs/current/user-guide/
- Usage: unit and integration testing.

## JaCoCo
- Version: 0.8.11
- Docs: https://www.jacoco.org/jacoco/
- Usage: test coverage reporting and verification.

## Lombok
- Version: managed by build configuration (not pinned in build.gradle)
- Docs: https://projectlombok.org/
- Usage: code generation to reduce boilerplate.
