# Architecture Documentation

This folder contains the System Architect documentation package for the BYOD Registration and Monitoring System.

## Read First

1. `01-architecture-overview.md`
2. `04-application-architecture.md`
3. `05-module-architecture.md`
4. `06-data-architecture.md`
5. `13-architecture-gap-analysis.md`

## Architecture Documents

| File | Purpose |
| --- | --- |
| `01-architecture-overview.md` | Overall architecture summary, scope, constraints, and current/future boundaries. |
| `02-architecture-goals-and-principles.md` | Architecture goals and implementation principles. |
| `03-system-context-architecture.md` | System context, actors, external process, and context diagram. |
| `04-application-architecture.md` | Recommended layered Java desktop architecture. |
| `05-module-architecture.md` | Major modules, responsibilities, tables, services, and requirements. |
| `06-data-architecture.md` | Database structure, keys, constraints, status fields, and ERD. |
| `07-security-architecture.md` | Authentication, authorization, permissions, session handling, and audit trail. |
| `08-deployment-architecture.md` | Desktop runtime, JDBC/database connection, file storage, backup assumptions, and deployment view. |
| `09-integration-architecture.md` | Current internal integrations and future integration boundaries. |
| `10-error-handling-and-logging-architecture.md` | Validation, database, permission, file, and audit logging approach. |
| `11-performance-and-scalability-considerations.md` | Search, indexing, report queries, filtering, and peak-hour considerations. |
| `12-architecture-decisions.md` | ADR-style architecture decisions. |
| `13-architecture-gap-analysis.md` | Current architecture gaps, impact, recommendations, and priority. |
| `14-future-architecture-enhancements.md` | Future architecture options outside current scope. |

## Diagram Formats

| Folder | Format | Use |
| --- | --- | --- |
| `diagrams/mermaid/` | `.mmd` | Flowcharts, sequence diagrams, ERD, state diagrams, deployment view. |
| `diagrams/plantuml/` | `.puml` | UML class, component, and deployment diagrams. |
| `diagrams/structurizr/` | `.dsl` | C4 model context, container, component, and deployment views. |
| `diagrams/bpmn/` | `.bpmn.md` | BPMN-style process descriptions. |
| `diagrams/visual/` | Markdown guidance | Notes for stakeholder-facing diagram tools. |

## Source of Truth

This package is based on `../system-analysis/` and the project brief. It documents recommended architecture only; it does not implement Java code, database migrations, or deployment scripts.

## Additional Mermaid Diagram

`diagrams/mermaid/automatic-logout-sequence.mmd` documents the school-closing automatic logout process for devices still marked Inside.
