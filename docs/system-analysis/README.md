# System Analysis Documentation

This folder contains the system analysis package for the BYOD Registration and Monitoring System.

## Baseline

The current documentation baseline follows the uploaded PostgreSQL schema, ERD, architecture document, and revised screen requirements:

- Target runtime: JavaFX frontend -> Spring Boot REST API -> Railway PostgreSQL.
- Database tables: `users`, `students`, `devices`, `event_requests`, `event_request_devices`, `device_logs`, `audit_logs`.
- Database views: `v_device_campus_status`, `v_pending_devices`, `v_active_event_requests`.
- Gate logs and audit logs are immutable.
- Campus status is derived from latest `device_logs`, not stored on `devices`.

## Document Index

| File | Purpose |
| --- | --- |
| `01-system-analysis.md` | Full system analysis: purpose, scope, users, workflows, modules, assumptions, risks, and recommendations. |
| `02-functional-requirements.md` | Numbered functional requirements grouped by module. |
| `03-non-functional-requirements.md` | Quality requirements for usability, security, reliability, performance, maintainability, data integrity, and auditability. |
| `04-use-cases.md` | Actor list and use case descriptions with flows, alternatives, exceptions, and postconditions. |
| `05-business-rules-and-validation-rules.md` | Business rules separated from enforceable validation rules. |
| `06-data-requirements-and-data-dictionary.md` | Data dictionary aligned to the PostgreSQL schema. |
| `07-screen-requirements.md` | Screen-level requirements for the JavaFX frontend. |
| `08-report-requirements.md` | Report definitions, filters, columns, and output expectations. |
| `09-acceptance-criteria-and-test-scenarios.md` | Acceptance criteria and QA scenarios. |
| `10-gap-analysis-and-recommendations.md` | Current target gaps, impacts, recommendations, and open questions. |
| `11-requirements-traceability-matrix.md` | Traceability between rules, requirements, screens, database objects, and tests. |
| `12-change-log.md` | Documentation change log. |
| `13-user-interactions.md` | Role-based interaction guide. |
| `14-data-flow-diagrams.md` | Formal DFD package showing context, system-level, and workflow-specific data flows. |
