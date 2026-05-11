# 09 - Acceptance Criteria and Test Scenarios

## Acceptance Criteria

### Authentication and Roles

- Given an active admin account, when the admin logs in with valid credentials, then the admin dashboard is displayed.
- Given an active guard account, when the guard logs in with valid credentials, then the guard dashboard is displayed.
- Given a guard account, when the user attempts to access admin-only functions, then the system denies access.
- Given an inactive account, when the user logs in, then the system rejects the login.

### Student Management

- Admin can register a student with complete required details.
- System rejects duplicate student IDs.
- System rejects missing required student fields.
- Admin can deactivate a student without deleting historical records.
- Guard can submit a pending student record only with accepted proof, and only an admin can make the record official.

### Device Management

- Admin can register a device under an active student.
- System rejects duplicate serial numbers.
- System allows one student to have multiple devices.
- System separates registration status, campus status, device status, and device purpose.
- Inactive and rejected devices cannot enter through normal ingress.

### Pending Registration

- Guard can submit an unregistered device as pending.
- Pending registration appears in the admin approval list.
- Admin can approve pending registration.
- Admin can reject pending registration with reason.
- Guard cannot approve or reject pending registrations.
- Pending devices can be logged for temporary entry while waiting for admin approval.

### Temporary/Event Devices

- User can register event equipment with responsible person, event name, and approval document details.
- Guard can verify paper approval or signed GPOA before accepting temporary/event device entry.
- System rejects event device records without responsible person, event name, approval document details, purpose, or expected exit.
- Temporary/event devices appear in event-specific reports.
- Temporary/event devices are not counted as regular Academic BYOD devices unless explicitly registered as such.

### Ingress and Egress

- Guard can log ingress for an approved active device currently Outside.
- System records ingress timestamp and logged-in user.
- Guard can log egress for a device currently Inside.
- System records egress timestamp and logged-out user.
- System prevents ingress for devices already Inside.
- System prevents egress for devices without active ingress.
- Devices still Inside at 10:00 PM are automatically logged out with a system-generated remark.

### Reports

- Admin can generate all required reports with valid filters.
- Reports show expected columns.
- Reports match saved records.
- Invalid date ranges are rejected.

## QA Test Scenarios

| Test ID | Scenario | Given | When | Then |
| --- | --- | --- | --- | --- |
| TS-001 | Valid admin login | Active admin account exists | Admin enters valid credentials | Admin dashboard opens. |
| TS-002 | Valid guard login | Active guard account exists | Guard enters valid credentials | Guard dashboard opens. |
| TS-003 | Invalid password | User account exists | User enters wrong password | Login is rejected. |
| TS-004 | Inactive login | User account is inactive | User enters valid credentials | Login is rejected with inactive account message. |
| TS-005 | Guard permission denial | Guard is logged in | Guard attempts to open User Management | Access is denied. |
| TS-006 | Add valid student | Admin is logged in | Admin saves complete student form | Student record is saved. |
| TS-007 | Duplicate student ID | Student ID already exists | Admin saves same ID | System rejects duplicate. |
| TS-008 | Missing student fields | Required fields are blank | Admin saves form | System displays required field messages. |
| TS-009 | Pending student submission | Valid student is not encoded but is manually verified with accepted proof | Guard submits pending student/device details with proof type and proof reference/remarks | Pending student/device record is saved for admin review and student `record_status` remains Pending. |
| TS-010 | Register valid device | Active student exists | Admin saves device with unique serial | Device is registered. |
| TS-011 | Duplicate serial number | Serial number exists | Admin saves new device with same serial | System rejects duplicate. |
| TS-012 | Multiple devices per student | Student has existing device | Admin registers another unique device | New device is saved. |
| TS-013 | Submit pending device | Device is unregistered | Guard submits quick registration | Pending record appears in admin queue. |
| TS-014 | Approve pending | Pending record exists | Admin approves it | Registration status becomes Approved. |
| TS-015 | Reject pending | Pending record exists | Admin enters reason and rejects | Registration status becomes Rejected. |
| TS-016 | Guard approval denied | Guard is logged in | Guard attempts approval | System denies access. |
| TS-017 | Pending repeat temporary entry | Pending device is still waiting for admin approval | Guard logs another ingress on a later visit | System allows temporary entry and keeps registration status Pending. |
| TS-018 | Register event device | Event details and approval document details are complete | User saves event device | Device is stored as Temporary/Event Device. |
| TS-019 | Missing event responsible person | Event device form lacks responsible person | User saves form | System rejects record. |
| TS-020 | Missing event approval document | Event device form lacks paper approval or signed GPOA details | Guard saves form | System rejects record. |
| TS-021 | Approved device ingress | Approved active device is Outside | Guard logs ingress | Ingress is saved and campus status becomes Inside. |
| TS-022 | Already inside ingress | Device is already Inside | Guard logs ingress | System rejects duplicate ingress. |
| TS-023 | Inactive device ingress | Device is Inactive | Guard logs ingress | System rejects ingress. |
| TS-024 | Rejected device ingress | Device is Rejected | Guard logs ingress | System rejects ingress. |
| TS-025 | Valid egress | Device is Inside with active log | Guard logs egress | Egress is saved and campus status becomes Outside. |
| TS-026 | Egress without ingress | Device is Outside | Guard logs egress | System rejects egress. |
| TS-027 | Automatic school-closing logout | Device is still Inside at 10:00 PM | System runs automatic logout | Device egress is recorded at 10:00 PM, campus status becomes Outside, and remarks show automatic logout. |
| TS-028 | Daily report | Logs exist for selected day | Admin generates daily report | Report shows matching logs. |
| TS-029 | Monthly report | Logs exist for month | Admin generates monthly report | Summary totals match logs. |
| TS-030 | Event device report | Event device logs exist | Admin generates event report | Event device records and approval document details appear. |
| TS-031 | Invalid report date range | Start date is after end date | Admin generates report | System rejects date range. |
| TS-032 | Missing pending student proof | Valid student is not encoded but proof type or proof reference is blank | Guard submits pending student/device details | System rejects the pending student submission and shows required proof messages. |
