# Architecture Documentation

This folder contains the target architecture package for the BYOD Device Management System.

## Architecture Baseline

The current target architecture is a 3-tier client-server system:

```text
JavaFX Desktop Frontend -> Spring Boot REST API -> Railway PostgreSQL
```

The JavaFX frontend communicates only with the backend API over HTTPS/JSON. The Spring Boot backend owns validation, role checks, transactions, JDBC data access, and audit writing. PostgreSQL stores the canonical schema, views, triggers, functions, and indexes.

## Read First

1. `01-architecture-overview.md`
2. `04-application-architecture.md`
3. `05-module-architecture.md`
4. `06-data-architecture.md`
5. `08-deployment-architecture.md`
6. `13-architecture-gap-analysis.md`

## Architecture Documents

| File | Purpose |
| --- | --- |
| `01-architecture-overview.md` | Overall 3-tier architecture summary, scope, and constraints. |
| `02-architecture-goals-and-principles.md` | Architecture goals and implementation principles. |
| `03-system-context-architecture.md` | System context, actors, external systems, and context diagram. |
| `04-application-architecture.md` | Frontend/backend/database layering. |
| `05-module-architecture.md` | Major modules, responsibilities, endpoints, services, and tables/views. |
| `06-data-architecture.md` | PostgreSQL schema structure, keys, constraints, views, triggers, and indexes. |
| `07-security-architecture.md` | Authentication, authorization, permissions, session/token handling, and audit trail. |
| `08-deployment-architecture.md` | JavaFX desktop, Railway backend, Railway PostgreSQL, env vars, and backup considerations. |
| `09-integration-architecture.md` | Frontend/backend/database/API integration rules. |
| `10-error-handling-and-logging-architecture.md` | HTTP error mapping, validation, trigger errors, and audit logging. |
| `11-performance-and-scalability-considerations.md` | Search, indexing, report queries, and high-volume log handling. |
| `12-architecture-decisions.md` | ADR-style architecture decisions. |
| `13-architecture-gap-analysis.md` | Current architecture gaps and recommendations. |
| `14-future-architecture-enhancements.md` | Future options outside current scope. |

## Diagram Formats

| Folder | Format | Use |
| --- | --- | --- |
| `diagrams/mermaid/` | `.mmd` | Flowcharts, sequence diagrams, ERD, state diagrams, deployment view. |
| `diagrams/plantuml/` | `.puml` | UML class, component, and deployment diagrams. |
| `diagrams/structurizr/` | `.dsl` | C4 model context, container, component, and deployment views. |
| `diagrams/bpmn/` | `.bpmn.md` | BPMN-style process descriptions. |
| `diagrams/visual/` | Markdown guidance | Stakeholder-facing diagram guidance. |

## Source Of Truth

Architecture documents must remain aligned with:

- `../system-analysis/06-data-requirements-and-data-dictionary.md`
- `05-module-architecture.md`
- `../system-analysis/`

## Data Flow Diagrams

Formal DFD documentation is maintained in `../system-analysis/14-data-flow-diagrams.md`.

| Mermaid Source | Purpose |
| --- | --- |
| `diagrams/mermaid/dfd-level-0-context.mmd` | Context-level DFD for external entities and the BYOD system. |
| `diagrams/mermaid/dfd-level-1-system.mmd` | System-level DFD for major backend processes and data stores. |
| `diagrams/mermaid/dfd-level-2-gate-monitoring.mmd` | Detailed DFD for search, eligibility, entry/exit logging, and audit. |
| `diagrams/mermaid/dfd-level-2-pending-registration.mmd` | Detailed DFD for guard pending submission and admin decision. |
| `diagrams/mermaid/dfd-level-2-event-requests.mmd` | Detailed DFD for event request headers, line items, verification, and review. |
