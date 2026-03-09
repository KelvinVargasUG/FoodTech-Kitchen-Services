# DevOps Implementation Plan

Date: 2026-03-06
Repository: FoodTech Kitchen Service
Baseline: DEVOPS_GAP_REPORT.md

## Purpose
Define the execution strategy to implement the DevOps + Testing pipeline improvements identified in the gap report. This is a plan only; no pipeline or Dockerfile changes are included.

## Proposed Phase Order
1) Plan and policy alignment
2) Test separation and reporting
3) CI job structure and caching
4) Security scanning and SBOM
5) Docker hardening and container validation
6) Release evidence and artifact publishing

## Rationale for the Order
- Establish governance and test boundaries first to prevent rework.
- Separate tests before restructuring CI so jobs can map cleanly to tags.
- Add security and SBOM after the core pipeline is stable.
- Harden Docker after test and CI flows are predictable.
- Release evidence last so artifacts reflect the final pipeline state.

## Phase 1: Plan and Policy Alignment (Now)
Objectives:
- Confirm scope with workshop requirements and Git strategy.
- Define test classification and tagging rules.
- Define required evidence artifacts.

Likely files to update:
- .spec/specs/devops-testing-pipeline/spec.md
- .spec/specs/devops-testing-pipeline/plan.md
- .spec/specs/devops-testing-pipeline/tasks.md
- TEST_PLAN.md (already created)

Deliverables:
- Finalized test plan with tagging and execution strategy.
- Evidence checklist aligned with workshop scoring.

## Phase 2: Test Separation and Reporting (Now)
Objectives:
- Introduce JUnit tagging for unit/component/integration/blackbox.
- Split Gradle tasks or build logic to run tagged suites.
- Ensure test reports and coverage are generated per suite.

Likely files to update:
- build.gradle
- src/test/** (add tags to existing tests)

Deliverables:
- Tagged tests and Gradle tasks for unit and integration.
- JUnit XML and JaCoCo XML artifacts per suite.

## Phase 3: CI Job Structure and Caching (Now)
Objectives:
- Split CI workflow into separate jobs (unit, component, integration).
- Add Gradle dependency caching.
- Publish test reports and coverage artifacts.

Likely files to update:
- .github/workflows/ci.yml

Deliverables:
- Minimal viable CI pipeline with job separation and artifacts.

## Phase 4: Security Scanning and SBOM (Now)
Objectives:
- Add SAST and dependency vulnerability scanning.
- Generate and upload SBOM artifacts.

Likely files to update or add:
- .github/workflows/ci.yml
- .github/workflows/security.yml (optional separation)

Deliverables:
- Security scan job outputs and SBOM artifacts.

## Phase 5: Docker Hardening and Container Validation (Now)
Objectives:
- Harden Dockerfile (non-root, healthcheck, labels, pinned base images).
- Add container build and smoke test step in CI.

Likely files to update or add:
- Dockerfile
- .github/workflows/ci.yml

Deliverables:
- Hardened Docker image build with validation.

## Phase 6: Release Evidence and Artifact Publishing (Later)
Objectives:
- Add release tagging, changelog, and artifact publishing.
- Establish GitFlow-compliant evidence (tags, releases, required checks).

Likely files to update or add:
- .github/workflows/release.yml
- docs/workshops/week3/ (evidence or report if required)

Deliverables:
- Release workflow and documented evidence.

## Minimal Viable CI/CD Pipeline for Workshop Compliance
- Preflight: checkout, JDK, Gradle cache.
- Unit tests: tagged unit suite + JaCoCo + JUnit XML.
- Component tests: tagged component suite + reports.
- Integration tests: tagged integration suite + reports (H2 profile).
- Artifact upload: coverage + test reports.

## Recommended Docker Hardening Scope
- Non-root user in runtime stage.
- HEALTHCHECK for /actuator/health or basic port check.
- OCI labels (source, version, revision).
- Pin base images by digest.
- Configurable JVM options for memory limits.

## Dependencies
- DEVOPS_GAP_REPORT.md
- TEST_PLAN.md
- docs/core/GIT_STRATEGY.md
- .spec/constitution.md

## Risks
- Over-splitting tests without clear tagging leads to inconsistent CI results.
- Security tools may require additional configuration or permissions.
- Docker hardening changes can impact runtime behavior if not tested.

## Now vs Later
Now:
- Test tagging and Gradle task separation.
- CI job separation, caching, and artifact publishing.
- Security scanning and SBOM generation.
- Docker hardening and container validation.

Later:
- Release workflow, versioning, and artifact publishing to registries.
- Environment promotion (dev/staging/prod).
- Performance and load testing.
