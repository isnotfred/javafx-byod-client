# 01 - System Analysis

## System Overview

Project title: BYOD Device Management System Subtitle: Ingress-Egress Monitoring for Student Devices

The system is a JavaFX desktop client backed by a Spring Boot REST API and a Railway-hosted PostgreSQL database. It supports student records, BYOD device registration, pending device review, gate entry/exit logging, event-based temporary access requests, active-device monitoring, reporting, role-based access, and immutable audit history.

Students are indirect users. Super Admin, Admin, and Guard accounts operate the system.

## Purpose And Objectives

The purpose is to replace manual logbook-based device monitoring with a searchable, timestamped, role-controlled system.

| Objective | Description |
| --- | --- |
| Student and device registration | Maintain active/inactive students and permanent BYOD device records across five device categories. |
| Gate monitoring | Record immutable entry and exit events for eligible permanent and temporary event devices. |
| Pending approval | Let guards submit pending device registrations for admin decision. |
| Event access | Track temporary event requests, manifest devices, gate entry/exit, and reconciliation separately from permanent BYOD records. |
| Reporting | Provide administrative reports from saved records and derived views, with export/print support. |
| Security and auditability | Enforce three-tier role access, hashed passwords, immutable logs, and standardized audit actions. |
| System configuration | Allow Super Admin to manage configurable policy settings such as maximum devices per student. |

## Scope

Included:

- Login and role-based access for super_admin, admin, and guard.

- JavaFX frontend screens that call the backend API over HTTPS/JSON.

- Spring Boot backend controllers, services, DAOs, validation, and audit orchestration.

- PostgreSQL schema, views, triggers, indexes, functions, and system_settings table.

- Student, device, event request, gate log, user, report, system configuration, and audit workflows.

- Profile management: all roles can update their own username and password.

- Forgot password and reset password flow.

Excluded unless later approved:

- Student self-service access.

- Web portal or mobile app.

- RFID, barcode scanner, GPS, or automated physical detection.

- Faculty, staff, and external visitor tracking.

- Exact REST DTO shape and token/session format.

## Users And Permissions

| Role | Description | Allowed Actions | Not Allowed |
| --- | --- | --- | --- |
| super_admin | Full system authority. | Manage all user accounts (admins and guards), assign and change roles, configure system settings, view all data, manage own profile. | Direct gate operations, direct device approval. |
| admin | Operational/IT user. | Manage students, devices, pending approvals, create/review event requests, reconcile event devices, generate reports, review audit history, and manage own profile. | Manage user accounts or system configuration (Super Admin only), call Guard-only event entry/exit endpoints, or directly edit immutable log history. |
| guard | Gate-monitoring user. | Search students/devices, submit pending registrations, create event requests only when authorized as a submitter, verify and scan event devices, reconcile event devices, log eligible permanent-device entry/exit, view active devices, manage own profile. | Approve/reject pending BYOD devices or event requests, manage users, deactivate official records, or read full audit history unless explicitly granted. |
| Student | Indirect actor. | Provides student and device details to staff. | No login or direct data access. |

## Core Workflows

### Approved BYOD Registration

1. Admin creates or locates an active student.

1. Admin enters device details and selects the device category.

1. Backend validates required fields, serial-number uniqueness, enum values, and student ownership.

1. Backend checks the max_devices_per_student system setting before allowing registration.

1. Backend inserts or updates devices.

1. Approval sets registration_status = 'approved', reviewed_by, and reviewed_at.

1. Audit entry is written through fn_write_audit_log().

### Pending Device Registration

1. Guard searches at the gate and finds no approved active device record.

1. Guard enters student and device details. Permanent-device serial number is required and globally unique.

1. Backend stores any new student as a normal students row and stores the device with registration_status = 'pending'.

1. Proof details are captured in devices.remarks.

1. Admin reviews pending records through v_pending_devices.

1. Admin approves or rejects. Rejection requires devices.remarks.

Active pending devices may be checked in when allow_unregistered_devices permits it. A valid later exit remains part of the alternating gate history. Rejected or inactive devices remain blocked.

### Students Without Devices

Students arriving at the gate without a device pass through freely. No system interaction, logging, or tagging is required.

### Event Access Request

1. An authorized submitter creates an event_requests header with responsible student/person, organization, event, approval document, date range, and remarks.

1. The user adds event_request_devices manifest rows. Each row has quantity > 0 and defaults to quantity = 1.

1. The backend validates the date range and event_request_max_duration_days setting, which defaults to 7 days.

1. A normal API submission is immediately approved and cascades device_status = 'approved' to its manifest devices.

1. A manually queued or returned request follows Admin review. Admin may approve, return with remarks, or reject with remarks.

1. A creator may edit a returned request and resubmit it to the workflow PendingApproval state, persisted as status = 'pending'.

1. During active event dates, guards log manifest entry and exit rows in event_device_logs. Consecutive same-type events are blocked.

1. Guard or Admin reconciliation persists event_request_devices.device_status = 'returned'. The Reconciliation Report identifies unreturned devices.

### Entry And Exit Monitoring

1. Guard searches by student ID, student name, or device serial number.

1. Backend reads device details and derived campus status from latest device_logs or v_device_campus_status.

1. Entry inserts a new device_logs row with event_type = 'entry', auto_exit = FALSE, and handled_by = current user.

1. Exit inserts a new device_logs row with event_type = 'exit', logout_type = 'manual', auto_exit = FALSE, and handled_by = current user.

1. Database triggers block rejected/inactive devices and consecutive same-type events. Pending devices are eligible when the unregistered-device policy permits them.

1. Audit entries use DEVICE_ENTRY or DEVICE_EXIT.

### End-Of-Day Automatic Logout

The backend scheduled process finds devices whose latest log is an entry and inserts system-generated exit rows with event_type = 'exit', auto_exit = TRUE, logout_type = 'automatic', and handled_by = NULL. The system records SYSTEM_AUTO_EXIT_BATCH and/or DEVICE_AUTO_EXIT audit actions.

### System Configuration

Super Admin accesses the System Configuration screen to view and update system_settings key-value pairs. Changes are audited with SYSTEM_CONFIG_UPDATED.

### Profile Management

All roles (super_admin, admin, guard) may update their own username and password via the Profile screen. Password changes require current password verification. Changes are reflected immediately in the active session.

## Modules

| Module | Main Responsibility |
| --- | --- |
| Authentication and Access Control | Login, inactive/pending-account checks, role routing, forgot/reset password, and session/token data. |
| Student Management | Create, update, search, and deactivate student records. |
| Device Management | Register, update, approve, reject, activate, deactivate, and search BYOD devices across five categories. |
| Pending Registration | Guard submission and admin approval/rejection of pending device records. |
| Event Requests | Temporary request headers, manifest devices, event gate logs, status views, review, return, and reconciliation. |
| Gate Monitoring | Entry, exit, active-device view, and automatic logout. |
| Reports | Administrative, gate, pending, event, frequency, incident, and audit-oriented reports. |
| User Management | Super Admin: full account lifecycle for admin and guard accounts, role assignment. |
| Profile Management | Self-service username and password update for all roles. |
| System Configuration | Super Admin management of configurable system settings. |
| Audit Logging | Standardized immutable audit records. |

## Assumptions

- The final PostgreSQL schema and Temporary Event Device Request Workflow are authoritative for this revision.

- The dev branch contains a fully initialized JavaFX MVC architecture aligned with the target system.

- PostgreSQL is hosted on Railway and accessed only by the backend.

- Permanent-device serial numbers are required and globally unique. The final schema retains an optional devices.image_path column; upload/storage behavior is not defined by the authoritative sources.

- The system_settings table drives configurable policy at runtime.

## Risks And Recommendations

| Risk | Impact | Recommendation |
| --- | --- | --- |
| Application device-purpose labels may not match the final schema values. | Database validation can reject values outside the final CHECK constraint. | Use the final schema values: Academic BYOD, School Event, Organization Activity, Temporary Equipment, Other Approved Purpose, PROTOTYPE, and APPLIANCE. |
| users.status UI displays deactivated but schema CHECK only allows inactive. | UI-to-backend mismatch on status updates. | Treat deactivated as a UI display label that maps to inactive in the database. |
| The schema allows event_request_devices.quantity > 1 while event_device_logs references one manifest row. | One entry, exit, or reconciliation action applies to the whole manifest row; partial-unit movement is undefined. | Preserve the schema rule quantity > 0 and clarify grouped-quantity scan behavior before implementation. |
| Railway URL and env names are unspecified. | Deployment docs cannot be exact. | Define backend base URL, database env variables, and backup policy. |
| Configurable policy and event active-date rules are not fully database-enforced. | A client could bypass duration, pending-device, or active-event checks. | Enforce system_settings and active event dates in backend services for every affected operation. |
