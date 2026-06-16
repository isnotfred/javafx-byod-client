# 02 - Functional Requirements

## Authentication And Role Access

| ID | Requirement |
| --- | --- |
| FR-001 | The system shall require login before protected functions are used. |
| FR-002 | The backend shall authenticate users by users.username and users.password_hash. |
| FR-003 | The system shall allow only active accounts where users.status = 'active'. Accounts with status = 'pending' cannot log in until activated. |
| FR-004 | The system shall route authenticated users by stored role value: super_admin, admin, or guard. |
| FR-005 | The frontend and backend shall both enforce role-based access. |
| FR-006 | The system shall audit login, logout, and failed login events using standardized audit action types. |
| FR-007 | The system shall provide a Forgot Password screen allowing users to initiate a password reset. |
| FR-008 | The system shall provide a Reset Password screen to complete the password reset flow. |

## Profile Management

| ID | Requirement |
| --- | --- |
| FR-009 | All roles (super_admin, admin, guard) shall be able to update their own username via the Profile screen. |
| FR-010 | All roles shall be able to update their own password via the Profile screen after verifying their current password. |
| FR-011 | Username updates shall enforce the minimum length of 3 characters and uniqueness constraint. |
| FR-012 | Password confirmation shall be required: new password and confirm password must match before saving. |

## Student Management

| ID | Requirement |
| --- | --- |
| FR-013 | Admin users shall create student records with student_id, first_name, last_name, and optional course_year_level. |
| FR-014 | Admin users shall update student names, course/year level, and status. |
| FR-015 | The system shall prevent duplicate student_id values. |
| FR-016 | The system shall support active/inactive student status using students.status. |
| FR-017 | The system shall use deactivation instead of hard deletion when linked devices or logs exist. |
| FR-018 | Guards may create a student record only as part of quick pending device registration. |

## Device Management

| ID | Requirement |
| --- | --- |
| FR-019 | Admin users shall register permanent BYOD devices linked to one students.student_id. |
| FR-020 | The system shall enforce globally unique devices.serial_number. |
| FR-021 | The system shall support the following device categories: Personal Computers, Components & Peripherals, Display & Projection, Project Prototypes (Optional SN), and Appliances (TLE). |
| FR-022 | The system shall support the following device purposes: Academic BYOD, School Event, Organization Activity, Temporary Equipment, Other Approved Purpose, PROTOTYPE, and APPLIANCE. |
| FR-023 | Admin users shall approve, reject, update, activate, and deactivate device records. |
| FR-024 | Rejected devices shall require a rejection remark stored in devices.remarks. |
| FR-025 | Device review actions shall store reviewed_by and reviewed_at together. |
| FR-026 | Campus presence shall be derived from latest device_logs, not stored on devices. |
| FR-027 | The system shall enforce the maximum number of active registered devices per student as configured in system_settings (max_devices_per_student, default 5). |
| FR-028 | The data layer shall retain the optional devices.image_path field and expose it through v_pending_devices; the authoritative sources do not define an upload or storage workflow. |

## Pending Registration

| ID | Requirement |
| --- | --- |
| FR-029 | Guards shall submit unregistered devices as devices rows with registration_status = 'pending'. |
| FR-030 | The allow_unregistered_devices system setting shall control whether guards may check in unapproved active devices; its default value shall be true. |
| FR-031 | Pending device records shall appear in the admin approval queue through v_pending_devices. |
| FR-032 | Admin users shall approve pending devices by setting registration_status = 'approved'. |
| FR-033 | Admin users shall reject pending devices by setting registration_status = 'rejected' with required remarks. |
| FR-034 | Guards shall not approve or reject pending devices. |
| FR-035 | Active pending devices may receive a gate entry when allow_unregistered_devices permits unapproved-device check-in; rejected and inactive devices shall remain ineligible. |

## Event Requests

| ID | Requirement |
| --- | --- |
| FR-036 | An admin or other authorized submitter shall create event access request headers and manifest rows through POST /api/v1/event-requests. |
| FR-037 | Normal API submissions shall be automatically approved and cascade device_status = 'approved' to all event_request_devices rows. |
| FR-038 | Event requests shall capture student ID, responsible person, organization, event name, purpose, approval document details, start/end dates, and remarks using the fields defined by event_requests. |
| FR-039 | Event document type shall be Paper Approval or Signed GPOA, and event duration shall not exceed event_request_max_duration_days (default 7). |
| FR-040 | Manually queued requests shall support PendingApproval, Approved, Returned, and Rejected workflow states; PendingApproval is persisted as status = 'pending', return and rejection require remarks, and returned requests resubmit to pending. |
| FR-041 | Event manifest devices shall use pending, approved, or returned status; reconciliation shall persist device_status = 'returned'. |
| FR-042 | The system shall expose active requests through v_active_event_requests and current event-device presence through v_event_device_status. |
| FR-043 | Guards shall log event-device entry and exit in immutable event_device_logs rows, with consecutive same-type events blocked. |
| FR-044 | The event workflow shall support guard lookup and manifest scanning, manual reconciliation by Guard or Admin, reconciliation reporting, local session draft caching, and all six schema-approved event device types. |

## Gate Monitoring

| ID | Requirement |
| --- | --- |
| FR-045 | Admin and guard users shall search by student ID, student name, or device serial number before gate logging. |
| FR-046 | The system shall display student, device, registration, device status, and derived campus status data before logging. |
| FR-047 | The system shall insert a device_logs row for each entry event. |
| FR-048 | The system shall insert a device_logs row for each manual exit event. |
| FR-049 | Gate logs shall store event_type, event_time, handled_by, logout_type, auto_exit, notes, and created_at according to schema rules. |
| FR-050 | The database shall block permanent gate logs for rejected or inactive devices and allow active approved or pending devices according to policy. |
| FR-051 | The database shall block consecutive same-type manual events for the same device. |
| FR-052 | Device log rows shall be immutable after insert. |
| FR-053 | Students arriving at the gate without a device shall pass through freely. No log, tag, or system entry is created for deviceless gate passage. |

## Automatic Logout

| ID | Requirement |
| --- | --- |
| FR-054 | The backend shall run an automatic logout process for devices whose latest event is entry at school closing time. |
| FR-055 | Automatic logout shall insert exit rows with auto_exit = TRUE, logout_type = 'automatic', and handled_by = NULL. |
| FR-056 | Automatic logout shall write system audit entries using SYSTEM_AUTO_EXIT_BATCH and/or DEVICE_AUTO_EXIT. |

## Reports And Search

| ID | Requirement |
| --- | --- |
| FR-057 | Admin users shall generate seven reports: Daily Device Traffic Summary, Monthly Device Traffic Summary, Pending Registration Report, Active Devices on Campus, Device Frequency Report, Incident/Override Report, and Event Device Reconciliation Report. |
| FR-058 | Reports shall read from saved tables and schema views, not unsaved UI state. |
| FR-059 | Reports shall filter by date range before loading large historical result sets. |
| FR-060 | Active-device views shall be based on latest log state or v_device_campus_status. |
| FR-061 | Reports shall support export or print functionality. |

## Super Admin - User And System Management

| ID | Requirement |
| --- | --- |
| FR-062 | Super Admin shall create new user accounts for admin and guard roles via an onboarding workflow (full name, email, assigned role). |
| FR-063 | Onboarding shall set the account to status = 'pending' until the user completes credential setup. |
| FR-064 | Super Admin shall update user full name and status. |
| FR-065 | Super Admin shall change the role of any user account (super_admin, admin, guard). Role changes shall be audited with USER_ROLE_CHANGED. |
| FR-066 | Super Admin shall deactivate any user account. Deactivation sets status = 'inactive' in the database. |
| FR-067 | The system shall prevent duplicate usernames. |
| FR-068 | Passwords shall be stored only as bcrypt or argon2 hashes. |
| FR-069 | User records shall use role values super_admin, admin, and guard. |
| FR-070 | User records shall use status values active, inactive, and pending. |
| FR-071 | Super Admin shall view and update system configuration settings stored in system_settings. |
| FR-072 | System configuration changes shall be audited with SYSTEM_CONFIG_UPDATED. |

## Audit Logging

| ID | Requirement |
| --- | --- |
| FR-073 | Audit records shall be written through fn_write_audit_log(). |
| FR-074 | Audit rows shall be immutable after insert. |
| FR-075 | The system shall record the following audit action types: DEVICE_REGISTERED, DEVICE_APPROVED, DEVICE_REJECTED, DEVICE_DEACTIVATED, DEVICE_UPDATED, DEVICE_ENTRY, DEVICE_EXIT, DEVICE_AUTO_EXIT, STUDENT_CREATED, STUDENT_UPDATED, STUDENT_DEACTIVATED, USER_CREATED, USER_UPDATED, USER_DEACTIVATED, USER_LOGIN, USER_LOGOUT, USER_LOGIN_FAILED, USER_ROLE_CHANGED, ADMIN_CREATED, ADMIN_UPDATED, ADMIN_DEACTIVATED, GUARD_CREATED, GUARD_UPDATED, GUARD_DEACTIVATED_BY_SUPER, EVENT_REQUEST_CREATED, EVENT_REQUEST_APPROVED, EVENT_REQUEST_RETURNED, EVENT_REQUEST_REJECTED, SYSTEM_AUTO_EXIT_BATCH, SYSTEM_CONFIG_UPDATED. |
