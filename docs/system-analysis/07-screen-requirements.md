# 07 - Screen Requirements

## Revision Note

This screen baseline follows the uploaded PostgreSQL schema:

- Campus status is derived from latest `device_logs` or `v_device_campus_status`.
- Pending device registrations are `devices` rows with `registration_status = 'pending'`.
- Pending devices cannot be logged through the gate until approved.
- Temporary/event access uses `event_requests` and `event_request_devices`.
- `device_logs` contains immutable `entry` and `exit` rows.
- Stored role values are `admin` and `guard`.

## Login Screen

| Item | Requirement |
| --- | --- |
| Purpose | Authenticate users before access. |
| Access | unauthenticated users. |
| Fields | Username, password. |
| Actions | Login, exit. |
| Validation / Error Messages | Invalid username or password; account is inactive; backend unavailable; database connection failed. |
| Backend/API Notes | Calls `POST /auth/login`; backend reads `users.username`, `users.password_hash`, `users.role`, and `users.status`; failed attempts write `USER_LOGIN_FAILED`. |

## Admin Dashboard

| Item | Requirement |
| --- | --- |
| Purpose | Provide administrative summary and navigation. |
| Access | `admin`. |
| Display | Active students; registered devices; pending devices; devices currently inside; today's entries; today's exits; today's automatic exits. |
| Actions | Students, devices, pending approvals, event requests, logs, reports, users, logout. |
| Data Notes | Counts query `students`, `devices`, `device_logs`, and `v_device_campus_status`. |

## Guard Dashboard

| Item | Requirement |
| --- | --- |
| Purpose | Support fast gate monitoring. |
| Access | `guard`. |
| Fields | Search text for student ID, student name, or serial number. |
| Actions | Search, log entry, log exit, quick pending registration, event request, active devices, clear, logout. |
| Display | Student name, course/year level, device type, brand, model, serial number, purpose, registration status, derived campus status, device status, and warning banner. |
| Validation / Error Messages | Device not found; device already inside; device not currently inside; device inactive; registration pending; registration rejected. |
| Data Notes | Search queries `students`, `devices`, and `v_device_campus_status`; entry/exit writes `device_logs`. |

## Student Management Screen

| Item | Requirement |
| --- | --- |
| Purpose | Manage student records. |
| Access | `admin`. |
| Fields | Student ID, first name, last name, course/year level, status. |
| Actions | Add, update, search, clear, deactivate. |
| Table Columns | Student ID, last name, first name, course/year level, status, created at. |
| Search / Filters | Student ID, name, course/year level, status. |
| Validation / Error Messages | Student ID required; duplicate student ID; first name required; last name required; invalid status. |
| Data Notes | Maps to `students`; deactivation sets `status = 'inactive'`; unsafe hard deletion is blocked by trigger. |

## Device Management Screen

| Item | Requirement |
| --- | --- |
| Purpose | Manage permanent BYOD device records. |
| Access | `admin`. |
| Fields | Student owner, device name, brand, model, serial number, device type, purpose, registration status, device status, remarks, image path. |
| Actions | Register, update, upload image path, approve, reject, activate, deactivate, search, clear. |
| Table Columns | Device ID, student ID, student name, device name, brand/model, serial number, type, purpose, registration status, derived campus status, device status. |
| Search / Filters | Student ID/name, serial number, type, purpose, registration status, device status, derived campus status. |
| Validation / Error Messages | Serial number required; duplicate serial number; owner required; invalid type; invalid purpose; rejection remarks required; invalid registration transition. |
| Data Notes | Maps to `devices`; approval/rejection sets `reviewed_by` and `reviewed_at`; derived campus status is read-only. |

## Quick Pending Registration Screen

| Item | Requirement |
| --- | --- |
| Purpose | Let guards submit a pending device registration at the gate after manual proof verification. |
| Access | `guard`. |
| Fields | Student ID, first name, last name, course/year level, proof type, proof reference/remarks, device name, brand, model, serial number, device type, purpose, remarks. |
| Actions | Save pending registration, clear, cancel. |
| Post-save Display | New `device_id`, submitted by current guard, submitted timestamp, and `registration_status = 'pending'`. |
| Validation / Error Messages | Required student details; required proof details; serial number required; duplicate serial number; invalid type or purpose. |
| Data Notes | Inserts or links `students`; inserts `devices` with pending registration status; proof details are stored in `devices.remarks`; pending devices cannot be logged until admin approval. |

## Pending Registration Approval Screen

| Item | Requirement |
| --- | --- |
| Purpose | Let admins review pending device registrations. |
| Access | `admin`. |
| Fields | Student details, proof/remarks, device details, submitted timestamp, approval remarks or rejection reason. |
| Actions | View details, approve, reject, refresh. |
| Table Columns | Device ID, student ID, student name, course/year level, device name, brand/model, serial number, type, purpose, submitted at, registration status. |
| Search / Filters | Date submitted, student ID/name, serial number. |
| Validation / Error Messages | Rejection reason required; duplicate serial conflict; missing owner. |
| Data Notes | Uses `v_pending_devices`; approve sets `registration_status = 'approved'`; reject sets `registration_status = 'rejected'` and required remarks; writes `DEVICE_APPROVED` or `DEVICE_REJECTED`. |

## Event Request Screen

| Item | Requirement |
| --- | --- |
| Purpose | Register and manage temporary/event access requests and device line items. |
| Access | `admin`; `guard` for submission and verification where allowed. |
| Header Fields | Responsible student, responsible person, organization, event name, event purpose, approval document type, approval document reference, start date, end date, remarks. |
| Device Fields | Device name, brand, model, device type, serial number, quantity, remarks. |
| Actions | Save event request, add device, verify device, approve request, reject request, mark returned, search, clear. |
| Request Table Columns | Request ID, student name, event name, organization, start date, end date, status, device count. |
| Device Table Columns | Event device ID, device name, brand/model, type, serial number, quantity, device status, verified by, verified at. |
| Search / Filters | Event name, organization, responsible student, date range, request status. |
| Validation / Error Messages | Responsible student required; event name required; invalid document type; document reference required; invalid date range; at least one line item before approval. |
| Data Notes | Header maps to `event_requests`; line items map to `event_request_devices`; active queue reads `v_active_event_requests`; direct gate-log linkage for event line items is an open schema issue. |

## Ingress / Egress Monitoring Screen

| Item | Requirement |
| --- | --- |
| Purpose | Log device entry and exit transactions. |
| Access | `admin`, `guard`. |
| Fields | Search text, notes. |
| Actions | Search, log entry, log exit, quick pending registration, clear. |
| Display | Student, device details, registration status, derived campus status, device status, latest entry time, latest exit time. |
| Table Columns | Log ID, device ID, student name, device type, serial number, registration status, derived campus status, device status, latest event time. |
| Validation / Error Messages | Device not found; already inside; no active entry; device inactive; registration not approved. |
| Data Notes | Entry inserts `event_type = 'entry'`; manual exit inserts `event_type = 'exit'`, `logout_type = 'manual'`; `handled_by` stores current user; triggers enforce eligibility and event alternation. |

## Active Devices Inside Campus Screen

| Item | Requirement |
| --- | --- |
| Purpose | Show devices whose latest event is entry and school-closing automatic exit results. |
| Access | `admin`, `guard`. |
| Actions | Refresh, search, filter, view details, log exit. |
| Table Columns | Device ID, student name, device type, serial number, purpose, entry time, handled by, automatic exit flag where applicable. |
| Search / Filters | Student name, device type, purpose, entry date, guard user, overdue. |
| Validation / Error Messages | No active devices found; backend unavailable. |
| Data Notes | Uses `v_device_campus_status` filtered to `campus_status = 'inside'` or an equivalent latest-log query; automatic exit rows have `auto_exit = TRUE`, `logout_type = 'automatic'`, and `handled_by = NULL`. |

## Logs Screen

| Item | Requirement |
| --- | --- |
| Purpose | Browse gate logs and audit history where permitted. |
| Access | `admin`; guard access limited to operational gate logs if allowed. |
| Fields | Date range, action/event type, student, device, user. |
| Actions | Search, clear filters, view detail. |
| Data Notes | `device_logs` and `audit_logs` are read-only from the UI. |

## Reports Screen

| Item | Requirement |
| --- | --- |
| Purpose | Generate administrative monitoring reports. |
| Access | `admin`. |
| Fields | Report type, date range, student, device type, purpose, registration status, device status, user. |
| Actions | Generate, clear filters, print/export if implemented. |
| Validation / Error Messages | Invalid date range; no records found; backend unavailable. |
| Data Notes | Reads `students`, `devices`, `device_logs`, `event_requests`, `event_request_devices`, `audit_logs`, and schema views. |

## User Management Screen

| Item | Requirement |
| --- | --- |
| Purpose | Manage system user accounts. |
| Access | `admin`. |
| Fields | Username, password/new password, full name, role, status. |
| Actions | Add, update, deactivate, reset password, search, clear. |
| Table Columns | User ID, username, full name, role, status, created at. |
| Search / Filters | Username, role, status. |
| Validation / Error Messages | Username required; duplicate username; invalid role; password required for new accounts; password hash invalid. |
| Data Notes | Maps to `users`; roles are stored as `admin` and `guard`; deactivation sets `status = 'inactive'`; writes `USER_CREATED`, `USER_UPDATED`, or `USER_DEACTIVATED`. |
