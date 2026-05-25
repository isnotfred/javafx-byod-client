# 10 - Gap Analysis And Recommendations

| Gap | Impact | Recommendation | Priority |
| --- | --- | --- | --- |
| Current repository source is still a JavaFX starter project. | Implementation does not yet match the target 3-tier documentation. | Treat these docs as the target baseline and create separate frontend/backend implementation work items. | High |
| Spring Boot backend repository is not present in this repo. | API, service, DAO, Railway deployment, and backend tests cannot be verified here. | Create or link the backend repository and keep API docs synchronized. | High |
| Exact REST DTOs are not specified. | Frontend/backend integration may drift. | Define request/response models before implementation. | High |
| Authentication token/session format is not specified. | Role propagation and logout behavior remain unclear. | Decide whether the backend returns a token, session object, or simple authenticated user payload. | High |
| Event request devices have no direct relationship to `device_logs`. | Event device gate history cannot be queried from the uploaded schema alone. | Decide whether event line items need a gate-log FK, a companion `devices` row, or request-only tracking. | High |
| Image storage path policy is not specified. | Image paths may break between machines or deployments. | Choose managed frontend storage, shared storage, or backend-uploaded files. | Medium |
| Railway environment variable names are not specified. | Deployment setup is incomplete. | Define `DATABASE_URL` or JDBC host/user/password variables and backend base URL. | Medium |
| Backup and restore process is not specified. | Railway data loss recovery is not documented. | Add database backup frequency, owner, and restore procedure. | High |
| Automatic logout schedule owner is not fully specified. | Runtime behavior may differ between frontend and backend. | Implement automatic logout as a backend scheduled task and document schedule/timezone. | High |
| Audit viewer permissions are not finalized. | Guard access to logs may be too broad or too narrow. | Keep full audit history admin-only unless explicitly approved. | Medium |

## Resolved Legacy Conflicts

| Legacy Topic | Resolution |
| --- | --- |
| Stored device presence field | Replaced by derived latest-log state and `v_device_campus_status`. |
| Pending temporary gate entry | Removed; uploaded trigger allows only approved active devices to be logged. |
| Single event-device table | Replaced by `event_requests` and `event_request_devices`. |
| Update-style gate logs | Replaced by immutable append-only `device_logs` rows. |
| Direct desktop-to-database architecture | Replaced by JavaFX -> Spring Boot REST API -> Railway PostgreSQL. |

## Recommended Next Steps

1. Create backend implementation tasks for controller/service/DAO/API contracts.
2. Decide the event device gate-logging model before report implementation.
3. Define REST DTOs and authentication response shape.
4. Confirm Railway deployment environment variables and backup policy.
5. Add implementation tests for database trigger behavior.
