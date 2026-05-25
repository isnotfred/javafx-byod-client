# 13 - User Interactions

## Purpose

This document describes what each role can view, enter, update, approve, reject, generate, and access under the uploaded schema and 3-tier target architecture.

## Role Summary

| Role | System Access | Primary Interaction |
| --- | --- | --- |
| `admin` | Direct login | Administrative management, approvals, reports, users, and audit review. |
| `guard` | Direct login | Gate search, entry/exit logging, pending submission, event request support, and active-device monitoring. |
| Student | No login | Presents information and devices to authorized staff. |

## Admin Interactions

| Interaction Type | Allowed |
| --- | --- |
| Accessible Screens | Login, Admin Dashboard, Student Management, Device Management, Pending Registration Approval, Event Request, Ingress/Egress, Active Devices, Logs, Reports, User Management. |
| Can View | Students, devices, pending device queue, event requests, event line items, gate logs, derived active-device status, reports, users, and audit history. |
| Can Enter | Student records, device records, event requests, event line items, approval/rejection remarks, report filters, user accounts. |
| Can Update | Student details/status, device details/status, pending device decisions, event request state, user status and roles. |
| Can Approve/Reject | Pending BYOD devices and event requests. |
| Can Generate | Daily, monthly, active-device, registered-device, pending, rejected/inactive, event, and device history reports. |
| Cannot Perform | Update or delete immutable `device_logs` and `audit_logs` rows through normal workflows. |

## Guard Interactions

| Interaction Type | Allowed |
| --- | --- |
| Accessible Screens | Login, Guard Dashboard, Quick Pending Registration, Event Request, Ingress/Egress, Active Devices, limited Logs if allowed. |
| Can View | Search results, student/device details for gate verification, derived campus status, pending/rejected/inactive warnings, active devices, and relevant event request details. |
| Can Enter | Search text, gate notes, pending student/device details, proof remarks, event request details, event device verification fields. |
| Can Update | Gate event state by inserting entry/exit logs; event line-item verification fields where allowed. |
| Can Approve/Reject | None for pending BYOD devices; event request approval remains admin-controlled. |
| Cannot Perform | Manage users, approve/reject pending BYOD devices, deactivate official records, edit immutable logs, or view full audit history unless explicitly granted. |

## Student Interactions

Students do not log in.

| Interaction Type | Allowed |
| --- | --- |
| Can Provide | Student ID, name, course/year level, device details, serial number, and proof details requested by staff. |
| Can Present | Device for manual visual verification during entry and exit. |
| Cannot Perform | Self-register, directly edit records, approve devices, generate reports, or access system data. |

## Permission Matrix

| System Area | `admin` | `guard` | Student |
| --- | --- | --- | --- |
| Login | Yes | Yes | No |
| Student Management | Add, update, search, deactivate | Search through gate workflows; create only through quick pending flow | Provides details |
| Device Management | Add, update, approve, reject, activate, deactivate | Search only; submit pending through quick flow | Provides details |
| Pending Registration | Review, approve, reject | Submit only | Provides details |
| Event Request | Create, review, approve, reject, mark returned | Create/verify where allowed | Provides event/device details |
| Entry/Exit Logging | Yes | Yes | Presents device |
| Active Devices | Yes | Yes | No |
| Reports | Full | Limited operational views only if allowed | No |
| User Management | Yes | No | No |
| Audit Logs | Full admin review | No full audit review by default | No |

## Interaction Rules

| Rule | Description |
| --- | --- |
| Pending device gate logging | Pending devices cannot be logged until approved. |
| Derived campus status | Users see current inside/outside state derived from latest log data. |
| Immutable logs | Users insert new gate/audit rows; they do not edit or delete historical rows. |
| Event request limitation | Event request devices are tracked for request/verification/return; direct gate history requires a future schema decision. |
