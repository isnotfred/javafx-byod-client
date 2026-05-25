# 03 - Non-Functional Requirements

## Usability

| ID | Requirement |
| --- | --- |
| NFR-001 | Guard workflows shall prioritize fast search, clear eligibility messages, and direct entry/exit actions. |
| NFR-002 | Admin workflows shall group approval, event request, reporting, and user-management actions clearly. |
| NFR-003 | JavaFX screens shall display user-safe validation messages returned by the backend. |

## Security

| ID | Requirement |
| --- | --- |
| NFR-004 | Passwords shall never be stored or logged as plaintext. |
| NFR-005 | Role checks shall be enforced by the backend even if frontend navigation is bypassed. |
| NFR-006 | The JavaFX client shall not connect directly to PostgreSQL. |
| NFR-007 | Audit records shall capture sensitive lifecycle, login, gate, event, and system actions. |
| NFR-008 | Database credentials shall be stored in backend deployment configuration, not in frontend files. |

## Reliability And Data Integrity

| ID | Requirement |
| --- | --- |
| NFR-009 | PostgreSQL constraints and triggers shall enforce core business invariants. |
| NFR-010 | `device_logs` and `audit_logs` shall be immutable after insert. |
| NFR-011 | `created_at` for logs and audit rows shall be server-side generated to prevent backdating. |
| NFR-012 | Multi-step operations shall be transactional in the Spring Boot service/DAO flow. |
| NFR-013 | Deactivation shall be preferred over hard deletion for users, students, and devices with history. |

## Performance

| ID | Requirement |
| --- | --- |
| NFR-014 | Gate lookup by student ID, student name, and serial number shall be indexed or query-optimized. |
| NFR-015 | Latest-device-status queries shall use the schema view or an equivalent indexed latest-log query. |
| NFR-016 | Reports shall apply date and status filters in SQL before returning rows to the frontend. |
| NFR-017 | High-volume `device_logs` and `audit_logs` shall use the schema's index and autovacuum tuning strategy. |

## Maintainability

| ID | Requirement |
| --- | --- |
| NFR-018 | JavaFX controllers shall contain UI coordination only and call backend APIs. |
| NFR-019 | Spring Boot controllers shall not contain SQL or business rules. |
| NFR-020 | Services shall own validation, role checks, and workflow orchestration. |
| NFR-021 | DAOs shall own SQL and use parameterized queries. |
| NFR-022 | Documentation shall be kept aligned with the uploaded PostgreSQL schema and the system-analysis data dictionary. |

## Deployment

| ID | Requirement |
| --- | --- |
| NFR-023 | The target backend and PostgreSQL database shall deploy to Railway. |
| NFR-024 | The frontend shall use a configurable backend base URL. |
| NFR-025 | Railway environment variables, backup policy, and production URL shall be documented once finalized. |
