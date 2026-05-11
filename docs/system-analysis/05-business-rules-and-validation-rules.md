# 05 - Business Rules and Validation Rules

## Business Rules

| ID | Business Rule | Notes |
| --- | --- | --- |
| BR-001 | Only valid students may be associated with regular BYOD devices. | Pending student submission is allowed only when manually verified with accepted school proof. |
| BR-002 | A device must be registered or submitted as pending before it can be monitored. | Pending devices may receive temporary entry while waiting for admin approval. |
| BR-003 | Each normal BYOD device must belong to one student owner. | Event devices use responsible person instead of normal ownership. |
| BR-004 | A student may register more than one device. | Duplicate serial numbers are not allowed. |
| BR-005 | Security guards must visually verify devices before logging ingress or egress. | System supports this with displayed details and images. |
| BR-006 | Ingress and egress must use automatic system timestamps. | Manual timestamp editing is not allowed for normal users. |
| BR-007 | Admin users approve or reject pending registrations. | Guards cannot approve/reject. |
| BR-008 | Event equipment must be classified separately from regular BYOD devices. | Use Temporary/Event Device purpose. |
| BR-009 | Event equipment must have proof of approval before guard accepts it for entry. | Accept paper approval or signed GPOA details. |
| BR-010 | Inactive and rejected devices cannot enter through the normal workflow. | Admin reactivation or approval changes must be audit-tracked if implemented. |
| BR-011 | Device logs must be preserved for audit and reports. | Use corrections/remarks instead of deletion. |
| BR-012 | Reports must be available for administrative review. | Report data must match saved records. |
| BR-013 | Devices still inside at 10:00 PM must be automatically logged out. | Use 10:00 PM egress timestamp and system-generated remark. |

## Validation Rules

### Student Registration

| ID | Validation Rule |
| --- | --- |
| VR-001 | Student ID is required and must be unique. |
| VR-002 | First name and last name are required. |
| VR-003 | Course and section are required for normal student records. |
| VR-004 | Inactive students cannot receive new approved devices unless reactivated by admin. |
| VR-005 | Pending student records submitted by guards must include student ID, student name, course/section if available, proof type, proof reference or remarks, submitted_by, submitted_at, and `record_status = Pending`. |

### Device Registration

| ID | Validation Rule |
| --- | --- |
| VR-006 | Device type is required. |
| VR-007 | Brand and serial number are required for normal BYOD devices. |
| VR-008 | Serial number must be unique among active device records. |
| VR-009 | Normal BYOD devices must reference a student or pending student record. |
| VR-010 | `registration_status` must be Pending, Approved, or Rejected. |
| VR-011 | `campus_status` must be Inside or Outside. |
| VR-012 | `device_status` must be Active or Inactive. |
| VR-013 | `device_purpose` must be Academic BYOD, School Event, Organization Activity, Temporary Equipment, or Other Approved Purpose. |

### Pending Registration

| ID | Validation Rule |
| --- | --- |
| VR-014 | A guard may submit but not approve or reject pending registrations. |
| VR-015 | Pending records must store submitted_by and submitted_at. |
| VR-016 | Pending student proof type must be School ID, Registration Form, Enrollment Record, or Other School-Approved Proof. |
| VR-017 | Pending student proof reference or remarks are required when the pending student is not yet encoded in the system. |
| VR-018 | Approval must store approved_by and approved_at. |
| VR-019 | Rejection must store rejected_by, rejected_at, and rejection_reason. |
| VR-020 | A pending device may be logged for temporary ingress while waiting for admin approval. |

### Temporary/Event Devices

| ID | Validation Rule |
| --- | --- |
| VR-021 | Temporary/event devices require responsible person, event name, purpose, expected exit date/time, and approval document details. |
| VR-022 | Approval document type must be Paper Approval, Signed GPOA, or Other Approved Document. |
| VR-023 | Guard must record who verified the approval document and when it was verified. |
| VR-024 | Temporary/event devices must not be counted as normal student BYOD unless linked to a student-owned device. |
| VR-025 | Expected exit date/time must not be earlier than ingress time. |

### Ingress

| ID | Validation Rule |
| --- | --- |
| VR-026 | Device must exist as Approved or eligible Pending before ingress can be logged. |
| VR-027 | Device must currently be Outside. |
| VR-028 | Device must not be Rejected, Inactive, or unauthorized pending. |
| VR-029 | System must create only one active open ingress per device. |
| VR-030 | Ingress timestamp and logged_in_by are system-generated/stored. |

### Egress

| ID | Validation Rule |
| --- | --- |
| VR-031 | Device must have an active ingress record. |
| VR-032 | Device must currently be Inside. |
| VR-033 | Egress timestamp must not be earlier than ingress timestamp. |
| VR-034 | Egress timestamp and logged_out_by are system-generated/stored. |
| VR-035 | Automatic logout must only apply to devices still marked Inside at 10:00 PM. |
| VR-036 | Automatic logout must record a system user or system process marker and an auto-logout remark. |

### User Roles

| ID | Validation Rule |
| --- | --- |
| VR-037 | Username is required and unique. |
| VR-038 | Password hash is required. |
| VR-039 | Role must be Admin or Security Guard. |
| VR-040 | Inactive accounts cannot log in. |
| VR-041 | Guard accounts cannot access admin-only functions. |

### Reports and Data Integrity

| ID | Validation Rule |
| --- | --- |
| VR-042 | Report date range start must not be later than end date. |
| VR-043 | Reports must use saved database records, not unsaved UI state. |
| VR-044 | Normal users cannot permanently delete device logs. |
| VR-045 | Corrections, event approval document verification, automatic logout, overrides, and status changes must be audit-tracked. |
