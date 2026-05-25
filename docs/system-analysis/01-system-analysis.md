# 01 - System Analysis

## System Overview

**Project title:** BYOD Device Management System  
**Subtitle:** Ingress-Egress Monitoring for Student Devices

The system is a JavaFX desktop client backed by a Spring Boot REST API and a Railway-hosted PostgreSQL database. It supports student records, BYOD device registration, pending device review, gate entry/exit logging, event-based temporary access requests, active-device monitoring, reporting, role-based access, and immutable audit history.

Students are indirect users. Admin and guard accounts operate the system.

## Purpose And Objectives

The purpose is to replace manual logbook-based device monitoring with a searchable, timestamped, role-controlled system.

| Objective | Description |
| --- | --- |
| Student and device registration | Maintain active/inactive students and permanent BYOD device records. |
| Gate monitoring | Record immutable entry and exit events for approved active devices. |
| Pending approval | Let guards submit pending device registrations for admin decision. |
| Event access | Track event access requests and device line items separately from permanent BYOD records. |
| Reporting | Provide administrative reports from saved records and derived views. |
| Security and auditability | Enforce role access, hashed passwords, immutable logs, and standardized audit actions. |

## Scope

Included:

- Login and role-based access for `admin` and `guard`.
- JavaFX frontend screens that call the backend API over HTTPS/JSON.
- Spring Boot backend controllers, services, DAOs, validation, and audit orchestration.
- PostgreSQL schema, views, triggers, indexes, and functions.
- Student, device, event request, gate log, user, report, and audit workflows.

Excluded unless later approved:

- Student self-service access.
- Web portal or mobile app.
- RFID, barcode scanner, GPS, or automated physical detection.
- Exact REST DTO shape and token/session format.
- Final image storage path policy.

## Users And Permissions

| Role | Description | Allowed Actions | Not Allowed |
| --- | --- | --- | --- |
| `admin` | Administrative/IT user. | Manage students, devices, users, pending approvals, event requests, reports, and audit review. | Directly edit immutable `device_logs` or `audit_logs`. |
| `guard` | Gate-monitoring user. | Search students/devices, submit pending registrations, create/verify event requests, log entry/exit for approved active devices, and view active devices. | Approve/reject pending BYOD devices, manage users, deactivate official records, or read full audit history unless explicitly granted. |
| Student | Indirect actor. | Provides student and device details to staff. | No login or direct data access. |

## Core Workflows

### Approved BYOD Registration

1. Admin creates or locates an active student.
2. Admin enters device details and optional image path.
3. Backend validates required fields, serial-number uniqueness, enum values, and student ownership.
4. Backend inserts or updates `devices`.
5. Approval sets `registration_status = 'approved'`, `reviewed_by`, and `reviewed_at`.
6. Audit entry is written through `fn_write_audit_log()`.

### Pending Device Registration

1. Guard searches at the gate and finds no approved active device record.
2. Guard enters student and device details after manual proof verification.
3. Backend stores any new student as a normal `students` row and stores the device with `registration_status = 'pending'`.
4. Proof details are captured in `devices.remarks` because the uploaded schema has no dedicated proof columns.
5. Admin reviews pending records through `v_pending_devices`.
6. Admin approves or rejects. Rejection requires `devices.remarks`.

Pending devices are not eligible for `device_logs` entry/exit records until approved, because `trg_device_logs_approved_only` blocks logs for unapproved devices.

### Event Access Request

1. Admin or guard creates an `event_requests` header for the responsible student/person, organization, event, document type, document reference, and date range.
2. User adds one or more `event_request_devices` line items.
3. Admin may approve, reject, or mark returned according to event request status.
4. Guard verification may set `verified_by` and `verified_at` on device line items.
5. Audit entries use the standardized `EVENT_REQUEST_*` action types.

Open design note: the uploaded schema does not link `device_logs` directly to `event_request_devices`. Event line items are request/verification records unless a future schema link or accompanying permanent `devices` row is added.

### Entry And Exit Monitoring

1. Guard searches by student ID, student name, or device serial number.
2. Backend reads device details and derived campus status from latest `device_logs` or `v_device_campus_status`.
3. Entry inserts a new `device_logs` row with `event_type = 'entry'`, `auto_exit = FALSE`, and `handled_by = current user`.
4. Exit inserts a new `device_logs` row with `event_type = 'exit'`, `logout_type = 'manual'`, `auto_exit = FALSE`, and `handled_by = current user`.
5. Database triggers block unapproved/inactive devices and consecutive same-type events.
6. Audit entries use `DEVICE_ENTRY` or `DEVICE_EXIT`.

### End-Of-Day Automatic Logout

The backend scheduled process finds devices whose latest log is an entry and inserts system-generated exit rows with `event_type = 'exit'`, `auto_exit = TRUE`, `logout_type = 'automatic'`, and `handled_by = NULL`. The system records `SYSTEM_AUTO_EXIT_BATCH` and/or `DEVICE_AUTO_EXIT` audit actions.

## Modules

| Module | Main Responsibility |
| --- | --- |
| Authentication and Access Control | Login, inactive-account checks, role routing, and session/token data. |
| Student Management | Create, update, search, and deactivate student records. |
| Device Management | Register, update, approve, reject, activate, deactivate, and search BYOD devices. |
| Pending Registration | Guard submission and admin approval/rejection of pending device records. |
| Event Requests | Temporary access request headers and event device line items. |
| Gate Monitoring | Entry, exit, active-device view, and automatic logout. |
| Reports | Administrative, gate, pending, event, and audit-oriented reports. |
| User Management | Admin/guard account lifecycle. |
| Audit Logging | Standardized immutable audit records. |

## Assumptions

- Uploaded files override older repo documentation.
- The repo source code is not yet aligned with the target architecture.
- PostgreSQL is hosted on Railway and accessed only by the backend.
- JavaFX image handling stores paths; final storage policy is not yet specified.

## Risks And Recommendations

| Risk | Impact | Recommendation |
| --- | --- | --- |
| Current JavaFX starter code does not implement the documented backend/API architecture. | Implementation may diverge from analysis. | Treat this documentation as target baseline and track implementation as a gap. |
| Event devices are not directly loggable through `device_logs`. | Event gate reporting may be incomplete. | Decide whether event devices need a log FK or a companion `devices` row before implementation. |
| Railway URL and env names are unspecified. | Deployment docs cannot be exact. | Define backend base URL, database env variables, and backup policy. |
| Image path storage is unspecified. | Paths may break across machines. | Choose managed upload storage or a shared path policy. |
