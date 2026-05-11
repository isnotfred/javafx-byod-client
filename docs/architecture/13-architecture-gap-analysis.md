# 13 - Architecture Gap Analysis

| Gap | Impact | Recommendation | Priority |
| --- | --- | --- | --- |
| Source code is still a JavaFX starter structure. | No real architecture is implemented yet. | Introduce packages for controller, service, DAO, model, db, and util. | High |
| DAO/service/controller package structure is missing. | Business logic and SQL may become mixed into UI classes. | Enforce layered architecture before adding features. | High |
| Audit logging is only recommended. | Sensitive actions may not be traceable. | Add `audit_logs` table and AuditService if time allows. | Medium |
| Event-device document verification details are not implemented. | Guards can accept event devices at the gate, but missing paper approval or signed GPOA details would weaken accountability. | Add approval document type/reference and guard verification fields to event-device records and reports. | High |
| Approval/rejection/override fields are not implemented. | Pending decisions and overrides may lack accountability. | Include reviewer, timestamp, reason, and remarks fields in schema. | High |
| Error handling and logging strategy is not implemented. | Users may see unclear failures and developers may lack diagnostics. | Define validation, database, permission, and file error handling. | Medium |
| Backup strategy and database location are unresolved. | Data loss or multi-gate inconsistency may occur. | Confirm local vs centralized database and backup process. | High |
| Pending student proof workflow is documented but not implemented. | Guards may submit incomplete pending student records if proof fields are missing. | Add proof type, proof reference/remarks, submitted_by, submitted_at, and admin review handling in the student service and DAO. | High |
| Pending-device repeat entry monitoring is not yet designed. | Pending devices may enter repeatedly while waiting for admin approval, so admins need visibility into repeated temporary use. | Include temporary entry counts in pending reports and prioritize admin review of frequently used pending devices. | Medium |
| Automatic school-closing logout schedule is not implemented. | Devices still Inside need a reliable 10:00 PM egress record. | Implement automatic logout at 10:00 PM with system-generated remarks. | High |
| Guard permissions and editing limits need confirmation. | Guards may change records beyond intended authority. | Restrict guards to search, logging, and submission unless confirmed otherwise. | High |
| Indexes and report query strategy are not implemented. | Search and reports may become slow. | Add indexes and date-filtered report queries. | Medium |
| Uploaded image storage location needs confirmation. | Image links may break across machines. | Choose managed local folder or shared storage path. | Medium |
| Database deployment is unresolved. | Multi-user behavior depends on database location. | Confirm local per machine vs centralized campus server. | High |

## Needs Team Confirmation

- Should admins be allowed to reactivate inactive devices?
- Are logs ever editable, or should corrections be made through remarks/audit entries?
- Where should uploaded device images be stored?
- Will the database be local per machine or centralized on a campus server?
