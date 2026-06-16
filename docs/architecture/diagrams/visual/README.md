# Visual Diagram Notes

Use the diagram source files in this folder set as the current visual baseline.

## Current Diagram Themes

- JavaFX Desktop Frontend -> Spring Boot REST API -> Railway PostgreSQL.
- Backend-only JDBC access.
- PostgreSQL views, triggers, and functions as part of the architecture.
- Immutable `device_logs` and `audit_logs`.
- Derived campus status from latest log rows.
- Active pending devices may be gate-logged when policy permits.
- Event requests use `event_requests`, `event_request_devices`, `event_device_logs`, and event status views.
- Three operational roles: `super_admin`, `admin`, and `guard`.
- Super Admin owns user onboarding, role changes, deactivation, and `system_settings`.
- All roles can manage their own profile and use password recovery.
- Permanent-device serial numbers are required and globally unique. The schema retains optional devices.image_path, but no upload workflow is defined.

## Recommended Stakeholder Diagrams

- System context.
- Container/deployment view.
- Database ERD.
- Gate entry/exit sequence.
- Pending registration sequence.
- Automatic logout sequence.
- Event request flow.
- Event request lifecycle state diagram.
- Profile management and password recovery sequences.
- Super Admin user and system configuration sequences.
- DFD Level 0 context.
- DFD Level 1 system data flow.
- DFD Level 2 gate monitoring, pending registration, event request, and Super Admin management flows.
