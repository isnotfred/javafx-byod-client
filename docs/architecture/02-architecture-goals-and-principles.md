# 02 - Architecture Goals and Principles

## Architecture Goals

| Goal | Description |
| --- | --- |
| Simplicity | Keep the architecture suitable for a student Java desktop project. |
| Maintainability | Separate screens, business rules, and database access. |
| Role-based security | Restrict features by Admin and Security Guard roles. |
| Data integrity | Protect unique students, unique device identifiers, valid statuses, and valid log transitions. |
| Fast gate search | Prioritize quick lookup by student ID, name, and serial number. |
| Reliable logging | Preserve accurate ingress and egress timestamps. |
| Auditability | Record sensitive actions such as approvals, rejections, overrides, and status changes. |
| Local desktop suitability | Support operation from authorized campus desktop machines. |

## Architecture Principles

- Keep JavaFX controllers focused on UI events, screen updates, and navigation.
- Use service classes for business rules, validation, permission checks, and workflow decisions.
- Use DAO or repository classes for SQL and JDBC operations.
- Validate input before database writes.
- Use transactions for multi-step operations such as ingress logging and campus-status updates.
- Use deactivation or status changes instead of permanent deletion for important records.
- Preserve device logs for audit and reporting.
- Keep status concepts separate: registration status, campus status, device status, and device purpose.
- Clearly mark unresolved policy decisions as **Needs Team Confirmation**.

