package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.User;
import com.pup.byod.javafxbyodclient.service.SuperAdminService;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

public class SuperAdminSummaryDashboardController {
    @FXML private Label totalOperatorsLabel;
    @FXML private Label activeOperatorsLabel;
    @FXML private Label pendingOperatorsLabel;

    private final SuperAdminService superAdminService = new SuperAdminService();

    @FXML
    public void initialize() {
        refreshStats();
    }

    @FXML
    public void refreshStats() {
        totalOperatorsLabel.setText("...");
        activeOperatorsLabel.setText("...");
        pendingOperatorsLabel.setText("...");

        new Thread(() -> {
            try {
                // Fetch all system users/operators
                List<User> users = superAdminService.getAllUsers();
                long totalUsers = users.size();
                long activeUsers = users.stream().filter(u -> "active".equalsIgnoreCase(u.getStatus())).count();
                long pendingUsers = users.stream().filter(u -> "pending".equalsIgnoreCase(u.getStatus())).count();

                Platform.runLater(() -> {
                    totalOperatorsLabel.setText(String.valueOf(totalUsers));
                    activeOperatorsLabel.setText(String.valueOf(activeUsers));
                    pendingOperatorsLabel.setText(String.valueOf(pendingUsers));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    totalOperatorsLabel.setText("Error");
                    activeOperatorsLabel.setText("Error");
                    pendingOperatorsLabel.setText("Error");
                });
            }
        }).start();
    }

    @FXML
    public void goToOperators() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "UserManagementScreen.fxml");
    }

    @FXML
    public void goToConfiguration() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "SystemConfigurationScreen.fxml");
    }

    @FXML
    public void goToProfile() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "ProfileScreen.fxml");
    }
}
