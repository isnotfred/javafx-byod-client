# 14 - Data Flow Diagrams

## Purpose

This document defines the formal Data Flow Diagram (DFD) package for the BYOD Device Management System. It describes how data moves between external actors, the JavaFX desktop frontend, the Spring Boot backend processes, and Railway PostgreSQL data stores.

The DFDs are documentation only. They do not change the schema, API, or application source code.

## DFD Notation

| DFD Element | Representation In This Document | Meaning |
| --- | --- | --- |
| External entity | Named actor node | A person, organization process, or system outside the BYOD application boundary. |
| Process | Numbered process node | A transformation or validation step performed by the system. |
| Data store | `D#` data store node | A persistent PostgreSQL table or derived read view. |
| Data flow | Labeled arrow | Data passed between entities, processes, and stores. |

All write access to PostgreSQL flows through Spring Boot backend processes. The JavaFX frontend never reads or writes the database directly.

## DFD Level 0 - Context Diagram

Level 0 treats the whole BYOD system as one process.

```mermaid
flowchart LR
    Admin["External Entity: Admin"]
    Guard["External Entity: Guard"]
    Student["External Entity: Student"]
    Policy["External Entity: Campus Administration Process"]
    Scheduler["External Entity: Backend Scheduler"]
    System(("0. BYOD Device Management System"))
    DB[("Railway PostgreSQL Data Stores")]

    Admin -->|"student/device/user/report/audit requests"| System
    System -->|"admin dashboards, approvals, reports, audit views"| Admin
    Guard -->|"search, pending registration, event request, entry/exit data"| System
    System -->|"eligibility results, warnings, active-device data"| Guard
    Student -->|"student identity and device details"| Guard
    Policy -->|"event, access, and closing-time rules"| System
    Scheduler -->|"automatic logout trigger"| System
    System -->|"validated reads and writes"| DB
    DB -->|"records, derived status, queues, reports"| System
```

Source: `../architecture/diagrams/mermaid/dfd-level-0-context.mmd`

## DFD Level 1 - System Processes

Level 1 decomposes the system into major data processes and stores.

```mermaid
flowchart TB
    Admin["Admin"]
    Guard["Guard"]
    Student["Student"]
    Policy["Campus Administration Process"]
    Scheduler["Backend Scheduler"]

    P0(("0.1 Capture and Present JavaFX UI Data"))
    P1(("1. Authenticate User"))
    P2(("2. Manage Students"))
    P3(("3. Manage Devices and Pending Approval"))
    P4(("4. Manage Event Requests"))
    P5(("5. Log Entry/Exit"))
    P6(("6. Generate Reports"))
    P7(("7. Manage Users"))
    P8(("8. Write Audit Logs"))
    P9(("9. Run Automatic Logout"))

    D1[("D1 users")]
    D2[("D2 students")]
    D3[("D3 devices")]
    D4[("D4 event_requests")]
    D5[("D5 event_request_devices")]
    D6[("D6 device_logs")]
    D7[("D7 audit_logs")]
    V1[("V1 derived views")]

    Admin -->|"screen input and commands"| P0
    Guard -->|"screen input and commands"| P0
    P0 -->|"credentials"| P1
    P1 <-->|"account and role data"| D1
    P1 -->|"login events"| P8

    P0 -->|"student maintenance data"| P2
    P0 -->|"student data through pending flow"| P2
    P2 <-->|"student records"| D2
    P2 --> P8

    P0 -->|"device registration and decisions"| P3
    P0 -->|"pending device submission"| P3
    Student -->|"identity and device details"| Guard
    P3 <-->|"device records"| D3
    P3 -->|"pending queue reads"| V1
    P3 --> P8

    P0 -->|"event request decisions"| P4
    P0 -->|"event request and verification data"| P4
    Policy -->|"event access rules"| P4
    P4 <-->|"event headers"| D4
    P4 <-->|"event device line items"| D5
    P4 -->|"active event request reads"| V1
    P4 --> P8

    P0 -->|"entry/exit action and notes"| P5
    P0 -->|"entry/exit action when needed"| P5
    P5 -->|"device eligibility reads"| D3
    P5 -->|"student display reads"| D2
    P5 <-->|"gate event rows"| D6
    P5 -->|"derived status reads"| V1
    P5 --> P8

    P0 -->|"report filters"| P6
    P6 -->|"student/device/log/event/audit reads"| D2
    P6 --> D3
    P6 --> D4
    P6 --> D5
    P6 --> D6
    P6 --> D7
    P6 --> V1
    P6 -->|"report results"| P0

    P0 -->|"user account changes"| P7
    P7 <-->|"user records"| D1
    P7 --> P8

    Scheduler -->|"closing-time run signal"| P9
    P9 -->|"inside-device reads"| V1
    P9 -->|"automatic exit rows"| D6
    P9 --> P8

    P8 -->|"standard audit rows"| D7
    P1 -->|"login result"| P0
    P2 -->|"student save/search result"| P0
    P3 -->|"device queue/decision result"| P0
    P4 -->|"event request result"| P0
    P5 -->|"gate action result"| P0
    P7 -->|"user management result"| P0
    P0 -->|"screen results and alerts"| Admin
    P0 -->|"screen results and alerts"| Guard
```

Source: `../architecture/diagrams/mermaid/dfd-level-1-system.mmd`

## DFD Level 2 - Gate Monitoring

This DFD details device search, eligibility checking, entry/exit logging, derived campus status, and audit writing.

```mermaid
flowchart TB
    Guard["Guard"]
    Admin["Admin"]

    P50(("5.0 Capture Gate Screen Input"))
    P51(("5.1 Search Device"))
    P52(("5.2 Read Derived Campus Status"))
    P53(("5.3 Validate Gate Eligibility"))
    P54(("5.4 Insert Entry/Exit Log"))
    P55(("5.5 Return Gate Result"))
    P8(("8. Write Audit Logs"))

    D2[("D2 students")]
    D3[("D3 devices")]
    D6[("D6 device_logs")]
    D7[("D7 audit_logs")]
    V1[("V1 v_device_campus_status")]

    Guard -->|"student ID, name, or serial number"| P50
    Admin -->|"student ID, name, or serial number"| P50
    P50 -->|"search request"| P51
    P51 -->|"student lookup"| D2
    P51 -->|"device lookup"| D3
    P51 -->|"matching device and owner"| P52
    P52 -->|"latest derived status read"| V1
    P52 -->|"device state and last event"| P53
    P53 -->|"approved, active, alternating event validation"| D3
    P53 -->|"latest event validation"| D6
    P53 -->|"valid entry or exit request"| P54
    P54 -->|"immutable event row"| D6
    P54 -->|"DEVICE_ENTRY or DEVICE_EXIT"| P8
    P8 -->|"audit row"| D7
    P54 -->|"save result"| P55
    P55 -->|"success, warning, or validation error"| P50
    P50 -->|"screen result"| Guard
    P50 -->|"screen result"| Admin
```

Source: `../architecture/diagrams/mermaid/dfd-level-2-gate-monitoring.mmd`

## DFD Level 2 - Pending Registration

This DFD details quick pending submission and admin approval/rejection. Pending devices are stored in `devices` but are not gate-loggable until approved.

```mermaid
flowchart TB
    Guard["Guard"]
    Student["Student"]
    Admin["Admin"]

    P30(("3.0 Capture Quick Pending Screen Input"))
    P31(("3.1 Capture Pending Details"))
    P32(("3.2 Validate Student and Device Data"))
    P33(("3.3 Store Pending Device"))
    P34(("3.4 Load Pending Approval Queue"))
    P35(("3.5 Decide Pending Registration"))
    P8(("8. Write Audit Logs"))

    D2[("D2 students")]
    D3[("D3 devices")]
    D7[("D7 audit_logs")]
    V2[("V2 v_pending_devices")]

    Student -->|"identity and device details"| Guard
    Guard -->|"student, proof remarks, device details"| P30
    P30 -->|"pending registration form data"| P31
    P31 -->|"pending registration data"| P32
    P32 -->|"student existence check"| D2
    P32 -->|"serial uniqueness check"| D3
    P32 -->|"validated pending data"| P33
    P33 -->|"insert/link student record"| D2
    P33 -->|"device row with registration_status pending"| D3
    P33 -->|"DEVICE_REGISTERED"| P8
    P8 -->|"audit row"| D7

    Admin -->|"open approval screen"| P30
    P30 -->|"pending queue request"| P34
    P34 -->|"pending queue read"| V2
    V2 -->|"pending devices with student details"| P34
    P34 -->|"pending approval list"| P30
    P30 -->|"pending approval list"| Admin
    Admin -->|"approve or reject with remarks"| P30
    P30 -->|"approval decision data"| P35
    P35 -->|"update registration_status and reviewer data"| D3
    P35 -->|"DEVICE_APPROVED or DEVICE_REJECTED"| P8
```

Source: `../architecture/diagrams/mermaid/dfd-level-2-pending-registration.mmd`

## DFD Level 2 - Event Requests

This DFD details event request headers, line-item devices, verification, and reporting data. Event request devices are request/verification records only unless a future schema relationship is added.

```mermaid
flowchart TB
    Admin["Admin"]
    Guard["Guard"]
    Student["Student"]
    Policy["Campus Administration Process"]

    P40(("4.0 Capture Event Request Screen Input"))
    P41(("4.1 Capture Event Request"))
    P42(("4.2 Validate Event Request"))
    P43(("4.3 Store Event Header and Line Items"))
    P44(("4.4 Verify Event Device Line Item"))
    P45(("4.5 Review Event Request"))
    P46(("4.6 Read Active Event Requests"))
    P8(("8. Write Audit Logs"))

    D2[("D2 students")]
    D4[("D4 event_requests")]
    D5[("D5 event_request_devices")]
    D7[("D7 audit_logs")]
    V3[("V3 v_active_event_requests")]

    Student -->|"responsible student details"| Guard
    Policy -->|"event access rules and document requirements"| P42
    Admin -->|"event request data"| P40
    Guard -->|"event request data"| P40
    P40 -->|"event request form data"| P41
    P41 -->|"header and line-item data"| P42
    P42 -->|"responsible student check"| D2
    P42 -->|"validated request data"| P43
    P43 -->|"event header"| D4
    P43 -->|"event device line items"| D5
    P43 -->|"EVENT_REQUEST_CREATED"| P8

    Guard -->|"verification result"| P40
    P40 -->|"verification result"| P44
    P44 -->|"verified_by and verified_at"| D5

    Admin -->|"approve, reject, or mark returned"| P40
    P40 -->|"review decision data"| P45
    P45 -->|"request status and reviewer data"| D4
    P45 -->|"EVENT_REQUEST_APPROVED, RETURNED, or REJECTED"| P8

    Admin -->|"active request queue request"| P40
    Guard -->|"active request queue request"| P40
    P40 -->|"active request queue request"| P46
    P46 -->|"active event request read"| V3
    P46 -->|"active event requests with device counts"| P40
    P40 -->|"active event requests with device counts"| Admin
    P40 -->|"active event requests with device counts"| Guard
    P8 -->|"audit row"| D7
```

Source: `../architecture/diagrams/mermaid/dfd-level-2-event-requests.mmd`

## DFD Control Notes

| Area | Required DFD Rule |
| --- | --- |
| Frontend/database access | JavaFX sends data to Spring Boot only; it does not directly access PostgreSQL. |
| Pending devices | Pending devices flow to approval data stores and queues, not to gate logs. |
| Event request devices | Event request device rows are request/verification records; direct gate-log linkage is an open schema decision. |
| Audit | Sensitive actions flow through the audit process and then to `audit_logs`. |
| Derived views | Views are read-only DFD stores used for status, pending queue, and active event request reads. |
