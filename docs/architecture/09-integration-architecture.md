# 09 - Integration Architecture

## Current Target Integration Points

| Integration | Direction | Protocol/Mechanism | Purpose |
| --- | --- | --- | --- |
| JavaFX controller to backend REST controller | Frontend -> backend | HTTPS/JSON | Submit user actions and fetch screen data. |
| Backend REST controller to service | Backend internal | Method call | Delegate validated request handling. |
| Service to DAO | Backend internal | Method call | Execute business workflow reads/writes. |
| DAO to PostgreSQL | Backend -> database | JDBC over TLS | Execute parameterized SQL and call schema functions/views. |
| Backend scheduler to service | Backend internal | Scheduled task | Run automatic school-closing exit process. |
| JavaFX to optional image storage | Frontend/backend TBD | File path or upload policy TBD | Store or reference device images. |

## Integration Rules

- JavaFX uses the backend API only.
- Backend API endpoint groups are summarized in `05-module-architecture.md` until a dedicated API contract is restored.
- DAOs must use parameterized SQL.
- Audit writes use the database function `fn_write_audit_log()`.
- Database trigger exceptions are converted to safe HTTP errors.
- Event request devices use dedicated `/api/v1/event-requests/devices/log-entry` and `/log-exit` operations backed by `event_device_logs`.

## Future Integrations Only

- QR code generation.
- Barcode scanners.
- RFID.
- Email/SMS notification.
- Mobile app.
- Web portal.
- Student self-service.
