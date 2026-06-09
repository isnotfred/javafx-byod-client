package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.User;
import com.pup.byod.javafxbyodclient.model.enums.UserRole;
import com.pup.byod.javafxbyodclient.service.AuthService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginScreenController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Mock connection check or actual request
            User user = authService.login(username, password);
            if (UserRole.super_admin.getValue().equalsIgnoreCase(user.getRole())) {
                NavigationManager.getInstance().switchRootScene("SuperAdminDashboard.fxml");
            } else if (UserRole.admin.getValue().equalsIgnoreCase(user.getRole())) {
                NavigationManager.getInstance().switchRootScene("AdminDashboard.fxml");
            } else {
                NavigationManager.getInstance().switchRootScene("SecurityGuardDashboard.fxml");
            }
        } catch (Exception e) {
            AlertHelper.showError("Login Failed", "Invalid Credentials or Connection issue", e.getMessage());
        }
    }

    @FXML
    private void handleForgotPassword() {
        NavigationManager.getInstance().openModal("ForgotPasswordScreen.fxml", "Forgot Password");
    }
}
