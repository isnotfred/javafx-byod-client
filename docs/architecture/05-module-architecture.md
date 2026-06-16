# 05 - Module Architecture

## Module Summary

| Module | Frontend Screens | Backend Components | Database Objects | API Group |
| --- | --- | --- | --- | --- |
| Authentication | Login, dashboards | `AuthController`, `AuthService`, `UserDAO` | `users`, `audit_logs` | `/auth/login` |
| User Management | User Management | `UserController`, `UserService`, `UserDAO` | `users`, `audit_logs` | `/users` |
| Student Management | Student Management, lookup panels | `StudentController`, `StudentService`, `StudentDAO` | `students`, `audit_logs` | `/students` |
| Device Management | Device Management, Pending Approval | `DeviceController`, `DeviceService`, `DeviceDAO` | `devices`, `v_pending_devices`, `v_device_campus_status`, `audit_logs` | `/devices` |
| Event Requests | Event Request, Temporary Event Device Guard Panel | `EventRequestController`, `EventRequestService`, event DAOs | `event_requests`, `event_request_devices`, `event_device_logs`, `v_active_event_requests`, `v_event_device_status` | `/api/v1/event-requests` |
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

## Event Device Boundary

Temporary event devices use `event_device_logs`, not permanent `device_logs`. Each manifest row has a positive quantity that defaults to 1, and each log references the whole manifest row.

## Diagram

See `diagrams/mermaid/module-architecture.mmd`.
