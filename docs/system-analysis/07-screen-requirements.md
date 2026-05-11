# 07 - Screen Requirements

## Login Screen

| Item | Requirement |
| --- | --- |
| Purpose | Authenticate users. |
| Access | Admin, Security Guard. |
| Fields | Username, Password. |
| Actions | Login, Exit. |
| Validation/Error Messages | Invalid username or password; Account is inactive; Database connection failed. |

## Admin Dashboard

| Item | Requirement |
| --- | --- |
| Purpose | Give administrators a summary and navigation entry point. |
| Access | Admin. |
| Display | Total students, registered devices, pending registrations, devices inside, today ingress, today egress, automatic logout count. |
| Actions | Students, Devices, Pending Approvals, Temporary/Event Devices, Logs, Reports, Users, Logout. |
| Filters | Optional dashboard date filter. |

## Security Guard Dashboard

| Item | Requirement |
| --- | --- |
| Purpose | Support fast gate monitoring. |
| Access | Security Guard. |
| Fields | Search text for student ID, name, or serial number. |
| Actions | Search, Log Ingress, Log Egress, Quick Pending Registration, View Active Devices, Clear, Logout. |
| Display | Student details, device details, warning/status banner, active devices summary. |
| Validation/Error Messages | Device not found; Device already inside; Device not currently inside; Device is inactive; Device is pending approval. |

## Student Management Screen

| Item | Requirement |
| --- | --- |
| Purpose | Manage student records. |
| Access | Admin. |
| Fields | Student ID, first name, last name, course, section, contact number, email, status, record status. |
| Actions | Add, Update, Search, Clear, Deactivate. |
| Table Columns | Student ID, Name, Course, Section, Contact, Email, Status, Record Status. |
| Search/Filters | Student ID, name, course, section, status. |
| Validation/Error Messages | Student ID is required; Student ID already exists; Required fields missing. |

## Device Management Screen

| Item | Requirement |
| --- | --- |
| Purpose | Manage official device records. |
| Access | Admin. |
| Fields | Student owner, device type, brand, model, serial number, asset tag, color, image path, registration status, campus status, device status, device purpose, remarks. |
| Actions | Register, Update, Upload Image, Approve, Reject, Activate, Deactivate, Search, Clear. |
| Table Columns | Device ID, Student ID, Owner, Type, Brand/Model, Serial/Asset Tag, Registration Status, Campus Status, Device Status, Purpose. |
| Search/Filters | Student, serial number, type, registration status, campus status, device status, purpose. |
| Validation/Error Messages | Device serial number is required; Duplicate serial number; Student owner required for Academic BYOD. |

## Quick Pending Registration Screen

| Item | Requirement |
| --- | --- |
| Purpose | Let guards submit pending student/device details when a valid student or device is not yet encoded. |
| Access | Security Guard. |
| Fields | Student ID, student name, course, section, proof type, proof reference/remarks, device type, brand/model, serial number or asset tag, color, device purpose, remarks. |
| Actions | Save Pending Registration, Clear, Cancel. |
| Table Columns | Not applicable for entry form; after save, show pending reference number, submitted by, submitted at, and status. |
| Search/Filters | Optional lookup by student ID, name, serial number, or asset tag before submission. |
| Validation/Error Messages | Student ID is required; Student name is required; Proof type is required for pending student; Proof reference or remarks are required for pending student; Device details are required; Duplicate pending registration exists. |

## Pending Registration Approval Screen

| Item | Requirement |
| --- | --- |
| Purpose | Let admins review pending student/device submissions. |
| Access | Admin. |
| Fields | Student details, student proof type, student proof reference/remarks, device details, submitted by, submitted at, approval remarks, rejection reason. |
| Actions | View Details, Approve, Reject, Refresh. |
| Table Columns | Pending ID/Device ID, Student Details, Proof Type, Proof Reference, Device Details, Submitted By, Submitted At, Temporary Entry Used, Status. |
| Search/Filters | Date submitted, student, serial number, submitted by. |
| Validation/Error Messages | Rejection reason is required; Duplicate serial number conflict; Missing owner details; Missing or unacceptable student proof. |

## Temporary/Event Device Registration Screen

| Item | Requirement |
| --- | --- |
| Purpose | Register event equipment separately from normal BYOD. |
| Access | Admin; Security Guard. |
| Fields | Device type, brand/model, serial/asset tag, responsible person, contact, event name, organization/department, purpose, approval document type, approval document reference/description, expected exit date/time, remarks. |
| Actions | Verify Approval Document, Save Event Device, Log Ingress, Search, Clear. |
| Table Columns | Device ID, Type, Serial/Asset Tag, Responsible Person, Event Name, Purpose, Approval Document Type, Verified By, Expected Exit, Campus Status. |
| Search/Filters | Event name, responsible person, date, purpose, campus status. |
| Validation/Error Messages | Responsible person is required; Event name is required; Paper approval or signed GPOA details are required; Expected exit date is invalid. |

## Ingress/Egress Monitoring Screen

| Item | Requirement |
| --- | --- |
| Purpose | Log entry and exit transactions. |
| Access | Admin, Security Guard. |
| Fields | Search text, optional remarks. |
| Actions | Search, Log Ingress, Log Egress, Quick Pending Registration, Clear. |
| Display | Student/owner details, device details, status indicators, last ingress, last egress. |
| Table Columns | Device ID, Owner, Type, Serial, Registration Status, Campus Status, Device Status, Last Ingress, Last Egress. |
| Validation/Error Messages | Already inside; No active ingress; Device inactive; Device registration is pending approval. |

## Active Devices Inside Campus Screen

| Item | Requirement |
| --- | --- |
| Purpose | Show devices currently inside before 10:00 PM and automatically logged-out records after 10:00 PM. |
| Access | Admin, Security Guard. |
| Actions | Refresh, Search, Filter, View Details, Log Egress. |
| Table Columns | Log ID, Device ID, Owner/Responsible Person, Device Type, Serial/Asset Tag, Ingress Time, Logged In By, Purpose, Expected Exit, Auto Logout Status. |
| Search/Filters | Student, device type, purpose, date, guard, overdue expected exit. |
| Validation/Error Messages | No active devices found; Database connection failed. |

## Reports Screen

| Item | Requirement |
| --- | --- |
| Purpose | Generate administrative reports. |
| Access | Admin. |
| Fields | Report type, date range, student, device type, status, purpose, user. |
| Actions | Generate, Clear Filters, Print optional, Export optional. |
| Table Columns | Depend on selected report type in `08-report-requirements.md`. |
| Validation/Error Messages | Start date cannot be after end date; No records found. |

## User Management Screen

| Item | Requirement |
| --- | --- |
| Purpose | Manage system user accounts. |
| Access | Admin. |
| Fields | Username, password/new password, full name, role, status. |
| Actions | Add, Update, Deactivate, Reset Password, Search, Clear. |
| Table Columns | User ID, Username, Full Name, Role, Status, Created At. |
| Search/Filters | Username, role, status. |
| Validation/Error Messages | Username is required; Username already exists; Role is required; Password is required for new account. |
