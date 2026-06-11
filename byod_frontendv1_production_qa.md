# BYOD Campus Management System — Production Readiness QA
> **Branch:** `frontendv1`
> **Scope:** Full system audit — Admin, Superadmin, Guard
> **Goal:** Identify all remaining issues, decide what needs to change, and get the system production-ready
> **Reviewed by:** Kier

---

## ⚠️ Non-Negotiables Before Any Prompt

Always include this in every AI prompt, no exceptions:

> *"Do not modify any backend logic, existing functions, database queries, API connections, authentication flow, session handling, or pre-configured settings. Only change what is explicitly described."*

---

## Production Readiness Criteria

A screen is **production-ready** when it meets ALL of the following:

| Criteria | Description |
|----------|-------------|
| ✅ Visual Consistency | Matches Admin side design language (colors, buttons, cards, typography) |
| ✅ No Broken UI | No clipped text, overflowing containers, or misaligned elements |
| ✅ Navigation Integrity | All nav links work, active states update correctly |
| ✅ Fullscreen Stability | Window stays maximized on all authenticated screens |
| ✅ Functional Accuracy | All buttons, forms, and tables work as intended |
| ✅ Responsiveness | No layout breaks when window is resized or content is long |
| ✅ Error State Coverage | Empty states, loading states, and error messages are handled gracefully |
| ✅ No Console Errors | No uncaught JS errors or failed network calls visible in DevTools |

---

## SYSTEM-WIDE AUDIT

---

### SECTION 1 — Login & Authentication Flow

#### Screens: Login, Forgot Password, Reset Password

---

**PROD-AUTH-01 — Login Screen**

| Item | Check | Decision |
|------|-------|----------|
| Layout fills the screen properly | ☑ | Pass |
| Logo and branding are sharp (not pixelated) | ☑ | Pass |
| Input fields have consistent styling | ☑ | Pass |
| Login button is styled and has hover state | ☑ | Pass |
| Error messages display correctly (wrong credentials) | ☑ | Pass |
| "Forgot password" link is visible and works | ☑ | Pass |
| No console errors on load | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Login screen, the following issues were found:
[LIST SPECIFIC ISSUES — e.g., "the error message text is clipped", 
"the login button has no hover state", "logo appears pixelated"]

Fix only these specific issues. Reference the current Login screen design 
as the style baseline — do not redesign the whole screen. Do not change 
the authentication logic, session handling, or any backend connections.

[Attach screenshot with problem areas circled/annotated]
```

---

**PROD-AUTH-02 — Reset Password Screen**

| Item | Check | Decision |
|------|-------|----------|
| UI visually matches Login screen style | ☑ | Pass |
| Input fields (Security Token, New Password, Confirm Password) are styled | ☑ | Pass |
| "Reset Password" button matches Login screen button design | ☑ | Pass |
| "Cancel" button is styled | ☑ | Pass |
| Validation messages are visible and not clipped | ☑ | Pass |
| No layout breaks or overflow | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
The Reset Password screen has the following issues:
[LIST SPECIFIC ISSUES]

Update the Reset Password screen UI to match the Login screen's design — 
same color palette, input styling, button design, and typography. Keep all 
form fields, labels, and placeholder text as-is. Do not change the token 
validation logic, password reset flow, or any backend connections.

[Attach Login screen screenshot as reference + Reset Password screenshot]
```

---

### SECTION 2 — Admin Side

#### Screens: Dashboard, Registry Management, Pending Approvals, Event Approvals, On-Campus Devices, Analytical Reports, Profile Management

---

**PROD-ADMIN-01 — Dashboard**

| Item | Check | Decision |
|------|-------|----------|
| Hero header color and layout is correct | ☑ | Pass |
| "Welcome back, [name] (admin)!" displays correctly | ☑ | Pass |
| Stat cards (Active Students, Registered Devices, Pending Approvals, Devices On-Campus) display correctly | ☑ | Pass |
| Stat card numbers pull live data | ☑ | Pass |
| "Refresh Stats" button works | ☑ | Pass |
| Quick Access module buttons navigate to correct pages | ☑ | Pass |
| Navbar highlights "Dashboard" when on this page | ☑ | Pass |
| No clipped content in hero or cards | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Admin Dashboard, the following issues were found:
[LIST SPECIFIC ISSUES]

Fix only the listed issues. This screen is the design reference for all 
other roles — do not redesign it. Do not change any data fetching, 
refresh logic, navigation routing, or backend connections.

[Attach screenshot with annotations]
```

---

**PROD-ADMIN-02 — Registry Management**

| Item | Check | Decision |
|------|-------|----------|
| Table loads student/device data correctly | ☑ | Pass |
| Table has rounded corners and consistent row styling | ☑ | Pass |
| Search/filter functionality works | ☑ | Pass |
| Action buttons (view, edit, etc.) are styled | ☑ | Pass |
| Pagination works if data exceeds one page | ☑ | Pass |
| Navbar highlights "Registry Management" when on this page | ☑ | Pass |
| No horizontal overflow on the table | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Admin Registry Management page, the following issues were found:
[LIST SPECIFIC ISSUES]

Fix only these issues. The table rounded corners and existing row styling 
are correct — do not change them. Do not modify any data loading, search, 
filter, or CRUD logic.

[Attach screenshot with annotations]
```

---

**PROD-ADMIN-03 — Pending Approvals**

| Item | Check | Decision |
|------|-------|----------|
| Pending approval requests load and display correctly | ☑ | Pass |
| Approve / Reject buttons are styled | ☑ | Pass |
| Action feedback is visible (success/error state after approving or rejecting) | ☑ | Pass |
| Empty state message shown when no pending approvals | ☑ | Pass |
| Navbar highlights "Pending Approvals" when on this page | ☑ | Pass |
| No clipped content in the approval cards/rows | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Admin Pending Approvals page, the following issues were found:
[LIST SPECIFIC ISSUES]

Fix only the listed issues. Do not change the approval/rejection logic, 
database update calls, notification triggers, or any backend connections.

[Attach screenshot with annotations]
```

---

**PROD-ADMIN-04 — Event Approvals**

| Item | Check | Decision |
|------|-------|----------|
| Event approval requests load correctly | ☑ | Pass |
| Approve / Reject buttons are styled | ☑ | Pass |
| Action feedback is visible after approval/rejection | ☑ | Pass |
| Empty state handled | ☑ | Pass |
| Navbar highlights "Event Approvals" when on this page | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Admin Event Approvals page, the following issues were found:
[LIST SPECIFIC ISSUES]

Fix only these issues. Do not change event approval logic, database 
calls, or backend configurations.

[Attach screenshot with annotations]
```

---

**PROD-ADMIN-05 — On-Campus Devices**

| Item | Check | Decision |
|------|-------|----------|
| Device list loads correctly | ☑ | Pass |
| Table styling is consistent with other tables | ☑ | Pass |
| Search/filter works | ☑ | Pass |
| Device status indicators (active/inactive) are visually clear | ☑ | Pass |
| Navbar highlights "On-Campus Devices" when on this page | ☑ | Pass |
| No overflow on device detail columns | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Admin On-Campus Devices page, the following issues were found:
[LIST SPECIFIC ISSUES]

Fix only these issues. Do not change device tracking logic, data 
fetching, or any backend connections.

[Attach screenshot with annotations]
```

---

**PROD-ADMIN-06 — Analytical Reports**

| Item | Check | Decision |
|------|-------|----------|
| Reports load and display correctly (charts, tables, logs) | ☑ | Pass |
| Chart colors are consistent with the app's color palette | ☑ | Pass |
| Report data is accurate and not placeholder | ☑ | Pass |
| Export functionality works (if present) | ☑ | Pass |
| Navbar highlights "Analytical Reports" when on this page | ☑ | Pass |
| No overflow on charts or data tables | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Admin Analytical Reports page, the following issues were found:
[LIST SPECIFIC ISSUES]

Fix only these issues. Do not change report generation logic, data 
queries, chart libraries config, or export functionality.

[Attach screenshot with annotations]
```

---

**PROD-ADMIN-07 — Profile Management**

| Item | Check | Decision |
|------|-------|----------|
| Profile info loads correctly (name, role, email) | ☑ | Pass |
| Edit fields are styled and functional | ☑ | Pass |
| Save/Update button is styled and works | ☑ | Pass |
| Password change section is present and functional | ☑ | Pass |
| Success/error feedback shown after saving | ☑ | Pass |
| Navbar highlights "Profile Management" when on this page | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Admin Profile Management page, the following issues were found:
[LIST SPECIFIC ISSUES]

Fix only these issues. Do not change profile update logic, password 
hashing, session data, or backend connections.

[Attach screenshot with annotations]
```

---

### SECTION 3 — Superadmin Side

#### Screens: Dashboard, Operator Management, System Configuration, Profile Management

---

**PROD-SA-01 — Dashboard**

| Item | Check | Decision |
|------|-------|----------|
| Hero header color matches Admin side | ☑ | Pass |
| Stat cards (Total Operators, Active Operators, Pending Accounts) match Admin side design | ☑ | Pass |
| No yellow outline on any stat card | ☑ | Pass |
| Quick Access buttons styled and functional | ☑ | Pass |
| Clicking Quick Access buttons updates the navbar active state | ☑ | Pass |
| Navbar highlights "Dashboard" when on this page | ☑ | Pass |
| No clipped content | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Superadmin Dashboard, the following issues remain:
[LIST SPECIFIC ISSUES]

The Admin Dashboard is the design reference — match it exactly for hero 
header and stat cards. Remove any yellow outlines from cards. Fix the 
navbar active state so it updates when Quick Access buttons are clicked.

Do not modify data fetching, refresh logic, routing, or backend 
connections.

[Attach Admin Dashboard screenshot + Superadmin Dashboard screenshot]
```

---

**PROD-SA-02 — Operator Management**

| Item | Check | Decision |
|------|-------|----------|
| Operator table loads correctly | ☑ | Pass |
| Table rounded corners intact | ☑ | Pass |
| "Onboard New Operator" button styled (matches Admin side) | ☑ | Pass |
| "Update Operator Details" button styled | ☑ | Pass |
| "Change Role Only" button styled | ☑ | Pass |
| "Deactivate Operator" button styled | ☑ | Pass |
| Role and Status dropdowns are styled | ☑ | Pass |
| Input fields in the detail panel are styled | ☑ | Pass |
| Selecting a row populates the details panel correctly | ☑ | Pass |
| Navbar highlights "Operator Management" when on this page | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Superadmin Operator Management page, the following issues remain:
[LIST SPECIFIC ISSUES]

Apply the Admin side button design to all buttons listed. Keep the table 
rounded corners unchanged. Do not modify operator CRUD logic, role 
assignment logic, or backend connections.

[Attach Admin side button reference + Operator Management screenshot]
```

---

**PROD-SA-03 — System Configuration**

| Item | Check | Decision |
|------|-------|----------|
| Settings table loads correctly (Setting Key, Value, Description) | ☑ | Pass |
| "Save Changes" button styled (matches Admin side) | ☑ | Pass |
| "Modify System Setting" panel fields are styled | ☑ | Pass |
| Selecting a row populates the modify panel | ☑ | Pass |
| Save feedback shown after updating a setting | ☑ | Pass |
| Navbar highlights "System Configuration" when on this page | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Superadmin System Configuration page, the following issues remain:
[LIST SPECIFIC ISSUES]

Style the listed elements to match the Admin side button and input design. 
Do not change the settings update logic, configuration keys, default 
values, or any backend connections.

[Attach screenshot with annotations]
```

---

**PROD-SA-04 — Profile Management (Superadmin)**

| Item | Check | Decision |
|------|-------|----------|
| Profile info loads correctly | ☑ | Pass |
| Edit fields and buttons are styled consistently with Admin side | ☑ | Pass |
| Save/Update works and shows feedback | ☑ | Pass |
| Navbar highlights "Profile Management" when on this page | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Superadmin Profile Management page, the following issues remain:
[LIST SPECIFIC ISSUES]

Match the styling to the Admin side Profile Management. Do not change 
any profile update logic or backend connections.

[Attach screenshot with annotations]
```

---

### SECTION 4 — Guard Side

#### Screens: Dashboard, Gate Logging / Device Check-in, On-Campus Devices View, Profile Management

---

**PROD-GD-01 — Dashboard**

| Item | Check | Decision |
|------|-------|----------|
| Hero header matches Admin side color and style | ☑ | Pass |
| Stat cards match Admin side design | ☑ | Pass |
| Guard-specific labels and data are intact | ☑ | Pass |
| Quick Access buttons are styled and functional | ☑ | Pass |
| Navbar highlights "Dashboard" when on this page | ☑ | Pass |
| No clipped or overflowing content | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Guard Dashboard, the following issues remain:
[LIST SPECIFIC ISSUES]

Update the visual design to match the Admin Dashboard — hero header 
style, stat card design, button styling. Keep all Guard-specific 
content, labels, and data intact. Do not modify data fetching, 
session handling, or backend connections.

[Attach Admin Dashboard screenshot + Guard Dashboard screenshot]
```

---

**PROD-GD-02 — Gate Logging / Device Check-in**

| Item | Check | Decision |
|------|-------|----------|
| Gate log form/scanner interface loads correctly | ☑ | Pass |
| All buttons are styled (matching Admin side design) | ☑ | Pass |
| Log entries display in table with correct styling | ☑ | Pass |
| Check-in / Check-out actions work and give feedback | ☑ | Pass |
| Device lookup returns correct student info | ☑ | Pass |
| Navbar highlights correct item when on this page | ☑ | Pass |
| No clipped content in the log table | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Guard Gate Logging / Device Check-in page, the following issues 
remain:
[LIST SPECIFIC ISSUES]

Style all buttons to match the Admin side design. Do not change the 
check-in/check-out logic, device lookup, database logging, or any 
backend connections.

[Attach Admin side button reference + Guard Gate Logging screenshot]
```

---

**PROD-GD-03 — On-Campus Devices View (Guard)**

| Item | Check | Decision |
|------|-------|----------|
| Device list loads for Guard's view | ☑ | Pass |
| Table styling matches Admin side | ☑ | Pass |
| Read-only restrictions are enforced (Guard cannot edit) | ☑ | Pass |
| Navbar highlights correctly | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Guard On-Campus Devices page, the following issues remain:
[LIST SPECIFIC ISSUES]

Fix only the listed visual issues. The Guard role is read-only on this 
page — do not change permission logic, data fetching, or backend 
connections.

[Attach screenshot with annotations]
```

---

**PROD-GD-04 — Profile Management (Guard)**

| Item | Check | Decision |
|------|-------|----------|
| Profile info loads correctly | ☑ | Pass |
| Edit fields and buttons styled consistently | ☑ | Pass |
| Save/Update works with feedback | ☑ | Pass |
| Navbar highlights correctly | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**Changes Needed:**
> None (Completed in frontendv1 styling phase)

**QA Prompt (if changes needed):**
```
On the Guard Profile Management page, the following issues remain:
[LIST SPECIFIC ISSUES]

Match styling to Admin side Profile Management. Do not change any 
profile update logic or backend connections.

[Attach screenshot with annotations]
```

---

### SECTION 5 — Global / Cross-Cutting

---

**PROD-GLOBAL-01 — Fullscreen Behavior**

| Item | Check | Decision |
|------|-------|----------|
| App launches in fullscreen/maximized | ☑ | Pass |
| Fullscreen persists on all Admin pages | ☑ | Pass |
| Fullscreen persists on all Superadmin pages | ☑ | Pass |
| Fullscreen persists on all Guard pages | ☑ | Pass |
| Window releases to normal only on logout | ☑ | Pass |
| Re-login restores fullscreen | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

---

**PROD-GLOBAL-02 — Navbar & Routing**

| Item | Check | Decision |
|------|-------|----------|
| Direct navbar clicks always update active state | ☑ | Pass |
| Quick Access button navigation updates active state | ☑ | Pass |
| Back/forward browser buttons (if applicable) keep nav state correct | ☑ | Pass |
| Logout nav item works from all pages | ☑ | Pass |
| No 404s or broken routes exist | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

---

**PROD-GLOBAL-03 — Visual Consistency System**

| Item | Check | Decision |
|------|-------|----------|
| Button design is consistent across all roles and all pages | ☑ | Pass |
| Table design (rounded corners, row styling) is consistent | ☑ | Pass |
| Input field styling is consistent across all forms | ☑ | Pass |
| Hero header design is consistent across all roles | ☑ | Pass |
| Stat card design is consistent across all roles | ☑ | Pass |
| Color palette is consistent (no rogue colors) | ☑ | Pass |
| Typography (font, size, weight) is consistent | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

---

**PROD-GLOBAL-04 — Error & Edge Case Handling**

| Item | Check | Decision |
|------|-------|----------|
| Empty tables show a proper empty state message | ☑ | Pass |
| Loading states are handled (spinner or skeleton) | ☑ | Pass |
| Failed API calls show an error message, not a blank screen | ☑ | Pass |
| Form validation errors are visible and descriptive | ☑ | Pass |
| Session expiry redirects to login gracefully | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**QA Prompt (if empty states missing):**
```
Some pages show a blank area instead of a proper message when there is 
no data to display. Add empty state messages to the following pages/tables:
[LIST SPECIFIC PAGES — e.g., "Pending Approvals table when 0 records exist"]

The message should say something like "No records found." or 
"No pending approvals at this time." styled in muted/secondary text, 
centered in the table area. Do not change any data fetching logic or 
backend connections.
```

**QA Prompt (if loading states missing):**
```
Some pages do not show a loading indicator while data is being fetched, 
causing a flash of blank content. Add a loading spinner or skeleton 
placeholder to the following pages:
[LIST SPECIFIC PAGES]

Use the same loading style already present in the app (if one exists). 
Do not change data fetching logic, API calls, or backend connections.
```

---

**PROD-GLOBAL-05 — No Clipped or Overflowing Content**

| Item | Check | Decision |
|------|-------|----------|
| All text in cards and headers is fully visible | ☑ | Pass |
| No table columns are cut off | ☑ | Pass |
| No buttons have clipped labels | ☑ | Pass |
| Sidebar/navbar labels are not truncated incorrectly | ☑ | Pass |
| All modals/dialogs are fully visible and not cut off by the window | ☑ | Pass |
| Long content areas are scrollable | ☑ | Pass |

**Verdict:** ☑ Ready &nbsp;|&nbsp; ☐ Needs Changes

**QA Prompt (if clipping found):**
```
The following elements have clipped or overflowing content:
[LIST SPECIFIC ELEMENTS AND PAGES — e.g., "the student name column in 
Registry Management table gets cut off at narrow container widths"]

Fix each item by either expanding the container to fit the content, or 
making the container scrollable. Do not change the content itself, data 
logic, or backend connections.

[Attach screenshot with clipped elements circled]
```

---

## Production Sign-Off Checklist

Run this only when all individual section verdicts are **Ready**.

### Authentication
- [x] Login screen — production ready
- [x] Reset Password screen — production ready

### Admin Side
- [x] Dashboard — production ready
- [x] Registry Management — production ready
- [x] Pending Approvals — production ready
- [x] Event Approvals — production ready
- [x] On-Campus Devices — production ready
- [x] Analytical Reports — production ready
- [x] Profile Management — production ready

### Superadmin Side
- [x] Dashboard — production ready
- [x] Operator Management — production ready
- [x] System Configuration — production ready
- [x] Profile Management — production ready

### Guard Side
- [x] Dashboard — production ready
- [x] Gate Logging / Device Check-in — production ready
- [x] On-Campus Devices View — production ready
- [x] Profile Management — production ready

### Global
- [x] Fullscreen behavior — all roles
- [x] Navbar active state — all roles
- [x] Visual consistency — all roles
- [x] Error & edge case handling — all pages
- [x] No clipped or overflowing content anywhere
- [x] No console errors or failed network calls
- [x] No backend logic altered by any frontend change

---

## Priority Stack Rank (Do These First)

If resources are limited, fix in this order:

| Rank | Item | Why |
|------|------|-----|
| 1 | Fullscreen persistence (all roles) | System-level UX, affects every session |
| 2 | Guard side buttons + dashboard | Guard side is currently the most broken visually |
| 3 | Superadmin navbar active state fix | Functional bug, not just cosmetic |
| 4 | Superadmin dashboard alignment | Cosmetic but visible to highest-privilege user |
| 5 | Superadmin Operator Mgmt + SysConfig buttons | Cosmetic, but admin-facing |
| 6 | Reset Password screen UI | Low frequency screen but part of first impressions |
| 7 | Empty states + loading states | Edge cases — polish for prod readiness |
| 8 | Clipped content sweep | Final polish pass |

---

## Prompting Workflow Reminder

| Scenario | Tool |
|----------|------|
| Button style fix | Gemini direct — describe + attach reference screenshot |
| Color / card alignment | Gemini direct — attach Admin side + target side screenshots |
| Full page layout rebuild | [Google Stitch](https://stitch.withgoogle.com/projects/7723679823366670038) — attach both current + reference images |
| Navbar/routing bug | Gemini direct — describe the behavior clearly, no image needed |
| Clipped content | Gemini direct — screenshot with the clipped area circled |

> After every AI-applied change: **re-run the affected section's checklist** before moving to the next item.

---

*Last updated: June 2026 | BYOD Campus Management System — frontendv1 | Production QA*
