# 13 - User Interactions

## Purpose

This document defines what each role can view, enter, update, approve, scan, reconcile, generate, and configure under the final workflow and schema.

## Role Summary

| Role | System Access | Primary Interaction |
| --- | --- | --- |
| super_admin | Direct login | Manage user accounts, roles, system settings, and own profile. |
| admin | Direct login | Manage students/devices, review requests, reconcile event devices, generate reports, and review audit history. |
| guard | Direct login | Search, submit pending devices, create event requests only when authorized as a submitter, scan permanent/event devices, and reconcile event manifests. |
| Student | No login | Presents identity, request details, and physical devices to staff. |

## Role Interactions

### Super Admin

| Interaction Type | Allowed |
| --- | --- |
| Accessible Screens | Login, Super Admin Dashboard, User Management, System Configuration, Profile. |
| Can View | User accounts, system settings, own profile, and Event Device Reconciliation Report where system visibility is required. |
| Can Enter/Update | User onboarding data, roles, account status, setting values, own username/password. |
| Cannot Perform | Direct student/device administration, request approval, or gate logging. |

### Admin

| Interaction Type | Allowed |
| --- | --- |
| Accessible Screens | Admin Dashboard, Student/Device Management, Pending Approval, Event Request, Temporary Event Device Guard Panel, Ingress/Egress, Active Devices, Logs, Reports, Profile. |
| Can View | Students, devices, pending queue, requests/manifests, permanent/event logs, derived status, reconciliation data, reports, and audit history. |
| Can Enter | Student/device records, event requests, positive-quantity manifest rows, review remarks, gate notes, report filters. |
| Can Update | Student/device lifecycle, pending decisions, event approval/return/rejection, and event-device reconciliation; event entry/exit endpoints remain Guard-only. |
| Can Generate | All seven reports with export or print. |
| Cannot Perform | User or system-setting management; direct edits to immutable log history. |

### Guard

| Interaction Type | Allowed |
| --- | --- |
| Accessible Screens | Guard Dashboard, Quick Pending Registration, Event Request, Temporary Event Device Guard Panel, Ingress/Egress, Active Devices, Profile. |
| Can View | Search results, pending eligibility, active requests/manifests, and permanent/event derived status. |
| Can Enter | Pending details, event requests where authorized, positive-quantity manifest rows, gate notes, entry/exit selections. |
| Can Update | Insert permanent/event entry/exit logs; verify and reconcile event manifest devices. |
| Cannot Perform | Approve/reject permanent devices; approve/return/reject event requests; manage users/settings; alter immutable history. |

### Student

Students do not log in. They provide identity, event, approval-document, and device details to authorized staff and present devices for visual verification. Students without a device pass freely with no system record.

## Permission Matrix

| System Area | super_admin | admin | guard | Student |
| --- | --- | --- | --- | --- |
| Login/Profile/Recovery | Yes | Yes | Yes | No |
| Student Management | No | Full | Search; create through pending flow | Provides details |
| Device Management | No | Full | Search; submit pending | Presents device |
| Pending Registration | No | Review/decide | Submit and gate-log when policy permits | Provides details |
| Event Request | No | Create/review/reconcile | Create when authorized as submitter; scan/verify/reconcile | Provides details |
| Permanent Entry/Exit | No | Yes | Yes | Presents device |
| Event Entry/Exit | No | No; may view/reconcile | Yes | Presents device |
| Reports | Reconciliation visibility | All 7 | Operational views only | No |
| User Management | Full | No | No | No |
| System Configuration | Full | No | No | No |
| Audit History | No direct operations | Full review | No full review | No |

## Interaction Flows

### Permanent Device Entry

1. Guard searches by student ID, student name, or serial number.
2. If no device exists, Guard may create a quick pending registration.
3. Backend permits entry for an active approved device or an active policy-eligible pending device.
4. Rejected, inactive, or policy-disallowed pending devices receive a warning and no log.
5. Successful entry inserts device_logs and records DEVICE_ENTRY.

### Permanent Device Exit

1. Guard finds a device whose latest state is entry.
2. Guard confirms exit.
3. Backend inserts a manual device_logs exit row and records DEVICE_EXIT.

### Pending Review

1. Guard submits a pending registration.
2. The active pending device may be checked in while allow_unregistered_devices remains true.
3. Admin approves it or rejects it with remarks through v_pending_devices.

### Temporary Event Request

1. An admin or other authorized submitter enters the request and manifest rows with quantity > 0.
2. Backend validates document type, dates, and event_request_max_duration_days.
3. Normal API submission auto-approves the request and all manifest rows.
4. A manually queued request waits for Admin approval, return with remarks, or rejection with remarks.
5. The creator corrects a returned request and resubmits it to pending.
6. During active dates, Guard searches the request and selects manifest devices.
7. Confirm Entry or Confirm Exit inserts immutable event_device_logs rows; duplicate consecutive events are rejected.
8. Guard or Admin reconciles an item by setting manifest device_status = 'returned'.
9. Admin reviews the Event Device Reconciliation Report for outstanding items.

## Interaction Rules

| Rule | Description |
| --- | --- |
| Pending device gate logging | Active pending devices may be checked in when allow_unregistered_devices is true. |
| Deviceless passage | Students without devices pass with no system interaction. |
| Derived status | Permanent status uses v_device_campus_status; event status uses v_event_device_status. |
| Immutable history | Users insert gate/audit rows and never edit historical rows. |
| Event manifest granularity | A manifest row has quantity > 0, defaults to 1, and is the unit referenced by event_device_logs. |
| Event reconciliation | Reconciled means event_request_devices.device_status = 'returned'. |
| Settings enforcement | All four seeded settings are enforced by backend services. |
| Profile changes | Username/password self-service is available to every authenticated role. |
