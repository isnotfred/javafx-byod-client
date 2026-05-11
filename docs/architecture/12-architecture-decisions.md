# 12 - Architecture Decisions

## ADR-001 Use Desktop Java Application

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Project scope requires a desktop-based application. |
| Decision | Build as a Java desktop application. |
| Consequences | No web/mobile access in current scope; deployment targets campus computers. |

## ADR-002 Prefer JavaFX for UI

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Project brief allows JavaFX/Swing and recommends JavaFX. |
| Decision | Use JavaFX as preferred UI framework. |
| Consequences | Controllers should remain UI-focused and delegate logic to services. |

## ADR-003 Use JDBC for Database Access

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Project requires JDBC-based database storage. |
| Decision | Use JDBC through DAO/repository classes. |
| Consequences | SQL stays out of controllers; DAOs handle persistence. |

## ADR-004 Use Layered Architecture

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | The system needs maintainable UI, rules, and database access. |
| Decision | Separate presentation, controller, service, DAO, database, and utility layers. |
| Consequences | More files/classes, but clearer developer handoff and testability. |

## ADR-005 Use Role-Based Access Control

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Admins and Guards have different permissions. |
| Decision | Enforce roles in UI navigation and service methods. |
| Consequences | Prevents guards from approving, rejecting, deleting logs, managing users, or changing official device status. |

## ADR-006 Use Automatic Timestamping

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Ingress and egress logs must be reliable. |
| Decision | Generate timestamps through the system at save time. |
| Consequences | Users cannot manually alter normal ingress/egress timestamps. |

## ADR-007 Use Pending Registration Workflow

| Field | Value |
| --- | --- |
| Status | Proposed |
| Context | Guards may encounter unregistered devices at the gate. |
| Decision | Allow pending submissions routed to admin review; pending devices may have repeat temporary entries until approved or rejected. |
| Consequences | Faster gate handling but requires strict reporting and approval follow-up. |

## ADR-008 Separate Device Status Concepts

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Registration, campus presence, security condition, and purpose are different concepts. |
| Decision | Separate `registration_status`, `campus_status`, `device_status`, and `device_purpose`. |
| Consequences | Reduces inconsistent workflow logic and report ambiguity. |

## ADR-009 Track Event Equipment Separately

| Field | Value |
| --- | --- |
| Status | Proposed |
| Context | Event equipment is temporary and may not belong to one student. |
| Decision | Track as Temporary/Event Device with responsible person, event details, and paper approval or signed GPOA verification details. |
| Consequences | Requires event-device fields, guard verification fields, and separate reports. |

## ADR-010 Preserve Logs Instead of Deleting

| Field | Value |
| --- | --- |
| Status | Accepted |
| Context | Logs support accountability and reports. |
| Decision | Do not permanently delete device logs through normal application functions. |
| Consequences | Corrections require remarks or audit entries. |
