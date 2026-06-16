package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.User;
import com.pup.byod.javafxbyodclient.service.SuperAdminService;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

public class SuperAdminSummaryDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label totalOperatorsLabel;
    @FXML private Label activeOperatorsLabel;
    @FXML private Label pendingOperatorsLabel;

    private final SuperAdminService superAdminService = new SuperAdminService();

    @FXML
    public void initialize() {
        if (com.pup.byod.javafxbyodclient.session.SessionManager.getInstance().getCurrentUser() != null) {
            String fullName = com.pup.byod.javafxbyodclient.session.SessionManager.getInstance().getCurrentUser().getFullName();
            welcomeLabel.setText("Welcome back, " + fullName + "!");
        } else {
            welcomeLabel.setText("Welcome back!");
        }
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
        syncSidebarSelection("Operator Management");
    }

    @FXML
    public void goToConfiguration() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "SystemConfigurationScreen.fxml");
        syncSidebarSelection("System Configuration");
    }

    @FXML
    public void goToProfile() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "ProfileScreen.fxml");
        syncSidebarSelection("Profile Management");
    }

    private void syncSidebarSelection(String buttonText) {
        try {
            javafx.scene.layout.Pane container = NavigationManager.getInstance().getContentArea();
            if (container != null && container.getScene() != null) {
                for (javafx.scene.Node node : container.getScene().getRoot().lookupAll(".sidebar-btn")) {
                    if (node instanceof javafx.scene.control.ToggleButton) {
                        javafx.scene.control.ToggleButton btn = (javafx.scene.control.ToggleButton) node;
                        if (buttonText.equals(btn.getText())) {
                            btn.setSelected(true);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
