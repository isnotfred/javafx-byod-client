# 10 - Error Handling And Logging Architecture

## Error Categories

| Category | Backend Handling | Frontend Display |
| --- | --- | --- |
| Validation failure | Service returns HTTP 400/422 with field/action message. | Field message or alert. |
| Duplicate key | DAO/database exception mapped to HTTP 409. | Specific duplicate message. |
| Permission failure | Service returns HTTP 403. | Access denied message. |
| Authentication failure | Auth service returns HTTP 401. | Invalid username or password. |
| Inactive account | Auth service returns HTTP 403. | Account inactive message. |
| Database unavailable | DataSource/DAO failure mapped to HTTP 503. | Backend/database unavailable message. |
| Trigger rule failure | Database exception mapped to HTTP 400/409/422 depending on rule. | Friendly rule-specific message. |
| Unexpected backend error | Global handler logs details and returns HTTP 500. | Generic failure message. |

## Logging Approach

- Backend application logs capture technical diagnostics.
- PostgreSQL `audit_logs` captures business/security history.
- JavaFX displays safe messages and does not show stack traces.
- Audit rows are written through `fn_write_audit_log()`.
- `device_logs` and `audit_logs` are immutable.

## Standard Audit Events

| Category | Actions |
| --- | --- |
| Device lifecycle | `DEVICE_REGISTERED`, `DEVICE_APPROVED`, `DEVICE_REJECTED`, `DEVICE_DEACTIVATED`, `DEVICE_UPDATED` |
| Gate events | `DEVICE_ENTRY`, `DEVICE_EXIT`, `DEVICE_AUTO_EXIT` |
| Students | `STUDENT_CREATED`, `STUDENT_UPDATED`, `STUDENT_DEACTIVATED` |
| Event requests | `EVENT_REQUEST_CREATED`, `EVENT_REQUEST_APPROVED`, `EVENT_REQUEST_RETURNED`, `EVENT_REQUEST_REJECTED` |
| Users | `USER_CREATED`, `USER_UPDATED`, `USER_DEACTIVATED`, `USER_LOGIN`, `USER_LOGOUT`, `USER_LOGIN_FAILED` |
| System | `SYSTEM_AUTO_EXIT_BATCH` |

## Recovery Guidance

- Failed validation must not write partial records.
- Multi-step approve/reject, event request, and gate operations must be transactional.
- Automatic logout should roll back failed batch units or record failures for admin review.
- Database trigger failures should be treated as authoritative.
