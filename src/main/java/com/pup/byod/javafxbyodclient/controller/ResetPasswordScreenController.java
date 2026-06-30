package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.service.AuthService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ResetPasswordScreenController {
    @FXML private TextField tokenField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleResetSubmit(ActionEvent event) {
        String token = tokenField.getText();
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (ValidationHelper.isEmpty(token)) {
            AlertHelper.showWarning("Form Validation", "Token Required", "Please enter the security token from your email.");
            return;
        }

        if (ValidationHelper.isEmpty(newPassword) || newPassword.length() < 8) {
            AlertHelper.showWarning("Form Validation", "Invalid Password", "Password must be at least 8 characters long.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            AlertHelper.showWarning("Form Validation", "Password Mismatch", "Passwords do not match. Please verify.");
            return;
        }

        // Asynchronous submission
        new Thread(() -> {
            try {
                authService.resetPassword(token.trim(), newPassword);
                Platform.runLater(() -> {
                    AlertHelper.showInfo("Success", "Password Reset", "Your password has been reset successfully! You can now log in.");
                    NavigationManager.getInstance().switchRootScene("LoginScreen.fxml");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Error", "Reset Failed", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        NavigationManager.getInstance().switchRootScene("LoginScreen.fxml");
    }
}
