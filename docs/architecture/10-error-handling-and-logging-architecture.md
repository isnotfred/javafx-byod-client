# 10 - Error Handling and Logging Architecture

## Error Handling Categories

| Category | Handling Approach |
| --- | --- |
| Validation errors | Show clear user-facing message before database write. |
| Duplicate records | Reject save and identify conflicting student ID, username, serial number, or asset tag. |
| Invalid ingress/egress | Reject action and explain status problem, such as already Inside or no active ingress. |
| Permission errors | Deny action and show access denied message. |
| Database errors | Show non-technical failure message and log technical details. |
| File upload errors | Show image selection/storage failure and allow user to continue without image if optional. |

## Logging Approach

- Use application logs for technical errors and troubleshooting.
- Use audit logs for business/security events.
- Do not expose stack traces in UI.
- Include user ID, timestamp, action, entity, and remarks for audit events.

## Recommended Audit Events

- Login attempt.
- Student created or updated.
- Device created or updated.
- Pending registration submitted.
- Pending registration approved or rejected.
- Ingress logged.
- Egress logged.
- Device activated or deactivated.
- Automatic school-closing logout.
- Report generated.
- Admin override or correction.

## Recovery Guidance

- Failed validation should not write partial records.
- Multi-step monitoring actions should be transactional.
- If egress update fails after log update, the transaction should roll back.
- If automatic logout fails, the system should log the failure and keep affected records available for admin review.
