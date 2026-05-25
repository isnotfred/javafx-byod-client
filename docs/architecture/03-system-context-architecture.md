# 03 - System Context Architecture

## Context Description

The BYOD system gives campus admins and guards a JavaFX desktop client connected to a Spring Boot backend API. The backend stores and retrieves all data from PostgreSQL on Railway. Students do not directly access the system.

## External Actors And Systems

| Actor/System | Relationship |
| --- | --- |
| Admin | Uses JavaFX to manage records, approvals, reports, users, and audit history. |
| Guard | Uses JavaFX to search, submit pending devices, manage gate operations, and view active devices. |
| Student | Presents information and devices to staff. |
| Campus Administration Process | Defines policies for event access, closing-time logout, and operational review. |
| Spring Boot Backend API | Receives HTTPS/JSON requests from JavaFX and owns business workflows. |
| Railway PostgreSQL | Stores canonical data, constraints, views, triggers, and functions. |
| Railway Hosting | Runs backend API and PostgreSQL database. |
| Optional Image Storage | Stores or references device images once policy is finalized. |

## Diagram

See `diagrams/mermaid/system-context.mmd`.
