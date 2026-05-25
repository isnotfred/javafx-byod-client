# 14 - Future Architecture Enhancements

These items are outside the current baseline unless separately approved.

| Enhancement | Description |
| --- | --- |
| Event device gate-log relationship | Add a direct way to record entry/exit for `event_request_devices` if event equipment needs full gate history. |
| QR code or barcode support | Speed up device lookup while keeping manual fallback. |
| RFID support | Automate device identification at gates. |
| Web portal | Add browser-based admin or student self-service workflows. |
| Mobile app | Add mobile gate monitoring or student-facing features. |
| Email/SMS notifications | Notify students/admins about approvals, rejections, or overdue devices. |
| Dedicated report endpoints | Formalize `/reports/*` API contracts for large report workflows. |
| Centralized image storage | Move image handling to backend-managed object/file storage. |
| Backup automation | Automate Railway PostgreSQL backup verification and restore drills. |
| Observability | Add structured logs, metrics, and uptime checks for the backend. |
