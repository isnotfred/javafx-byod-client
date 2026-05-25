# 02 - Functional Requirements

## Authentication And Role Access

| ID | Requirement |
| --- | --- |
| FR-001 | The system shall require login before protected functions are used. |
| FR-002 | The backend shall authenticate users by `users.username` and `users.password_hash`. |
| FR-003 | The system shall allow only active accounts where `users.status = 'active'`. |
| FR-004 | The system shall route authenticated users by stored role value: `admin` or `guard`. |
| FR-005 | The frontend and backend shall both enforce role-based access. |
| FR-006 | The system shall audit login, logout, and failed login events using standardized audit action types. |

## Student Management

| ID | Requirement |
| --- | --- |
| FR-007 | Admin users shall create student records with `student_id`, `first_name`, `last_name`, and optional `course_year_level`. |
| FR-008 | Admin users shall update student names, course/year level, and status. |
| FR-009 | The system shall prevent duplicate `student_id` values. |
| FR-010 | The system shall support active/inactive student status using `students.status`. |
| FR-011 | The system shall use deactivation instead of hard deletion when linked devices or logs exist. |
| FR-012 | Guards may create a student record only as part of quick pending device registration. |

## Device Management

| ID | Requirement |
| --- | --- |
| FR-013 | Admin users shall register permanent BYOD devices linked to one `students.student_id`. |
| FR-014 | The system shall enforce globally unique `devices.serial_number`. |
| FR-015 | The system shall support the device types `laptop`, `tablet`, and `phone`. |
| FR-016 | The system shall support the configured device purposes from the PostgreSQL check constraint. |
| FR-017 | Admin users shall approve, reject, update, activate, and deactivate device records. |
| FR-018 | Rejected devices shall require a rejection remark. |
| FR-019 | Device review actions shall store `reviewed_by` and `reviewed_at` together. |
| FR-020 | Campus presence shall be derived from latest `device_logs`, not stored on `devices`. |

## Pending Registration

| ID | Requirement |
| --- | --- |
| FR-021 | Guards shall submit unregistered devices as `devices` rows with `registration_status = 'pending'`. |
| FR-022 | Pending device records shall appear in the admin approval queue through `v_pending_devices`. |
| FR-023 | Admin users shall approve pending devices by setting `registration_status = 'approved'`. |
| FR-024 | Admin users shall reject pending devices by setting `registration_status = 'rejected'` with required remarks. |
| FR-025 | Guards shall not approve or reject pending devices. |
| FR-026 | Pending devices shall not receive gate entry/exit log records until approved, because database triggers block unapproved devices. |

## Event Requests

| ID | Requirement |
| --- | --- |
| FR-027 | The system shall support event access request headers in `event_requests`. |
| FR-028 | The system shall support one or more event request device line items in `event_request_devices`. |
| FR-029 | Event requests shall capture responsible student, responsible person, organization, event name, purpose, document type, document reference, start date, and end date. |
| FR-030 | Event document type shall be limited to `Paper Approval` or `Signed GPOA`. |
| FR-031 | Event request status shall be `pending`, `approved`, `returned`, or `rejected`. |
| FR-032 | Event request device status shall be `pending`, `approved`, or `returned`. |
| FR-033 | The system shall expose active event requests through `v_active_event_requests`. |
| FR-034 | Event request devices shall be treated as request/verification records unless a future schema link to gate logs is added. |

## Gate Monitoring

| ID | Requirement |
| --- | --- |
| FR-035 | Admin and guard users shall search by student ID, student name, or device serial number before gate logging. |
| FR-036 | The system shall display student, device, registration, device status, and derived campus status data before logging. |
| FR-037 | The system shall insert a `device_logs` row for each entry event. |
| FR-038 | The system shall insert a `device_logs` row for each manual exit event. |
| FR-039 | Gate logs shall store `event_type`, `event_time`, `handled_by`, `logout_type`, `auto_exit`, `notes`, and `created_at` according to schema rules. |
| FR-040 | The database shall block gate logs for unapproved or inactive devices. |
| FR-041 | The database shall block consecutive same-type manual events for the same device. |
| FR-042 | Device log rows shall be immutable after insert. |

## Automatic Logout

| ID | Requirement |
| --- | --- |
| FR-043 | The backend shall run an automatic logout process for devices whose latest event is entry at school closing. |
| FR-044 | Automatic logout shall insert exit rows with `auto_exit = TRUE`, `logout_type = 'automatic'`, and `handled_by = NULL`. |
| FR-045 | Automatic logout shall write system audit entries. |

## Reports And Search

| ID | Requirement |
| --- | --- |
| FR-046 | Admin users shall generate gate, monthly, active-device, registered-device, pending, rejected/inactive, event, and history reports. |
| FR-047 | Reports shall read from saved tables and schema views, not unsaved UI state. |
| FR-048 | Reports shall filter by date range before loading large historical result sets. |
| FR-049 | Active-device views shall be based on latest log state or `v_device_campus_status`. |

## User Management And Audit

| ID | Requirement |
| --- | --- |
| FR-050 | Admin users shall create and update user accounts. |
| FR-051 | The system shall prevent duplicate usernames. |
| FR-052 | Passwords shall be stored only as bcrypt or argon2 hashes. |
| FR-053 | User records shall use `role` values `admin` and `guard`. |
| FR-054 | User records shall use `status` values `active` and `inactive`. |
| FR-055 | Audit records shall be written through `fn_write_audit_log()`. |
| FR-056 | Audit rows shall be immutable after insert. |
