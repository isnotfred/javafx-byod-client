package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.User;
import com.pup.byod.javafxbyodclient.service.AuthService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileScreenController {
    @FXML private TextField usernameField;
    @FXML private Label roleLabel;
    @FXML private Label fullNameLabel;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            roleLabel.setText(currentUser.getRole());
            fullNameLabel.setText(currentUser.getFullName());
        }
    }

    @FXML
    public void handleUpdateUsername() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            AlertHelper.showError("Error", "No Active Session", "Please log in again.");
            return;
        }

        String newUsername = usernameField.getText();

        if (ValidationHelper.isEmpty(newUsername)) {
            AlertHelper.showWarning("Profile Update", "Missing Fields", "Username cannot be empty.");
            return;
        }

        String trimmed = newUsername.trim();
        if (trimmed.length() < 3) {
            AlertHelper.showWarning("Profile Update", "Invalid Username", "Username must be at least 3 characters.");
            return;
        }

        try {
            authService.updateUsername(currentUser.getUserId(), trimmed);
            AlertHelper.showInfo("Profile Update", "Success", "Your username has been updated successfully.");
        } catch (Exception e) {
            AlertHelper.showError("Error", "Username Update Failed", e.getMessage());
        }
    }

    @FXML
    public void handleUpdatePassword() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            AlertHelper.showError("Error", "No Active Session", "Please log in again.");
            return;
        }

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (ValidationHelper.isEmpty(currentPassword) || ValidationHelper.isEmpty(newPassword) || ValidationHelper.isEmpty(confirmPassword)) {
            AlertHelper.showWarning("Profile Update", "Missing Fields", "Please fill in all password fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            AlertHelper.showWarning("Profile Update", "Password Mismatch", "Passwords do not match. Please verify.");
            return;
        }

        try {
            authService.updatePassword(currentUser.getUserId(), currentPassword, newPassword);
            AlertHelper.showInfo("Profile Update", "Success", "Your password has been updated successfully.");
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Password Update Failed", e.getMessage());
        }
    }
}
