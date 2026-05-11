# 05 - Module Architecture

## Module Summary

| Module | Purpose | Main Screens | Main Services | Tables Used | Related Requirements |
| --- | --- | --- | --- | --- | --- |
| Authentication and Role Access | Login, logout, sessions, role checks. | Login, dashboards | AuthService, SessionService | users, audit_logs | FR-001 to FR-006 |
| Student Management | Manage student records. | Student Management | StudentService | students, audit_logs | FR-007 to FR-012 |
| Device Management | Manage approved BYOD records and device statuses. | Device Management | DeviceService | devices, students, users, audit_logs | FR-013 to FR-020 |
| Pending Registration | Submit, approve, reject pending records. | Pending Approval, Guard Dashboard | PendingRegistrationService | devices, students, users, audit_logs | FR-021 to FR-027 |
| Temporary/Event Device | Track approved event equipment and approval document verification separately. | Temporary/Event Device | EventDeviceService | devices, event_devices, device_logs | FR-028 to FR-034 |
| Ingress-Egress Monitoring | Log entry and exit. | Monitoring, Active Devices | MonitoringService | devices, device_logs, users | FR-035 to FR-048 |
| Search and Monitoring | Search records and active devices. | Guard Dashboard, Active Devices | SearchService | students, devices, device_logs | FR-049 to FR-052 |
| Reports | Generate monitoring reports. | Reports | ReportService | students, devices, device_logs, event_devices, users | FR-053 to FR-061 |
| User Management | Manage user accounts and roles. | User Management | UserService | users, audit_logs | FR-062 to FR-065 |
| Audit/Logging | Record sensitive actions. | Admin audit view if implemented | AuditService | audit_logs | FR-066 to FR-067 |
| Database Utility | Create connections and manage transactions. | None | DatabaseConnectionProvider | All tables | NFR-011 to NFR-028 |

## Module Interaction

Controllers route user actions to module services. Services validate inputs, enforce roles, coordinate DAOs, update statuses, and record audit entries where required.

## Diagram

See `diagrams/mermaid/module-architecture.mmd`.
