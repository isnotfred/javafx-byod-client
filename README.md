# PUP BYOD System - JavaFX Client

Welcome to the client application of the **PUP BYOD (Bring Your Own Device) System**. This desktop client is built using **JavaFX 21** and provides a secure, efficient desktop interface for students, administrators, and security guards to manage, approve, and monitor external device access inside the university campus.

---

## 🚀 Key Features

### 1. Student Portal & Access Requests
* **Standard Academic BYOD Requests:** Register personal laptops, mobile phones, tablets, or peripherals for standard academic use.
* **Event BYOD Requests:** Group-based event registrations for competitions, hackathons, and seminars (e.g., PUP Robotics, IT Society).
* **Reschedule & Edit Workflow:** Modify upcoming requests or reschedule expired standard requests with a single click.

### 2. Guard Ingress & Egress Monitoring (Gate Monitor)
* **High-Speed Student Lookup:** Pre-cached data queries running on background threads ensure zero UI freeze during search.
* **Smart Once-a-Day Check-in Logic:**
  * Displays expected gate actions dynamically (e.g., `Entry`, `Exit`, `Missed`).
  * Switch to `Exit` status as soon as the first device is logged in.
  * Hides devices not checked in today during subsequent egress scans (preventing duplicate check-ins).
* **Pastel-Red Missed Egress Alerting:** Missed checkouts from past days are automatically flagged and highlighted in pastel red, allowing guards to process delayed exit logs.

### 3. On-Campus Device Presence Tracker
* **Real-time Table:** Live view of all active devices currently on the campus.
* **Device Specifications:** Click **View** to open modal detailing student details, brand, model, type, and serial number.
* **Presence CSV Export:** Export current presence registers to Excel/CSV with a single click.

---

## 🛠️ Technical Stack & Architecture

* **UI Framework:** JavaFX 21 (FXML-driven layout + CSS styling)
* **Build System:** Apache Maven
* **Core Technologies:** Java 21 (OpenJDK), HttpClient (JSON-based REST API integration)
* **Session Management:** Secure token-based session management across roles (Student, Admin, Guard).

---

## 📥 Getting Started

### Prerequisites
* **Java Development Kit (JDK) 21** (e.g., Eclipse Temurin or Liberica JDK recommended)
* **Maven** (included via wrapper `mvnw`)
* A running instance of the **PUP BYOD Backend REST API**

### Running the Application

1. **Verify Backend Status:** Ensure the backend Spring Boot server is running (normally on port `8080` or Railway).
2. **Compile and Build:**
   ```bash
   ./mvnw clean compile
   ```
3. **Launch the Desktop App:**
   ```bash
   ./mvnw javafx:run
   ```

---

## 📦 Standalone Installer Releases
You can bundle the desktop application into a standalone Windows installer (`.exe`) that includes its own lightweight Java runtime:

1. **Clean and Package JAR:**
   ```bash
   ./mvnw clean package
   ```
2. **Generate Java Runtime Image:**
   ```bash
   ./mvnw javafx:jlink
   ```
3. **Build the `.exe` Installer:**
   *(Requires Wix Toolset installed on Windows)*
   ```bash
   jpackage --type exe --name "BYODClient" --app-version 1.1.0 --runtime-image target/app --module-path target --module com.pup.byod.javafxbyodclient/com.pup.byod.javafxbyodclient.BYODApplication --dest releases --win-dir-chooser --win-shortcut --icon BYOD_Logo.ico
   ```

All generated binaries and historical releases are saved in the [releases/](file:///D:/IdeaProjects/javafx-byod-client/releases) directory.

---

## 📂 Project Structure

```text
javafx-byod-client/
├── src/main/java/com/pup/byod/javafxbyodclient/
│   ├── controller/      # FXML Screen Controllers (Requests, Monitoring, On-Campus, etc.)
│   ├── model/           # Data Transfer Objects (DTOs) & Enums
│   ├── service/         # API Service Client handlers (HttpClient integrations)
│   ├── session/         # Session manager & credentials
│   └── util/            # Helpers (Validation, Date Formatter, CSV Exporter)
└── src/main/resources/com/pup/byod/javafxbyodclient/
    ├── fxml/            # Screen views & modals
    └── css/             # Stylesheets (Modern Slate & blue-themed interface)
```

---

## 👥 Authors & Contributors
This application was designed and developed by:
* **Angelo Castroverde**
* **Chazlene Bacay**
* **Frederick Orlain**
* **Kier Bardelosa**

---

## 🔒 License
This project is licensed under the MIT License. See the [LICENSE](file:///D:/IdeaProjects/javafx-byod-client/LICENSE) file for details.
