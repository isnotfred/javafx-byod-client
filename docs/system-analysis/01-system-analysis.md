# 01 - System Analysis

## System Overview

**Project title:** BYOD Registration and Monitoring System  
**Subtitle:** Ingress-Egress Monitoring for Student Devices

The system is a desktop-based Java application for registering, verifying, and monitoring student-owned academic devices brought to campus under a Bring Your Own Device policy. It supports student and device registration, ingress and egress logging, searchable records, active device tracking, pending registration review, temporary/event equipment handling, role-based access, and reporting through JDBC-backed database storage.

Students are indirect users in the current version. Security guards and system administrators operate the application.

## Purpose and Objectives

The purpose is to replace or improve manual logbook-based device monitoring with a searchable, timestamped, role-controlled desktop system.

Objectives:

1. Register student profiles and device ownership records.
2. Verify student-owned devices at campus entry and exit points.
3. Log ingress and egress with automated timestamps.
4. Show devices currently inside campus.
5. Support pending device registration for unregistered devices.
6. Support temporary/event devices without treating them as normal BYOD devices.
7. Generate daily, monthly, pending, active, history, and exception reports.
8. Restrict access by user role.
9. Preserve audit history for logs, approvals, rejections, and overrides.

## Current Documentation Review

Existing documents found:

| Source | Status | Notes |
| --- | --- | --- |
| `../../../SYSTEM-ANALYSIS.md` | Useful but monolithic | Contains most system-analysis sections, but needs reorganization, stronger traceability, temporary/event device rules, clearer permission rules, and deeper QA coverage. |
| `../../OOP-Finals-Project.md` | Project brief | Defines expected desktop Java output, team roles, source PDFs, and initial feature list. |
| Business analysis PDF | External source | Confirms business problem, stakeholders, proposed process, business rules, scope, limitations, and pending-entry behavior. |
| Analyst task-division PDF | External source | Confirms technical system analyst deliverables: FRs, NFRs, use cases, data dictionary, validation, screens, reports, acceptance criteria, and tests. |

## Scope

Included:

- Login and role-based access for System Administrator and Security Guard.
- Student profile management.
- Device registration and management.
- Pending student/device registration submission and admin approval.
- Temporary/event device tracking.
- Ingress and egress monitoring.
- Search by student ID, student name, device serial number, and status.
- Active devices inside campus view.
- Reports and device history.
- JDBC database storage.
- JavaFX or Swing desktop UI.

Excluded:

- Student self-registration.
- Web portal or mobile application.
- RFID, barcode scanner, biometric, or GPS integration.
- Cloud synchronization.
- Automated physical device detection.
- Real-time multi-campus or multi-branch operations.

## Users and Permissions

| User Role | Description | Allowed Actions | Not Allowed |
| --- | --- | --- | --- |
| System Administrator | IT/admin staff with full system authority. | Manage students, devices, users, reports, pending approvals, event devices, device active/inactive status, and audit review. | None within current scope, except actions restricted by school policy. |
| Security Guard | Gate personnel who operate daily monitoring. | Search records, log ingress, log egress, view active devices, submit pending student/device registrations after manual proof verification, and submit temporary/event device entries at the gate. | Approve/reject pending items, make pending students official, delete logs, manage users, permanently delete official records, change official device status. |
| Student | Indirect actor and device owner. | Presents device and details to authorized staff. | Does not log in or directly use the system in current version. |

## Core Workflows

### Approved BYOD Registration

1. Admin registers or searches the student.
2. Admin enters device details.
3. System validates required fields and duplicate serial number.
4. System stores the device with `registration_status = Approved`, `campus_status = Outside`, and `device_status = Active`.
5. Device becomes available for ingress and egress monitoring.

### Pending Device Registration

1. Guard searches student and device at the gate.
2. Device is not registered, or the student is valid but not yet encoded in the system.
3. If the student is not yet encoded, the guard manually verifies the student using accepted proof such as school ID, registration form, enrollment record, or other school-approved proof.
4. Guard enters minimum required student/device details, including proof type, proof reference or remarks, submitter, and submission timestamp.
5. System saves the student as `record_status = Pending` when needed and links the pending student record to the pending device record.
6. System routes the pending student/device record to the admin queue.
7. Default policy: the pending device may be logged for repeat temporary entry while it remains pending.
8. Admin later approves the pending student as Official and approves the device, or rejects/deactivates invalid records.

Pending devices are allowed temporary entry while waiting for admin approval. The registration remains Pending until an administrator approves or rejects it. A pending student record can support gate monitoring, but it is not an official student record until an administrator reviews and approves it.

### Temporary/Event Device Handling

1. Admin or Security Guard records event equipment as a Temporary/Event Device.
2. Organization or responsible person presents a paper approval or signed GPOA to the guard.
3. Guard verifies the approval document and records document details in the system.
4. System captures responsible person, event name, purpose, date/time, expected exit or return, approval document type, and remarks.
5. Device is monitored through ingress and egress logs.
6. Device appears separately in temporary/event reports.

### Ingress

1. Guard searches by student ID, name, or serial number.
2. System displays owner, device, registration status, campus status, and device status.
3. Guard visually verifies the device.
4. System blocks invalid status combinations such as already inside, rejected, inactive, or unauthorized pending.
5. System records ingress timestamp and guard user ID.
6. System updates `campus_status` to Inside.

### Egress

1. Guard searches the device or active inside record.
2. System displays the active ingress record.
3. Guard visually verifies the device.
4. System records egress timestamp and guard user ID.
5. System updates `campus_status` to Outside.

### End-of-Day Automatic Logout

Devices still marked Inside at 10:00 PM are automatically logged out by the system. The system records an egress timestamp of 10:00 PM, sets the campus status to Outside, and adds a remark such as `Auto-logged out at school closing`.

## Modules

| Module | Main Responsibility |
| --- | --- |
| Authentication and Access Control | Login, logout, role detection, and permission checks. |
| Student Management | Create, update, search, view, and deactivate student records. |
| Device Management | Register, update, approve, reject, activate, deactivate, and search devices. |
| Pending Registration | Submit, review, approve, reject, and audit pending student/device records. |
| Temporary/Event Devices | Track non-BYOD event equipment with responsible person and event purpose. |
| Ingress/Egress Monitoring | Log entry and exit transactions with status validation. |
| Active Device Monitoring | Show devices currently inside campus before 10:00 PM and confirm automatic logout results after 10:00 PM. |
| Reports | Generate administrative monitoring and exception reports. |
| User Management | Manage Admin and Security Guard accounts. |
| Audit Logging | Track sensitive changes, approvals, rejections, overrides, and corrections. |

## Assumptions

- The application runs on authorized campus desktop computers.
- The database is available during gate operations.
- Guards perform manual visual verification.
- Serial numbers or identifying marks are readable enough for manual entry.
- Pending devices may receive repeat temporary entry while waiting for admin approval.
- Guards may submit pending student records only after manual proof verification.
- Event equipment is allowed only for approved school events or organization activities.

## Risks

| Risk | Impact | Mitigation |
| --- | --- | --- |
| Incorrect serial number entry | Wrong device record or failed lookup | Required validation, clear search results, optional device image. |
| Pending process abuse | Unapproved devices may enter repeatedly | Show repeat pending-entry counts in reports and require admin follow-up. |
| Database outage | Gate monitoring is interrupted | Provide clear error messages and backup manual procedure outside system scope. |
| Missing event approval document | Event equipment may enter without proof of school authorization | Require paper approval or signed GPOA details before guard accepts event device entry. |
| Logs edited or deleted | Audit trail is weakened | Do not allow permanent deletion; use audit notes/corrections. |

## Recommendations

- Treat registration status, campus status, device status, and purpose as separate fields.
- Keep permanent logs immutable for normal users.
- Add audit fields for approval, rejection, overrides, and corrections.
- Keep UI screens simple and optimized for guard search speed.
- Use this documentation set as the handoff baseline for database design, UI mockups, implementation, and QA.
