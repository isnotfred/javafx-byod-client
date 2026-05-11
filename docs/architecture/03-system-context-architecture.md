# 03 - System Context Architecture

## Context Description

The BYOD system supports campus gate monitoring by giving guards and administrators a shared desktop application backed by a relational database. Students do not directly access the system; they provide device information and present devices for verification.

## External Actors and Systems

| Actor/System | Relationship |
| --- | --- |
| System Administrator | Manages official records, users, reports, approvals, rejections, and exceptions. |
| Security Guard | Searches records, logs ingress/egress, views active devices, and submits pending registrations. |
| Student | Presents device and owner information for verification. |
| Campus Administration Process | Defines policies for pending entry, event equipment, 10:00 PM school closing auto logout, and overrides. |
| Relational Database | Stores students, devices, logs, users, event devices, and audit records. |
| Local File Storage | Stores optional device image files or references. |

## Diagram

See `diagrams/mermaid/system-context.mmd`.
