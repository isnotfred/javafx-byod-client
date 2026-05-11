# 09 - Integration Architecture

## Current Integration Points

| Integration | Direction | Purpose |
| --- | --- | --- |
| JavaFX/Swing UI to services | UI to service | User actions invoke business workflows. |
| Services to DAOs | Service to DAO | Business workflows persist and retrieve data. |
| DAOs to JDBC | DAO to driver | Execute SQL through JDBC. |
| JDBC to relational database | Driver to database | Store and retrieve records. |
| FileChooser to image storage | UI/helper to local storage | Select optional device image files. |

## Integration Rules

- Controllers do not execute SQL.
- Services do not depend on JavaFX controls.
- DAOs do not implement business policy.
- File/image storage must store stable paths or copied managed files, subject to team confirmation.

## Future Integrations Only

- QR code generation.
- Barcode scanner.
- RFID.
- Cloud database sync.
- Email/SMS notification.
- Mobile app.
- Web portal.

These are not part of current implementation scope.

