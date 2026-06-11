package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import com.pup.byod.javafxbyodclient.util.AlertHelper;

public class AdminDashboardController {
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        NavigationManager.getInstance().setContentArea(contentArea);
        // Load default screen
        showSummaryDashboard();
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
    public void showPendingApprovals() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "PendingRegistrationApprovalScreen.fxml");
    }

    @FXML
    public void showEventApprovals() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "EventApprovalScreen.fxml");
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
