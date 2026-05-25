# 01 - Architecture Overview

## System Purpose

The BYOD Device Management System manages student-owned device registration and gate entry/exit monitoring. It supports admin and guard workflows, pending device approval, event access requests, immutable gate logs, reports, role-based access, and audit history.

## Architecture Summary

The target architecture is a 3-tier client-server system:

| Tier | Technology | Main Responsibility |
| --- | --- | --- |
| Frontend | JavaFX, FXML, CSS | UI, navigation, form input, table display, and HTTPS/JSON calls. |
| Backend | Spring Boot REST API, JDBC/NamedParameterJdbcTemplate | Authentication, authorization, validation, business workflows, transactions, SQL access, and audit orchestration. |
| Database | PostgreSQL on Railway | Persistent records, constraints, views, triggers, functions, indexes, and audit/log immutability. |

The frontend never connects directly to PostgreSQL. All database access runs through backend DAOs.

## Main Users

| User | Architecture Relevance |
| --- | --- |
| Admin | Needs full administrative screens, backend-protected permissions, reports, users, and audit access. |
| Guard | Needs fast JavaFX gate screens and backend-protected entry/exit operations. |
| Student | Indirect actor whose information is stored and verified by staff. |

## Main Capabilities

- Login and role-based API access.
- Student management.
- Device management and pending approval.
- Event access request management.
- Gate entry/exit logging.
- Automatic school-closing exit process.
- Active-device view from derived latest-log state.
- Reports and immutable audit trail.

## Architectural Constraints

- JavaFX is the frontend client.
- Spring Boot is the backend API boundary.
- PostgreSQL is hosted on Railway.
- Backend is the only database client.
- SQL must stay in DAO classes.
- Business rules must stay in services and database constraints/triggers, not JavaFX controllers.
- `device_logs` and `audit_logs` are append-only from application perspective.

## Implementation State

The current repository still contains a JavaFX starter project. This architecture package documents the target system and should guide implementation across frontend and backend repositories.
