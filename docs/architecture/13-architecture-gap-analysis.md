# 13 - Architecture Gap Analysis

| Gap | Impact | Recommendation | Priority |
| --- | --- | --- | --- |
| Frontend repo is currently a JavaFX starter project. | Target screens, API client, models, and session handling are not implemented. | Add JavaFX packages and screens according to `../system-analysis/07-screen-requirements.md`. | High |
| Backend repo is not present here. | REST API, services, DAOs, scheduler, and Railway deployment cannot be verified. | Create/link backend repository and implement endpoint groups summarized in `05-module-architecture.md`. | High |
| Exact REST DTOs and response envelopes are undefined. | Frontend/backend integration could drift. | Define DTOs before coding API client and controllers. | High |
| Authentication response format is undefined. | Session/token handling is unclear. | Decide token/session payload and logout behavior. | High |
| Event manifest quantity can exceed 1 while logs reference a whole manifest row. | Partial-unit scanning and reconciliation are ambiguous. | Preserve quantity > 0 and define whether each operation applies to every unit in the row. | High |
| Railway env variables are unnamed. | Deployment documentation and code configuration remain incomplete. | Define backend base URL and database env names. | Medium |
| Automatic logout timezone and schedule are not finalized. | Closing-time reconciliation may run at the wrong time. | Configure backend scheduler timezone explicitly. | High |
| Event duration, active-date scans, review remarks, and resubmission are not database-enforced. | API paths could bypass workflow policy. | Enforce them transactionally in EventRequestService. | High |
| `device_logs` has no UPDATE protection trigger. | Permanent gate history can be altered despite immutable design. | Add a BEFORE UPDATE trigger matching event_device_logs protection. | High |
| Backup/restore procedure is missing. | Data recovery is not ready for production. | Document Railway backup frequency, retention, owner, and restore steps. | High |
| API-level report endpoints are not specified. | Report implementation may scatter across modules. | Define report endpoint group or specific report endpoints before implementation. | Medium |

## Needs Confirmation

- Final Railway backend URL.
- Backend database environment variable names.
- Authentication token/session strategy.
- Grouped event-quantity scan and reconciliation semantics.
- Automatic logout time and timezone.
