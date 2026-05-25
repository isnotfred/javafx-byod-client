# Visual Diagram Notes

Use the diagram source files in this folder set as the current visual baseline.

## Current Diagram Themes

- JavaFX Desktop Frontend -> Spring Boot REST API -> Railway PostgreSQL.
- Backend-only JDBC access.
- PostgreSQL views, triggers, and functions as part of the architecture.
- Immutable `device_logs` and `audit_logs`.
- Derived campus status from latest log rows.
- Pending devices blocked from gate logging until approved.
- Event requests modeled with `event_requests` and `event_request_devices`.

## Recommended Stakeholder Diagrams

- System context.
- Container/deployment view.
- Database ERD.
- Gate entry/exit sequence.
- Pending registration sequence.
- Automatic logout sequence.
- Event request flow.
- DFD Level 0 context.
- DFD Level 1 system data flow.
- DFD Level 2 gate monitoring, pending registration, and event request flows.
