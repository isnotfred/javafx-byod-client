# System Analysis Documentation

This folder contains the full system analysis documentation for the BYOD Device Management System.

## Current Target Baseline

Revision V3 - June 2026

Authoritative sources:

- Final PostgreSQL schema (`schema (1).sql`) - authoritative structural specification.

- Temporary Event Device Request Workflow (`EVENT_REQUESTS_FLOW.md`) - authoritative event behavior.

The documented design follows these sources:

- Three roles: super_admin, admin, guard.

- JavaFX desktop frontend for all three roles.

- Spring Boot REST API backend over HTTPS/JSON.

- PostgreSQL database hosted on Railway.

- Backend-only database access through JDBC/NamedParameterJdbcTemplate.

- Five device categories: Personal Computers, Components & Peripherals, Display & Projection, Project Prototypes (Optional SN), Appliances (TLE).

- Permanent-device serial numbers are required and globally unique; optional devices.image_path remains in the final schema with no defined upload workflow.

- system_settings drives maximum-device, unapproved-device check-in, event-duration, and automatic-exit policy.

- Temporary event devices have separate immutable gate logs and derived status.

## Document Index

| # | Document | Description |
| --- | --- | --- |
| 01 | 01-system-analysis.md | System overview, objectives, scope, roles, core workflows, modules, assumptions, risks. |
| 02 | 02-functional-requirements.md | All functional requirements (FR-001 to FR-075). |
| 03 | 03-non-functional-requirements.md | Usability, security, reliability, performance, maintainability, and deployment NFRs. |
| 04 | 04-use-cases.md | 15 use cases covering all actors and workflows. |
| 05 | 05-business-rules-and-validation-rules.md | Business rules (BR-001 to BR-022) and validation rules (VR-001 to VR-047). |
| 06 | 06-data-requirements-and-data-dictionary.md | Database objects, enum values, all table definitions, ERD. |
| 07 | 07-screen-requirements.md | All screen requirements for all roles and workflows. |
| 08 | 08-report-requirements.md | Seven report types with data sources and validation rules. |
| 09 | 09-acceptance-criteria-and-test-scenarios.md | Acceptance criteria and 65 QA test scenarios. |
| 10 | 10-gap-analysis-and-recommendations.md | Resolved and remaining schema/service gaps with recommendations. |
| 11 | 11-requirements-traceability-matrix.md | All 75 FRs traced to use cases, screens, and data entities. |
| 12 | 12-change-log.md | Change history for this documentation set. |
| 13 | 13-user-interactions.md | Role-level interaction details, permission matrix, and interaction flows. |
| 14 | 14-data-flow-diagrams.md | DFD Level 0, Level 1, and Level 2 diagrams for all major workflows. |

## Source Of Truth

| Source | Status |
| --- | --- |
| 06-data-requirements-and-data-dictionary.md | Database objects aligned to the final supplied SQL. |
| 07-screen-requirements.md | Canonical screen behavior aligned to the final workflow and schema. |
| 02-functional-requirements.md | All 75 FRs aligned to the final workflow and schema. |

## Out Of Scope

- Student self-registration or student-facing interface.

- Web portal or mobile application.

- RFID, barcode scanner, GPS, or automated physical device detection.

- Faculty, staff, and external visitor tracking.

- Exact REST DTO shape and token/session format (backend implementation detail).

- Railway environment variable names and backup policy (to be documented post-deployment).
