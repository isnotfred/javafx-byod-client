# 11 - Requirements Traceability Matrix

| Business Rule | Functional Requirements | Screens | Database Objects | Test Scenarios |
| --- | --- | --- | --- | --- |
| BR-001, BR-002 Authentication and active accounts | FR-001 to FR-006 | Login, dashboards | `users`, `audit_logs` | TS-001 to TS-005 |
| BR-003 Student deactivation and ownership support | FR-007 to FR-012 | Student Management, Quick Pending Registration | `students`, `devices`, `audit_logs` | TS-006 to TS-008, TS-011 |
| BR-004, BR-005 BYOD device ownership and uniqueness | FR-013 to FR-020 | Device Management | `devices`, `students`, `users`, `audit_logs` | TS-009, TS-010 |
| BR-006, BR-007 Pending device approval and rejection | FR-021 to FR-026 | Quick Pending Registration, Pending Approval | `devices`, `v_pending_devices`, `audit_logs` | TS-011 to TS-017 |
| BR-008 Derived campus presence | FR-020, FR-035 to FR-042, FR-049 | Guard Dashboard, Ingress/Egress, Active Devices | `device_logs`, `v_device_campus_status` | TS-021, TS-022, TS-025, TS-026 |
| BR-009, BR-010 Immutable gate logging and event alternation | FR-037 to FR-042 | Ingress/Egress, Logs | `device_logs`, triggers | TS-021 to TS-028 |
| BR-011 Automatic logout | FR-043 to FR-045 | Active Devices, Logs, Reports | `device_logs`, `audit_logs` | TS-027 |
| BR-012 Event request tracking | FR-027 to FR-034 | Event Request, Reports | `event_requests`, `event_request_devices`, `v_active_event_requests` | TS-018 to TS-020, TS-031 |
| BR-013 Audit immutability | FR-006, FR-055, FR-056 | Logs, Reports | `audit_logs`, `fn_write_audit_log()` | TS-001, TS-003, TS-028, TS-029 |
| Report rules | FR-046 to FR-049 | Reports | All reporting tables/views | TS-030 to TS-032 |
| User management rules | FR-050 to FR-054 | User Management | `users`, `audit_logs` | TS-001 to TS-005 |

## Coverage Notes

- Every business rule has at least one functional requirement and test scenario.
- Event gate logging remains traceable as a documented open schema issue, not an implemented behavior.
- Derived campus status is intentionally linked to `device_logs` and `v_device_campus_status`, not to a stored device field.
