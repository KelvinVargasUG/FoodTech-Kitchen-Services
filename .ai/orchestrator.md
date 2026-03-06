# AI Orchestrator

Purpose: central coordination for AI agents so responsibilities are clear and repetitive guidance is avoided.

## How Agents Discover Context
- Always start with [.ai/bootstrap.md](./bootstrap.md).
- Use [.spec/index.md](../.spec/index.md) for the repository knowledge map.
- Use [.spec/constitution.md](../.spec/constitution.md) for governance rules.
- Use [docs/core/DOCS_INDEX.md](../docs/core/DOCS_INDEX.md) for canonical documentation roles.

## Default Execution Sequence
1) Navigator
2) Architect (architecture validation)
3) Spec Author
4) Planner
5) Architect (design validation)
6) Implementer
7) QA Engineer
8) Librarian
9) Git Steward

## Handoff Rules
- Each agent must output a concise artifact summary before handing off.
- If inputs are missing, the agent stops and returns control with a request for the missing artifact.
- If an architectural exception is needed, the agent stops and routes to governance update in [docs/core/ARCHITECTURE_CONTEXT.md](../docs/core/ARCHITECTURE_CONTEXT.md).

## Stop Conditions
- Ambiguous scope or conflicting requirements.
- Missing or unapproved spec/plan when implementation is requested.
- Architecture rules are violated or unclear.

## Agent Responsibilities and Artifacts

### Navigator
- Reads: [.ai/bootstrap.md](./bootstrap.md), [.spec/index.md](../.spec/index.md), [docs/core/SYSTEM_MAP.md](../docs/core/SYSTEM_MAP.md)
- Produces: repo and system overview, impacted areas, relevant files list.

### Architect
- Reads: [.ai/bootstrap.md](./bootstrap.md), [.spec/constitution.md](../.spec/constitution.md), [.ai/knowledge/architecture.md](./knowledge/architecture.md), [.ai/knowledge/principles.md](./knowledge/principles.md), [docs/core/ARCHITECTURE_CONTEXT.md](../docs/core/ARCHITECTURE_CONTEXT.md), [docs/core/SYSTEM_MAP.md](../docs/core/SYSTEM_MAP.md)
- Produces: architecture compliance report with violations, risks, and suggestions.

### Spec Author
- Reads: [.spec/constitution.md](../.spec/constitution.md), [.spec/index.md](../.spec/index.md)
- Produces: spec.md in the active spec folder.

### Planner
- Reads: spec.md, [.spec/constitution.md](../.spec/constitution.md)
- Produces: plan.md and tasks.md.

### Implementer
- Reads: spec.md, plan.md, tasks.md, [docs/core/ARCHITECTURE_CONTEXT.md](../docs/core/ARCHITECTURE_CONTEXT.md)
- Produces: scoped code or documentation changes with notes.

### QA Engineer
- Reads: plan.md, tasks.md, repo test configuration.
- Produces: validation summary, test results, and risks.

### Librarian
- Reads: [docs/core/DOCS_INDEX.md](../docs/core/DOCS_INDEX.md), [.spec/index.md](../.spec/index.md)
- Produces: documentation updates and link consistency checks.

### Git Steward
- Reads: [docs/core/GIT_STRATEGY.md](../docs/core/GIT_STRATEGY.md), [.spec/constitution.md](../.spec/constitution.md), [.ai/orchestrator.md](./orchestrator.md)
- Produces: commit message, pull request summary, and release notes if required.

## Duplication Control
- Agents must not embed long context blocks in their own files.
- Use this orchestrator plus [.ai/bootstrap.md](./bootstrap.md) and [.spec/index.md](../.spec/index.md) instead of repeating reading lists.
