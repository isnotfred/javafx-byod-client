# 10 - Gap Analysis and Recommendations

## Review Result

The existing system analysis is a strong starting point and covers many core sections, but it is not yet fully ready as a developer and QA handoff document because it is too broad in one file and leaves some business rules unresolved.

## Gaps Found

| Gap | Impact | Resolution in New Docs |
| --- | --- | --- |
| Pending device entry rules were unclear. | Developers could implement different behavior than business process expects. | Documented rule: pending devices may have temporary entry while waiting for admin approval. |
| Pending student handling was unclear. | Guards may not know what to do when a valid student is not encoded. | Confirmed guard-submitted pending student records with required proof and admin review before becoming Official. |
| Temporary/event equipment was not separated from regular BYOD. | Speakers, projectors, and similar equipment could pollute student BYOD records. | Added Temporary/Event Device requirements, screen, fields, reports, and tests. |
| Registration status and campus status could be mixed. | Database and UI logic may become inconsistent. | Separated registration, campus, device, and purpose statuses. |
| User permissions were not detailed enough. | Admin-only and guard-only boundaries could be weak. | Added explicit permission and denial rules. |
| Report definitions lacked complete filters and columns. | QA and developers could not verify report completeness. | Added report-specific filters, columns, and output expectations. |
| Use cases had limited alternative/error flows. | Edge cases could be missed in implementation. | Added detailed use cases with alternatives and exceptions. |
| Validation rules did not cover all invalid scenarios. | Duplicate pending entry, event device, and audit cases could fail. | Added detailed validation rules by domain. |
| Database fields lacked approval and audit tracking. | Admin decisions and overrides may not be traceable. | Added submitted/approved/rejected fields and audit_logs table. |
| Traceability was missing. | Hard to prove that rules are implemented and tested. | Added traceability matrix. |

## Needs Team Confirmation

1. Can admins reactivate inactive devices, and what approval evidence is required?
2. Should report export/printing be required for the current version or treated as optional?

## Recommendations for Current Version

- Keep the current scope desktop-only with JavaFX or Swing and JDBC.
- Prioritize reliable search, validation, ingress/egress logging, and reports over advanced features.
- Use soft deactivation instead of deletion for students, devices, and users.
- Make device logs immutable for normal users.
- Implement audit logging for sensitive changes if time allows.
- Use the docs in this folder as the baseline for UI mockups, schema design, backend logic, and QA tests.

## Future Enhancements

- QR code or barcode support.
- RFID integration.
- Student self-registration portal.
- Mobile application.
- Cloud database synchronization.
- Email or SMS notifications for pending approvals.
- PDF/Excel export.
- Backup and restore module.
- Multi-gate or multi-campus monitoring.
