# 08 - Report Requirements

## Overview

All reports are accessible to admin users. Super Admin may also view reports where system-level visibility is needed. Reports read from saved database tables and views; they do not modify any records. Reports shall support export or print functionality.

## Report Types

### Daily Device Traffic Summary

| Item | Detail |
| --- | --- |
| Purpose | Summarized view of device entries and exits for a selected day. |
| Access | admin |
| Filters | Date, student name, device category, status. |
| Data Sources | device_logs, devices, students |
| Key Columns | Student name, device category, serial number, event type (entry/exit), event time, handled by, auto exit flag. |
| Notes | Entries with auto_exit = TRUE should be clearly marked as system-generated. |

### Monthly Device Traffic Summary

| Item | Detail |
| --- | --- |
| Purpose | Aggregated count of device entries and exits per month, broken down by category and student. |
| Access | admin |
| Filters | Month, year, device category. |
| Data Sources | device_logs, devices, students |
| Key Columns | Month, student name, device category, total entries, total exits, total auto exits. |

### Pending Registration Report

| Item | Detail |
| --- | --- |
| Purpose | List all devices currently in registration_status = 'pending' with submission detail. |
| Access | admin |
| Filters | Date submitted range, student ID/name, serial number. |
| Data Sources | v_pending_devices, devices, students |
| Key Columns | Device ID, student ID, student name, device category, serial number, submitted at, remarks (proof details). |

### Active Devices on Campus

| Item | Detail |
| --- | --- |
| Purpose | Real-time snapshot of all devices currently logged as inside campus. |
| Access | admin |
| Filters | Device category, purpose, student name, entry date. |
| Data Sources | v_device_campus_status filtered to campus_status = 'inside' |
| Key Columns | Device ID, student name, device category, serial number, purpose, latest entry time, handled by. |

### Device Frequency Report

| Item | Detail |
| --- | --- |
| Purpose | Historical data showing which devices are brought in most frequently, supporting resource planning. |
| Access | admin |
| Filters | Date range, device category, student name. |
| Data Sources | device_logs, devices, students |
| Key Columns | Device ID, student name, device category, serial number, total entry count, last entry date. |
| Notes | Ordered by entry count descending. Useful for identifying high-frequency or overdue devices. |

### Incident / Override Report

| Item | Detail |
| --- | --- |
| Purpose | Summary of admin overrides, dispute resolutions, rejected registrations, and deactivations for audit and administrative purposes. |
| Access | admin |
| Filters | Date range, action type, student name, device ID. |
| Data Sources | audit_logs, devices, students |
| Key Columns | Audit ID, action type, performed by, target table, target ID, old values, new values, created at. |
| Notes | Filtered to action types relevant to overrides and rejections: DEVICE_REJECTED, DEVICE_DEACTIVATED, STUDENT_DEACTIVATED, USER_DEACTIVATED, USER_ROLE_CHANGED, SYSTEM_CONFIG_UPDATED. |

### Event Device Reconciliation Report

| Item | Detail |
| --- | --- |
| Purpose | Identify event manifest devices that remain inside, have not been reconciled, or need closeout review. |
| Access | admin, super_admin |
| Filters | Event date range, event name, student ID, organization, manifest status, current-day status. |
| Data Sources | event_requests, event_request_devices, event_device_logs, v_event_device_status |
| Key Columns | Request ID, event name, responsible student/person, event device ID, device details, serial number, manifest status, current-day status, latest event time, handled by. |
| Notes | Reconciled devices persist event_request_devices.device_status = 'returned'. Entry status at closeout identifies potentially unreturned hardware. |

## Report Validation Rules

| Rule | Description |
| --- | --- |
| Date Range Required | All time-series reports must have a start date and end date. Start date must not be after end date. |
| SQL-Side Filtering | Reports must apply filters in the database query before returning data to the frontend. |
| Read-Only | Reports display data only. No records are created, updated, or deleted during report generation. |
| Export / Print | Each report screen must provide an export or print action for submission to campus administrators. |
