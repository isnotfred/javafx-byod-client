package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class AdminDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        NavigationManager.getInstance().setContentArea(contentArea);
        if (SessionManager.getInstance().getCurrentUser() != null) {
            String fullName = SessionManager.getInstance().getCurrentUser().getFullName();
            String role = SessionManager.getInstance().getCurrentUser().getRole();
            welcomeLabel.setText("Welcome, " + fullName + " (" + role + ")");
        }
        // Load default screen
        showSummaryDashboard();
    }

    @FXML
    public void showSummaryDashboard() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "AdminSummaryDashboard.fxml");
    }

    @FXML
    public void showDeviceManagement() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "DeviceManagementScreen.fxml");
    }

    @FXML
    public void showPendingApprovals() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "PendingRegistrationApprovalScreen.fxml");
    }

    @FXML
    public void showStudentManagement() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "StudentManagementScreen.fxml");
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
        SessionManager.getInstance().logout();
        NavigationManager.getInstance().switchRootScene("LoginScreen.fxml");
    }
}
