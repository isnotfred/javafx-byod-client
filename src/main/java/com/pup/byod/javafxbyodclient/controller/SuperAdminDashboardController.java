package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class SuperAdminDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        NavigationManager.getInstance().setContentArea(contentArea);
        if (SessionManager.getInstance().getCurrentUser() != null) {
            String fullName = SessionManager.getInstance().getCurrentUser().getFullName();
            welcomeLabel.setText("Welcome, " + fullName + " (Super Admin)");
        }
        // Load default screen
        showSummaryDashboard();
    }

    @FXML
    public void showSummaryDashboard() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "SuperAdminSummaryDashboard.fxml");
    }

    @FXML
    public void showUserManagement() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "UserManagementScreen.fxml");
    }

    @FXML
    public void showSystemConfiguration() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "SystemConfigurationScreen.fxml");
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
