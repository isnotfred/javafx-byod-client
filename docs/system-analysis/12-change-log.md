# 12 - Change Log

## 2026-05-25 - Documentation Reconciliation To Uploaded Schema And Architecture

Updated the system analysis documentation to align with the uploaded May 2026 source files:

- Adopted the target 3-tier architecture: JavaFX frontend, Spring Boot REST API, Railway PostgreSQL.
- Replaced legacy desktop database-access assumptions with backend API responsibilities.
- Aligned all data requirements to the PostgreSQL schema containing `users`, `students`, `devices`, `event_requests`, `event_request_devices`, `device_logs`, and `audit_logs`.
- Documented schema views: `v_device_campus_status`, `v_pending_devices`, and `v_active_event_requests`.
- Updated requirements, use cases, rules, screens, reports, tests, traceability, and user interactions.
- Removed the old policy allowing pending devices to receive temporary gate logs; pending devices are blocked until approved by the uploaded database trigger rules.
- Replaced update-style gate logging with immutable entry/exit event rows.
- Added explicit open questions for event request device gate logging, REST DTO shape, Railway env names, and image storage.

## Prior Baseline

The previous documentation described a layered Java desktop application with database access in the desktop tier and older data concepts. Those concepts are superseded by this change log entry and the canonical database/API documentation.
