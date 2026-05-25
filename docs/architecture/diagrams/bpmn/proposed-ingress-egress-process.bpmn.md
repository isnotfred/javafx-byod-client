# BPMN - Gate Entry/Exit Process

## Participants

| Lane | Responsibility |
| --- | --- |
| Guard | Searches, verifies, and submits entry/exit action. |
| JavaFX Frontend | Captures input and displays backend result. |
| Spring Boot Backend | Validates role, device state, and workflow rules. |
| PostgreSQL | Enforces trigger rules and stores immutable logs. |

## Process

1. Guard searches by student ID, name, or serial number.
2. JavaFX sends search request to the backend.
3. Backend queries `devices`, `students`, and latest log state or `v_device_campus_status`.
4. Backend returns device eligibility and derived campus state.
5. Guard visually verifies the physical device.
6. Gateway: action is entry or exit.
7. Entry path:
   - Backend attempts to insert `device_logs` with `event_type = 'entry'`.
   - PostgreSQL blocks unapproved, inactive, or consecutive entry cases.
8. Exit path:
   - Backend attempts to insert `device_logs` with `event_type = 'exit'` and `logout_type = 'manual'`.
   - PostgreSQL blocks unapproved, inactive, or consecutive exit cases.
9. Backend writes audit through `fn_write_audit_log()`.
10. JavaFX displays success or a safe validation error.
11. Backend scheduler later inserts automatic exit rows for devices still inside at school closing.
