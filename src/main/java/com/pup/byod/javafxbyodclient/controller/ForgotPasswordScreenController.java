package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.service.AuthService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ForgotPasswordScreenController {
    @FXML private TextField emailField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleSubmit(ActionEvent event) {
        String email = emailField.getText();

        if (ValidationHelper.isEmpty(email)) {
            AlertHelper.showWarning("Form Validation", "Email Required", "Please enter your email address.");
            return;
        }

        if (!ValidationHelper.isValidEmail(email)) {
            AlertHelper.showWarning("Form Validation", "Invalid Email", "Please enter a valid email format.");
            return;
        }

        // Asynchronous submission
        new Thread(() -> {
            try {
                authService.forgotPassword(email);
                Platform.runLater(() -> {
                    AlertHelper.showInfo("Success", "Request Sent", "A password reset token has been sent to your email.");
                    NavigationManager.getInstance().switchRootScene("ResetPasswordScreen.fxml");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                    if (msg.contains("not found") || msg.contains("invalid") || msg.contains("not exist") || msg.contains("no user")) {
                        AlertHelper.showError("Error", "Email Not Registered", "That email is not registered in the system.");
                    } else {
                        AlertHelper.showError("Error", "Email Not Registered", "That email is not registered in the system. (Or another error occurred)");
                    }
                    emailField.clear();
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        NavigationManager.getInstance().switchRootScene("LoginScreen.fxml");
    }
}
