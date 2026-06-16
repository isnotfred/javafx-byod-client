# 12 - Change Log

## 2026-06-16 - V3: Final Event Workflow And Schema Alignment

- Adopted the final PostgreSQL schema and Temporary Event Device Request Workflow as authoritative sources.
- Added event_device_logs and v_event_device_status, including immutable event-device entry/exit history and consecutive-scan protection.
- Documented default event-request auto-approval with approval cascading to manifest rows.
- Added manual pending review, return with remarks, returned-request resubmission, rejection, event scanning, and reconciliation.
- Added Event Device Reconciliation Report and Temporary Event Device Guard Panel.
- Updated system setting defaults to max_devices_per_student = 5, allow_unregistered_devices = true, event_request_max_duration_days = 7, and auto_exit_cutoff_time = 22:00.
- Updated permanent-device policy so active pending devices may be logged when allow_unregistered_devices permits it.
- Restored the final-schema manifest rule: quantity defaults to 1 and must be greater than zero.
- Removed the unsupported contact-details-to-remarks mapping and event audit-flow assertions.
- Clarified that PendingApproval is persisted as event_requests.status = 'pending'.
- Limited allow_unregistered_devices to its final-schema meaning: guard check-in of unapproved devices.
- Corrected permanent-device serial validation to the final NOT NULL UNIQUE rule, including the Project Prototypes category.
- Restored devices.image_path as an optional schema field while leaving its application upload workflow unspecified.
- Marked the former event gate-log relationship gap resolved and documented remaining schema/service enforcement gaps.
- Preserved 75 functional requirements, 22 business rules, 47 validation rules, and 15 use cases while expanding test coverage.

## 2026-06-10 - V2: Revision To Dev Branch And V4 Business System Analysis

Updated the system analysis documentation to align with the dev branch implementation and V4 Business System Analysis (Group 1):

- New role: super_admin - added to all role tables, use cases, screen requirements, validation rules, business rules, traceability matrix, and user interactions. Super Admin manages all user accounts, assigns roles, and configures system settings.

- New device categories - replaced laptop, tablet, phone with the five official categories: Personal Computers, Components & Peripherals, Display & Projection, Project Prototypes (Optional SN), Appliances (TLE).

- Updated device purposes at that revision to the then-current application labels; V3 supersedes those labels with the final SQL values.

- New system_settings table - documented with seed data (max_devices_per_student = 3, allow_unregistered_devices = false) and full business rule enforcement requirements.

- users.email added - documented as required field for Super Admin onboarding flow.

- users.status = 'pending' added - pending accounts cannot log in; used for newly onboarded users.

- Image storage removed - image_path column noted as legacy/unused. No image upload is required; device proof relies on serial number and guard visual inspection only.

- New screens documented - Super Admin Dashboard, Super Admin Summary Dashboard, System Configuration Screen, Profile Screen, Forgot Password Screen, Reset Password Screen, Active Devices Admin Screen.

- Profile management - all three roles can update own username and password via Profile Screen.

- Forgot/Reset Password flow - documented as in-scope screens.

- Deviceless gate passage - explicitly documented: students without devices pass through the gate with no system interaction.

- New report types - Device Frequency Report and Incident/Override Report added from V4 BSA.

- Export/print - report screens now require export or print functionality.

- New audit actions - USER_ROLE_CHANGED, ADMIN_CREATED, ADMIN_UPDATED, ADMIN_DEACTIVATED, GUARD_CREATED, GUARD_UPDATED, GUARD_DEACTIVATED_BY_SUPER, SYSTEM_CONFIG_UPDATED documented.

- New use cases - UC-002 (Update Profile), UC-003 (Forgot/Reset Password), UC-013 (Manage System Users), UC-015 (Manage System Configuration) added.

- FR renumbered - all functional requirements renumbered from FR-001 to FR-075; all cross-references updated.

- Test scenarios expanded - from 32 to 51 test scenarios covering all new features.

- Gap analysis updated - previous "source not implemented" gaps marked as resolved; 7 new remaining gaps documented.

- Traceability matrix updated - all 75 FRs traced to use cases, screens, and data entities.

## 2026-05-25 - V1: Documentation Reconciliation To Uploaded Schema And Architecture

Updated the system analysis documentation to align with the uploaded May 2026 source files:

- Adopted the target 3-tier architecture: JavaFX frontend, Spring Boot REST API, Railway PostgreSQL.

- Replaced legacy desktop database-access assumptions with backend API responsibilities.

- Aligned all data requirements to the PostgreSQL schema containing users, students, devices, event_requests, event_request_devices, device_logs, and audit_logs.

- Documented schema views: v_device_campus_status, v_pending_devices, and v_active_event_requests.

- Updated requirements, use cases, rules, screens, reports, tests, traceability, and user interactions.

- Removed the old policy allowing pending devices to receive temporary gate logs.

- Replaced update-style gate logging with immutable entry/exit event rows.

## Prior Baseline

The previous documentation described a layered Java desktop application with database access in the desktop tier and older data concepts. Those concepts are superseded by this change log and the canonical database/API documentation.
