# BPMN-Style Process: Proposed Ingress-Egress

## Lanes

| Lane | Responsibility |
| --- | --- |
| Student | Presents device and identity information. |
| Security Guard | Searches, verifies, and logs ingress/egress. |
| System | Validates records and statuses. |
| Database | Stores device, status, and log updates. |
| Administrator | Reviews exceptions and reports. |

## Process

1. Student arrives at entry or exit point with device.
2. Guard searches by student ID, name, serial number, or asset tag.
3. System retrieves student, device, and latest log data.
4. Gateway: record found?
   - Yes: system displays verification details.
   - No: guard may submit pending registration if policy allows.
5. Guard visually verifies the physical device.
6. Gateway: action is ingress or egress?
   - Ingress: system validates Approved or eligible Pending, Active, and Outside.
   - Egress: system validates active open ingress and Inside.
7. System writes timestamped log and updates campus status.
8. Database stores the transaction.
9. System displays success or validation error.
10. At 10:00 PM, system automatically logs out devices still marked Inside.
11. Administrator reviews auto-logout, pending entries, and reports as needed.
