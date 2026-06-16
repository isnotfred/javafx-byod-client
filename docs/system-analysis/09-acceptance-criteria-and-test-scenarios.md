# 09 - Acceptance Criteria And Test Scenarios

## Acceptance Criteria

### Authentication And Roles

- Given an active super_admin account, when valid credentials are submitted, then the Super Admin dashboard is displayed.

- Given an active admin account, when valid credentials are submitted, then the Admin dashboard is displayed.

- Given an active guard account, when valid credentials are submitted, then the Guard dashboard is displayed.

- Given an inactive account (status = 'inactive'), when valid credentials are submitted, then login is rejected.

- Given a pending account (status = 'pending'), when valid credentials are submitted, then login is rejected.

- Given a guard or admin account, when a Super Admin-only function is requested, then the backend denies access.

### Profile Management

- Any authenticated user can update their own username (min 3 characters, must be unique).

- Any authenticated user can update their own password after providing the correct current password.

- New password and confirm password must match; mismatch is rejected.

### Students

- Admin can create a student with student ID, first name, last name, and status.

- Duplicate student IDs are rejected.

- Missing first name or last name is rejected.

- Students with linked history are deactivated rather than hard-deleted.

### Devices And Pending Approval

- Admin can register a permanent BYOD device under an existing student using one of the five device categories.

- Permanent-device serial numbers are required and duplicates are rejected for every category.

- The system enforces the max_devices_per_student limit from system_settings.

- Pending device registrations appear in the approval queue (v_pending_devices).

- Admin can approve a pending device.

- Admin can reject a pending device only when remarks are provided.

- Guard cannot approve or reject a pending device.

- Active pending devices can be checked in when allow_unregistered_devices is true; rejected and inactive devices cannot.

- devices.image_path remains optional; no upload behavior is asserted by this specification.

### Event Requests

- An admin or other authorized submitter can create event request headers and manifest rows with quantity > 0.

- Event document type must be Paper Approval or Signed GPOA.

- End date cannot be before start date.

- Normal API submission auto-approves the request and cascades approval to manifest rows.

- Manually queued requests can be approved, returned with remarks, or rejected with remarks by Admin.

- Returned requests can be corrected and resubmitted to pending.

- Event duration cannot exceed event_request_max_duration_days.

- Event-device entry/exit creates immutable event_device_logs rows and duplicate consecutive scans are rejected.

- Guard or Admin reconciliation persists device_status = 'returned'.

### Gate Monitoring

- Entry for an approved active outside device creates one immutable device_logs row.

- Exit for an approved active inside device creates one immutable device_logs row.

- Consecutive entry or consecutive exit for the same device is rejected by trigger.

- Automatic logout creates system exit rows with auto_exit = TRUE, logout_type = 'automatic', and no human handler.

- Campus status displays from derived latest-log state (v_device_campus_status).

- Students without a device pass through the gate freely; no system entry is created.

### System Configuration

- Super Admin can view all system_settings key-value pairs.

- Super Admin can update a setting value; SYSTEM_CONFIG_UPDATED audit is recorded.

- An empty or blank value is rejected.

- Admin and guard accounts cannot access the System Configuration screen.

### Reports

- Admin can generate all seven required reports with valid filters.

- Invalid date ranges are rejected.

- Reports match saved database records.

- Export or print action is available on each report screen.

## QA Test Scenarios

| Test ID | Scenario | Given | When | Then |
| --- | --- | --- | --- | --- |
| TS-001 | Valid super_admin login | Active user has role super_admin | Valid credentials submitted | Super Admin dashboard opens; USER_LOGIN audit recorded. |
| TS-002 | Valid admin login | Active user has role admin | Valid credentials submitted | Admin dashboard opens; USER_LOGIN audit recorded. |
| TS-003 | Valid guard login | Active user has role guard | Valid credentials submitted | Guard dashboard opens; USER_LOGIN audit recorded. |
| TS-004 | Invalid password | User exists | Wrong password submitted | Login rejected; USER_LOGIN_FAILED audited. |
| TS-005 | Inactive account | users.status = 'inactive' | Valid credentials submitted | Login rejected. |
| TS-006 | Pending account | users.status = 'pending' | Valid credentials submitted | Login rejected. |
| TS-007 | Guard permission denial | Guard is authenticated | Guard requests User Management | Backend denies access. |
| TS-008 | Admin permission denial | Admin is authenticated | Admin requests System Configuration | Backend denies access. |
| TS-009 | Profile: update username | Any authenticated user | Submits new unique username >= 3 chars | Username updated. |
| TS-010 | Profile: duplicate username | New username already exists | User submits | Rejected with uniqueness error. |
| TS-011 | Profile: update password | Any authenticated user | Provides correct current password + matching new/confirm | Password hash updated. |
| TS-012 | Profile: wrong current password | Any authenticated user | Provides wrong current password | Rejected. |
| TS-013 | Profile: password mismatch | Any authenticated user | New password != confirm password | Rejected. |
| TS-014 | Add valid student | Admin is authenticated | Admin saves required student fields | Student row inserted; STUDENT_CREATED audited. |
| TS-015 | Duplicate student ID | Student ID exists | Admin saves same ID | Rejected. |
| TS-016 | Missing student names | Required name is blank | Admin saves form | Rejected. |
| TS-017 | Register valid device (Personal Computers) | Student exists | Admin saves device with unique serial number, category = Personal Computers | Device inserted; DEVICE_REGISTERED audited. |
| TS-018 | Register Project Prototype without SN | Student exists | Admin saves device with category = Project Prototypes (Optional SN), no serial | Rejected because devices.serial_number is NOT NULL. |
| TS-019 | Duplicate serial number | Serial number exists | User saves another device with same serial | Rejected. |
| TS-020 | Max devices exceeded | Student already at max_devices_per_student | Admin registers another device | Rejected with policy violation message. |
| TS-021 | Submit pending device | Device not registered | Guard saves quick pending registration | Device inserted with registration_status = 'pending'. |
| TS-022 | Unapproved check-in disabled | Active pending device exists; allow_unregistered_devices = false | Guard attempts gate entry | Backend rejects the check-in. |
| TS-023 | Pending appears in queue | Pending device exists | Admin opens pending approval | Device appears from v_pending_devices. |
| TS-024 | Approve pending | Pending device exists | Admin approves | registration_status = 'approved'; reviewed_by and reviewed_at set; DEVICE_APPROVED audited. |
| TS-025 | Reject pending with remarks | Pending device exists | Admin enters remarks and rejects | registration_status = 'rejected'; DEVICE_REJECTED audited. |
| TS-026 | Reject pending without remarks | Pending device exists | Admin rejects without remarks | Rejected. |
| TS-027 | Guard approval denied | Guard is authenticated | Guard attempts approval action | Access denied. |
| TS-028 | Pending device entry allowed | Active pending device; allow_unregistered_devices = true | Guard logs entry | device_logs entry row is inserted. |
| TS-029 | Register event request | Valid event header and positive-quantity line item | Admin or authorized submitter submits | Request and line items are inserted and auto-approved. |
| TS-030 | Invalid event document type | Unsupported document type | User saves | Rejected. |
| TS-031 | Invalid event date range | End date before start date | User saves | Rejected. |
| TS-032 | Approved device entry | Approved active device last state is outside | Guard logs entry | device_logs entry row inserted; DEVICE_ENTRY audited. |
| TS-033 | Duplicate entry blocked | Device latest event is entry | Guard logs another entry | Trigger rejects. |
| TS-034 | Inactive device entry blocked | device_status = 'inactive' | Guard logs entry | Trigger rejects. |
| TS-035 | Rejected device entry blocked | registration_status = 'rejected' | Guard logs entry | Trigger rejects. |
| TS-036 | Valid exit | Device latest event is entry | Guard logs exit | device_logs exit row inserted; DEVICE_EXIT audited. |
| TS-037 | Duplicate exit blocked | Device latest state is outside | Guard logs exit | Trigger rejects. |
| TS-038 | Automatic logout | Device latest event is entry at school closing | Scheduler runs | Automatic exit row inserted; auto_exit = TRUE; handled_by = NULL; SYSTEM_AUTO_EXIT_BATCH audited. |
| TS-039 | Deviceless gate passage | Student has no device | Student passes gate | No device_logs row created; no system interaction. |
| TS-040 | Immutable device log | Log row exists | User attempts delete | Database rejects; direct UPDATE remains a documented schema gap. |
| TS-041 | Immutable audit log | Audit row exists | User attempts update/delete | Database rejects. |
| TS-042 | System config update | Super Admin is authenticated | Selects setting, changes value, saves | system_settings updated; SYSTEM_CONFIG_UPDATED audited. |
| TS-043 | System config empty value | Super Admin selects setting | Submits blank value | Rejected. |
| TS-044 | Admin denied system config | Admin is authenticated | Tries to access system configuration | Backend denies access. |
| TS-045 | Super Admin onboard user | Super Admin is authenticated | Enters full name, email, role; saves | User created with status = 'pending'; ADMIN_CREATED or GUARD_CREATED audited. |
| TS-046 | Super Admin change role | Super Admin selects active user | Changes role | Role updated; USER_ROLE_CHANGED audited. |
| TS-047 | Super Admin deactivate user | Super Admin selects active user | Deactivates | status = 'inactive'; ADMIN_DEACTIVATED or GUARD_DEACTIVATED_BY_SUPER audited. |
| TS-048 | Daily report | Logs exist for selected day | Admin generates report | Matching events displayed with export option. |
| TS-049 | Device frequency report | Logs exist | Admin generates report | Devices sorted by entry count descending. |
| TS-050 | Incident/override report | Audit logs have override actions | Admin generates report | Relevant audit actions displayed. |
| TS-051 | Invalid report date range | Start date after end date | Admin generates report | Rejected. |
| TS-052 | Event auto-approval cascade | Valid normal API submission has multiple manifest rows | POST /api/v1/event-requests succeeds | Request status and all device statuses are approved. |
| TS-053 | Manual event approval | Request is manually queued as pending | Admin approves | Request and child devices become approved. |
| TS-054 | Return requires remarks | Pending request exists | Admin returns without remarks | Request is rejected by validation. |
| TS-055 | Return and resubmit | Returned request has remarks | Creator corrects and resubmits | Request status becomes pending for Admin review. |
| TS-056 | Reject event request | Pending request exists | Admin rejects with remarks | Request becomes rejected. |
| TS-057 | Event duration limit | event_request_max_duration_days = 7 | User submits an 8-day request | Backend rejects submission. |
| TS-058 | Event review role denial | Guard is authenticated | Guard calls approve/return/reject endpoint | Access denied. |
| TS-059 | Event device entry and exit | Approved request is within active dates | Guard logs entry then exit | Two event_device_logs rows inserted; v_event_device_status shows exit. |
| TS-060 | Duplicate event scan blocked | Latest event-device event is entry | Guard logs another entry | trg_event_device_logs_consecutive_events rejects it. |
| TS-061 | Multi-day event re-entry | Device exited on prior day and event remains active | Guard logs entry next day | Entry succeeds and latest status is entry. |
| TS-062 | Manual event reconciliation | Event device needs manual closeout | Guard or Admin verifies/reconciles | Manifest device_status becomes returned. |
| TS-063 | Event reconciliation report | Event devices have mixed current/manifest states | Admin generates report | Outstanding inside or unreconciled devices are listed. |
| TS-064 | Grouped event quantity | User enters quantity greater than 1 | User submits a valid request | Submission is accepted because the final schema permits any quantity > 0. |
| TS-065 | Invalid event quantity | User enters quantity 0 or less | User submits request | Database or backend validation rejects the manifest row. |
