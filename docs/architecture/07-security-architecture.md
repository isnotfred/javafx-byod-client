# 07 - Security Architecture

## Authentication

Users authenticate through username and password. Passwords must be stored as hashes, not plain text. Inactive users are denied login.

## Role-Based Access Control

| Capability | Admin | Security Guard |
| --- | --- | --- |
| Manage students | Yes | No |
| Search students/devices | Yes | Yes |
| Manage approved devices | Yes | No |
| Submit pending registration | Yes | Yes |
| Approve/reject pending registration | Yes | No |
| Register event device | Yes | Yes |
| Log ingress/egress | Yes | Yes |
| Generate reports | Yes | Limited view if allowed |
| Manage users | Yes | No |
| Reactivate inactive device | Needs Team Confirmation | No |
| Delete logs | No normal deletion | No |

## Session Handling

- Store current user ID, role, and display name in a session context after login.
- Controllers must check session state before opening protected screens.
- Services must enforce role permissions even if a screen is opened incorrectly.
- Clear session state on logout.

## Data Protection Recommendations

- Hash passwords with a current password hashing algorithm.
- Avoid storing sensitive values in UI logs or error dialogs.
- Restrict direct database access to authorized application users.
- Store uploaded image paths consistently and avoid exposing system folders in UI messages.

## Audit Trail

Recommended audit events:

- Login attempt.
- Student created/updated/deactivated.
- Device created/updated/status changed.
- Pending registration submitted.
- Pending registration approved/rejected.
- Ingress logged.
- Egress logged.
- Device activated or deactivated.
- Report generated.
- Admin override or log correction.

## Diagram

See `diagrams/mermaid/role-access-flow.mmd`.
