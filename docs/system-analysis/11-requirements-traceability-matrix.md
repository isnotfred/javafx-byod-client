# 11 - Requirements Traceability Matrix

## Matrix

| Business Rule | Functional Requirements | Screens | Database Tables | Test Scenarios |
| --- | --- | --- | --- | --- |
| BR-001 Only valid students may be associated with regular BYOD devices. Pending student proof is required when guards submit a not-yet-encoded student. | FR-007, FR-012, FR-013, FR-014, FR-022 | Student Management, Quick Pending Registration, Pending Approval, Device Management | students, devices, users | TS-006, TS-007, TS-009, TS-010, TS-032 |
| BR-002 Device must be registered or pending before monitoring. | FR-021, FR-023, FR-027, FR-035, FR-037 | Guard Dashboard, Pending Approval, Ingress/Egress | devices, device_logs | TS-013, TS-017, TS-021 |
| BR-003 Each normal BYOD device belongs to one student owner. | FR-014, FR-020 | Device Management | students, devices | TS-010, TS-011 |
| BR-004 A student may register more than one device. | FR-015 | Student Management, Device Management | students, devices | TS-012 |
| BR-005 Guards visually verify devices before logging. | FR-036, FR-037, FR-043, FR-044 | Guard Dashboard, Ingress/Egress | devices, device_logs | TS-021, TS-025 |
| BR-006 Ingress/egress use automatic timestamps. | FR-038, FR-039, FR-045, FR-046 | Ingress/Egress, Active Devices | device_logs, users | TS-021, TS-025 |
| BR-007 Admin approves/rejects pending registrations. | FR-023, FR-024, FR-025, FR-026 | Pending Approval | devices, users, audit_logs | TS-014, TS-015, TS-016 |
| BR-008 Event equipment is separate from BYOD. | FR-028, FR-029, FR-030, FR-031, FR-032 | Temporary/Event Device, Reports | devices, event_devices, device_logs | TS-018, TS-019, TS-030 |
| BR-009 Event equipment requires proof of approval before guard accepts entry. | FR-033, FR-034, FR-068 | Temporary/Event Device | event_devices, users, audit_logs | TS-018, TS-020, TS-030 |
| BR-010 Inactive or rejected devices cannot enter normally. | FR-018, FR-042, FR-054 | Device Management, Guard Dashboard, Ingress/Egress | devices, audit_logs | TS-022, TS-023, TS-024 |
| BR-011 Device logs must be preserved. | FR-053, FR-068, FR-069 | Active Devices, Reports | device_logs, audit_logs | TS-027, TS-028 |
| BR-012 Reports are available for admin review. | FR-055 to FR-063 | Reports | students, devices, device_logs, event_devices, users | TS-028, TS-029, TS-030, TS-031 |
| BR-013 Devices still inside at 10:00 PM are automatically logged out. | FR-049, FR-050, FR-053, FR-055 | Active Devices, Reports | device_logs | TS-027 |

## Coverage Notes

- Every business rule has at least one linked functional requirement.
- Every major workflow has at least one screen and QA scenario.
- Status separation is covered by FR-020 and the data dictionary.
- Remaining policy-sensitive behavior is documented as Needs Team Confirmation in the system analysis and gap analysis.
