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
| `audit_logs` | `audit_id` | Immutable audit trail. |

## Views

| View | Purpose |
| --- | --- |
| `v_device_campus_status` | Derives inside/outside state for approved active devices from latest log row. |
| `v_pending_devices` | Pending device registrations joined with student names for admin review. |
| `v_active_event_requests` | Pending and approved event requests with student names and device counts. |

## Integrity Rules

- Unique usernames and serial numbers.
- Check constraints for roles, statuses, device types, event types, document types, and audit action types.
- Reviewer fields must be populated together.
- Rejected devices require remarks.
- Gate logs are allowed only for approved active devices.
- Manual gate events must alternate between entry and exit.
- Automatic exits are always exit events with no human handler.
- Logs and audit rows are immutable.
- Server-side timestamps prevent backdating of logs and audit records.

## Index Strategy

Indexes prioritize:

- Student name and status lookup.
- Device owner, serial number, registration status, and pending queue.
- Event request student/status/date lookup.
- Latest log lookup per device.
- Audit lookup by user, target, and created timestamp.

## Data Lifecycle

| Entity | Lifecycle |
| --- | --- |
| Users | Created by admin, deactivated instead of unsafe deletion. |
| Students | Created by admin or pending flow, deactivated instead of unsafe deletion. |
| Devices | Pending -> approved or rejected; approved devices are decommissioned by setting inactive. |
| Event requests | Pending -> approved or rejected; approved -> returned. |
| Device logs | Insert-only. |
| Audit logs | Insert-only through `fn_write_audit_log()`. |

## Diagram

See `diagrams/mermaid/database-erd.mmd`.
