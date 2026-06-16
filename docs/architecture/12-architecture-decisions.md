# 12 - Architecture Decisions

## ADR-001 Use JavaFX Desktop Frontend

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | The project requires a desktop application experience for campus staff. |
| Decision | Use JavaFX for the frontend. |
| Consequences | UI logic stays in JavaFX controllers; backend access is through API calls. |

## ADR-002 Use Spring Boot REST Backend

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | The uploaded architecture defines JavaFX -> Spring Boot -> PostgreSQL. |
| Decision | Use Spring Boot as the API and business logic tier. |
| Consequences | Frontend does not own JDBC access; backend repository and deployment are required. |

## ADR-003 Use PostgreSQL On Railway

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Uploaded schema and architecture target PostgreSQL hosted on Railway. |
| Decision | Use Railway PostgreSQL as the database. |
| Consequences | Backend configuration must define Railway database credentials and backup procedure. |

## ADR-004 Backend-Only JDBC Access

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Database credentials and SQL should not live in the JavaFX client. |
| Decision | Use JDBC/NamedParameterJdbcTemplate only inside backend DAOs. |
| Consequences | JavaFX calls JSON REST endpoints and never connects directly to PostgreSQL. |

## ADR-005 Use Role-Based Access Control

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Admin and guard users have different capabilities. |
| Decision | Enforce roles in frontend navigation and backend services. |
| Consequences | Guards cannot approve pending BYOD devices, manage users, or view full audit history by default. |

## ADR-006 Use Immutable Gate Logs

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Gate history must support accountability and reporting. |
| Decision | Model gate activity as append-only `device_logs` rows. |
| Consequences | Corrections require new records, remarks, or audit entries, not row edits. |

## ADR-007 Derive Campus Presence From Logs

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | The uploaded schema has no stored presence field on `devices`. |
| Decision | Derive inside/outside from latest `device_logs` using `v_device_campus_status` or equivalent queries. |
| Consequences | Gate screens and reports must not update device rows for presence changes. |

## ADR-008 Allow Policy-Eligible Pending Devices

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | The final trigger accepts active devices whose registration status is approved or pending. |
| Decision | Permit active pending-device check-in only when `allow_unregistered_devices` is true. |
| Consequences | Backend service checks the setting before inserting the pending device's entry event; rejected and inactive devices remain blocked. |

## ADR-009 Track Event Access With Request Tables

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | The final schema adds `event_device_logs` and `v_event_device_status`. |
| Decision | Preserve the schema rule that each manifest row has quantity > 0 with default 1, and store event entry/exit history separately from permanent `device_logs`. |
| Consequences | Guards scan the manifest row referenced by event_device_logs; grouped quantities require a defined whole-row operational meaning. |

## ADR-010 Use Database-Enforced Audit Writer

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Audit rows must be consistent and immutable. |
| Decision | Write audit records through `fn_write_audit_log()`. |
| Consequences | Application code should not insert directly into `audit_logs`. |
