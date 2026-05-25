workspace "BYOD Device Management System" "C4 model for the target 3-tier BYOD monitoring system" {
    model {
        admin = person "Admin" "Manages students, devices, approvals, reports, users, and audit history."
        guard = person "Guard" "Searches records, submits pending devices, and logs approved device entry/exit."
        student = person "Student" "Indirect user who presents devices and details for verification."

        byod = softwareSystem "BYOD Device Management System" "JavaFX desktop frontend, Spring Boot REST API, and Railway PostgreSQL." {
            frontend = container "JavaFX Desktop Frontend" "Desktop UI, controllers, API client, and session context." "Java / JavaFX"
            backend = container "Spring Boot REST API" "HTTP endpoints, services, transactions, validation, scheduler, and DAO orchestration." "Java / Spring Boot"
            database = container "Railway PostgreSQL" "Stores tables, views, triggers, functions, indexes, logs, and audit trail." "PostgreSQL"
            images = container "Image Storage" "Stores or references optional device images; policy TBD." "TBD"

            auth = component "Authentication API" "Authenticates users and returns role context." "Controller + Service"
            device = component "Device API" "Manages devices and pending approvals." "Controller + Service"
            eventRequest = component "Event Request API" "Manages event request headers and device line items." "Controller + Service"
            gateLog = component "Gate Log API" "Writes immutable entry/exit records and reads derived status." "Controller + Service"
            audit = component "Audit DAO" "Calls fn_write_audit_log for standardized audit rows." "DAO"
        }

        admin -> frontend "Uses"
        guard -> frontend "Uses"
        student -> guard "Presents device/details to"
        frontend -> backend "Calls HTTPS JSON API"
        frontend -> images "Uses optional image path/storage"
        backend -> database "Reads/writes using JDBC over TLS"
        auth -> database "Reads users and writes audit"
        device -> database "Reads/writes devices and views"
        eventRequest -> database "Reads/writes event requests"
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
