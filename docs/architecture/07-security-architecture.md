# 07 - Security Architecture

## Authentication

Users authenticate through the Spring Boot backend. Passwords are stored as bcrypt or argon2 hashes in `users.password_hash`; plaintext passwords are never stored.

Inactive accounts are denied before dashboard access.

## Role-Based Access Control

| Capability | `admin` | `guard` |
| --- | --- | --- |
| Login | Yes | Yes |
| Search students/devices | Yes | Yes |
| Manage students | Yes | Limited to quick pending flow only |
| Manage permanent BYOD devices | Yes | No |
| Submit pending device registration | Yes | Yes |
| Approve/reject pending device | Yes | No |
| Manage event requests | Yes | Submit/verify where allowed |
| Log approved active device entry/exit | Yes | Yes |
| Generate reports | Yes | Limited operational views only if allowed |
| Manage users | Yes | No |
| View full audit history | Yes | No by default |
| Update/delete immutable logs | No | No |

## Session Or Token Handling

- The backend returns authenticated user context after successful login.
- Frontend stores only the minimum session/token data needed for API calls and screen routing.
- Backend services enforce roles regardless of frontend navigation.
- Logout clears frontend session/token data and records audit where implemented.

## Data Protection

- Keep database credentials only in backend Railway/local configuration.
- Use HTTPS for frontend/backend communication in deployed environments.
- Use parameterized SQL in DAOs.
- Avoid displaying stack traces or raw database errors in JavaFX alerts.
- Avoid exposing local filesystem details when image path handling fails.

## Audit Trail

Audit writes use `fn_write_audit_log()` and standardized `action_type` values. Required audit categories include:

- User login, logout, failed login, account changes.
- Student create/update/deactivate.
- Device register/update/approve/reject/deactivate.
- Gate entry, exit, automatic exit.
- Event request create/approve/return/reject.
- Automatic logout batch.

## Diagram

See `diagrams/mermaid/role-access-flow.mmd`.
