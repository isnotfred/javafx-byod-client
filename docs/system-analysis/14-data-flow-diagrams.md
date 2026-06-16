# 14 - Data Flow Diagrams

## Purpose

This document defines the formal Data Flow Diagram (DFD) package for the BYOD Device Management System. It describes how data moves between external actors, the JavaFX desktop frontend, the Spring Boot backend processes, and Railway PostgreSQL data stores.

The DFDs are documentation only. They do not change the schema, API, or application source code.

## DFD Notation

| DFD Element | Representation In This Document | Meaning |
| --- | --- | --- |
| External entity | Named actor node | A person, organization process, or system outside the BYOD application boundary. |
| Process | Numbered process node | A transformation or validation step performed by the system. |
| Data store | D# data store node | A persistent PostgreSQL table or derived read view. |
| Data flow | Labeled arrow | Data passed between entities, processes, and stores. |

All write access to PostgreSQL flows through Spring Boot backend processes. The JavaFX frontend never reads or writes the database directly.

## DFD Level 0 - Context Diagram

Level 0 treats the whole BYOD system as one process.

```mermaid
flowchart LR
    SuperAdmin["External Entity: Super Admin"]
    Admin["External Entity: Admin"]
    Guard["External Entity: Guard"]
    Student["External Entity: Student"]
    Policy["External Entity: Campus Administration Process"]
    Scheduler["External Entity: Backend Scheduler"]
    System(("0. BYOD Device Management System"))
    DB[("Railway PostgreSQL Data Stores")]

    SuperAdmin -->|"user mgmt, system config, role mgmt requests"| System
    System -->|"user lists, config panels, audit confirmations"| SuperAdmin
    Admin -->|"student/device/report/audit requests"| System
    System -->|"admin dashboards, approvals, reports, audit views"| Admin
    Guard -->|"search, pending registration, authorized event submission, entry/exit data"| System
    System -->|"eligibility results, warnings, active-device data"| Guard
    Student -->|"student identity and device details"| Guard
    Policy -->|"event, access, and closing-time rules"| System
    Scheduler -->|"automatic logout trigger"| System
    System -->|"validated reads and writes"| DB
    DB -->|"records, derived status, queues, reports"| System
```

Source: ../architecture/diagrams/mermaid/dfd-level-0-context.mmd

## DFD Level 1 - System Processes

Level 1 decomposes the system into major data processes and stores.

![DFD Level 1 - System Processes](../architecture/diagrams/visual/dfd-level-1-system.png)

```mermaid
flowchart TB
    SuperAdmin["Super Admin"]
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
    P7(("7. Manage Profile"))
    P8(("8. Write Audit Logs"))
    P9(("9. Run Automatic Logout"))
    P10(("10. Manage System Users"))
    P11(("11. Manage System Configuration"))

    D1[("D1 users")]
    D2[("D2 students")]
    D3[("D3 devices")]
    D4[("D4 event_requests")]
    D5[("D5 event_request_devices")]
    D6[("D6 device_logs")]
    D7[("D7 audit_logs")]
    D8[("D8 system_settings")]
    D9[("D9 event_device_logs")]
    V1[("V1 derived views")]

    SuperAdmin -->|"screen input and commands"| P0
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
    P3 -->|"system settings check"| D8
    P3 --> P8

    P0 -->|"event submission, review, scan, reconciliation"| P4
    Policy -->|"event access rules"| P4
    P4 <-->|"event headers"| D4
    P4 <-->|"event device line items"| D5
    P4 <-->|"event entry/exit rows"| D9
    P4 -->|"duration and policy reads"| D8
    P4 -->|"active event request reads"| V1
    P4 -->|"event device status reads"| V1

    P0 -->|"entry/exit action and notes"| P5
    P5 -->|"device eligibility reads"| D3
    P5 -->|"student display reads"| D2
    P5 <-->|"gate event rows"| D6
    P5 -->|"derived status reads"| V1
    P5 -->|"system settings check"| D8
    P5 --> P8

    P0 -->|"report filters"| P6
    P6 -->|"student/device/log/event/audit reads"| D2
    P6 --> D3
    P6 --> D4
    P6 --> D5
    P6 --> D6
    P6 --> D7
    P6 --> D9
    P6 --> V1
    P6 -->|"report results"| P0

    P0 -->|"profile update data"| P7
    P7 <-->|"user records"| D1
    P7 --> P8

    Scheduler -->|"closing-time run signal"| P9
    P9 -->|"inside-device reads"| V1
    P9 -->|"automatic exit rows"| D6
    P9 --> P8

    P0 -->|"user account changes"| P10
    SuperAdmin -->|"user mgmt commands"| P0
    P10 <-->|"user records"| D1
    P10 --> P8

    P0 -->|"system setting changes"| P11
    SuperAdmin -->|"system config commands"| P0
    P11 <-->|"system settings"| D8
    P11 --> P8

    P8 -->|"standard audit rows"| D7
    P1 -->|"login result"| P0
    P2 -->|"student save/search result"| P0
    P3 -->|"device queue/decision result"| P0
    P4 -->|"event request result"| P0
    P5 -->|"gate action result"| P0
    P7 -->|"profile update result"| P0
    P10 -->|"user management result"| P0
    P11 -->|"system config result"| P0
    P0 -->|"screen results and alerts"| SuperAdmin
    P0 -->|"screen results and alerts"| Admin
    P0 -->|"screen results and alerts"| Guard
```

Source: ../architecture/diagrams/mermaid/dfd-level-1-system.mmd

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
    D8[("D8 system_settings")]
    V1[("V1 v_device_campus_status")]

    Guard -->|"student ID, name, or serial number"| P50
    Admin -->|"student ID, name, or serial number"| P50
    P50 -->|"search request"| P51
    P51 -->|"student lookup"| D2
    P51 -->|"device lookup"| D3
    P51 -->|"no device found -> no log -> end"| P55
    P51 -->|"matching device and owner"| P52
    P52 -->|"latest derived status read"| V1
    P52 -->|"device state and last event"| P53
    P53 -->|"approved/pending, active validation"| D3
    P53 -->|"allow_unregistered_devices read for pending entry"| D8
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

Note: Students without devices pass through the gate without any system interaction. No process node or data flow is triggered in the DFD for deviceless gate passage.

Source: ../architecture/diagrams/mermaid/dfd-level-2-gate-monitoring.mmd

## DFD Level 2 - Pending Registration

This DFD details quick pending submission and admin approval/rejection. Active pending devices are stored in devices and may be checked in while allow_unregistered_devices is true.

```mermaid
flowchart TB
    Guard["Guard"]
    Student["Student"]
    Admin["Admin"]

    P30(("3.0 Capture Quick Pending Screen Input"))
    P31(("3.1 Capture Pending Details"))
    P32(("3.2 Validate Student and Device Data"))
    P34(("3.3 Store Pending Device"))
    P35(("3.4 Load Pending Approval Queue"))
    P36(("3.5 Decide Pending Registration"))
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
    P32 -->|"validated pending data"| P34
    P34 -->|"insert/link student record"| D2
    P34 -->|"device row with registration_status pending"| D3
    P34 -->|"DEVICE_REGISTERED"| P8
    P8 -->|"audit row"| D7

    Admin -->|"open approval screen"| P30
    P30 -->|"pending queue request"| P35
    P35 -->|"pending queue read"| V2
    V2 -->|"pending devices with student details"| P35
    P35 -->|"pending approval list"| P30
    P30 -->|"pending approval list"| Admin
    Admin -->|"approve or reject with remarks"| P30
    P30 -->|"approval decision data"| P36
    P36 -->|"update registration_status and reviewer data"| D3
    P36 -->|"DEVICE_APPROVED or DEVICE_REJECTED"| P8
```

Source: ../architecture/diagrams/mermaid/dfd-level-2-pending-registration.mmd

## DFD Level 2 - Event Requests

This DFD details request auto-approval/manual review, positive-quantity manifests, event-device scanning, reconciliation, reporting, and settings.

```mermaid
flowchart TB
    Submitter["Admin or Authorized Submitter"]
    Admin["Admin Reviewer"]
    Guard["Guard"]
    Student["Student"]
    Policy["Campus Administration Process"]

    P40(("4.0 Capture Event Request UI"))
    P41(("4.1 Validate Header, Duration, and Manifest"))
    P42(("4.2 Store and Auto-Approve Submission"))
    P43(("4.3 Review Manual Queue"))
    P44(("4.4 Resubmit Returned Request"))
    P45(("4.5 Read Guard Request and Manifest"))
    P46(("4.6 Log Event Device Entry/Exit"))
    P47(("4.7 Reconcile Event Device"))
    P48(("4.8 Generate Reconciliation Report"))
    D2[("D2 students")]
    D4[("D4 event_requests")]
    D5[("D5 event_request_devices")]
    D8[("D8 system_settings")]
    D9[("D9 event_device_logs")]
    V3[("V3 v_active_event_requests")]
    V4[("V4 v_event_device_status")]

    Student -->|"identity, event, and device details"| Submitter
    Policy -->|"document and access rules"| P41
    Submitter -->|"request data"| P40
    P40 -->|"schema-defined header and positive-quantity manifest"| P41
    P41 -->|"student check"| D2
    P41 -->|"duration setting read"| D8
    P41 -->|"validated request"| P42
    P42 -->|"approved request header"| D4
    P42 -->|"approved manifest rows"| D5

    Admin -->|"approve, return, or reject with remarks"| P43
    P43 <-->|"manual request status and reviewer data"| D4
    P43 -->|"cascade manifest status"| D5

    Submitter -->|"correct returned request"| P44
    P44 -->|"PendingApproval persisted as pending"| D4
    P44 -->|"updated manifest"| D5

    Guard -->|"student ID or event name lookup"| P45
    P45 -->|"active request read"| V3
    P45 -->|"manifest status read"| V4
    P45 -->|"request and manifest result"| Guard

    Guard -->|"selected entry or exit action"| P46
    P46 -->|"approved request and active date read"| D4
    P46 -->|"manifest eligibility read"| D5
    P46 <-->|"immutable entry/exit rows"| D9
    P46 -->|"latest status read"| V4

    Guard -->|"verify/reconcile action"| P47
    Admin -->|"verify/reconcile action"| P47
    P47 -->|"device_status returned"| D5

    Admin -->|"report filters"| P48
    P48 -->|"request and manifest reads"| D4
    P48 --> D5
    P48 -->|"entry/exit history"| D9
    P48 -->|"latest status"| V4
    P48 -->|"outstanding and reconciled devices"| Admin
```

Source: ../architecture/diagrams/mermaid/dfd-level-2-event-requests.mmd

## DFD Level 2 - Super Admin System Management

This DFD details Super Admin user management, role assignment, and system configuration processes.

```mermaid
flowchart TB
    SuperAdmin["Super Admin"]

    P100(("10.0 Capture User Mgmt Screen Input"))
    P101(("10.1 Onboard New User"))
    P102(("10.2 Update User"))
    P103(("10.3 Change User Role"))
    P104(("10.4 Deactivate User"))
    P110(("11.0 Capture System Config Screen Input"))
    P111(("11.1 Read System Settings"))
    P112(("11.2 Update System Setting"))
    P8(("8. Write Audit Logs"))

    D1[("D1 users")]
    D7[("D7 audit_logs")]
    D8[("D8 system_settings")]

    SuperAdmin -->|"onboard data"| P100
    P100 -->|"new user data"| P101
    P101 -->|"insert user with status pending"| D1
    P101 -->|"ADMIN_CREATED or GUARD_CREATED"| P8

    P100 -->|"update data"| P102
    P102 -->|"update full name or status"| D1
    P102 -->|"ADMIN_UPDATED or GUARD_UPDATED"| P8

    P100 -->|"role change data"| P103
    P103 -->|"update role"| D1
    P103 -->|"USER_ROLE_CHANGED"| P8

    P100 -->|"deactivate command"| P104
    P104 -->|"set status inactive"| D1
    P104 -->|"ADMIN_DEACTIVATED or GUARD_DEACTIVATED_BY_SUPER"| P8

    SuperAdmin -->|"config update data"| P110
    P110 -->|"read all settings"| P111
    P111 -->|"settings read"| D8
    P111 -->|"settings list"| P110
    P110 -->|"updated setting data"| P112
    P112 -->|"update setting value"| D8
    P112 -->|"SYSTEM_CONFIG_UPDATED"| P8

    P8 -->|"audit row"| D7
    P100 -->|"operation result"| SuperAdmin
    P110 -->|"config result"| SuperAdmin
```

Source: ../architecture/diagrams/mermaid/dfd-level-2-super-admin-system-management.mmd

## DFD Control Notes

| Area | Required DFD Rule |
| --- | --- |
| Frontend/database access | JavaFX sends data to Spring Boot only; it does not directly access PostgreSQL. |
| Pending devices | Active pending devices may receive an entry row when allow_unregistered_devices is true. |
| Deviceless gate passage | Students without devices generate no DFD flows; the gate process terminates with no system event. |
| Event request devices | A manifest row has quantity > 0, defaults to 1, and is the row referenced by event_device_logs. |
| Event reconciliation | Reconciled workflow state is persisted as event_request_devices.device_status = 'returned'. |
| Audit | Actions with defined audit behavior flow through Process 8. The final schema defines EVENT_REQUEST_* action values, but the event workflow does not itself state when those audit rows are written. |
| Derived views | Views are read-only stores for permanent status, pending queue, active requests, and event-device status. |
| System settings | D8 system_settings is read by Process 3 (device registration) and Process 5 (gate monitoring) to enforce configurable policy. It is written only by Process 11 (Manage System Configuration). |
| Super Admin scope | Processes 10 and 11 are exclusively accessible to the super_admin role. |
