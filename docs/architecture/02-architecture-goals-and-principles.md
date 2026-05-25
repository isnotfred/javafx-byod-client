# 02 - Architecture Goals And Principles

## Goals

| Goal | Description |
| --- | --- |
| Clear tier boundaries | JavaFX handles UI, Spring Boot handles workflows, PostgreSQL enforces data integrity. |
| Secure access | Role checks protect admin and guard capabilities. |
| Reliable gate logging | Entry/exit history is append-only and trigger-protected. |
| Database-backed integrity | Constraints, views, triggers, and functions enforce critical rules. |
| Deployable backend | Spring Boot and PostgreSQL target Railway deployment. |
| Maintainable documentation | Docs remain aligned with the uploaded schema and API boundary. |

## Principles

- Frontend controllers call backend APIs, not DAOs or JDBC.
- Backend controllers map HTTP requests and responses only.
- Backend services own business rules, validation, role checks, and transactions.
- Backend DAOs own SQL and use parameterized queries.
- Audit writes go through `fn_write_audit_log()`.
- Database triggers protect rules that must not be bypassed by application bugs.
- Reports query saved records and views with filters applied in SQL.
- Open design gaps are documented explicitly instead of hidden.
