# 06 - Data Architecture

## Data Model Scope

PostgreSQL on Railway is the single source of truth. The current documentation baseline follows the uploaded PostgreSQL schema and the data dictionary in `../system-analysis/06-data-requirements-and-data-dictionary.md`.

## Core Tables

| Table | Primary Key | Purpose |
| --- | --- | --- |
| `users` | `user_id` | Admin and guard accounts. |
| `students` | `student_id` | Student registry. |
| `devices` | `device_id` | Permanent BYOD devices and pending device registrations. |
| `event_requests` | `event_request_id` | Event access request headers. |
| `event_request_devices` | `event_device_id` | Device line items under event requests. |
| `device_logs` | `log_id` | Immutable entry/exit gate events. |
| `event_device_logs` | `event_log_id` | Immutable temporary event-device entry/exit events. |
| `audit_logs` | `audit_id` | Immutable audit trail. |
| `system_settings` | `setting_key` | Runtime policy values. |

## Views

| View | Purpose |
| --- | --- |
| `v_device_campus_status` | Derives inside/outside state for active approved or pending devices from latest log row. |
| `v_event_device_status` | Derives current-day event-device status from latest event_device_logs row. |
| `v_pending_devices` | Pending device registrations joined with student names for admin review. |
| `v_active_event_requests` | Pending and approved event requests with student names and device counts. |

## Integrity Rules

- Unique usernames and serial numbers.
- Check constraints for roles, statuses, device types, event types, document types, and audit action types.
- Reviewer fields must be populated together.
- Rejected devices require remarks.
- Permanent-device entry is allowed for active approved devices and for active pending devices when the unapproved-device check-in policy permits it.
- Manual gate events must alternate between entry and exit.
- Event-device events must alternate and are immutable.
- Automatic exits are always exit events with no human handler.
- Logs and audit rows are immutable.
- Server-side timestamps prevent backdating of logs and audit records.

## Index Strategy

Indexes prioritize:

- Student name and status lookup.
- Device owner, serial number, registration status, and pending queue.
- Event request student/status/date lookup.
- Event request device lookup and latest event-device log.
- Latest log lookup per device.
- Audit lookup by user, target, and created timestamp.

## Data Lifecycle

| Entity | Lifecycle |
| --- | --- |
| Users | Created by admin, deactivated instead of unsafe deletion. |
| Students | Created by admin or pending flow, deactivated instead of unsafe deletion. |
| Devices | Pending -> approved or rejected; approved devices are decommissioned by setting inactive. |
| Event requests | Normal submission -> approved; manual queue pending -> approved/returned/rejected; returned -> pending on resubmission. |
| Event manifest devices | Pending/approved -> returned on reconciliation; quantity defaults to 1 and must be greater than zero. |
| Device logs | Append-only by design; final schema still needs UPDATE protection. |
| Event device logs | Insert-only with update/delete triggers. |
| Audit logs | Insert-only through `fn_write_audit_log()`. |

## Diagram

See `diagrams/mermaid/database-erd.mmd`.
