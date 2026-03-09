# DevOps + Testing Pipeline Gap Report

Date: 2026-03-06
Repository: FoodTech Kitchen Service

## 1) Current CI Pipeline Structure

Source: .github/workflows/ci.yml

- Triggered on push to main, develop, and feature/*, and on PRs to main and develop.
- Single job: build-and-test on ubuntu-latest.
- Steps:
  - Checkout code.
  - Setup JDK 17 (Temurin).
  - chmod +x gradlew.
  - Run ./gradlew clean build (includes tests, JaCoCo report, and coverage verification).
  - Upload JaCoCo HTML report as artifact.

Observed build tooling:
- Gradle with Spring Boot 3.2.1, Java 17 toolchain, JaCoCo coverage, JUnit 5.

## 2) Missing CI/CD Best Practices

- No separate lint/static analysis stage (e.g., Checkstyle/SpotBugs/PMD/Spotless).
- No SAST or dependency vulnerability scanning (e.g., CodeQL, dependency scanning, Snyk).
- No test result publishing (JUnit XML) or code coverage publishing (XML) to CI summary.
- No cache for Gradle dependencies (slower builds).
- No build matrix (OS/JDK versions) or build reproducibility checks.
- No container build/test in CI (Dockerfile not validated in pipeline).
- No SBOM generation or artifact provenance (SLSA, build attestations).
- No release workflow (versioning, changelog, tagging, artifacts, or deployment jobs).
- No environment segregation or promotion flow (dev/staging/prod).
- No job-level concurrency or cancel-in-progress for PRs.
- No pipeline policy enforcement (branch protection, required checks) indicated in repo.

## 3) Required Pipeline Jobs (Recommended)

Minimum professional CI/CD for this service:

- preflight
  - checkout, toolchain setup, dependency cache.
- unit-tests
  - run fast unit tests (mock-based) with coverage.
- integration-tests
  - run Spring Boot tests requiring DB (H2/containers), separate from unit tests.
- api-contract-tests
  - validate API contract / schema (FoodTech_v2.json) if applicable.
- static-analysis
  - Checkstyle/SpotBugs/PMD/Spotless; fail on violations.
- security-scan
  - SAST (CodeQL) and dependency scan.
- build-artifact
  - assemble jar (without tests), archive outputs.
- docker-build
  - build image, run a basic container smoke test.
- publish-artifacts (on tags)
  - publish jar, docker image, SBOM, coverage, and test reports.
- deploy (optional, environment-gated)
  - deploy to dev/staging/prod with approvals.

## 4) Required Docker Improvements

Current Dockerfile:
- Multi-stage build with Gradle, but tests are skipped in container build (gradlew build -x test).
- No non-root user, no healthcheck, no metadata labels.
- No build-time args for versioning.
- No explicit JVM options for memory/CPU limits.
- No SBOM or security scanning for image.

Recommended improvements:
- Run tests outside the image build in CI; optionally add a test stage for image build verification.
- Use a non-root user in runtime stage.
- Add HEALTHCHECK and configurable port.
- Add OCI labels (source, version, revision).
- Make JAR name deterministic or use explicit build arg.
- Pin base images by digest to reduce supply-chain risk.
- Add minimal runtime packages only (already alpine, but verify base image security).
- Provide a docker-compose.yml for local Postgres and app.

## 5) Test Strategy Gaps

Current state:
- Unit tests exist (application/usecases, domain/commands, infrastructure/security).
- Integration tests exist (SpringBootTest + MockMvc, data persistence, REST endpoints).
- H2 test profile is available via application-test.yaml.

Gaps:
- No explicit test classification or tagging (e.g., JUnit tags) to separate unit vs integration in CI.
- No contract tests against external dependencies or schema validation.
- No performance, load, or resiliency tests.
- No mutation testing to validate test strength.
- No test coverage reporting in CI summary or PR annotations.
- No quarantine or flaky test management.
- No test data management or deterministic test environment setup for local dev/CI.

## 6) Required Artifacts for a Professional DevOps Pipeline

- Build artifacts:
  - Application JAR.
  - Docker image with tags (version, commit SHA, latest).
  - SBOM (CycloneDX or SPDX).

- Quality artifacts:
  - JUnit XML test reports.
  - JaCoCo XML + HTML coverage reports.
  - Static analysis reports.
  - Security scan reports (SAST + dependency).

- Release artifacts:
  - Changelog and release notes.
  - Signed tags / provenance attestations.
  - Deployment manifests or Helm chart (if applicable).

## Summary

The repository has a functional but minimal CI setup focused on build/test and JaCoCo HTML upload. It lacks layered test separation, security scanning, static analysis, container build validation, and release/deployment workflows expected in modern CI/CD. Dockerization is partial and should be hardened for runtime and supply-chain best practices.