# 05 - Module Architecture

## Module Summary

| Module | Frontend Screens | Backend Components | Database Objects | API Group |
| --- | --- | --- | --- | --- |
| Authentication | Login, dashboards | `AuthController`, `AuthService`, `UserDAO` | `users`, `audit_logs` | `/auth/login` |
| User Management | User Management | `UserController`, `UserService`, `UserDAO` | `users`, `audit_logs` | `/users` |
| Student Management | Student Management, lookup panels | `StudentController`, `StudentService`, `StudentDAO` | `students`, `audit_logs` | `/students` |
| Device Management | Device Management, Pending Approval | `DeviceController`, `DeviceService`, `DeviceDAO` | `devices`, `v_pending_devices`, `v_device_campus_status`, `audit_logs` | `/devices` |
| Event Requests | Event Request | `EventRequestController`, `EventRequestService`, event DAOs | `event_requests`, `event_request_devices`, `v_active_event_requests`, `audit_logs` | `/event-requests` |
| Gate Monitoring | Guard Dashboard, Ingress/Egress, Active Devices | `DeviceLogController`, `DeviceLogService`, `DeviceLogDAO` | `device_logs`, `devices`, `v_device_campus_status`, `audit_logs` | `/device-logs` |
| Reports | Reports | Report service methods across DAOs | Reporting tables/views | Existing endpoint groups until report endpoints are specified |
| Audit | Logs/Audit view | `AuditLogController`, `AuditLogService`, `AuditLogDAO` | `audit_logs`, `fn_write_audit_log()` | `/audit-logs` |

## Module Interaction

1. JavaFX screen collects input and calls the backend API.
2. REST controller maps the request and invokes a service.
3. Service validates role, data, and workflow state.
4. DAO executes parameterized SQL and calls schema views/functions.
5. PostgreSQL constraints/triggers enforce final data rules.
6. Backend returns JSON and HTTP status for JavaFX display.

## Known Design Limitation

`event_request_devices` are not directly related to `device_logs` in the uploaded schema. The Event Requests module can track request/verification/return data, but event-device gate history needs a future schema decision.

## Diagram

See `diagrams/mermaid/module-architecture.mmd`.
