# 09 - Acceptance Criteria And Test Scenarios

## Acceptance Criteria

### Authentication And Roles

- Given an active `admin` account, when valid credentials are submitted, then the admin dashboard is displayed.
- Given an active `guard` account, when valid credentials are submitted, then the guard dashboard is displayed.
- Given an inactive account, when valid credentials are submitted, then login is rejected.
- Given a guard account, when an admin-only function is requested, then the backend denies access.

### Students

- Admin can create a student with student ID, first name, last name, and status.
- Duplicate student IDs are rejected.
- Missing first name or last name is rejected.
- Students with linked history are deactivated rather than hard-deleted.

### Devices And Pending Approval

- Admin can register a permanent BYOD device under an existing student.
- Duplicate serial numbers are rejected.
- Pending device registrations appear in the approval queue.
- Admin can approve a pending device.
- Admin can reject a pending device only with remarks.
- Guard cannot approve or reject a pending device.
- Pending, rejected, and inactive devices cannot receive gate log records.

### Event Requests

- Users with allowed access can create event request headers and device line items.
- Event document type must be `Paper Approval` or `Signed GPOA`.
- End date cannot be before start date.
- Event request devices appear in event reports.
- The system documents that event line items are not directly linked to `device_logs` in the uploaded schema.

### Gate Monitoring

- Entry for an approved active outside device creates one immutable `device_logs` row.
- Exit for an approved active inside device creates one immutable `device_logs` row.
- Consecutive entry or consecutive exit for the same device is rejected.
- Automatic logout creates system exit rows with `auto_exit = TRUE`, `logout_type = 'automatic'`, and no human handler.
- Campus status displays from derived latest-log state.

### Reports

- Admin can generate all required reports with valid filters.
- Invalid date ranges are rejected.
- Reports match saved database records.

## QA Test Scenarios

| Test ID | Scenario | Given | When | Then |
| --- | --- | --- | --- | --- |
| TS-001 | Valid admin login | Active user has role `admin` | User submits valid credentials | Admin dashboard opens and audit login event is recorded. |
| TS-002 | Valid guard login | Active user has role `guard` | User submits valid credentials | Guard dashboard opens and audit login event is recorded. |
| TS-003 | Invalid password | User exists | Wrong password is submitted | Login is rejected and `USER_LOGIN_FAILED` is audited. |
| TS-004 | Inactive account | User status is `inactive` | Valid credentials are submitted | Login is rejected. |
| TS-005 | Guard permission denial | Guard is authenticated | Guard requests User Management | Backend denies access. |
| TS-006 | Add valid student | Admin is authenticated | Admin saves required student fields | Student row is inserted. |
| TS-007 | Duplicate student ID | Student ID exists | Admin saves same ID | Save is rejected. |
| TS-008 | Missing student names | Required name is blank | Admin saves form | Save is rejected. |
| TS-009 | Register valid device | Student exists | Admin saves device with unique serial number | Device row is inserted. |
| TS-010 | Duplicate serial number | Serial number exists | User saves another device with same serial number | Save is rejected. |
| TS-011 | Submit pending device | Device is not registered | Guard saves quick pending registration | Device row is created with `registration_status = 'pending'`. |
| TS-012 | Pending appears in queue | Pending device exists | Admin opens pending approval | Device appears from `v_pending_devices`. |
| TS-013 | Approve pending | Pending device exists | Admin approves | Device status becomes `approved` with reviewer fields. |
| TS-014 | Reject pending | Pending device exists | Admin enters remarks and rejects | Device status becomes `rejected`. |
| TS-015 | Reject without remarks | Pending device exists | Admin rejects without remarks | Database/application rejects the action. |
| TS-016 | Guard approval denied | Guard is authenticated | Guard attempts approval endpoint/action | Access is denied. |
| TS-017 | Pending device entry blocked | Device registration status is `pending` | Guard logs entry | Database trigger blocks the log. |
| TS-018 | Register event request | Valid event header and line item exist | User saves event request | Request and line item rows are inserted. |
| TS-019 | Invalid event document type | Event request has unsupported document type | User saves request | Save is rejected. |
| TS-020 | Invalid event date range | End date is before start date | User saves request | Save is rejected. |
| TS-021 | Approved device entry | Approved active device latest state is outside | Guard logs entry | `device_logs` entry row is inserted. |
| TS-022 | Duplicate entry blocked | Device latest event is entry | Guard logs another entry | Trigger rejects consecutive event. |
| TS-023 | Inactive device entry blocked | Device status is `inactive` | Guard logs entry | Trigger rejects log. |
| TS-024 | Rejected device entry blocked | Registration status is `rejected` | Guard logs entry | Trigger rejects log. |
| TS-025 | Valid exit | Device latest event is entry | Guard logs exit | `device_logs` exit row is inserted. |
| TS-026 | Exit without entry blocked | Device latest state is outside | Guard logs exit | Trigger rejects consecutive/invalid exit. |
| TS-027 | Automatic logout | Device latest event is entry at school closing | Scheduler runs | Automatic exit row is inserted with no human handler. |
| TS-028 | Immutable device log | Log row exists | User attempts update/delete | Database rejects the change. |
| TS-029 | Immutable audit log | Audit row exists | User attempts update/delete | Database rejects the change. |
| TS-030 | Daily report | Logs exist for selected day | Admin generates report | Report shows matching events. |
| TS-031 | Event report | Event requests exist | Admin generates event report | Request and line-item data appear. |
| TS-032 | Invalid report range | Start date is after end date | Admin generates report | Request is rejected. |
