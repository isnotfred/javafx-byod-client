package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class SecurityGuardDashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label roleBadge;

    @FXML
    public void initialize() {
        NavigationManager.getInstance().setContentArea(contentArea);
        
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

        // Load the only screen: Ingress/Egress Screen
        showGateScan();
    }

    @FXML
    public void showGateScan() {
        NavigationManager.getInstance().loadViewIntoContainer(contentArea, "IngressEgressMonitoringScreen.fxml");
    }

    @FXML
    public void handleLogout() {
        if (AlertHelper.showConfirmation("Logout", "Confirm Logout", "Are you sure you want to log out of the system?")) {
            SessionManager.getInstance().logout();
            NavigationManager.getInstance().switchRootScene("LoginScreen.fxml");
        }
    }
}
