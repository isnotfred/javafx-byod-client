# 04 - Application Architecture

## Target Layers

| Layer | Location | Responsibility |
| --- | --- | --- |
| JavaFX Presentation | Frontend repo | FXML views, controls, styles, alerts, table rendering. |
| JavaFX Controller | Frontend repo | Screen events, form binding, validation display, API calls. |
| API Client | Frontend repo | HTTPS/JSON requests to Spring Boot and response parsing. |
| REST Controller | Backend repo | Endpoint mapping, request deserialization, response status/body. |
| Service | Backend repo | Business rules, role checks, validation, transactions, workflow orchestration. |
| DAO | Backend repo | Parameterized SQL, RowMapper logic, calls to database functions/views. |
| Database | Railway PostgreSQL | Tables, keys, constraints, triggers, views, functions, indexes. |
| Utility/Config | Frontend/backend as needed | Session/token storage, password hashing, date handling, validation helpers, DataSource configuration. |

## Layer Rules

- JavaFX controllers do not contain SQL.
- JavaFX does not use JDBC or database credentials.
- REST controllers do not call DAOs directly.
- Services do not reference JavaFX classes.
- DAOs do not implement role policy.
- Database constraints remain the final guard for critical integrity rules.

## Suggested Backend Package Direction

```text
com.pup.byod.backend
  controller
  service
  dao
  model
  config
  util
  exception
```

## Suggested Frontend Package Direction

```text
pup.edu.ph.it.javabyodsystem
  controller
  model
  api
  session
  util
```

## Diagram

See `diagrams/mermaid/application-layer-architecture.mmd`.
