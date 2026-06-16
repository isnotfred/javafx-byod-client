# 07 - Screen Requirements

## Revision Note

This screen baseline follows the dev branch FXML and Java controller implementations, the V4 Business System Analysis, and the updated PostgreSQL schema:

- Three roles: super_admin, admin, guard.

- Campus status is derived from latest device_logs or v_device_campus_status.

- Pending device registrations are devices rows with registration_status = 'pending'.

- Active pending devices may be checked in when allow_unregistered_devices is true.

- Temporary/event access uses event_requests, event_request_devices, event_device_logs, v_active_event_requests, and v_event_device_status.

- device_logs contains immutable entry and exit rows.

- Device categories: Personal Computers, Components & Peripherals, Display & Projection, Project Prototypes (Optional SN), Appliances (TLE).

- The final schema includes optional devices.image_path, but the authoritative sources do not define an image upload UI.

- Students without devices pass the gate without any system interaction.

## Login Screen

| Item | Requirement |
| --- | --- |
| Purpose | Authenticate users before access. |
| Access | Unauthenticated users. |
| Fields | Username, password. |
| Actions | Login, Forgot Password link. |
| Validation / Error Messages | Invalid username or password; account is inactive; account is pending activation; backend unavailable; database connection failed. |
| Backend/API Notes | Calls POST /auth/login; backend reads users.username, users.password_hash, users.role, and users.status; failed attempts write USER_LOGIN_FAILED. |

## Forgot Password Screen

| Item | Requirement |
| --- | --- |
| Purpose | Initiate a password reset for a locked or forgotten account. |
| Access | Unauthenticated users (accessible from Login screen). |
| Fields | Username or email. |
| Actions | Submit reset request, back to login. |
| Validation / Error Messages | Account not found; backend unavailable. |

## Reset Password Screen

| Item | Requirement |
| --- | --- |
| Purpose | Complete password reset via a valid reset token or flow. |
| Access | Unauthenticated users with a valid reset token. |
| Fields | New password, confirm password. |
| Actions | Set new password, cancel. |
| Validation / Error Messages | Passwords do not match; token invalid or expired; password too short. |

## Super Admin Dashboard

| Item | Requirement |
| --- | --- |
| Purpose | Provide full system control navigation and summary for Super Admin. |
| Access | super_admin. |
| Display | Welcome message with full name. Default content area loads SuperAdminSummaryDashboard. |
| Actions | User Management, System Configuration, Profile, Logout. |
| Data Notes | Summary data sourced from users table counts by role and status. |

## Super Admin Summary Dashboard

| Item | Requirement |
| --- | --- |
| Purpose | At-a-glance system overview for Super Admin. |
| Access | super_admin. |
| Display | Total users by role; active vs. inactive/pending counts; system settings summary. |

## Admin Dashboard

| Item | Requirement |
| --- | --- |
| Purpose | Provide administrative summary and navigation. |
| Access | admin. |
| Display | Active students; registered devices; pending devices; devices currently inside; today's entries; today's exits; today's automatic exits. |
| Actions | Students, Devices, Pending Approvals, Event Requests, Logs, Reports, Profile, Logout. |
| Data Notes | Counts query students, devices, device_logs, and v_device_campus_status. |

## Guard Dashboard

| Item | Requirement |
| --- | --- |
| Purpose | Support fast gate monitoring. |
| Access | guard. |
| Fields | Search text for student ID, student name, or serial number. |
| Actions | Search, Log Entry, Log Exit, Quick Pending Registration, Event Request, Active Devices, Clear, Profile, Logout. |
| Display | Student name, course/year level, device category, brand, model, serial number, purpose, registration status, derived campus status, device status, and warning banner. |
| Validation / Error Messages | Device not found; device already inside; device not currently inside; device inactive; pending device disallowed by policy; registration rejected. |
| Data Notes | Search queries students, devices, and v_device_campus_status; entry/exit writes device_logs. Active pending devices may be checked in when allow_unregistered_devices is true. Students without devices do not require any system interaction. |

## Student Management Screen

| Item | Requirement |
| --- | --- |
| Purpose | Manage student records. |
| Access | admin. |
| Fields | Student ID, first name, last name, course/year level, status. |
| Actions | Add, Update, Search, Clear, Deactivate. |
| Table Columns | Student ID, last name, first name, course/year level, status, created at. |
| Search / Filters | Student ID, name, course/year level, status. |
| Validation / Error Messages | Student ID required; duplicate student ID; first name required; last name required; invalid status. |
| Data Notes | Maps to students; deactivation sets status = 'inactive'; unsafe hard deletion is blocked by trigger. |

## Device Management Screen

| Item | Requirement |
| --- | --- |
| Purpose | Manage permanent BYOD device records. |
| Access | admin. |
| Fields | Student owner, device name, brand, model, serial number, device category, purpose, registration status, device status, remarks. |
| Actions | Register, Update, Approve, Reject, Activate, Deactivate, Search, Clear. |
| Table Columns | Device ID, student ID, student name, device name, brand/model, serial number, category, purpose, registration status, derived campus status, device status. |
| Search / Filters | Student ID/name, serial number, category, purpose, registration status, device status, derived campus status. |
| Validation / Error Messages | Serial number required; duplicate serial number; owner required; invalid category; invalid purpose; rejection remarks required; invalid registration transition; max devices per student exceeded. |
| Data Notes | Maps to devices; approval/rejection sets reviewed_by and reviewed_at; derived campus status is read-only; devices.image_path is optional but its UI workflow is not specified. |

## Quick Pending Registration Screen

| Item | Requirement |
| --- | --- |
| Purpose | Let guards submit a pending device registration at the gate using a required globally unique serial number. |
| Access | guard. |
| Fields | Student ID, first name, last name, course/year level, proof remarks, device name, brand, model, serial number, device category, purpose, remarks. |
| Actions | Save Pending Registration, Clear, Cancel. |
| Post-save Display | New device_id, submitted by current guard, submitted timestamp, and registration_status = 'pending'. |
| Validation / Error Messages | Required student details; required serial number; duplicate serial number; invalid category or purpose. |
| Data Notes | Inserts or links students; inserts devices with registration_status = 'pending'; proof details may be stored in devices.remarks; active pending devices may be checked in while allow_unregistered_devices is true. |

## Pending Registration Approval Screen

| Item | Requirement |
| --- | --- |
| Purpose | Let admins review pending device registrations. |
| Access | admin. |
| Fields | Student details, proof/remarks, device details, submitted timestamp, approval remarks or rejection reason. |
| Actions | View Details, Approve, Reject, Refresh. |
| Table Columns | Device ID, student ID, student name, course/year level, device name, brand/model, serial number, category, purpose, submitted at, registration status. |
| Search / Filters | Date submitted, student ID/name, serial number. |
| Validation / Error Messages | Rejection reason required; duplicate serial conflict; missing owner. |
| Data Notes | Uses v_pending_devices; approve sets registration_status = 'approved'; reject sets registration_status = 'rejected' and required remarks; writes DEVICE_APPROVED or DEVICE_REJECTED. |

## Event Request Screen

| Item | Requirement |
| --- | --- |
| Purpose | Register and manage temporary/event access requests and device line items. |
| Access | admin; another role only when authorized as a submitter; guard for scanning and device verification. |
| Header Fields | Responsible student, responsible person, organization, event name, event purpose, approval document type, approval document reference, start date, end date, remarks. |
| Device Fields | Device name, brand, model, device category, serial number, positive quantity (default 1), remarks. |
| Actions | Save/Submit Event Request, Save Local Draft, Add Device, Approve Request, Return for Revision, Reject Request, Resubmit Returned Request, Search, Clear. |
| Request Table Columns | Request ID, student name, event name, organization, start date, end date, status, device count. |
| Device Table Columns | Event device ID, device name, brand/model, category, serial number, quantity, device status, verified by, verified at. |
| Search / Filters | Event name, organization, responsible student, date range, request status. |
| Validation / Error Messages | Responsible student required; event name required; invalid document type; document reference required; invalid or over-limit date range; at least one line item; quantity must be greater than zero; return/rejection remarks required. |
| Data Notes | Normal POST submission auto-approves the header and child devices. Manually queued and returned requests use Admin review. Local drafts are session-only and are not database records. Active queue reads v_active_event_requests. |

## Temporary Event Device Guard Panel

| Item | Requirement |
| --- | --- |
| Purpose | Let guards find active event requests, inspect manifest devices, and record temporary-device entry or exit. |
| Access | guard for lookup and entry/exit; admin and guard for manifest lookup and reconciliation. |
| Search / Filters | Student ID, event name, active date range, request status. |
| Display | Request details, approval document reference, event dates, responsible person, manifest rows, manifest status, current-day status, latest event time. |
| Actions | Guard: Search, Select Manifest Devices, Confirm Entry, Confirm Exit, Verify/Reconcile, Refresh. Admin: Search, View Manifest, Verify/Reconcile, Refresh. |
| Validation / Error Messages | Request not approved; event not active; device already inside; device already outside; backend unavailable. |
| Data Notes | Reads GET /api/v1/event-requests/guard, manifest endpoint, and v_event_device_status. Entry/exit creates event_device_logs rows. Verify/Reconcile sets manifest device_status = 'returned'. |

## Event Request API Reference

| Action | Method And Endpoint | Roles |
| --- | --- | --- |
| Create request | POST /api/v1/event-requests | admin (or submitter) |
| Load guard grid | GET /api/v1/event-requests/guard | admin, guard |
| Load manifest | GET /api/v1/event-requests/{eventRequestId}/devices | admin, guard |
| Approve request | PUT /api/v1/event-requests/{eventRequestId}/approve | admin |
| Return for revision | PUT /api/v1/event-requests/{eventRequestId}/return | admin |
| Reject request | PUT /api/v1/event-requests/{eventRequestId}/reject | admin |
| Log event-device entry | POST /api/v1/event-requests/devices/log-entry | guard |
| Log event-device exit | POST /api/v1/event-requests/devices/log-exit | guard |
| Verify/reconcile device | PUT /api/v1/event-requests/devices/{eventDeviceId}/verify | admin, guard |
| Generate reconciliation report | GET /api/v1/event-requests/devices/reconciliation-report | admin, super_admin |

## Ingress / Egress Monitoring Screen

| Item | Requirement |
| --- | --- |
| Purpose | Log device entry and exit transactions. |
| Access | admin, guard. |
| Fields | Search text, notes. |
| Actions | Search, Log Entry, Log Exit, Quick Pending Registration, Clear. |
| Display | Student, device details, registration status, derived campus status, device status, latest entry time, latest exit time. |
| Table Columns | Log ID, device ID, student name, device category, serial number, registration status, derived campus status, device status, latest event time. |
| Validation / Error Messages | Device not found; already inside; no active entry; device inactive; registration rejected; pending device disallowed by policy. |
| Deviceless Gate Passage | Students arriving without a device pass through freely. No entry is created in the system. |
| Data Notes | Entry inserts event_type = 'entry'; manual exit inserts event_type = 'exit', logout_type = 'manual'; handled_by stores current user; triggers enforce eligibility and event alternation. |

## Active Devices Inside Campus Screen (Guard)

| Item | Requirement |
| --- | --- |
| Purpose | Show devices whose latest event is entry, accessible to guards. |
| Access | guard. |
| Actions | Refresh, Search, Filter, View Details, Log Exit. |
| Table Columns | Device ID, student name, device category, serial number, purpose, entry time, handled by. |
| Search / Filters | Student name, device category, purpose, entry date, guard user. |
| Validation / Error Messages | No active devices found; backend unavailable. |
| Data Notes | Uses v_device_campus_status filtered to campus_status = 'inside'. |

## Active Devices Inside Campus Screen (Admin)

| Item | Requirement |
| --- | --- |
| Purpose | Show devices currently inside campus with automatic exit results, accessible to admins. |
| Access | admin. |
| Actions | Refresh, Search, Filter, View Details, Log Exit. |
| Table Columns | Device ID, student name, device category, serial number, purpose, entry time, handled by, automatic exit flag where applicable. |
| Search / Filters | Student name, device category, purpose, entry date, guard user, overdue. |
| Validation / Error Messages | No active devices found; backend unavailable. |
| Data Notes | Uses v_device_campus_status filtered to campus_status = 'inside' or equivalent latest-log query; automatic exit rows have auto_exit = TRUE, logout_type = 'automatic', and handled_by = NULL. |

## Logs Screen

| Item | Requirement |
| --- | --- |
| Purpose | Browse gate logs and audit history where permitted. |
| Access | admin; guard access limited to operational gate logs if allowed. |
| Fields | Date range, action/event type, student, device, user. |
| Actions | Search, Clear Filters, View Detail. |
| Data Notes | device_logs and audit_logs are read-only from the UI. |

## Reports Screen

| Item | Requirement |
| --- | --- |
| Purpose | Generate administrative monitoring reports. |
| Access | admin. |
| Report Types | Daily Device Traffic Summary, Monthly Device Traffic Summary, Pending Registration Report, Active Devices on Campus, Device Frequency Report, Incident/Override Report, Event Device Reconciliation Report. |
| Fields | Report type, date range, student, device category, purpose, registration status, device status, user. |
| Actions | Generate, Clear Filters, Print/Export. |
| Validation / Error Messages | Invalid date range; no records found; backend unavailable. |
| Data Notes | Reads students, devices, device_logs, event_requests, event_request_devices, event_device_logs, audit_logs, and schema views. |

## User Management Screen

| Item | Requirement |
| --- | --- |
| Purpose | Manage all system user accounts. |
| Access | super_admin. |
| Fields | Full name, username (display only), email, role, status. |
| Actions | Onboard New User, Update, Change Role, Deactivate, Refresh, Clear. |
| Table Columns | User ID, username, email, full name, role, status, created at. |
| Search / Filters | Username, email, role, status. |
| Validation / Error Messages | Full name required; email required; role required; duplicate username; cannot deactivate own account. |
| Data Notes | Onboarding sets status = 'pending'; deactivation sets status = 'inactive'; role changes write USER_ROLE_CHANGED; creation writes ADMIN_CREATED or GUARD_CREATED; update writes ADMIN_UPDATED or GUARD_UPDATED; deactivation writes ADMIN_DEACTIVATED or GUARD_DEACTIVATED_BY_SUPER. |

## System Configuration Screen

| Item | Requirement |
| --- | --- |
| Purpose | View and update configurable system policy settings. |
| Access | super_admin. |
| Display | Table of all system_settings rows (key, value, description). |
| Fields | Selected setting value (editable). |
| Actions | Select setting, Edit value, Save, Refresh. |
| Configurable Settings | max_devices_per_student (default 5); allow_unregistered_devices (default true); event_request_max_duration_days (default 7); auto_exit_cutoff_time (default 22:00). |
| Validation / Error Messages | No setting selected; value cannot be empty; invalid value format. |
| Data Notes | Reads and writes system_settings; SYSTEM_CONFIG_UPDATED audit is recorded on every save. |

## Profile Screen

| Item | Requirement |
| --- | --- |
| Purpose | Allow any authenticated user to update their own username and password. |
| Access | super_admin, admin, guard. |
| Fields | Username (editable), current password, new password, confirm new password. Displays role and full name as read-only labels. |
| Actions | Update Username, Update Password. |
| Validation / Error Messages | Username required; username min 3 characters; duplicate username; current password incorrect; passwords do not match. |
| Data Notes | Username update calls PUT /auth/username; password update calls PUT /auth/password with current password verification. |
