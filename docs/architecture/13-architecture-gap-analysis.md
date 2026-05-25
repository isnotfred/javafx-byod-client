# 13 - Architecture Gap Analysis

| Gap | Impact | Recommendation | Priority |
| --- | --- | --- | --- |
| Frontend repo is currently a JavaFX starter project. | Target screens, API client, models, and session handling are not implemented. | Add JavaFX packages and screens according to `../system-analysis/07-screen-requirements.md`. | High |
| Backend repo is not present here. | REST API, services, DAOs, scheduler, and Railway deployment cannot be verified. | Create/link backend repository and implement endpoint groups summarized in `05-module-architecture.md`. | High |
| Exact REST DTOs and response envelopes are undefined. | Frontend/backend integration could drift. | Define DTOs before coding API client and controllers. | High |
| Authentication response format is undefined. | Session/token handling is unclear. | Decide token/session payload and logout behavior. | High |
| Event request devices are not linked to gate logs. | Event device entry/exit reporting is not directly supported by the schema. | Decide between request-only tracking, an added FK, or companion permanent device rows. | High |
| Railway env variables are unnamed. | Deployment documentation and code configuration remain incomplete. | Define backend base URL and database env names. | Medium |
| Automatic logout timezone and schedule are not finalized. | Closing-time reconciliation may run at the wrong time. | Configure backend scheduler timezone explicitly. | High |
| Image storage policy is open. | Device image paths may fail across machines. | Choose frontend-managed files, backend upload storage, or shared storage. | Medium |
| Backup/restore procedure is missing. | Data recovery is not ready for production. | Document Railway backup frequency, retention, owner, and restore steps. | High |
| API-level report endpoints are not specified. | Report implementation may scatter across modules. | Define report endpoint group or specific report endpoints before implementation. | Medium |

## Needs Confirmation

- Final Railway backend URL.
- Backend database environment variable names.
- Authentication token/session strategy.
- Event device gate-logging strategy.
- Image storage location and backup scope.
- Automatic logout time and timezone.
