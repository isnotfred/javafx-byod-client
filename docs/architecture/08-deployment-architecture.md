# 08 - Deployment Architecture

## Deployment Summary

The current scope is a Java desktop application installed on authorized campus computers. The application connects to a relational database through JDBC.

## Runtime Components

| Component | Description |
| --- | --- |
| Desktop Machine | Runs the Java application used by Admins or Guards. |
| Java Runtime | Required runtime for JavaFX/Swing application. |
| JavaFX Application | Main desktop client and UI. |
| JDBC Driver | Database connectivity driver. |
| Relational Database | Local or campus server database. **Needs Team Confirmation.** |
| Local Image Storage | Stores optional uploaded device images or image paths. **Needs Team Confirmation.** |

## Deployment Assumptions

- The system runs on authorized campus desktops.
- The database can be local to one machine or centralized on a campus server.
- If multiple gate computers are used, a centralized database is recommended.
- Backup and restore procedures are required but not yet defined.
- Cloud deployment is not current scope.

## Backup Considerations

- Back up the relational database regularly.
- Include uploaded image folder backup if image files are stored locally.
- Restrict backup access to authorized IT/admin personnel.

## Diagram

See `diagrams/mermaid/deployment-view.mmd`.

