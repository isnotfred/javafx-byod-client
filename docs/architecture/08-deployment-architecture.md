# 08 - Deployment Architecture

## Deployment Summary

The target deployment uses a JavaFX desktop frontend and Railway-hosted backend/database services.

| Component | Location | Description |
| --- | --- | --- |
| JavaFX Desktop Client | Authorized campus/admin computers | Runs UI screens and calls the backend API. |
| Spring Boot Backend API | Railway | Hosts REST controllers, services, DAOs, validation, and scheduled automatic logout. |
| PostgreSQL Database | Railway | Stores canonical schema and data. |
| GitHub Repositories | GitHub | Frontend and backend version control. |
| Optional Image Storage | To be decided | Stores or references device images. |

## Runtime Flow

1. User opens JavaFX.
2. JavaFX calls Railway backend over HTTPS/JSON.
3. Backend validates request and uses JDBC over TLS to Railway PostgreSQL.
4. PostgreSQL executes constraints/triggers/views/functions.
5. Backend returns HTTP status and JSON response.
6. JavaFX updates the UI.

## Configuration

| Configuration | Owner | Status |
| --- | --- | --- |
| Backend base URL for JavaFX | Frontend config | Needs final Railway URL. |
| PostgreSQL JDBC URL | Backend config | Needs Railway env variable names. |
| Database username/password | Backend config | Needs Railway env variable names. |
| Image path/storage policy | Frontend/backend | Open. |
| Automatic logout schedule timezone | Backend config | Needs confirmation. |

## Backup Considerations

- Back up Railway PostgreSQL regularly.
- Restrict backup access to authorized IT/admin personnel.
- Include image storage in backup scope after storage policy is finalized.
- Document restore procedure before production use.

## Diagram

See `diagrams/mermaid/deployment-view.mmd`.
