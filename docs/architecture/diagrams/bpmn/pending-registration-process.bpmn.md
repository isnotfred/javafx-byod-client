# BPMN - Pending Device Registration Process

## Participants

| Lane | Responsibility |
| --- | --- |
| Guard | Submits pending student/device details after manual proof verification. |
| Admin | Reviews and approves or rejects pending devices. |
| JavaFX Frontend | Captures forms and displays queues. |
| Spring Boot Backend | Validates and coordinates writes. |
| PostgreSQL | Stores students/devices and exposes pending queue view. |

## Process

1. Guard searches for student/device at the gate.
2. Gateway: approved active device found?
3. If yes, proceed to normal gate workflow.
4. If no, guard opens Quick Pending Registration.
5. Guard enters student details, proof remarks, and device details.
6. Backend validates required fields and duplicate serial number.
7. Backend inserts or links `students`.
8. Backend inserts `devices` with `registration_status = 'pending'`.
9. Backend writes `DEVICE_REGISTERED` audit action.
10. Admin opens Pending Registration Approval.
11. Backend loads pending devices from `v_pending_devices`.
12. Admin approves or rejects.
13. Approve path sets `registration_status = 'approved'` and reviewer fields.
14. Reject path sets `registration_status = 'rejected'`, reviewer fields, and required remarks.
15. Backend writes `DEVICE_APPROVED` or `DEVICE_REJECTED`.

## Rule

Pending devices cannot be logged through `device_logs` until approved.
