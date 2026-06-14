# BYOD Campus Management System — UI/UX Revision Tracker
> Generated from tester feedback (frontend v23). Audience: developers and Claude Code.  
> Priorities are numbered **1 = most urgent**. Bugs are tracked separately at the bottom.

---

## 🔵 SUPERADMIN

### Revisions

| # | Priority | Screen | Issue | Action Required |
|---|----------|--------|-------|-----------------|
| 1 | 1 | Operator Management | **Deactivate button** should dynamically become a **Reactivate button** when the selected operator has `inactive` status | Conditionally render button label + action based on `Account Status` field of selected row. If `status === 'inactive'`, show `Reactivate Operator`; else show `Deactivate Operator`. |
| 2 | 2 | Operator Management | **Unnecessary buttons remain clickable** even when no operator is selected or when the action is irrelevant to the current context | Disable (`setDisable(true)`) buttons like `Update Operator Details`, `Change Role Only`, and `Deactivate Operator` when no row is selected. Re-enable on row selection. |
| 3 | 3 | Operator Management | **Username field is Read-only** with no clear explanation of its purpose — confusing to users | Either: (a) add a tooltip/label explaining it's system-generated and non-editable, or (b) remove it from the form if it serves no user-facing function. |
| 4 | 4 | Profile Management | **Screen content does not maximize** to fill available width | Expand the content container to utilize full panel width. Remove fixed-width constraints on the Profile Information and Security & Password cards. |
| 5 | 5 | Dashboard | **Quick Access cards and stat tiles do not maximize** to fill available width | Apply `width: 100%` or equivalent layout fill to stat cards and Quick Access panels so they stretch with the window. |

---

## 🟡 GUARD

### Revisions

| # | Priority | Screen | Issue | Action Required |
|---|----------|--------|-------|-----------------|
| 1 | 1 | Ingress/Egress | **Egress checkbox logic is broken** — all devices are checked by default on egress, but it should only pre-check devices that were logged during ingress | On egress lookup, filter `Registered Devices` list to only show + pre-check devices with `campus_status === 'entry'` for that student. Devices not currently on campus should not appear or be checked. |
| 2 | 2 | Quick Registration | **Student ID field lacks a search dropdown** similar to Ingress/Egress | Add an autocomplete/typeahead dropdown on the Student ID Number field in Quick Registration, consistent with the Ingress/Egress lookup behavior. |
| 3 | 3 | Dashboard | **Layout width not maximized**, alignment issues on cards | Expand dashboard container to full available width; align Quick Access Gate Control cards consistently. |
| 4 | 4 | Quick Registration | **Content width not maximized** | Expand the registration form container to fill the content area width. |

---

## 🔴 ADMIN

### Revisions

| # | Priority | Screen | Issue | Action Required |
|---|----------|--------|-------|-----------------|
| 1 | 1 | Registry Management | **"Import Devices" button label is misleading** — the action actually imports students, not just devices | Rename button to `Import Students` or `Import Students & Devices` to accurately reflect the operation. |
| 2 | 2 | Pending Approvals | **Duplicate entry appearing** — one device showing as two records in the approval queue (also reported on Guard side) | Investigate registration submission logic; add unique constraint check on `serial_number` + `student_id` before inserting into the pending approvals table. |
| 3 | 3 | Analytical Reports | **No logs/reports are visible by default** — requires manual "Generate Report" tap to populate | Consider auto-loading today's report on page entry, or show a clear empty state with a prompt CTA instead of a blank table. |
| 4 | 4 | Analytical Reports | **Report requires manual tap to generate** — not intuitive | On page load, auto-trigger `Generate Report` with default values (Report Type: Daily Traffic, Date: today), or make the Generate button more prominent with a helper label. |
| 5 | 5 | System Audit Logs / Profile Management | **Top bar height is inconsistent** across these two screens | Standardize the header/top-bar component height across all pages. Use a shared `PageHeader` component with consistent padding and font sizing. |
| 6 | 6 | Dashboard | **Arrangement & alignment of cards** needs improvement | Review grid layout of Quick Access Modules and stat tiles; ensure consistent spacing, card sizing, and alignment across all screen sizes. |
| 7 | 7 | Analytical Reports | **"Empty Dataset" popup warning triggers on page entry** when no records exist | Suppress the Empty Dataset popup alert during silent auto-load on page initialization, but keep it active for manual generation. |
| 8 | 8 | Analytical Reports | **Monthly Traffic Report calendar selects by day** — counter-intuitive for monthly aggregation | Replace the standard DatePicker with dedicated month and year ComboBox drop-down selectors when Monthly Traffic Report is selected. |

---

## 🐛 BUG REPORTS

### BUG-001 — Duplicate Device on Quick Registration
- **Role:** Guard
- **Screen:** Quick Registration
- **Severity:** High
- **Description:** When performing a Quick Registration, the same device is being registered twice, resulting in two entries for a single device in the system.
- **Steps to Reproduce:**
  1. Go to Guard → Quick Registration
  2. Fill in all fields for a device
  3. Click `Register Device`
  4. Observe that 2 entries appear instead of 1
- **Expected:** One entry per registration submission
- **Actual:** Two identical entries created
- **Suggested Fix:** Add a duplicate submission guard (e.g., disable button after first click, check for existing `serial_number` before insert, or wrap in a transaction with a unique constraint).

---

### BUG-002 — Egress Checkbox Pre-selects All Devices Instead of Ingressed Only
- **Role:** Guard
- **Screen:** Ingress/Egress → Egress flow
- **Severity:** High
- **Description:** When logging egress for a student, all of their registered devices are pre-checked by default. The correct behavior is to only pre-check devices that were previously logged as `entry` (i.e., currently on campus).
- **Steps to Reproduce:**
  1. Log ingress for a student with multiple devices (select only some)
  2. Look up same student for egress
  3. Observe that ALL devices are checked, not just those that entered
- **Expected:** Only devices with `campus_status === 'entry'` are pre-checked on egress
- **Actual:** All devices are pre-checked regardless of ingress status
- **Suggested Fix:** Filter the `Registered Devices` list on egress to only include devices where `campus_status = 'entry'` for the current student session.

---

### BUG-003 — Duplicate Device Entry in Pending Approvals (Admin)
- **Role:** Admin
- **Screen:** Pending Approvals
- **Severity:** Medium
- **Description:** A single device appears as two separate entries in the Pending Approvals Registry.
- **Suspected Cause:** Likely related to BUG-001 (duplicate registration from Guard Quick Registration), or a re-submission without deduplication logic.
- **Suggested Fix:** Same as BUG-001 fix; additionally add a unique constraint at the DB level on `(student_id, serial_number)` in the pending approvals table.

---

## 📋 GENERAL NOTES (For All Roles)

- **Layout Width:** Multiple screens across all three roles (Superadmin, Guard, Admin) have content that does not expand to fill available window width. Audit all `content-area` / `main-panel` containers and remove hardcoded `max-width` or fixed-pixel widths that prevent responsive stretching within the JavaFX scene.
- **Event Devices module** has not been tested yet — flag for next round of QA.
- **Top bar component inconsistency** (System Audit Logs vs Profile Management) — standardize into a reusable `PageHeader` JavaFX component.

---

*Last updated: 2026-06-14 | Source: Changes_frontendv23.pdf tester feedback*
