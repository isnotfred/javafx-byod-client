# 01 - Architecture Overview

## System Purpose

The BYOD Registration and Monitoring System is a desktop Java application for registering and monitoring student-owned academic devices entering and leaving campus. It supports student and device registration, pending registration, temporary/event device tracking, ingress and egress logs, active device monitoring, reports, role-based access, and JDBC-backed database storage.

## Architecture Summary

The recommended architecture is a layered Java desktop application:

- JavaFX presentation layer for screens and controllers.
- Application/controller layer for screen flow and user-action coordination.
- Service layer for business rules, validation, role checks, and workflow decisions.
- DAO/JDBC layer for SQL and database access.
- Relational database for students, devices, logs, users, event devices, and audit logs.
- Utility layer for session state, validation helpers, date/time handling, file/image paths, and logging.

JavaFX is preferred. Swing may be referenced only as an allowed desktop UI alternative from the project brief.

## Main Users

| User | Architecture Relevance |
| --- | --- |
| System Administrator / IT Staff | Needs full administrative modules, approval workflows, user management, reports, and audit visibility. |
| Security Guard / Gate Personnel | Needs fast search, ingress/egress logging, active device monitoring, and pending submission workflows. |
| Student | Indirect user whose data and devices are stored and verified by authorized staff. |

## Main Capabilities

- Login and role-based access.
- Student management.
- Device management.
- Pending registration review.
- Temporary/event device registration.
- Ingress and egress monitoring.
- Search and active device tracking.
- Reports.
- Audit logging for sensitive actions.

## Architectural Constraints

- Desktop application only.
- JDBC database access.
- No current mobile app, web portal, RFID, barcode scanner, GPS tracking, cloud sync, or student self-registration.
- Business rules must not be embedded directly in UI code.
- SQL/database operations must stay in DAO or repository classes.
- Device logs must be preserved for accountability.

## Current Scope vs Future Enhancements

Current architecture covers local/campus desktop deployment with database storage and optional local image file paths. Future integrations such as QR codes, barcode scanners, RFID, cloud sync, email/SMS notification, mobile app, and web portal are documented only in `14-future-architecture-enhancements.md`.

