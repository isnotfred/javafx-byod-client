# 10 - Gap Analysis And Recommendations

## Purpose

This document identifies remaining gaps between the documented requirements and the current dev branch implementation, and provides recommendations to close them.

## Resolved Gaps

| Gap | Previous Status | Resolution |
| --- | --- | --- |
| JavaFX source was a bare starter project with no business logic | Unresolved in v1 docs | RESOLVED - dev branch contains a full JavaFX MVC architecture with controllers, services, models, enums, FXML screens, and schema. |
| No super_admin role defined | Unresolved in v1 docs | RESOLVED - super_admin role is fully implemented in UserRole.java, schema.sql, and related controllers. |
| Device categories were limited to laptop, tablet, phone | Unresolved in v1 docs | RESOLVED - Five device categories now defined, matching V4 Business System Analysis and DeviceType.java enum. |
| No system configuration capability | Unresolved in v1 docs | RESOLVED - system_settings table and SystemConfigurationScreen implemented in dev branch. |
| No profile self-service for users | Unresolved in v1 docs | RESOLVED - ProfileScreen implemented for all three roles. |
| No forgot/reset password flow | Unresolved in v1 docs | RESOLVED - ForgotPasswordScreen and ResetPasswordScreen implemented in dev branch. |
| Event request devices not linked to gate logs | GAP-003 | RESOLVED - final schema adds event_device_logs and v_event_device_status with immutable entry/exit history. |

## Remaining Gaps

### GAP-001: device_purpose Enum / Schema Mismatch

| Attribute | Detail |
| --- | --- |
| Severity | High |
| Description | Application device-purpose labels must match the final SQL CHECK constraint exactly. |
| Impact | Any older label such as Organization Use or Other will be rejected by the final database. |
| Recommendation | Align application values to Academic BYOD, School Event, Organization Activity, Temporary Equipment, Other Approved Purpose, PROTOTYPE, and APPLIANCE. |

### GAP-002: users.status - UI / Schema Mismatch

| Attribute | Detail |
| --- | --- |
| Severity | Medium |
| Description | UserManagementScreenController displays "deactivated" as a status option, but the database CHECK constraint only allows 'active', 'inactive', 'pending'. |
| Impact | If the UI sends deactivated as the status value to the backend, the database will reject the UPDATE. |
| Recommendation | Treat deactivated as a UI display label only. The backend should map it to inactive before writing to the database. Document this mapping in the backend service layer. |

### GAP-003: Event Manifest Quantity Ambiguity

| Attribute | Detail |
| --- | --- |
| Severity | High |
| Description | The schema permits quantity > 1, but event_device_logs references one manifest row and cannot represent partial-unit entry, exit, or reconciliation. |
| Impact | A grouped row could incorrectly mark several physical items as moved or returned together. |
| Recommendation | Preserve quantity > 0 and default 1 as defined by the final schema. Before implementation, define whether a scan/reconciliation action applies to every unit represented by a grouped row. |

### GAP-004: users.email Workflow Requirement Is Stricter Than Schema

| Attribute | Detail |
| --- | --- |
| Severity | Medium |
| Description | The final schema contains a unique nullable users.email column, while the onboarding workflow requires email. |
| Impact | Direct database writes can create users without email even though the application workflow requires one. |
| Recommendation | Enforce required email in the onboarding service, or add NOT NULL in a future migration if every account must have email. |

### GAP-005: Railway URL And Environment Variables Undefined

| Attribute | Detail |
| --- | --- |
| Severity | Low |
| Description | Backend base URL, Railway service names, environment variable names, and backup policy are not yet finalized. |
| Impact | Deployment documentation cannot be completed. |
| Recommendation | Define and document the Railway deployment configuration once the backend is deployed. Update docs/architecture/08-deployment-architecture.md. |

### GAP-006: allow_unregistered_devices Enforcement Location

| Attribute | Detail |
| --- | --- |
| Severity | Medium |
| Description | The allow_unregistered_devices setting is seeded as the policy for guards checking in unapproved devices, but the database insertion trigger accepts active pending devices without reading the setting. |
| Impact | If not enforced server-side, guards may bypass the setting from the frontend. |
| Recommendation | Ensure the backend permanent-device check-in service reads system_settings.allow_unregistered_devices before inserting a gate entry for an unapproved device. |

### GAP-007: max_devices_per_student Enforcement Location

| Attribute | Detail |
| --- | --- |
| Severity | Medium |
| Description | The max_devices_per_student system setting is seeded but its enforcement must be implemented in the backend device registration service. |
| Impact | Students may register more devices than the configured limit if not enforced server-side. |
| Recommendation | Implement backend count check on device registration: query active registered devices for the student against system_settings.max_devices_per_student before inserting. |

### GAP-008: Event Policy Enforcement Is Service-Level

| Attribute | Detail |
| --- | --- |
| Severity | High |
| Description | The database validates start <= end but does not enforce event_request_max_duration_days, active event dates during scanning, required return/rejection remarks, or the documented resubmission transition. |
| Impact | Direct or incomplete API writes can bypass event lifecycle policy. |
| Recommendation | Enforce duration, active dates, remarks, role permissions, and returned -> pending resubmission transactionally in EventRequestService. |

### GAP-009: device_logs Update Protection Missing

| Attribute | Detail |
| --- | --- |
| Severity | Medium |
| Description | The final schema describes device_logs as immutable but defines only a DELETE trigger. event_device_logs correctly blocks both UPDATE and DELETE. |
| Impact | Permanent gate history can be modified through a direct UPDATE. |
| Recommendation | Add a BEFORE UPDATE trigger on device_logs using fn_device_log_immutable(). |
