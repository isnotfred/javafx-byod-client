# BYOD Device Management System — System Architecture Document

`com.pup.byod.javafxbyodclient` | JavaFX + Spring Boot + PostgreSQL

| Layer | Technology |
|---|---|
| **Frontend** | JavaFX (IntelliJ IDEA, FXML, CSS) |
| **Backend** | Spring Boot + JDBC (IntelliJ IDEA) |
| **Database** | PostgreSQL (hosted on Railway) |
| **Hosting** | Railway — Backend API + PostgreSQL database |
| **Version Control** | Git + GitHub (two separate repositories) |

---

## 1. Overview

The BYOD Device Management System is a two-repository desktop application. The JavaFX frontend communicates with a Spring Boot REST API backend over HTTP/HTTPS. The backend talks exclusively to a PostgreSQL database hosted on Railway. All three tiers run independently and are deployed or run separately.

**Architecture type:** Client–Server (3-tier: JavaFX Client → Spring Boot API → PostgreSQL)

| Tier | Technology | Runs On | Role |
|---|---|---|---|
| **Frontend** | JavaFX | Developer machine / end-user PC | UI, user input, HTTP calls to backend |
| **Backend** | Spring Boot + JDBC | Railway (cloud) | REST API, business logic, DB access |
| **Database** | PostgreSQL | Railway (cloud) | Persistent storage, triggers, views, functions |

---

## 2. System Architecture Diagram

```
FRONTEND (JavaFX — local machine)
  Controller → HTTP Client (RestTemplate / HttpClient)
  FXML / CSS / Model (JavaFX ObservableList, POJO)
          ↕  HTTPS (JSON REST API)
BACKEND (Spring Boot — Railway)
  @RestController → @Service → DAO (JDBC / NamedParameterJdbcTemplate)
  Model (POJO / RowMapper)
          ↕  JDBC (PostgreSQL Driver 42.7.x)
DATABASE (PostgreSQL — Railway)
  Tables · Views · Triggers · Functions (fn_write_audit_log, etc.)
```

---

## 3. Frontend–Backend Communication

The JavaFX frontend and the Spring Boot backend are completely separate processes. They communicate exclusively through a JSON REST API over HTTPS. The frontend never connects directly to PostgreSQL.

| From | To | Protocol | Format |
|---|---|---|---|
| JavaFX Controller | Spring Boot @RestController | HTTPS (HTTP/1.1 or HTTP/2) | JSON request body / query params |
| Spring Boot @RestController | JavaFX Controller | HTTPS response | JSON response body (+ HTTP status code) |
| Spring Boot @Service / DAO | PostgreSQL (Railway) | JDBC over TLS | PreparedStatement / RowMapper |

**Base URL (Railway):** `https://<app-name>.railway.app/api/v1/`

---

## 4. Backend — Spring Boot Package Structure

All backend source code lives under the base package: `com.pup.byod.javabyodbackend/`

| Package / Layer | Full Package Path | Responsibility |
|---|---|---|
| **controller/** | com.pup.byod.javabyodbackend.controller | @RestController — receives HTTP requests, returns ResponseEntity |
| **service/** | com.pup.byod.javabyodbackend.service | Business logic, auth, validation, transaction orchestration |
| **dao/** | com.pup.byod.javabyodbackend.dao | All SQL via JDBC / NamedParameterJdbcTemplate; RowMapper |
| **model/** | com.pup.byod.javabyodbackend.model | POJOs, enums, constants, and report rows matching DB |
| **config/** | com.pup.byod.javabyodbackend.config | DataSource bean, CORS config, security config, DB connection pool |
| **util/** | com.pup.byod.javabyodbackend.util | Password hashing (BCrypt), validation helpers, date formatters |
| **exception/** | com.pup.byod.javabyodbackend.exception | Custom exception classes + @ControllerAdvice global error handler |

---

## 5. Backend — Layered Architecture

The backend follows a strict unidirectional call chain. No layer skips another. A Controller never calls a DAO directly, and a DAO never calls a Service.

### 5.1 Controller Layer (`@RestController`)

The Controller is the HTTP entry point. It:

- Receives HTTP requests and maps them with `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping`
- Reads JSON request bodies (`@RequestBody`) and path/query parameters
- Calls the appropriate Service method
- Returns `ResponseEntity` with the correct HTTP status code and JSON body
- Catches exceptions thrown by Service and maps them to HTTP error responses

A Controller contains no SQL, business rules, or password hashing.

### 5.2 Service Layer (`@Service`)

The Service layer contains all business logic. It:

- Validates inputs — required fields, format checks, business rules
- Handles authentication — verifies password hash using BCrypt from `util/`
- Orchestrates multi-step operations with `@Transactional`
- Calls one or more DAO methods to read or write data
- Calls `AuditLogDAO` to write audit entries via `fn_write_audit_log()`
- Throws descriptive exceptions that the Controller maps to HTTP responses

A Service never references `HttpServletRequest`, `HttpServletResponse`, or any Spring MVC type.

### 5.3 DAO Layer (JDBC)

The DAO layer is the only part of the backend that talks to PostgreSQL. It:

- Uses `NamedParameterJdbcTemplate` or `JdbcTemplate` from Spring JDBC
- Writes all SQL as parameterised queries (no string concatenation)
- Maps each `ResultSet` row to a Model object via a `RowMapper`
- Calls the database function `fn_write_audit_log()` for audit entries
- Uses `@Transactional` on multi-step operations
- Throws `DataAccessException` upward — the Service layer decides what to do

Views used directly in DAO queries:

- `v_device_campus_status` — current inside/outside status per device
- `v_pending_devices` — pending device registrations for the approval queue
- `v_active_event_requests` — active event requests with device counts

### 5.4 Model Layer (POJOs)

Models are plain Java objects with fields that match the database table columns. One Model class per table. Models contain private fields with getters/setters, and enums for constrained fields (`DeviceType`, `RegistrationStatus`, `Role`, etc.). Models carry no logic. Additionally, this layer contains constants for audit actions (`AuditActionTypes`) and DTOs representing query result structures for reports (under `model/report/`).

---

## 6. Database — PostgreSQL on Railway

The PostgreSQL database is hosted on Railway and is the single source of truth. The backend connects via JDBC with credentials injected as Railway environment variables. The JavaFX frontend never connects directly to the database.

- **JDBC URL pattern:** `jdbc:postgresql://<railway-host>:<port>/byod_db`
- **Driver:** `org.postgresql.Driver` (PostgreSQL JDBC 42.7.x)
- **Connection pool:** HikariCP (Spring Boot default)

### 6.1 Tables

| Table | Primary Key | Purpose |
|---|---|---|
| **users** | user_id SERIAL | Admin, guard, and super admin accounts (email address in `email` column, `status` allows `pending` for first login onboarding) |
| **students** | student_id VARCHAR(50) | Student registry — never hard-delete; set status = inactive |
| **devices** | device_id SERIAL | Permanent BYOD device registrations |
| **event_requests** | event_request_id SERIAL | Header for a temporary device access request (school events, orgs) |
| **event_request_devices** | event_device_id SERIAL | Individual devices listed under an event request |
| **device_logs** | log_id SERIAL | Immutable gate entry/exit event log — never UPDATE or DELETE |
| **audit_logs** | audit_id SERIAL | Immutable system-wide audit trail — write via fn_write_audit_log() only |

### 6.2 Views

| View Name | Purpose |
|---|---|
| **v_device_campus_status** | Derives inside/outside status per approved active device from the latest device_log row |
| **v_pending_devices** | Pending device registrations for the admin approval queue, with student name joined |
| **v_active_event_requests** | Pending and approved event requests with device counts |

### 6.3 Key Functions and Triggers

| Name | Purpose |
|---|---|
| **fn_write_audit_log()** | Preferred writer for audit_logs. Called from DAO layer only. Prevents direct INSERT. |
| **fn_set_updated_at()** | Auto-refreshes updated_at on every UPDATE across all mutable tables |
| **fn_force_created_at()** | Forces server-side created_at timestamp on device_logs and audit_logs to prevent backdating |
| **fn_guard_registration_transition()** | Enforces device registration state machine: pending → approved \| pending → rejected \| rejected → pending |
| **fn_guard_device_log_approved_only()** | Blocks gate log inserts for unapproved or inactive devices |
| **fn_guard_consecutive_events()** | Blocks two consecutive same-type events (e.g. double-entry without exit). Auto-exit rows are exempt. |
| **fn_audit_log_immutable()** | Prevents UPDATE and DELETE on audit_logs rows |
| **fn_device_log_immutable()** | Prevents hard-delete on device_logs rows |
| **fn_protect_student/device/user_delete()** | Blocks hard-delete when referencing records exist; requires setting status = inactive instead |

---

## 7. Request Flow (End-to-End)

The table below traces a single user action — e.g. a guard approving a device — from the JavaFX UI to the database and back.

| # | Layer | What Happens | Passes To Next |
|---|---|---|---|
| **1** | JavaFX UI (FXML) | User clicks button / submits form | Raw input to Controller |
| **2** | JavaFX Controller | Intercepts UI event, collects input, and delegates to service layer | Method call with inputs/DTOs |
| **3** | JavaFX Service/API Layer | Configures HTTP request, serializes request DTO to JSON, sends HTTP request via HttpClient | HTTPS request to backend |
| **4** | Spring Boot @RestController | Deserializes JSON to DTO / model, calls Service method | Method call + parameters |
| **5** | Spring Boot @Service | Validates business rules, calls DAO, calls AuditLogDAO | Model objects or primitives |
| **6** | DAO (JDBC) | Executes PreparedStatement, calls fn_write_audit_log() | SQL + parameters to DB |
| **7** | PostgreSQL (Railway) | Executes SQL, fires triggers, returns ResultSet / confirmation | ResultSet rows |
| **8** | DAO → Service → Controller | ResultSet mapped to Model POJOs; returned up the chain | ResponseEntity (JSON) |
| **9** | JavaFX Service/API Layer | Deserializes JSON response payload, handles connection issues/exceptions | Model object, list, or error |
| **10** | JavaFX Controller | Receives callback/response on JavaFX Application thread, updates UI binding (ObservableList) | UI update (TableView / Label) |

---

## 8. Error Handling Strategy

Errors originate in three places: the Service layer (business rule violations), the DAO layer (SQL failures via `DataAccessException`), and the PostgreSQL database (trigger exceptions). All errors propagate as HTTP error responses to the JavaFX frontend.

| Trigger | Caught In | Shown to User As |
|---|---|---|
| DB trigger blocks INSERT/UPDATE | @ControllerAdvice (HTTP 400) | Friendly banner / alert dialog in JavaFX |
| Duplicate unique key | @ControllerAdvice (HTTP 409) | "Serial number already exists", "Username already exists", or "Student ID already exists" |
| Business rule violation | @ControllerAdvice (HTTP 422) | Custom message text surfaced in alert |
| Super admin access required | @ControllerAdvice (HTTP 403) | "Super admin access required" |
| Resource not found | @ControllerAdvice (HTTP 404) | "Acting user not found", "User to update not found", etc. |
| Auth failure | Service → HTTP 401 | "Invalid username or password" |
| Inactive account | Service → HTTP 403 | "Account is inactive" |
| DB connection lost | HikariCP / @ControllerAdvice 503 | "Database connection failed" |
| Field validation failure / invalid argument | @ControllerAdvice (HTTP 400) | Field-level error messages on form / validation error details |
| General internal error | @ControllerAdvice (HTTP 500) | "An unexpected error occurred." |

---

## 9. Audit Logging

Every significant action writes a row to `audit_logs` via the PostgreSQL function `fn_write_audit_log()`. This is always called from the DAO layer inside a transaction — never from the Controller or directly from the Service.

**Function signature:**

```sql
fn_write_audit_log(
    p_user_id      INT,
    p_action_type  VARCHAR,   -- e.g. 'DEVICE_APPROVED'
    p_target_table VARCHAR,   -- e.g. 'devices'
    p_target_id    VARCHAR,   -- e.g. '42'
    p_old_values   JSONB,     -- state before change
    p_new_values   JSONB,     -- state after change
    p_ip_address   VARCHAR
)
```

Standardised `action_type` values (enforced by CHECK constraint):

| Device Actions | Student/User Actions | Event / System Actions |
|---|---|---|
| DEVICE_REGISTERED | STUDENT_CREATED | EVENT_REQUEST_CREATED |
| DEVICE_APPROVED | STUDENT_UPDATED | EVENT_REQUEST_APPROVED |
| DEVICE_REJECTED | STUDENT_DEACTIVATED | EVENT_REQUEST_RETURNED |
| DEVICE_DEACTIVATED | USER_CREATED | EVENT_REQUEST_REJECTED |
| DEVICE_UPDATED | USER_UPDATED | SYSTEM_AUTO_EXIT_BATCH |
| DEVICE_ENTRY | USER_DEACTIVATED | SYSTEM_CONFIG_UPDATED |
| DEVICE_EXIT | USER_LOGIN | |
| DEVICE_AUTO_EXIT | USER_LOGOUT | |
| | USER_LOGIN_FAILED | |
| | USER_ROLE_CHANGED | |
| | ADMIN_CREATED | |
| | ADMIN_UPDATED | |
| | ADMIN_DEACTIVATED | |
| | GUARD_CREATED | |
| | GUARD_UPDATED | |
| | GUARD_DEACTIVATED_BY_SUPER | |

---

## 10. Project File Structure

The frontend and backend live in two separate Git repositories. Each follows### 10.1 Frontend Repository — JavaFX (IntelliJ IDEA)

The frontend is built using a decoupled **MVC + Service** pattern. Rather than initiating network connections directly inside FXML controllers, the frontend isolates presentation logic from data access and communication. This separation of concerns improves testability, prevents UI freezes, and guarantees code reusability.

#### 10.1.1 Architectural Package Design

- **`controller/`**: Responsible strictly for UI event handling, control flow, user input capture, form visual status updates, and binding data structures to FXML controls. All network or remote actions are delegated asynchronously to the Service layer to avoid blocking the JavaFX Application Thread.
- **`service/`** (or **`api/`**): Contains stateless client wrappers for the REST API endpoints. Utilizes Java's built-in `java.net.http.HttpClient` (supporting HTTP/1.1 and HTTP/2) to execute remote HTTPS requests and process responses. Deserialization and serialization are handled via Jackson ObjectMappers.
- **`session/`**: Houses the global session state. The `SessionManager` singleton stores the profile of the currently authenticated `User`, session token/session context, and handles client-side role validation.
- **`model/`**: Java classes representing backend entities and API transfer structures (DTOs). Employs JavaFX Property structures (e.g. `StringProperty`, `IntegerProperty`, `ObjectProperty`) where two-way binding with UI nodes (like `TableView` or `TextField`) is beneficial.
- **`util/`**: Includes reusable application helpers:
  - `NavigationManager`: Controls main stage switching, loading FXML resources, and caching active views.
  - `AlertHelper`: Displays premium, uniform alert dialogs (information, warning, error, confirmation).
  - `ValidationHelper`: Form field syntax validation (e.g. regex for student IDs, emails, serial numbers).
  - `JsonMapper`: Provider of a single Jackson ObjectMapper configured for date/time conversion.

---

#### 10.1.2 Controller Registry

| Controller Class | View FXML File | Screen Responsibility / Role Restrictions |
|---|---|---|
| **LoginScreenController** | `LoginScreen.fxml` | User authentication, session initiation, error handling for bad credentials |
| **ForgotPasswordScreenController** | `ForgotPasswordScreen.fxml` | Capture email/username and trigger onboarding password recovery token |
| **ResetPasswordScreenController** | `ResetPasswordScreen.fxml` | Submit security token and new password to finalize recovery process |
| **AdminDashboardController** | `AdminDashboard.fxml` | Shell navigation bar and statistics panels for `admin` role |
| **SecurityGuardDashboardController** | `SecurityGuardDashboard.fxml` | Entry/exit quick-paths and scanned statistics for `guard` role |
| **DeviceManagementScreenController** | `DeviceManagementScreen.fxml` | Main registry lookup — search, filter, and deactivate registered devices |
| **PendingRegistrationApprovalScreenController** | `PendingRegistrationApprovalScreen.fxml` | Approval queue — review specs, approve/reject device registrations (`admin` only) |
| **QuickPendingRegistrationScreenController** | `QuickPendingRegistrationScreen.fxml` | Guard onboarding form — register student devices directly at the gate |
| **StudentManagementScreenController** | `StudentManagementScreen.fxml` | Student directory CRUD — register students, edit details, and soft-delete students |
| **UserManagementScreenController** | `UserManagementScreen.fxml` | System operator directory — register, configure, or block admins/guards (`super_admin` only) |
| **IngressEgressMonitoringScreenController** | `IngressEgressMonitoringScreen.fxml` | Ingress scan gate — input serial numbers, prompt status warnings, log entry/exit events |
| **ActiveDevicesInsideCampusScreenController** | `ActiveDevicesInsideCampusScreen.fxml` | Real-time scan list — browse devices currently flagged as inside the campus (`guard` view) |
| **TemporaryEventDeviceScreenController** | `TemporaryEventDeviceScreen.fxml` | Event request wizard — create event entries, attach multiple devices, submit for review |
| **LogsScreenController** | `LogsScreen.fxml` | Logs explorer — filter and export gate logs (`device_logs`) and system audtis (`audit_logs`) |
| **ReportsScreenController** | `ReportsScreen.fxml` | Analytics hub — query, visualize, and print reports (`admin`/`super_admin` only) |

---

#### 10.1.3 Models and Data Structures

| Model Class | Source Backend Mapping | Presentation Data Binding Strategy |
|---|---|---|
| **User** | `users` Table | Property-bound user attributes for session context |
| **Student** | `students` Table | Bound to student management table controls |
| **Device** | `devices` Table | Bound to registry and detail views |
| **PendingDevice** | `v_pending_devices` View | Combines device details with Student Full Name for the approval queue |
| **DeviceCampusStatus** | `v_device_campus_status` View | Real-time status mapping, color-coded based on inside/outside states |
| **DeviceLog** | `device_logs` Table | Display data for recent entry/exit events |
| **EventRequest** | `event_requests` Table | Header information for school event bypass requests |
| **EventRequestDevice** | `event_request_devices` Table | Device list nested inside event details with check-in flags |
| **ActiveEventRequest** | `v_active_event_requests` View | Summary listing active events with active device counters |
| **AuditLog** | `audit_logs` Table | Immutable record representation for audit tables |

---

#### 10.1.4 Visual Styling & UX Principles

To deliver a high-end, premium experience, the application utilizes:
- **Typography**: Inter / Outfit fonts integrated via Google Fonts.
- **Color Palette**: Custom dark-theme design utilizing harmonious CSS variables (sleek grays, rich slate backgrounds, and custom accents for status badges — e.g. soft emerald for active, amber for pending, crimson for rejected/inactive).
- **Smooth Micro-animations**: Focus states, button hovers, and table row selections rely on subtle CSS transitions (`-fx-transition` equivalent styling or JavaFX Fade/Translate animations) to feel responsive and premium.
- **Glassmorphism**: Login screens and dashboard panels use semi-transparent background styling combined with soft borders to stand out.

---

#### 10.1.5 Maven Configuration (`pom.xml`)

To enable network communication and JSON mapping, the following dependencies must be added to the base project:
- **`com.fasterxml.jackson.core:jackson-databind`**: For mapping model DTOs to and from API JSON payloads.
- **`com.fasterxml.jackson.datatype:jackson-datatype-jsr310`**: For native parsing of Java 8 `java.time` classes (e.g. `LocalDateTime`, `LocalDate`) returned by the API.

---

#### 10.1.6 Repository File Tree

```
byod-frontend/                                        ← GitHub repo root
├── .idea/                                            ← IntelliJ IDEA project files
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java                      ← JavaFX module declaration
│       │   └── com/pup/byod/javafxbyodclient/
│       │       ├── Launcher.java                     ← Main entry point workaround (for shaded JARs)
│       │       ├── HelloApplication.java             ← Standard application subclass
│       │       ├── controller/                       ← Presentation Controllers
│       │       │   ├── LoginScreenController.java
│       │       │   ├── ForgotPasswordScreenController.java
│       │       │   ├── ResetPasswordScreenController.java
│       │       │   ├── AdminDashboardController.java
│       │       │   ├── SecurityGuardDashboardController.java
│       │       │   ├── DeviceManagementScreenController.java
│       │       │   ├── PendingRegistrationApprovalScreenController.java
│       │       │   ├── QuickPendingRegistrationScreenController.java
│       │       │   ├── StudentManagementScreenController.java
│       │       │   ├── UserManagementScreenController.java
│       │       │   ├── IngressEgressMonitoringScreenController.java
│       │       │   ├── ActiveDevicesInsideCampusScreenController.java
│       │       │   ├── TemporaryEventDeviceScreenController.java
│       │       │   ├── LogsScreenController.java
│       │       │   └── ReportsScreenController.java
│       │       ├── service/                          ← REST API client services
│       │       │   ├── ApiClient.java                ← Abstract Base HTTP Client handler
│       │       │   ├── AuthService.java              ← Authentication requests
│       │       │   ├── StudentService.java           ← Student registry requests & imports
│       │       │   ├── DeviceService.java            ← Device CRUD & verification requests
│       │       │   ├── EventRequestService.java      ← Temporary event request pipeline
│       │       │   ├── LogService.java               ← Ingress/egress logging & active trackers
│       │       │   ├── ReportService.java            ← Report query wrappers
│       │       │   └── SuperAdminService.java        ← User account modifications
│       │       ├── session/                          ← Client session storage
│       │       │   └── SessionManager.java           ← Active user state and session tracking
│       │       ├── model/                            ← Models & JavaFX property DTOs
│       │       │   ├── ActiveEventRequest.java
│       │       │   ├── AuditLog.java
│       │       │   ├── Device.java
│       │       │   ├── DeviceCampusStatus.java
│       │       │   ├── DeviceLog.java
│       │       │   ├── EventRequest.java
│       │       │   ├── EventRequestDevice.java
│       │       │   ├── PendingDevice.java
│       │       │   ├── Student.java
│       │       │   ├── User.java
│       │       │   └── enums/                        ← Enums mapping database constraints
│       │       │       ├── ApprovalDocType.java
│       │       │       ├── AuditActionType.java
│       │       │       ├── DevicePurpose.java
│       │       │       ├── DeviceType.java
│       │       │       ├── EntityStatus.java
│       │       │       ├── EventDeviceStatus.java
│       │       │       ├── EventDeviceType.java
│       │       │       ├── EventRequestStatus.java
│       │       │       ├── GateEventType.java
│       │       │       ├── LogoutType.java
│       │       │       ├── RegistrationStatus.java
│       │       │       └── UserRole.java
│       │       └── util/                             ← Utilities & UI helper tools
│       │           ├── AlertHelper.java              ← UI dialog styling helper
│       │           ├── ValidationHelper.java         ← Form field validator
│       │           └── NavigationManager.java        ← Page transitions & view caching
│       └── resources/
│           └── com/pup/byod/javafxbyodclient/
│               ├── fxml/                             ← FXML screen layouts
│               │   ├── LoginScreen.fxml
│               │   ├── ForgotPasswordScreen.fxml
│               │   ├── ResetPasswordScreen.fxml
│               │   ├── AdminDashboard.fxml
│               │   ├── SecurityGuardDashboard.fxml
│               │   ├── DeviceManagementScreen.fxml
│               │   ├── PendingRegistrationApprovalScreen.fxml
│               │   ├── QuickPendingRegistrationScreen.fxml
│               │   ├── StudentManagementScreen.fxml
│               │   ├── UserManagementScreen.fxml
│               │   ├── IngressEgressMonitoringScreen.fxml
│               │   ├── ActiveDevicesInsideCampusScreen.fxml
│               │   ├── ActiveDevicesAdminScreen.fxml
│               │   ├── TemporaryEventDeviceScreen.fxml
│               │   ├── TemporaryEventDeviceGuardScreen.fxml
│               │   ├── LogsScreen.fxml
│               │   └── ReportsScreen.fxml
│               └── css/
│                   └── styles.css                    ← Premium Dark-Theme stylesheets
├── pom.xml                                           ← Maven dependencies
├── .gitignore
└── README.md
```

### 10.2 Backend Repository — Spring Boot (IntelliJ IDEA)

The backend follows the standard Spring Boot layered structure. Each layer has one clear responsibility and communicates only with the layer directly adjacent to it. Controllers never call DAOs, and DAOs never call Services.

**Controllers (`controller/`)**

| Controller Class | Endpoints / Role |
|---|---|
| AuthController | POST /auth/login — authenticate user, return session/token |
| UserController | GET /users — retrieve users list, GET /users/{id} — retrieve user details, PUT /users/{id}/profile/password — user password updates |
| StudentController | GET/POST/PUT /students — student registry CRUD |
| DeviceController | GET/POST/PUT /devices — device registration, approval, deactivation |
| EventRequestController | GET/POST/PUT /event-requests — event request lifecycle |
| DeviceLogController | GET/POST /device-logs — gate entry/exit logging and history |
| AuditLogController | GET /audit-logs — read-only audit trail queries |
| ReportController | GET /reports/* — daily/monthly traffic, pending registrations, active devices, device frequency, incident reports |
| SuperAdminController | POST/PUT /super-admin/* — manage admins/guards (create, update, deactivate, change role) |
| SystemSettingController | GET /api/v1/settings — query config policies, PUT /api/v1/settings/{key} — modify config (Super Admin only) |

**Services (`service/`)**

| Service Class | Business Logic Handled |
|---|---|
| AuthService | Login validation, BCrypt password verification, session management |
| UserService | Retrieve user list and details, update individual profile passwords with BCrypt validation |
| StudentService | Student registration, soft-delete enforcement, search logic |
| DeviceService | Device registration, approval/rejection state machine, deactivation |
| EventRequestService | Event request submission, approval workflow, date range validation |
| DeviceLogService | Gate scan logic, consecutive-event prevention, auto-exit batch |
| AuditLogService | Orchestrates calls to AuditLogDAO / fn_write_audit_log() |
| ReportService | Produces all six report types required by the BYOD business analysis |
| SuperAdminService | Account CRUD, status updates, role changes, and super admin authorization checks |
| ResendEmailService | Sends credential onboarding emails using the Resend API |
| SystemSettingService | Manage system settings and configuration parameters with Super Admin checks |

**DAOs (`dao/`)**

| DAO Class | DB Table / View Accessed |
|---|---|
| UserDAO | users table |
| StudentDAO | students table |
| DeviceDAO | devices table, v_pending_devices, v_device_campus_status |
| EventRequestDAO | event_requests table, v_active_event_requests |
| EventRequestDeviceDAO | event_request_devices table |
| DeviceLogDAO | device_logs table |
| AuditLogDAO | Calls fn_write_audit_log() — never INSERTs into audit_logs directly |
| SystemSettingDAO | system_settings table |

**Models (`model/`)**

| Model Class | Maps To |
|---|---|
| User | users table |
| Student | students table |
| Device | devices table |
| EventRequest | event_requests table |
| EventRequestDevice | event_request_devices table |
| DeviceLog | device_logs table |
| AuditLog | audit_logs table |
| AuditActionTypes | Constant values for audit actions |
| SystemSetting | system_settings table |

**Report Models (`model/report/`)**

| Model Class | Maps To |
|---|---|
| ActiveDeviceRow | Real-time snapshot of active devices on campus |
| DailyTrafficRow | Entry/exit events on a given day |
| DeviceFrequencyRow | Device entry/exit frequency over a date range |
| IncidentOverrideRow | Admin overrides, rejections, and dispute resolutions |
| MonthlyTrafficRow | Aggregated monthly traffic grouped by category & student |
| PendingRegistrationRow | All devices in 'pending' status joined with submitter info |

**Enums (`model/enums/`)**

| Enum Class | Used For |
|---|---|
| Role | users.role — admin, guard, super_admin |
| DeviceType | devices.device_type and event_request_devices.device_type — Personal Computers, Components & Peripherals, Display & Projection, Project Prototypes (Optional SN), Appliances (TLE) |
| RegistrationStatus | devices.registration_status — JDBC mapping and Service validation |

**Supporting Packages:**

| Package | Contents |
|---|---|
| config/ | DataSourceConfig (HikariCP + Railway env vars), CorsConfig (allow JavaFX host), SecurityConfig (stateless security configuration) |
| exception/ | ResourceNotFoundException, BusinessRuleException, ForbiddenException, GlobalExceptionHandler (@ControllerAdvice) |
| util/ | PasswordUtil (BCrypt hash/verify), ValidationUtil, DateUtil |

**File Tree:**

```
byod-backend/                           ← GitHub repo root
├── .idea/                              ← IntelliJ IDEA project files
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pup/byod/javabyodbackend/
│   │   │       ├── JavaByodBackendApplication.java   ← Spring Boot entry point
│   │   │       ├── controller/                       ← @RestController — HTTP endpoints
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── UserController.java
│   │   │       │   ├── StudentController.java
│   │   │       │   ├── DeviceController.java
│   │   │       │   ├── EventRequestController.java
│   │   │       │   ├── DeviceLogController.java
│   │   │       │   ├── AuditLogController.java
│   │   │       │   ├── ReportController.java
│   │   │       │   ├── SuperAdminController.java
│   │   │       │   └── SystemSettingController.java
│   │   │       ├── service/                          ← @Service — business logic
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── UserService.java
│   │   │       │   ├── StudentService.java
│   │   │       │   ├── DeviceService.java
│   │   │       │   ├── EventRequestService.java
│   │   │       │   ├── DeviceLogService.java
│   │   │       │   ├── AuditLogService.java
│   │   │       │   ├── ReportService.java
│   │   │       │   ├── ResendEmailService.java
│   │   │       │   ├── SuperAdminService.java
│   │   │       │   └── SystemSettingService.java
│   │   │       ├── dao/                              ← JDBC; RowMapper; PreparedStatement
│   │   │       │   ├── UserDAO.java
│   │   │       │   ├── StudentDAO.java
│   │   │       │   ├── DeviceDAO.java
│   │   │       │   ├── EventRequestDAO.java
│   │   │       │   ├── EventRequestDeviceDAO.java
│   │   │       │   ├── DeviceLogDAO.java
│   │   │       │   ├── AuditLogDAO.java              ← calls fn_write_audit_log()
│   │   │       │   └── SystemSettingDAO.java
│   │   │       ├── model/                            ← POJOs + enums per DB table
│   │   │       │   ├── User.java
│   │   │       │   ├── Student.java
│   │   │       │   ├── Device.java
│   │   │       │   ├── EventRequest.java
│   │   │       │   ├── EventRequestDevice.java
│   │   │       │   ├── DeviceLog.java
│   │   │       │   ├── AuditLog.java
│   │   │       │   ├── AuditActionTypes.java
│   │   │       │   ├── SystemSetting.java
│   │   │       │   ├── enums/
│   │   │       │   │   ├── Role.java
│   │   │       │   │   ├── DeviceType.java
│   │   │       │   │   └── RegistrationStatus.java
│   │   │       │   └── report/                       ← DTO classes for report queries
│   │   │       │       ├── ActiveDeviceRow.java
│   │   │       │       ├── DailyTrafficRow.java
│   │   │       │       ├── DeviceFrequencyRow.java
│   │   │       │       ├── IncidentOverrideRow.java
│   │   │       │       ├── MonthlyTrafficRow.java
│   │   │       │       └── PendingRegistrationRow.java
│   │   │       ├── config/                           ← Spring configuration beans
│   │   │       │   ├── DataSourceConfig.java         ← HikariCP + Railway env vars
│   │   │       │   ├── CorsConfig.java               ← Allow requests from JavaFX host
│   │   │       │   └── SecurityConfig.java           ← Stateless Spring Security filter chain
│   │   │       ├── exception/                        ← Custom exceptions + global handler
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   ├── BusinessRuleException.java
│   │   │       │   ├── ForbiddenException.java
│   │   │       │   └── GlobalExceptionHandler.java   ← @ControllerAdvice
│   │   │       └── util/                             ← Stateless helpers
│   │   │           ├── PasswordUtil.java              ← BCrypt hash / verify
│   │   │           ├── ValidationUtil.java
│   │   │           └── DateUtil.java
│   │   └── resources/
│   │       ├── application.properties                ← Spring config (reads Railway env vars)
│   │       └── db/
│   │           └── schema.sql                        ← Full PostgreSQL schema (reference copy)
│   └── test/
│       └── java/com/pup/byod/javabyodbackend/       ← Unit / integration tests
├── pom.xml                                           ← Maven: Spring Boot, JDBC, PostgreSQL driver, BCrypt
├── Procfile                                          ← Railway start command
├── .gitignore
└── README.md
```

---

## 11. Toolchain Summary

| Tool | Used For | Notes |
|---|---|---|
| **IntelliJ IDEA** | Both repos | IDE for JavaFX (Community Edition) and Spring Boot (Ultimate) |
| **Git** | Both repos | Local version control; commit before every Railway deploy |
| **GitHub** | Both repos | Two separate repositories: byod-frontend and byod-backend |
| **Railway** | Backend + DB | Hosts Spring Boot JAR and PostgreSQL database; environment variables for JDBC credentials |
| **Maven (pom.xml)** | Both repos | Dependency management for JavaFX SDK, Spring Boot, PostgreSQL JDBC driver, BCrypt, Jackson |
