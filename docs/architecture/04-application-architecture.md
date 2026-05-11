# 04 - Application Architecture

## Recommended Layers

| Layer | Responsibility |
| --- | --- |
| Presentation Layer | JavaFX FXML views, forms, tables, alerts, and screen layouts. |
| Application/Controller Layer | JavaFX controllers, navigation, event handling, form binding, and UI state updates. |
| Service Layer | Business rules, validation, role checks, status transitions, workflow orchestration, and transaction boundaries. |
| Data Access Layer | DAO/repository classes using JDBC and SQL queries. |
| Database Layer | Relational tables, constraints, indexes, and persisted records. |
| Utility Layer | Session context, password hashing, date/time handling, validators, file/image helpers, and logging helpers. |

## Layer Rules

- Controllers call services, not DAOs directly.
- Services call DAOs and utilities.
- DAOs contain SQL and map result sets to domain objects.
- Database constraints protect critical data integrity.
- Utilities must not own business workflows.

## Suggested Package Direction

```text
pup.edu.ph.it.javabyodsystem
  app
  controller
  model
  service
  dao
  db
  util
```

## Diagram

See `diagrams/mermaid/application-layer-architecture.mmd`.

