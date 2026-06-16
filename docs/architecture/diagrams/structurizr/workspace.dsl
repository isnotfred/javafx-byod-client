workspace "BYOD Device Management System" "C4 model for the target 3-tier BYOD monitoring system" {
    model {
        superAdmin = person "Super Admin" "Manages user accounts, role assignments, system settings, and own profile."
        admin = person "Admin" "Manages students, devices, approvals, reports, and audit history."
        guard = person "Guard" "Searches records, submits pending devices, and logs approved device entry/exit."
        student = person "Student" "Indirect user who presents devices and details for verification."

        byod = softwareSystem "BYOD Device Management System" "JavaFX desktop frontend, Spring Boot REST API, and Railway PostgreSQL." {
            frontend = container "JavaFX Desktop Frontend" "Desktop UI, controllers, API client, and session context." "Java / JavaFX"
            backend = container "Spring Boot REST API" "HTTP endpoints, services, transactions, validation, scheduler, and DAO orchestration." "Java / Spring Boot"
            database = container "Railway PostgreSQL" "Stores tables, views, triggers, functions, indexes, logs, and audit trail." "PostgreSQL"

            auth = component "Authentication and Recovery API" "Authenticates users, returns role context, and handles password recovery." "Controller + Service"
            profile = component "Profile API" "Updates the authenticated user's username and password." "Controller + Service"
            userAdmin = component "User Administration API" "Onboards users and manages role and account status." "Controller + Service"
            settings = component "System Settings API" "Reads and updates configurable system policy." "Controller + Service"
            device = component "Device API" "Manages devices and pending approvals." "Controller + Service"
            eventRequest = component "Event Request API" "Manages request auto-approval/manual review, manifests, event entry/exit, and reconciliation." "Controller + Service"
            gateLog = component "Gate Log API" "Writes immutable entry/exit records and reads derived status." "Controller + Service"
            audit = component "Audit DAO" "Calls fn_write_audit_log for standardized audit rows." "DAO"
        }

        superAdmin -> frontend "Uses"
        admin -> frontend "Uses"
        guard -> frontend "Uses"
        student -> guard "Presents device/details to"
        frontend -> backend "Calls HTTPS JSON API"
        backend -> database "Reads/writes using JDBC over TLS"
        auth -> database "Reads users and writes audit"
        profile -> database "Updates users and writes audit"
        userAdmin -> database "Manages users and writes audit"
        settings -> database "Reads/writes system_settings and audit"
        device -> database "Reads/writes devices and views"
        eventRequest -> database "Reads/writes event requests, event_device_logs, status views, and settings"
        gateLog -> database "Inserts device_logs and reads latest state"
        audit -> database "Calls fn_write_audit_log"
    }

    views {
        systemContext byod "SystemContext" {
            include *
            autolayout lr
        }

        container byod "Containers" {
            include *
            autolayout tb
        }

        component backend "BackendComponents" {
            include *
            autolayout lr
        }

        styles {
            element "Person" {
                shape person
            }
            element "PostgreSQL" {
                shape cylinder
            }
        }
    }
}
