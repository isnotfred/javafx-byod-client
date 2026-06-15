# System Revision & Alignment Report — JavaFX BYOD Client

## 1. General & Cross-Role Features
*   **Autocomplete Student Search Dropdown:** Implemented a new `StudentSearchDropdown` utility. As the user types (3+ characters), matching registered students appear in a dropdown ContextMenu. Selecting a student auto-populates the ID. Pressing `ENTER` runs the search immediately.
*   **Outside Click Unfocusing:** Clicking outside of search fields now successfully unfocuses them and dismisses active dropdowns.
*   **Persistent Placeholders:** Placeholders (prompt texts) now remain visible when input fields gain focus, disappearing only when the user starts typing.
*   **Student ID Form Validation:** Added checks on the Temporary Event Clearance form to prevent submit attempts using unregistered Student IDs.
*   **DatePicker Month/Year Bug:** Fixed the DatePicker rendering issue where blank months/years appeared in Event Request dialogs.

## 2. Admin Module Revisions
*   **Registry Management Overlay Cleanup:**
    *   Removed redundant "Cancel/Close" button (leaving the main Header closing "✕").
    *   Hides the "Clear Form" button when editing, and hides the "Deactivate Record" button when adding a student.
    *   Hides the deactivation button entirely for already deactivated (`inactive`) records since deactivation is permanent.
    *   Fixed the "Edit Selected → Add Student" form caching bug.
    *   Required fields are now visually marked with a red asterisk (`*`).
*   **Dashboard Quick Actions:** Renamed *"Device Registry"* to **"Event Requests"** and *"Student Registry"* to **"Registry Management"**.
*   **System Audit Logs:** Changed column and search filters from *"Performed By"* to **"Operated By"**, and fixed the sidebar layout shift where the Log Out button moved down when selected.
*   **Event Requests Layout & Validation:**
    *   Removed `"Other"` from the Approval Document Type dropdown.
    *   Added a hard block validation preventing submission if the end date is less than 7 days after the start date.
    *   Renamed actions (e.g., *"Add Device Locally"* → *"Add Device"*, *"View Selected/Reconcile"* → *"View Selected"*).
    *   Removed all reconcile-related columns (*"Manifest Status"*) and buttons.

## 3. Security Guard Module Revisions
*   **Navbar Highlight Sync:** Fixed the dashboard shortcut card navigation link so clicking the Ingress/Egress shortcut card correctly highlights its navbar item.
*   **Vertical Scrolling Support:** Wrapped the Ingress/Egress layout in a `ScrollPane` to enable smooth vertical scrolling on smaller screens.
*   **Device Logging Logic:** Fixed unchecked devices status; unchecked devices remain in the table as `"exit"`, and selected items stay highlighted as `"entry"`.
*   **Registered Device Selection Behavior:** Devices are highlighted and checked by default. Clicking a row checks/unchecks and highlights/de-highlights simultaneously. Logs checked rows in a single batch.

## 4. SuperAdmin Module Revisions
*   **System Configuration spinners:** Made Spinner controls typable (`setEditable(true)`) and added commit-on-focus-lost filters to clamp values to valid ranges.
*   **Self-Deactivation Guard:** Added a blocker preventing the logged-in SuperAdmin from deactivating their own account in Operator Management.

## 5. UI/UX Aesthetics
*   **Inactive Record Highlighting:** Deactivated students (Admin Registry) and deactivated operators (SuperAdmin Operator Management) highlight the entire table row in a uniform light pinkish-red background (`#FFE4E6`) with red text (`#DC2626`). Clicking/selecting the row changes the background to a distinct, slightly darker pinkish-red (`#FECDD3`).
