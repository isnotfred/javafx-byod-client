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
                    // Close this modal
                    NavigationManager.getInstance().closeModal(event);
                    // Open the Reset Password modal
                    NavigationManager.getInstance().openModal("ResetPasswordScreen.fxml", "Reset Password");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Error", "Request Failed", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        NavigationManager.getInstance().closeModal(event);
    }
}
