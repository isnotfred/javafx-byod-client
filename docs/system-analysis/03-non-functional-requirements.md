# 03 - Non-Functional Requirements

## Usability

| ID | Requirement |
| --- | --- |
| NFR-001 | The interface shall be simple enough for guards to use during peak gate traffic. |
| NFR-002 | Common guard actions shall be reachable from the guard dashboard: search, log ingress, log egress, quick pending registration, and active devices. |
| NFR-003 | Error messages shall use clear non-technical wording. |
| NFR-004 | Destructive or sensitive actions shall require confirmation. |
| NFR-005 | Tables shall use readable column labels and support basic filtering where needed. |

## Security

| ID | Requirement |
| --- | --- |
| NFR-006 | The system shall enforce authentication before access. |
| NFR-007 | The system shall enforce role-based authorization for Admin and Security Guard users. |
| NFR-008 | Passwords shall be stored as secure hashes. |
| NFR-009 | Admin-only actions shall not be accessible from guard screens. |
| NFR-010 | Sensitive status changes and overrides shall be audit-tracked. |

## Reliability

| ID | Requirement |
| --- | --- |
| NFR-011 | The system shall prevent duplicate student IDs and duplicate active serial numbers. |
| NFR-012 | The system shall prevent invalid ingress and egress status transitions. |
| NFR-013 | The system shall display a clear message when database connection fails. |
| NFR-014 | The system shall preserve existing records when validation fails. |

## Performance

| ID | Requirement |
| --- | --- |
| NFR-015 | Search results should appear quickly enough for gate processing under normal school database size. |
| NFR-016 | Ingress and egress save operations should complete without noticeable delay under normal use. |
| NFR-017 | Reports should support filtering so users do not need to load all historical records at once. |

## Maintainability

| ID | Requirement |
| --- | --- |
| NFR-018 | Requirements, screens, reports, and tests shall use consistent terms for statuses and user roles. |
| NFR-019 | The codebase should keep UI, business logic, and database access separated where practical. |
| NFR-020 | Status values should be centralized in code to avoid inconsistent spelling. |
| NFR-021 | Database schema should use constraints and foreign keys to protect data integrity. |

## Data Integrity

| ID | Requirement |
| --- | --- |
| NFR-022 | Every normal BYOD device shall reference a valid student owner or a pending student record. |
| NFR-023 | Every device log shall reference one device and the user who performed the logged action. |
| NFR-024 | A device shall not have more than one open active ingress record. |
| NFR-025 | Egress time shall not be earlier than ingress time. |

## Auditability

| ID | Requirement |
| --- | --- |
| NFR-026 | Device logs shall not be permanently deleted through normal application functions. |
| NFR-027 | Approval, rejection, override, and correction actions shall store user, timestamp, action, and remarks. |
| NFR-028 | Reports shall be reproducible from saved database records. |

