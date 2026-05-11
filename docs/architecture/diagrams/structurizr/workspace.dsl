workspace "BYOD Registration and Monitoring System" "C4 model for the desktop BYOD monitoring system" {
    model {
        admin = person "System Administrator" "Manages students, devices, users, approvals, reports, and exceptions."
        guard = person "Security Guard" "Searches records, logs ingress/egress, and submits pending registrations."
        student = person "Student" "Indirect user who presents devices for verification."

        byod = softwareSystem "BYOD Registration and Monitoring System" "Desktop Java application for device registration and ingress-egress monitoring." {
            desktop = container "JavaFX Desktop Application" "Desktop UI, controllers, services, and DAO/JDBC access." "Java / JavaFX"
            database = container "Relational Database" "Stores students, devices, device logs, users, event devices, and audit logs." "Relational Database"
            files = container "Local File Storage" "Stores optional uploaded device images or image paths." "File System"

            auth = component "Authentication Controller" "Handles login and session routing." "JavaFX Controller"
            studentComponent = component "Student Management Component" "Manages student workflows." "Service + Controller"
            deviceComponent = component "Device Management Component" "Manages devices and statuses." "Service + Controller"
            monitoring = component "Monitoring Component" "Handles ingress, egress, and active devices." "Service + Controller"
            pending = component "Pending Registration Component" "Handles pending submission and approval." "Service + Controller"
            reports = component "Report Component" "Generates reports." "Service + Controller"
            dao = component "DAO/JDBC Component" "Executes SQL and maps rows to domain objects." "JDBC DAO"
        }

        admin -> desktop "Uses"
        guard -> desktop "Uses"
        student -> guard "Presents device to"
        desktop -> database "Reads/writes using JDBC"
        desktop -> files "Stores/retrieves optional images"
        auth -> dao "Uses"
        studentComponent -> dao "Uses"
        deviceComponent -> dao "Uses"
        monitoring -> dao "Uses"
        pending -> dao "Uses"
        reports -> dao "Uses"
        dao -> database "Executes SQL against"
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

        component desktop "DesktopComponents" {
            include *
            autolayout lr
        }

        styles {
            element "Person" {
                shape person
            }
            element "Relational Database" {
                shape cylinder
            }
        }
    }
}

