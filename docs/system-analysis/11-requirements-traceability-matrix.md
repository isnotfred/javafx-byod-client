# 11 - Requirements Traceability Matrix

## Traceability Table

This matrix traces each functional requirement to its primary use case, screen, and data entity. All FR IDs reference 02-functional-requirements.md.

| FR ID | Requirement Summary | Use Case(s) | Screen(s) | Primary Table / View |
| --- | --- | --- | --- | --- |
| FR-001 | Login required before protected functions | UC-001 | Login Screen | users |
| FR-002 | Backend authenticates by username + password_hash | UC-001 | Login Screen | users |
| FR-003 | Only status = 'active' accounts can log in | UC-001 | Login Screen | users |
| FR-004 | Route by stored role: super_admin, admin, guard | UC-001 | All Dashboards | users |
| FR-005 | Frontend and backend both enforce RBAC | UC-001, UC-010-UC-015 | All Screens | - |
| FR-006 | Audit login, logout, and failed login | UC-001 | Login Screen | audit_logs |
| FR-007 | Forgot Password screen | UC-003 | Forgot Password Screen | users |
| FR-008 | Reset Password screen | UC-003 | Reset Password Screen | users |
| FR-009 | All roles can update own username | UC-002 | Profile Screen | users |
| FR-010 | All roles can update own password | UC-002 | Profile Screen | users |
| FR-011 | Username min 3 chars, unique | UC-002 | Profile Screen | users |
| FR-012 | New password must match confirmation | UC-002 | Profile Screen | - |
| FR-013 | Admin creates student records | UC-004 | Student Management Screen | students |
| FR-014 | Admin updates student records | UC-004 | Student Management Screen | students |
| FR-015 | Prevent duplicate student_id | UC-004 | Student Management Screen | students |
| FR-016 | Student active/inactive status | UC-004 | Student Management Screen | students |
| FR-017 | Deactivation instead of hard delete | UC-004 | Student Management Screen | students |
| FR-018 | Guard creates student via pending flow | UC-006 | Quick Pending Registration Screen | students |
| FR-019 | Admin registers permanent BYOD devices | UC-005 | Device Management Screen | devices |
| FR-020 | Globally unique serial number | UC-005, UC-006 | Device Management Screen | devices |
| FR-021 | Five device categories | UC-005, UC-006 | Device Management, Quick Pending, Event Request Screens | devices, event_request_devices |
| FR-022 | Seven schema-approved device purposes | UC-005 | Device Management Screen | devices |
| FR-023 | Admin approve/reject/update/activate/deactivate devices | UC-005, UC-007, UC-008 | Device Management, Pending Approval Screens | devices |
| FR-024 | Rejection requires remarks | UC-008 | Pending Approval Screen | devices |
| FR-025 | reviewed_by + reviewed_at stored together | UC-007, UC-008 | Pending Approval Screen | devices |
| FR-026 | Campus presence derived from device_logs | UC-010, UC-011 | Guard/Admin Dashboards, Gate Screen | v_device_campus_status |
| FR-027 | max_devices_per_student enforced | UC-005 | Device Management Screen | system_settings, devices |
| FR-028 | Optional image_path retained; workflow unspecified | UC-005, UC-006 | Device Management, Quick Pending Screens | devices, v_pending_devices |
| FR-029 | Guard submits pending device | UC-006 | Quick Pending Registration Screen | devices |
| FR-030 | allow_unregistered_devices controls unapproved-device check-in | UC-010 | Ingress/Egress Screen | system_settings |
| FR-031 | Pending queue via v_pending_devices | UC-007 | Pending Approval Screen | v_pending_devices |
| FR-032 | Admin approves pending device | UC-007 | Pending Approval Screen | devices |
| FR-033 | Admin rejects pending device with remarks | UC-008 | Pending Approval Screen | devices |
| FR-034 | Guard cannot approve/reject pending | UC-006 | - | - |
| FR-035 | Policy-eligible pending devices may be logged | UC-006, UC-010, UC-011 | Quick Pending, Ingress/Egress Screens | device_logs trigger, system_settings |
| FR-036 | Admin or authorized submitter creates request and manifest rows | UC-009 | Event Request Screen | event_requests, event_request_devices |
| FR-037 | Normal submission auto-approves header and devices | UC-009 | Event Request Screen | event_requests, event_request_devices |
| FR-038 | Event header fields defined by final schema | UC-009 | Event Request Screen | event_requests |
| FR-039 | Document type and configurable duration | UC-009, UC-015 | Event Request, System Configuration Screens | event_requests, system_settings |
| FR-040 | Manual review, remarks, and resubmission states | UC-009 | Event Request Screen | event_requests |
| FR-041 | Manifest states and reconciliation | UC-009 | Event Request, Temporary Event Device Guard Panel | event_request_devices |
| FR-042 | Active request and event-device status views | UC-009, UC-014 | Event Guard Panel, Reports | v_active_event_requests, v_event_device_status |
| FR-043 | Immutable event-device entry/exit and alternation | UC-009 | Temporary Event Device Guard Panel | event_device_logs |
| FR-044 | Event lookup, scanning, reconciliation, drafts, and types | UC-009, UC-014 | Event Request, Event Guard Panel, Reports | event_request_devices, event_device_logs |
| FR-045 | Gate search by student ID, name, or serial | UC-010, UC-011 | Ingress/Egress Screen, Guard Dashboard | students, devices |
| FR-046 | Display student + device data before logging | UC-010, UC-011 | Ingress/Egress Screen | students, devices, v_device_campus_status |
| FR-047 | Insert device_logs entry row | UC-010 | Ingress/Egress Screen | device_logs |
| FR-048 | Insert device_logs exit row | UC-011 | Ingress/Egress Screen | device_logs |
| FR-049 | Gate log fields per schema | UC-010, UC-011 | Ingress/Egress Screen | device_logs |
| FR-050 | Trigger blocks rejected/inactive and permits eligible pending | UC-010, UC-011 | Ingress/Egress Screen | device_logs trigger |
| FR-051 | Trigger blocks consecutive same-type events | UC-010, UC-011 | Ingress/Egress Screen | device_logs trigger |
| FR-052 | device_logs rows are immutable | UC-010, UC-011 | - | device_logs |
| FR-053 | Students without devices pass freely (no log) | UC-010 | Ingress/Egress Screen | - |
| FR-054 | Backend auto-logout process | UC-012 | - | device_logs |
| FR-055 | Auto-logout row fields | UC-012 | - | device_logs |
| FR-056 | Auto-logout audited | UC-012 | - | audit_logs |
| FR-057 | Seven report types | UC-014 | Reports Screen | Multiple tables/views |
| FR-058 | Reports read from saved tables/views | UC-014 | Reports Screen | - |
| FR-059 | Date range filter in SQL | UC-014 | Reports Screen | - |
| FR-060 | Active device view via v_device_campus_status | UC-014 | Reports, Active Devices Screen | v_device_campus_status |
| FR-061 | Export or print support | UC-014 | Reports Screen | - |
| FR-062 | Super Admin creates user accounts (onboarding) | UC-013 | User Management Screen | users |
| FR-063 | Onboarded accounts start as pending | UC-013 | User Management Screen | users |
| FR-064 | Super Admin updates user full name and status | UC-013 | User Management Screen | users |
| FR-065 | Super Admin changes user role | UC-013 | User Management Screen | users, audit_logs |
| FR-066 | Super Admin deactivates user accounts | UC-013 | User Management Screen | users |
| FR-067 | Prevent duplicate usernames | UC-013 | User Management Screen | users |
| FR-068 | Passwords stored as hash only | UC-001, UC-013 | Login, Profile, User Mgmt Screens | users |
| FR-069 | Role values: super_admin, admin, guard | UC-001, UC-013 | All Screens | users |
| FR-070 | Status values: active, inactive, pending | UC-001, UC-013 | Login, User Mgmt Screens | users |
| FR-071 | Super Admin views and updates system_settings | UC-015 | System Configuration Screen | system_settings |
| FR-072 | SYSTEM_CONFIG_UPDATED audit on setting change | UC-015 | System Configuration Screen | audit_logs |
| FR-073 | Audit records via fn_write_audit_log() | All UCs | - | audit_logs |
| FR-074 | Audit rows immutable | All UCs | - | audit_logs |
| FR-075 | All audit action types defined | All UCs | - | audit_logs |
