# 12 - Change Log

## 2026-05-10

### Created

- `docs/README.md`
- `docs/01-system-analysis.md`
- `docs/02-functional-requirements.md`
- `docs/03-non-functional-requirements.md`
- `docs/04-use-cases.md`
- `docs/05-business-rules-and-validation-rules.md`
- `docs/06-data-requirements-and-data-dictionary.md`
- `docs/07-screen-requirements.md`
- `docs/08-report-requirements.md`
- `docs/09-acceptance-criteria-and-test-scenarios.md`
- `docs/10-gap-analysis-and-recommendations.md`
- `docs/11-requirements-traceability-matrix.md`
- `docs/12-change-log.md`
- `docs/system-analysis/13-user-interactions.md`

### Major Improvements

- Reorganized the single large system-analysis content into a complete documentation set.
- Reorganized all System Analyst Markdown files into one `docs/system-analysis/` folder so other team roles can maintain their own separate documentation folders.
- Added explicit handling for pending student/device registration.
- Added Temporary/Event Device classification for event equipment.
- Separated registration status, campus status, device status, and device purpose.
- Added report filters, columns, and output expectations.
- Added detailed use cases with alternative flows and exceptions.
- Added validation rules for pending records, event devices, ingress, egress, reports, and permissions.
- Added data dictionary fields for approval, rejection, submission, remarks, and audit tracking.
- Added QA acceptance criteria and test scenarios.
- Added requirements traceability matrix.
- Simplified `device_status` to Active and Inactive only across system-analysis requirements, rules, screens, reports, tests, and traceability.
- Confirmed guards can submit temporary/event device records at the gate; admin review remains optional based on school policy.
- Confirmed temporary/event devices do not require admin approval before ingress; guards may record and allow entry immediately.
- Updated pending-device policy to allow repeat temporary entries while waiting for admin approval.
- Added event approval document verification requirements for paper approval or signed GPOA, including guard verification fields.
- Updated end-of-day handling so devices still Inside at 10:00 PM are automatically logged out with a system-generated remark.
- Confirmed guards can submit pending student records after manual proof verification; admins remain responsible for making those records Official.
- Added pending student proof fields, validation, screen requirements, QA coverage, and traceability.
- Added a role-based user interaction document showing what admins, guards, and students can view, enter, update, approve, reject, generate, and access.
- Added a Mermaid user interaction diagram to show role access, allowed actions, and restricted actions visually.

### Source References

- `../../../SYSTEM-ANALYSIS.md`
- `../../OOP-Finals-Project.md`
- Business analysis PDF referenced by the project brief.
- System analyst task-division PDF referenced by the project brief.

### Remaining Team Confirmation Items

- Confirm admin reactivation policy for inactive devices.
- Confirm whether report export/printing is required now or future scope.
