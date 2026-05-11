# 06 - Data Architecture

## Data Model Scope

The architecture uses a relational database accessed through JDBC. The core tables are `students`, `devices`, `device_logs`, and `users`. Recommended support tables are `event_devices` and `audit_logs`.

## Status Separation

| Field | Values |
| --- | --- |
| `registration_status` | Pending, Approved, Rejected |
| `campus_status` | Inside, Outside |
| `device_status` | Active, Inactive |
| `device_purpose` | Academic BYOD, School Event, Organization Activity, Temporary Equipment, Other Approved Purpose |

## Ownership and Keys

| Table | Primary Key | Main Foreign Keys | Owner |
| --- | --- | --- | --- |
| students | student_id | submitted_by, reviewed_by | Admin; Security Guard for pending submission only |
| devices | device_id | student_id, submitted_by, approved_by, rejected_by | Admin, pending submitter |
| event_devices | event_device_id | device_id, approval_document_verified_by | Admin or Security Guard |
| device_logs | log_id | device_id, student_id, logged_in_by, logged_out_by | Monitoring workflow |
| users | user_id | None | Admin |
| audit_logs | audit_id | user_id | System |

## Important Constraints

- `students.student_id` must be unique.
- Pending student records must include proof type, proof reference or remarks, submitted_by, submitted_at, and `record_status = Pending`.
- Only administrators can change a pending student record to Official after review.
- `users.username` must be unique.
- Device serial number or asset tag should be unique where applicable.
- A device must not have more than one open ingress log before school-closing auto logout runs.
- Egress time must not be earlier than ingress time.
- Approved BYOD devices should reference an official active student.
- Pending BYOD devices may reference a pending student record while waiting for admin review.
- Event devices must capture responsible person, event name, and approval document details such as paper approval or signed GPOA.

## Data Integrity Approach

- Use validation in services before writes.
- Use database constraints for unique keys and foreign keys.
- Use transactions for log insertion plus device status update.
- Use a scheduled automatic logout process at 10:00 PM for devices still Inside.
- Use status changes/deactivation instead of permanent deletion.
- Preserve device logs for audit and reporting.

## Index Recommendations

- `students(student_id)`
- `students(last_name, first_name)`
- `devices(serial_number)`
- `devices(asset_tag)`
- `devices(registration_status, campus_status, device_status)`
- `device_logs(device_id, ingress_time)`
- `device_logs(egress_time)`

## Diagram

See `diagrams/mermaid/database-erd.mmd`.
