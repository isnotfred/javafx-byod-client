# BYOD Registration and Monitoring System Documentation

This folder is the shared documentation area for the BYOD Registration and Monitoring System.

## Current Target Baseline

The current target design follows the uploaded May 2026 change package:

- JavaFX desktop frontend for admin and guard users.
- Spring Boot REST API backend over HTTPS/JSON.
- PostgreSQL database hosted on Railway.
- Backend-only database access through JDBC/NamedParameterJdbcTemplate.
- Database schema baseline documented in `system-analysis/06-data-requirements-and-data-dictionary.md`.

The Java source currently remains a starter JavaFX project. These documents describe the target system analysis and architecture, not completed implementation.

## Folder Index

| Folder | Owner / Main Users | Purpose |
| --- | --- | --- |
| `system-analysis/` | System Analyst, QA, stakeholders | Requirements, use cases, rules, data dictionary, screens, reports, tests, gaps, traceability, and interactions. |
| `architecture/` | Solution Architect, developers, DevOps | 3-tier architecture, modules, security, deployment, performance, decisions, gaps, and diagram sources. |
| `api/` | Backend/frontend developers | Endpoint-level REST API contract overview. |

## Source Of Truth

| Source | Status |
| --- | --- |
| `system-analysis/06-data-requirements-and-data-dictionary.md` | Database schema summary aligned to the uploaded PostgreSQL schema. |
| `system-analysis/07-screen-requirements.md` | Canonical screen behavior aligned to the schema. |
| `architecture/` | Target 3-tier architecture documentation. |

## Out Of Scope Unless Added Later

Student self-registration, web portal UI, mobile app, RFID, barcode scanner, GPS tracking, automated physical device detection, and detailed REST DTO definitions remain outside the documented current baseline.
