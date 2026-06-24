package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

import com.pup.byod.javafxbyodclient.util.AlertHelper;

public class AdminDashboardController {
    @FXML private StackPane contentArea;
    @FXML private ToggleGroup sidebarGroup;
    @FXML private Label roleBadge;

    @FXML
    public void initialize() {
        NavigationManager.getInstance().setContentArea(contentArea);
        // Load default screen
        showSummaryDashboard();

        // Initialize role badge
        if (SessionManager.getInstance().getCurrentUser() != null) {
            String role = SessionManager.getInstance().getCurrentUser().getRole();
            if ("admin".equalsIgnoreCase(role)) {
                roleBadge.setText("ADMIN");
                roleBadge.getStyleClass().add("role-badge-admin");
            } else if ("guard".equalsIgnoreCase(role)) {
                roleBadge.setText("GUARD");
                roleBadge.getStyleClass().add("role-badge-guard");
            } else if ("super_admin".equalsIgnoreCase(role)) {
                roleBadge.setText("SUPER ADMIN");
                roleBadge.getStyleClass().add("role-badge-super-admin");
            }
        }

        // Prevent deselecting active sidebar item
        if (sidebarGroup != null) {
            sidebarGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null && oldVal != null) {
                    oldVal.setSelected(true);
                }
            });
        }
    }

    @FXML
    public void showSummaryDashboard() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "AdminSummaryDashboard.fxml");
    }

    @FXML
    public void showRegistryManagement() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "RegistryManagementScreen.fxml");
    }

    @FXML
    public void showDeviceManagement() {
        showRegistryManagement();
    }

    @FXML
    public void showEventRequests() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "TemporaryEventDeviceGuardScreen.fxml");
    }

    @FXML
    public void showIngressEgress() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "IngressEgressMonitoringScreen.fxml");
    }

    @FXML
    public void showStudentManagement() {
        showRegistryManagement();
    }

    @FXML
    public void showActiveDevices() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "ActiveDevicesAdminScreen.fxml");
    }

    @FXML
    public void showReports() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "ReportsScreen.fxml");
    }

    @FXML
    public void showProfile() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "ProfileScreen.fxml");
    }

    @FXML
    public void handleLogout() {
        if (AlertHelper.showConfirmation("Logout", "Confirm Logout", "Are you sure you want to log out of the system?")) {
            SessionManager.getInstance().logout();
            NavigationManager.getInstance().switchRootScene("LoginScreen.fxml");
        }
    }
}
