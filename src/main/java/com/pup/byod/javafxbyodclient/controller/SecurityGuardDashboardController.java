package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import com.pup.byod.javafxbyodclient.util.AlertHelper;

public class SecurityGuardDashboardController {
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        NavigationManager.getInstance().setContentArea(contentArea);
        // Load default screen
        showSummaryDashboard();
    }

    @FXML
    public void showSummaryDashboard() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "SecurityGuardSummaryDashboard.fxml");
    }

    @FXML
    public void showGateScan() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "IngressEgressMonitoringScreen.fxml");
    }

    @FXML
    public void showActiveDevices() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "ActiveDevicesInsideCampusScreen.fxml");
    }

    @FXML
    public void showQuickRegister() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "QuickPendingRegistrationScreen.fxml");
    }

    @FXML
    public void showEventScanner() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "TemporaryEventDeviceGuardScreen.fxml");
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
