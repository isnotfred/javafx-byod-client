# 08 - Report Requirements

## General Report Rules

- Reports are generated from saved database records and schema views.
- Admin users can generate all reports.
- Guards may view active devices and recent gate activity only when allowed by UI policy.
- Date ranges must be valid before backend query execution.
- Large reports must filter in SQL before data is returned to JavaFX.

## Daily Entry/Exit Report

| Item | Requirement |
| --- | --- |
| Purpose | Show all entry and exit events for a selected day. |
| User | `admin`; guard view optional. |
| Filters | Date, user, student, device type, purpose, event type, automatic exit flag. |
| Columns | Event time, event type, student ID, student name, device ID, device type, brand/model, serial number, purpose, handled by, logout type, automatic exit flag, notes. |
| Data Sources | `device_logs`, `devices`, `students`, `users`. |

## Monthly Monitoring Report

| Item | Requirement |
| --- | --- |
| Purpose | Summarize monitoring activity for a month. |
| User | `admin`. |
| Filters | Month, year, device purpose, device type. |
| Columns | Date, total entries, total exits, automatic exits, unique devices, rejected/inactive attempts if tracked by application logs. |
| Data Sources | `device_logs`, `devices`. |

## Active Devices Report

| Item | Requirement |
| --- | --- |
| Purpose | Identify devices currently inside campus. |
| User | `admin`; guard view allowed. |
| Filters | Entry date, student, device type, purpose, user who handled entry. |
| Columns | Device ID, student ID, student name, device type, serial number, purpose, latest event time, derived campus status. |
| Data Sources | `v_device_campus_status`, `devices`, `students`, latest-log query where needed. |

## Registered Devices Per Student Report

| Item | Requirement |
| --- | --- |
| Purpose | Show devices registered under each student. |
| User | `admin`. |
| Filters | Student ID/name, course/year level, device type, registration status, device status. |
| Columns | Student ID, student name, course/year level, device ID, device name, type, brand/model, serial number, purpose, registration status, device status, created at. |
| Data Sources | `students`, `devices`. |

## Pending Registrations Report

| Item | Requirement |
| --- | --- |
| Purpose | Review device registrations waiting for admin decision. |
| User | `admin`. |
| Filters | Date submitted, student, device type, purpose. |
| Columns | Device ID, student ID, student name, course/year level, device name, type, brand/model, serial number, purpose, image path, submitted at, remarks. |
| Data Sources | `v_pending_devices`, `devices`. |

## Rejected/Inactive Devices Report

| Item | Requirement |
| --- | --- |
| Purpose | Support review of rejected registrations and inactive devices. |
| User | `admin`. |
| Filters | Registration status, device status, updated date, student, device type, serial number. |
| Columns | Device ID, student ID, student name, device type, serial number, registration status, device status, remarks, reviewed by, reviewed at, updated at. |
| Data Sources | `devices`, `students`, `users`. |

## Event Request Report

| Item | Requirement |
| --- | --- |
| Purpose | Track event access requests separately from permanent BYOD devices. |
| User | `admin`. |
| Filters | Event name, organization, responsible student, date range, request status, device status. |
| Columns | Request ID, event name, organization, responsible person, student ID, approval document type, approval document reference, start date, end date, request status, device count, line-item device type, quantity, verified by, verified at. |
| Data Sources | `event_requests`, `event_request_devices`, `v_active_event_requests`, `students`, `users`. |
| Limitation | Direct gate entry/exit history for event line items is not available unless a future schema relationship is added. |

## Device History Report

| Item | Requirement |
| --- | --- |
| Purpose | Show complete gate history for one permanent BYOD device. |
| User | `admin`. |
| Filters | Device ID or serial number, date range. |
| Columns | Log ID, event time, event type, student ID, device ID, serial number, handled by, logout type, automatic exit flag, notes. |
| Data Sources | `device_logs`, `devices`, `students`, `users`. |
