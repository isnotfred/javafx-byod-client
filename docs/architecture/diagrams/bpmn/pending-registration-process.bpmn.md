# BPMN-Style Process: Pending Registration

## Lanes

| Lane | Responsibility |
| --- | --- |
| Student | Presents unregistered device and identity details. |
| Security Guard | Performs initial search and submits pending details. |
| System | Validates minimum data and routes pending record. |
| Database | Stores pending student/device record. |
| Administrator | Approves or rejects pending registration. |

## Process

1. Student presents an unregistered device at the gate.
2. Guard searches student and device records.
3. Gateway: student/device found and approved?
   - Yes: proceed to normal ingress.
   - No: guard opens pending registration.
4. Guard enters required student/device details.
5. Gateway: student not yet encoded?
   - Yes: guard enters proof type and proof reference or remarks from accepted school proof.
   - No: continue with existing student reference.
6. System validates minimum student/device details, required proof fields for pending students, and duplicate serial/asset tag.
7. Database stores pending student/device record with submitter and timestamp.
8. System permits temporary ingress while the device remains pending.
9. System keeps registration status as Pending and includes the device in pending reports.
10. Administrator reviews pending record and proof details.
11. Gateway: approve or reject?
   - Approve: pending student becomes Official if applicable, and device becomes officially registered.
   - Reject: pending student is rejected/deactivated if invalid, and device `registration_status` is marked Rejected with reason.
12. Audit record is stored for the decision.
