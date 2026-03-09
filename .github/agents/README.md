# Copilot Agents

This folder provides a Copilot Agent Mode integration layer for this repository. The canonical AI framework remains in .ai/.

## Agents

- repo-auditor-agent
  - Audits repo architecture, CI/CD, Docker, and testing strategy.
  - Outputs: DEVOPS_GAP_REPORT.md, TEST_STRATEGY_REPORT.md, ARCHITECTURE_AUDIT.md

- devops-agent
  - Designs production-grade CI/CD pipelines and Docker infrastructure.
  - Outputs: implementation plans, workflow YAMLs, and security/artifact checklists

- qa-strategy-agent
  - Defines testing strategy aligned with CI/CD and Seven Testing Principles.
  - Outputs: TEST_PLAN.md, TEST_CASES.md, TEST_STRATEGY.md

## How To Invoke In VS Code Copilot Agent Mode

Use these mentions in Copilot Agent Mode chat:

- @repo-auditor-agent
- @devops-agent
- @qa-strategy-agent

## Example Prompts

repo-auditor-agent:
- "@repo-auditor-agent audit the current CI pipeline and Dockerfile, then update DEVOPS_GAP_REPORT.md"

devops-agent:
- "@devops-agent propose a multi-stage CI pipeline with SBOM and container scans, no changes yet"

qa-strategy-agent:
- "@qa-strategy-agent create a test strategy that separates unit and integration tests with JUnit tags"

## Integration With .ai Framework

All agents must consult:
- .ai/knowledge for repository rules and domain context
- .ai/skills for preferred practices and execution patterns
