# 05 - Business Rules And Validation Rules

## Business Rules

| ID | Business Rule | Notes |
| --- | --- | --- |
| BR-001 | Only users with status = 'active' and a valid role (super_admin, admin, guard) can log in. | Students are indirect actors only. Pending accounts cannot log in. |
| BR-002 | Inactive and pending user accounts cannot access any protected function. | Enforced at authentication before dashboard access. |
| BR-003 | Student records are deactivated instead of deleted when linked devices or logs exist. | trg_protect_student_delete blocks unsafe deletes. |
| BR-004 | Permanent BYOD devices must belong to exactly one student. | devices.student_id references students. |
| BR-005 | Device serial numbers are globally unique for permanent BYOD records. | Enforced by unique constraint on devices.serial_number. |
| BR-006 | Active pending devices may be checked in when allow_unregistered_devices permits it. | The final trigger accepts approved or pending devices and rejects inactive devices; the setting must be enforced by the service. |
| BR-007 | Rejected devices require remarks and cannot be logged through the gate workflow. | Enforced by check constraint and log trigger. |
| BR-008 | Campus presence is derived from the latest gate log row. | No stored column on devices is updated for presence; use v_device_campus_status. |
| BR-009 | Permanent and event-device entry/exit histories are append-only. | event_device_logs blocks update/delete; device_logs blocks delete and requires missing update protection to be closed. |
| BR-010 | Two consecutive manual events of the same type for one device are invalid. | Prevents duplicate entry or duplicate exit rows; enforced by trigger. |
| BR-011 | Automatic logout is system-generated and has no human handler. | auto_exit = TRUE, handled_by = NULL, logout_type = 'automatic'. |
| BR-012 | Each event manifest row has one entry/exit and reconciliation identity regardless of its positive quantity. | quantity defaults to 1 and must be greater than zero; event_device_logs references the manifest row. |
| BR-013 | Audit logs are immutable and must be written through fn_write_audit_log(). | Direct INSERT into audit_logs from application code is not permitted. |
| BR-014 | A student may have no more than the configured maximum number of active registered devices. | Controlled by system_settings.max_devices_per_student (default 5); enforced by backend service. |
| BR-015 | Guard check-in of unapproved active devices is controlled by allow_unregistered_devices. | Default true; enforcement belongs in the backend because the database gate trigger itself accepts pending devices. |
| BR-016 | Students arriving at the gate without a device pass through freely. | No log, tag, or system entry is created for deviceless gate passage. |
| BR-017 | Permanent devices require a globally unique serial number; devices.image_path remains optional. | The final schema does not define an image upload or storage workflow. |
| BR-018 | Only Super Admin can create, update the role of, or deactivate user accounts. | Admin and guard roles cannot manage user accounts. |
| BR-019 | Role changes are audited with USER_ROLE_CHANGED. | Super Admin-initiated role change is an immutable audit event. |
| BR-020 | System configuration changes are restricted to Super Admin and audited with SYSTEM_CONFIG_UPDATED. | Admin and guard cannot modify system settings. |
| BR-021 | User records are deactivated instead of deleted when audit history exists. | trg_protect_user_delete blocks unsafe user deletes. |
| BR-022 | Device records are deactivated instead of deleted when gate log history exists. | trg_protect_device_delete blocks unsafe device deletes. |

## Validation Rules

### Authentication And Users

| ID | Validation Rule |
| --- | --- |
| VR-001 | users.username is required, unique, and at least 3 characters. |
| VR-002 | users.password_hash is required and must be at least 20 characters (prevents plaintext storage). |
| VR-003 | users.role must be super_admin, admin, or guard. |
| VR-004 | users.status must be active, inactive, or pending. |
| VR-005 | Only status = 'active' accounts can log in. |
| VR-006 | users.email is required on onboarding. |
| VR-007 | Profile password update requires current password verification before accepting a new password. |
| VR-008 | New password and confirmation password must match before saving. |

### Students

| ID | Validation Rule |
| --- | --- |
| VR-009 | students.student_id is required, non-blank, and unique. |
| VR-010 | students.first_name and students.last_name are required and non-blank. |
| VR-011 | students.status must be active or inactive. |

### Devices

| ID | Validation Rule |
| --- | --- |
| VR-012 | devices.student_id is required and must reference an existing student. |
| VR-013 | devices.serial_number is required and globally unique for every permanent device, including Project Prototypes (Optional SN), because the final column is NOT NULL UNIQUE. |
| VR-014 | devices.device_type must be one of: Personal Computers, Components & Peripherals, Display & Projection, Project Prototypes (Optional SN), Appliances (TLE). |
| VR-015 | devices.device_purpose must be one of: Academic BYOD, School Event, Organization Activity, Temporary Equipment, Other Approved Purpose, PROTOTYPE, APPLIANCE. |
| VR-016 | devices.registration_status must be pending, approved, or rejected. |
| VR-017 | devices.device_status must be active or inactive. |
| VR-018 | reviewed_by and reviewed_at must both be null or both be populated. |
| VR-019 | Rejected devices must have non-blank remarks. |
| VR-020 | Direct approved to rejected and rejected to approved transitions are blocked by trigger. |
| VR-021 | The total count of active registered devices for a student must not exceed system_settings.max_devices_per_student at the time of registration. |

### Event Requests

| ID | Validation Rule |
| --- | --- |
| VR-022 | event_requests.student_id must reference an existing student. |
| VR-023 | event_requests.event_name is required and non-blank. |
| VR-024 | event_requests.approval_doc_type must be Paper Approval or Signed GPOA. |
| VR-025 | event_requests.end_date must be on or after start_date when both are provided. |
| VR-026 | event_requests.status must be pending, approved, returned, or rejected. |
| VR-027 | Event review fields must follow the same reviewer/timestamp consistency rule as device reviews. |
| VR-028 | event_request_devices.quantity is required, defaults to 1, and must be greater than zero. |
| VR-029 | event_request_devices.device_type must be one of: Personal Computers, Components & Peripherals, Display & Projection, Project Prototypes (Optional SN), Appliances (TLE), Other. |
| VR-030 | event_request_devices.device_status must be pending, approved, or returned; manual reconciliation persists returned. |

### Gate Logs

| ID | Validation Rule |
| --- | --- |
| VR-031 | device_logs.event_type must be entry or exit. |
| VR-032 | Manual gate rows must have handled_by populated with the current user ID. |
| VR-033 | Automatic exit rows must have handled_by = NULL, auto_exit = TRUE, and event_type = 'exit'. |
| VR-034 | logout_type must be manual, automatic, or null where the schema permits. |
| VR-035 | Permanent gate logs can only be inserted for active devices whose registration_status is approved or policy-eligible pending; rejected devices are blocked. |
| VR-036 | Consecutive same-type manual events for one device are blocked by trigger. |
| VR-037 | device_logs.created_at is overwritten by the database trigger on insert and cannot be backdated. |

### System Configuration

| ID | Validation Rule |
| --- | --- |
| VR-038 | system_settings.setting_value must not be empty or blank; event_request_max_duration_days must be a positive integer and event submissions must not exceed it. |
| VR-039 | max_devices_per_student must be a positive integer, and auto_exit_cutoff_time must use a valid 24-hour time value. |
| VR-040 | allow_unregistered_devices must be true or false; event_device_logs.event_type must be entry or exit and consecutive same-type events are rejected. |

### Audit

| ID | Validation Rule |
| --- | --- |
| VR-041 | audit_logs.action_type must be one of the approved audit action values defined in AuditActionType. |
| VR-042 | audit_logs.target_table is required and non-blank. |
| VR-043 | audit_logs.ip_address must be null or between 7 and 45 characters. |
| VR-044 | Audit rows cannot be updated or deleted. |

### Reports

| ID | Validation Rule |
| --- | --- |
| VR-045 | Report start date must not be later than report end date. |
| VR-046 | Reports must query saved database records and views, not unsaved UI state. |
| VR-047 | Large reports must be filtered in SQL before results are returned to the JavaFX frontend. |
