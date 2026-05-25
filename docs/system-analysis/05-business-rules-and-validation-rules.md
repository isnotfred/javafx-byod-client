# 05 - Business Rules And Validation Rules

## Business Rules

| ID | Business Rule | Notes |
| --- | --- | --- |
| BR-001 | Only `admin` and `guard` users can log in. | Students are indirect actors only. |
| BR-002 | Inactive user accounts cannot access the system. | Enforced before dashboard access. |
| BR-003 | Student records are deactivated instead of deleted when history exists. | `trg_protect_student_delete` blocks unsafe deletes. |
| BR-004 | Permanent BYOD devices must belong to one student. | `devices.student_id` references `students`. |
| BR-005 | Device serial numbers are globally unique for permanent BYOD records. | Enforced by unique constraint. |
| BR-006 | Pending device registrations require admin approval before gate logging. | `trg_device_logs_approved_only` blocks unapproved devices. |
| BR-007 | Rejected devices require remarks and cannot be logged through the normal gate workflow. | Enforced by check constraint and log trigger. |
| BR-008 | Campus presence is derived from latest gate log. | No stored device column is updated for presence. |
| BR-009 | Entry and exit records are append-only. | `device_logs` rows cannot be updated or deleted. |
| BR-010 | Two consecutive manual events of the same type for one device are invalid. | Prevents duplicate entry or duplicate exit rows. |
| BR-011 | Automatic logout is system-generated and has no human handler. | `auto_exit = TRUE`, `handled_by = NULL`. |
| BR-012 | Event request devices are request/verification records unless a future gate-log relationship is added. | Current schema has no direct FK from `device_logs` to `event_request_devices`. |
| BR-013 | Audit logs are immutable and written through the database function. | Use `fn_write_audit_log()`. |

## Validation Rules

### Authentication And Users

| ID | Validation Rule |
| --- | --- |
| VR-001 | `users.username` is required, unique, and at least 3 characters. |
| VR-002 | `users.password_hash` is required and must be long enough to prevent plaintext storage. |
| VR-003 | `users.role` must be `admin` or `guard`. |
| VR-004 | `users.status` must be `active` or `inactive`. |
| VR-005 | Inactive accounts must be rejected at login. |

### Students

| ID | Validation Rule |
| --- | --- |
| VR-006 | `students.student_id` is required, non-blank, and unique. |
| VR-007 | `students.first_name` and `students.last_name` are required and non-blank. |
| VR-008 | `students.status` must be `active` or `inactive`. |

### Devices

| ID | Validation Rule |
| --- | --- |
| VR-009 | `devices.student_id` is required and must reference an existing student. |
| VR-010 | `devices.serial_number` is required and unique. |
| VR-011 | `devices.device_type` must be `laptop`, `tablet`, or `phone`. |
| VR-012 | `devices.device_purpose` must be one of the schema-approved values. |
| VR-013 | `devices.registration_status` must be `pending`, `approved`, or `rejected`. |
| VR-014 | `devices.device_status` must be `active` or `inactive`. |
| VR-015 | `reviewed_by` and `reviewed_at` must both be null or both be populated. |
| VR-016 | Rejected devices must have non-blank remarks. |
| VR-017 | Direct `approved` to `rejected` and `rejected` to `approved` transitions are blocked by trigger. |

### Event Requests

| ID | Validation Rule |
| --- | --- |
| VR-018 | `event_requests.student_id` must reference an existing student. |
| VR-019 | `event_requests.event_name` is required and non-blank. |
| VR-020 | `event_requests.approval_doc_type` must be `Paper Approval` or `Signed GPOA`. |
| VR-021 | `event_requests.end_date` must be on or after `start_date` when both are provided. |
| VR-022 | `event_requests.status` must be `pending`, `approved`, `returned`, or `rejected`. |
| VR-023 | Event review fields must follow the same reviewer/timestamp consistency rule. |
| VR-024 | `event_request_devices.quantity` must be greater than zero. |
| VR-025 | `event_request_devices.device_type` must be `laptop`, `tablet`, `phone`, `camera`, `projector`, or `other`. |
| VR-026 | `event_request_devices.device_status` must be `pending`, `approved`, or `returned`. |

### Gate Logs

| ID | Validation Rule |
| --- | --- |
| VR-027 | `device_logs.event_type` must be `entry` or `exit`. |
| VR-028 | Manual gate rows must have `handled_by` populated. |
| VR-029 | Automatic exit rows must have `handled_by = NULL`, `auto_exit = TRUE`, and `event_type = 'exit'`. |
| VR-030 | `logout_type` must be `manual`, `automatic`, or null where schema permits. |
| VR-031 | Gate logs can be inserted only for approved active devices. |
| VR-032 | Consecutive same-type manual events for one device are blocked. |
| VR-033 | `device_logs.created_at` is overwritten by the database on insert. |

### Audit

| ID | Validation Rule |
| --- | --- |
| VR-034 | `audit_logs.action_type` must be one of the schema-approved action values. |
| VR-035 | `audit_logs.target_table` is required and non-blank. |
| VR-036 | `audit_logs.ip_address` must be null or 7 to 45 characters. |
| VR-037 | Audit rows cannot be updated or deleted. |

### Reports

| ID | Validation Rule |
| --- | --- |
| VR-038 | Report start date must not be later than report end date. |
| VR-039 | Reports must query saved database records and views. |
| VR-040 | Large reports must be filtered in SQL before results are returned to JavaFX. |
