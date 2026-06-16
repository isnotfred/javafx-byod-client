# BPMN - Pending Device Registration Process

## Participants

| Lane | Responsibility |
| --- | --- |
| Guard | Submits pending student/device details after manual proof verification. |
| Admin | Reviews and approves or rejects pending devices. |
| JavaFX Frontend | Captures forms and displays queues. |
| Spring Boot Backend | Validates and coordinates writes. |
| PostgreSQL | Stores students/devices and exposes the pending queue view. |

## Process

1. Guard searches for student/device at the gate.
2. Gateway: eligible active device found?
3. If yes, proceed to normal gate workflow.
4. If no device is present, no system action is required and the student passes through.
5. If an unregistered device is present, the guard opens Quick Pending Registration.
6. Guard enters student details, proof remarks, and device details.
7. Backend validates required fields, category rules, and duplicate serial number.
8. Backend inserts or links `students`.
9. Backend inserts `devices` with `registration_status = 'pending'`.
10. Backend writes `DEVICE_REGISTERED` audit action.
11. If the guard attempts to check in the pending device, the backend reads `allow_unregistered_devices`.
12. If unapproved-device check-in is disabled, no gate entry is inserted.
13. Admin opens Pending Registration Approval.
14. Backend loads pending devices from `v_pending_devices`.
15. Admin approves or rejects.
16. Approve path sets `registration_status = 'approved'` and reviewer fields.
17. Reject path sets `registration_status = 'rejected'`, reviewer fields, and required remarks.
18. Backend writes `DEVICE_APPROVED` or `DEVICE_REJECTED`.

## Rule

Active pending devices may be checked in through `device_logs` while `allow_unregistered_devices` is true. Rejected and inactive devices remain blocked.
