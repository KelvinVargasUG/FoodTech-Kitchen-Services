# Local Development Runbook

## Purpose
This runbook explains how to build, test, and run the service locally based on repository evidence. If a step is not supported by the repo, it is marked as missing.

## Prerequisites
- Java 17 (required by Gradle toolchain).
- A local PostgreSQL instance for the default profile (see application.yaml).

## Build and Test
- Run all tests: `./gradlew test`
- Run full build: `./gradlew build`

## Run Locally
- Start the app with Spring Boot: `./gradlew bootRun`

## Configuration Notes
- Default datasource is PostgreSQL (see [src/main/resources/application.yaml](../../src/main/resources/application.yaml)).
- Test profile uses H2 (see [src/test/resources/application-test.yaml](../../src/test/resources/application-test.yaml) and the test task).
- JWT settings live under the `jwt` section in [src/main/resources/application.yaml](../../src/main/resources/application.yaml).

## Database
- Default connection settings are in [src/main/resources/application.yaml](../../src/main/resources/application.yaml).
- If the database is not available, the application will fail to start.
- Missing in repo: a local Docker Compose file or setup script for PostgreSQL.

## Troubleshooting
- If Gradle cannot find Java, set JAVA_HOME to a JDK 17 installation.
- If tests fail due to database, ensure the test profile is used (Gradle test task uses it by default).

## Missing or Inferred Details
- No documented local Docker Compose for Postgres.
- No documented runtime profile for H2 outside tests.
