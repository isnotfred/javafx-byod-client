# 08 - Report Requirements

## General Report Rules

- Reports are generated from saved database records.
- Admin users can generate all reports.
- Security guards may view active devices and daily gate records only if allowed by the UI design.
- Date ranges must be valid.
- Output format for current scope is on-screen table view. Print and export are optional future/current stretch features.

## Daily Ingress-Egress Report

| Item | Requirement |
| --- | --- |
| Purpose | Show all entries and exits for a selected day. |
| User | Admin; Guard view optional. |
| Filters | Date, guard, student, device type, purpose, log status. |
| Columns | Date, Student ID, Student/Responsible Person, Device Type, Brand/Model, Serial/Asset Tag, Purpose, Ingress Time, Egress Time, Log Status, Logged In By, Logged Out By, Auto Logout Flag, Remarks. |
| Output | Table; optional print/export. |

## Monthly Monitoring Report

| Item | Requirement |
| --- | --- |
| Purpose | Summarize monitoring activity for a selected month. |
| User | Admin. |
| Filters | Month, year, device purpose, device type. |
| Columns | Date, Total Ingress, Total Egress, Pending Entries, Event Device Entries, Auto Logout Count, Rejected/Inactive Attempts if tracked. |
| Output | Table summary; optional print/export. |

## Active Devices Report

| Item | Requirement |
| --- | --- |
| Purpose | Identify devices currently inside campus. |
| User | Admin; Guard view allowed. |
| Filters | Ingress date, student/responsible person, device type, purpose, guard, overdue expected exit. |
| Columns | Log ID, Device ID, Student ID, Owner/Responsible Person, Device Type, Serial/Asset Tag, Purpose, Ingress Time, Logged In By, Expected Exit, Auto Logout Status, Remarks. |
| Output | Table. |

## Registered Devices Per Student Report

| Item | Requirement |
| --- | --- |
| Purpose | Show devices registered under each student. |
| User | Admin. |
| Filters | Student ID/name, course, section, device type, registration status, device status. |
| Columns | Student ID, Student Name, Course, Section, Device Type, Brand/Model, Serial Number, Registration Status, Device Status, Registered At. |
| Output | Table. |

## Pending Registrations Report

| Item | Requirement |
| --- | --- |
| Purpose | Review registrations waiting for admin decision. |
| User | Admin. |
| Filters | Date submitted, submitted by, student, device type, temporary entry count. |
| Columns | Pending ID/Device ID, Student Details, Device Type, Brand/Model, Serial/Asset Tag, Submitted By, Submitted At, Temporary Entry Count, Remarks. |
| Output | Table. |

## Rejected/Inactive Devices Report

| Item | Requirement |
| --- | --- |
| Purpose | Support review of rejected registrations and inactive devices. |
| User | Admin. |
| Filters | Status, date changed, student, device type, serial number. |
| Columns | Device ID, Student ID, Student Name, Device Type, Serial Number, Registration Status, Device Status, Reason/Remarks, Last Updated By, Last Updated At. |
| Output | Table. |

## Temporary/Event Device Report

| Item | Requirement |
| --- | --- |
| Purpose | Track event equipment separately from regular BYOD. |
| User | Admin. |
| Filters | Event name, date range, responsible person, organization, purpose, campus status. |
| Columns | Device ID, Device Type, Serial/Asset Tag, Responsible Person, Event Name, Organization/Department, Purpose, Approval Document Type, Approval Document Reference, Verified By, Ingress Time, Egress Time, Expected Exit, Campus Status. |
| Output | Table. |

## Device History Report

| Item | Requirement |
| --- | --- |
| Purpose | Show complete ingress-egress history for one device. |
| User | Admin. |
| Filters | Serial number/device ID, date range. |
| Columns | Log ID, Device ID, Serial/Asset Tag, Owner/Responsible Person, Ingress Time, Egress Time, Logged In By, Logged Out By, Entry Type, Auto Logout Flag, Remarks. |
| Output | Table. |
