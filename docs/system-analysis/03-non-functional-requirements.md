# 03 - Non-Functional Requirements

## Usability

| ID | Requirement |
| --- | --- |
| NFR-001 | Guard workflows shall prioritize fast search, clear eligibility messages, and direct entry/exit actions. |
| NFR-002 | Admin workflows shall group approval, event request, reporting, and device-management actions clearly. |
| NFR-003 | Super Admin workflows shall group user account management, role assignment, and system configuration clearly. |
| NFR-004 | JavaFX screens shall display user-safe validation messages returned by the backend. |
| NFR-005 | All roles shall have access to a Profile screen for self-service username and password updates. |

## Security

| ID | Requirement |
| --- | --- |
| NFR-006 | Passwords shall never be stored or logged as plaintext. |
| NFR-007 | Role checks shall be enforced by the backend even if frontend navigation is bypassed. |
| NFR-008 | The JavaFX client shall not connect directly to PostgreSQL. |
| NFR-009 | Audit records shall capture sensitive lifecycle, login, gate, event, role-change, and system-configuration actions. |
| NFR-010 | Database credentials shall be stored in backend deployment configuration, not in frontend files. |
| NFR-011 | Only Super Admin can create, deactivate, or change the role of user accounts. |
| NFR-012 | System configuration changes (system_settings) shall be restricted to Super Admin and audited. |

## Reliability And Data Integrity

| ID | Requirement |
| --- | --- |
| NFR-013 | PostgreSQL constraints and triggers shall enforce core business invariants. |
| NFR-014 | device_logs and audit_logs shall be immutable after insert. |
| NFR-015 | created_at for logs and audit rows shall be server-side generated to prevent backdating. |
| NFR-016 | Multi-step operations shall be transactional in the Spring Boot service/DAO flow. |
| NFR-017 | Deactivation shall be preferred over hard deletion for users, students, and devices with history. |
| NFR-018 | The max_devices_per_student limit shall be enforced by the backend service layer on every device registration attempt, reading the current value from system_settings. |
| NFR-019 | The allow_unregistered_devices setting shall be checked server-side before a guard checks in an unapproved device. |

## Performance

| ID | Requirement |
| --- | --- |
| NFR-020 | Gate lookup by student ID, student name, and serial number shall be indexed or query-optimized. |
| NFR-021 | Latest-device-status queries shall use the v_device_campus_status view or an equivalent indexed latest-log query. |
| NFR-022 | Reports shall apply date and status filters in SQL before returning rows to the frontend. |
| NFR-023 | High-volume device_logs and audit_logs shall use the schema's index and autovacuum tuning strategy. |

## Maintainability

| ID | Requirement |
| --- | --- |
| NFR-024 | JavaFX controllers shall contain UI coordination only and call backend APIs through service classes. |
| NFR-025 | Spring Boot controllers shall not contain SQL or business rules. |
| NFR-026 | Services shall own validation, role checks, and workflow orchestration. |
| NFR-027 | DAOs shall own SQL and use parameterized queries. |
| NFR-028 | Documentation shall be kept aligned with the dev branch source code and V4 Business System Analysis. |

## Deployment

| ID | Requirement |
| --- | --- |
| NFR-029 | The target backend and PostgreSQL database shall deploy to Railway. |
| NFR-030 | The frontend shall use a configurable backend base URL. |
| NFR-031 | Railway environment variables, backup policy, and production URL shall be documented once finalized. |
