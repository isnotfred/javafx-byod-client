# 02 - Functional Requirements

## Authentication and Role-Based Access

| ID | Requirement |
| --- | --- |
| FR-001 | The system shall require users to log in before accessing application functions. |
| FR-002 | The system shall authenticate users using username and password. |
| FR-003 | The system shall identify the role of the authenticated user. |
| FR-004 | The system shall show only features allowed for the user's role. |
| FR-005 | The system shall prevent inactive users from logging in. |
| FR-006 | The system shall allow users to log out. |

## Student Management

| ID | Requirement |
| --- | --- |
| FR-007 | The system shall allow administrators to add student records. |
| FR-008 | The system shall allow administrators to update student records. |
| FR-009 | The system shall allow administrators and guards to search student records. |
| FR-010 | The system shall prevent duplicate student IDs. |
| FR-011 | The system shall allow administrators to deactivate student records instead of permanently deleting them. |
| FR-012 | The system shall allow a guard to submit a pending student record when a person is manually verified with accepted proof, such as school ID, registration form, enrollment record, or other school-approved proof, but is not yet encoded. The pending student record shall remain Pending until an administrator reviews it and makes it Official or rejects/deactivates it. |

## Device Management

| ID | Requirement |
| --- | --- |
| FR-013 | The system shall allow administrators to register approved student-owned academic devices. |
| FR-014 | The system shall link each normal BYOD device to one student owner. |
| FR-015 | The system shall allow one student to own multiple devices. |
| FR-016 | The system shall prevent duplicate active device serial numbers. |
| FR-017 | The system shall allow administrators to update device details. |
| FR-018 | The system shall allow administrators to set device status to Active or Inactive. |
| FR-019 | The system shall allow optional device image path storage for physical verification. |
| FR-020 | The system shall display registration status, campus status, device status, and device purpose separately. |

## Pending Registration

| ID | Requirement |
| --- | --- |
| FR-021 | The system shall allow security guards to submit unregistered devices as pending registrations. |
| FR-022 | The system shall capture who submitted the pending registration, when it was submitted, and any required student proof details. |
| FR-023 | The system shall route pending registrations to the administrator approval list. |
| FR-024 | The system shall allow administrators to approve pending registrations. |
| FR-025 | The system shall allow administrators to reject pending registrations with a rejection reason. |
| FR-026 | The system shall prevent guards from approving or rejecting pending registrations. |
| FR-027 | The system shall allow pending devices to be logged for temporary entry while waiting for admin approval. |

## Temporary/Event Device Handling

| ID | Requirement |
| --- | --- |
| FR-028 | The system shall support Temporary/Event Device records for approved event equipment. |
| FR-029 | The system shall capture responsible person, event name, event purpose, organization or department, expected exit date/time, and remarks. |
| FR-030 | The system shall classify event equipment separately from Academic BYOD devices. |
| FR-031 | The system shall include temporary/event devices in ingress and egress monitoring. |
| FR-032 | The system shall include temporary/event devices in separate reports. |
| FR-033 | The system shall allow security guards to approve temporary/event device entry at the gate after verifying a paper approval or signed GPOA. |
| FR-034 | The system shall record the event approval document type, document reference or description, verified_by user, and verified_at timestamp for temporary/event devices. |

## Ingress Monitoring

| ID | Requirement |
| --- | --- |
| FR-035 | The system shall allow guards to search by student ID, student name, or device serial number before ingress. |
| FR-036 | The system shall display owner and device details for verification. |
| FR-037 | The system shall allow guards to log ingress for approved active devices currently Outside. |
| FR-038 | The system shall automatically record ingress date/time. |
| FR-039 | The system shall record the user who logged ingress. |
| FR-040 | The system shall update campus status to Inside after successful ingress. |
| FR-041 | The system shall block ingress for devices already Inside. |
| FR-042 | The system shall block ingress for rejected, inactive, or unauthorized pending devices. |

## Egress Monitoring

| ID | Requirement |
| --- | --- |
| FR-043 | The system shall allow guards to search active inside devices before egress. |
| FR-044 | The system shall allow guards to log egress for a device currently Inside. |
| FR-045 | The system shall automatically record egress date/time. |
| FR-046 | The system shall record the user who logged egress. |
| FR-047 | The system shall update campus status to Outside after successful egress. |
| FR-048 | The system shall prevent egress when no active ingress record exists. |
| FR-049 | The system shall automatically log out devices still marked Inside at 10:00 PM. |
| FR-050 | The system shall mark automatic logout records with a system-generated remark and 10:00 PM egress timestamp. |

## Search and Monitoring

| ID | Requirement |
| --- | --- |
| FR-051 | The system shall allow searching records by student ID, student name, serial number, registration status, campus status, device status, and device purpose. |
| FR-052 | The system shall display active devices currently inside campus. |
| FR-053 | The system shall display records automatically logged out at 10:00 PM. |
| FR-054 | The system shall alert users when a searched device is rejected, inactive, or pending. |

## Reports

| ID | Requirement |
| --- | --- |
| FR-055 | The system shall generate a daily ingress-egress report. |
| FR-056 | The system shall generate a monthly monitoring report. |
| FR-057 | The system shall generate an active devices report. |
| FR-058 | The system shall generate a registered devices per student report. |
| FR-059 | The system shall generate a pending registrations report. |
| FR-060 | The system shall generate a rejected and inactive devices report. |
| FR-061 | The system shall generate a temporary/event device report. |
| FR-062 | The system shall generate a device history report. |
| FR-063 | The system shall support report filtering by date range, student, device type, status, purpose, and user where applicable. |

## User Management and Audit

| ID | Requirement |
| --- | --- |
| FR-064 | The system shall allow administrators to add user accounts. |
| FR-065 | The system shall allow administrators to update user roles and account status. |
| FR-066 | The system shall prevent duplicate usernames. |
| FR-067 | The system shall store passwords as hashes, not plain text. |
| FR-068 | The system shall record audit entries for approvals, rejections, event document verification, automatic logout, device status changes, and log corrections. |
| FR-069 | The system shall prevent normal users from permanently deleting device logs. |
