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
import javafx.scene.shape.SVGPath;

public class ProfileScreenController {
    @FXML private TextField usernameField;
    @FXML private Label roleLabel;
    @FXML private Label fullNameLabel;
    @FXML private PasswordField currentPasswordField;
    @FXML private TextField currentPasswordVisibleField;
    @FXML private SVGPath currentToggleIcon;
    
    @FXML private PasswordField newPasswordField;
    @FXML private TextField newPasswordVisibleField;
    @FXML private SVGPath newToggleIcon;
    
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField confirmPasswordVisibleField;
    @FXML private SVGPath confirmToggleIcon;

    private boolean isCurrentVisible = false;
    private boolean isNewVisible = false;
    private boolean isConfirmVisible = false;

    private static final String EYE_CLOSED = "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z";
    private static final String EYE_OPEN = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        currentPasswordVisibleField.textProperty().bindBidirectional(currentPasswordField.textProperty());
        newPasswordVisibleField.textProperty().bindBidirectional(newPasswordField.textProperty());
        confirmPasswordVisibleField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

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

        if (!AlertHelper.showConfirmation("Update Profile", "Confirm Username Update", "Are you sure you want to update your username?")) {
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
        // ... (keep logic the same)
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

        if (!AlertHelper.showConfirmation("Update Profile", "Confirm Password Change", "Are you sure you want to change your login password?")) {
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

    @FXML
    private void toggleCurrentPassword() {
        isCurrentVisible = !isCurrentVisible;
        togglePasswordFields(isCurrentVisible, currentPasswordField, currentPasswordVisibleField, currentToggleIcon);
    }

    @FXML
    private void toggleNewPassword() {
        isNewVisible = !isNewVisible;
        togglePasswordFields(isNewVisible, newPasswordField, newPasswordVisibleField, newToggleIcon);
    }

    @FXML
    private void toggleConfirmPassword() {
        isConfirmVisible = !isConfirmVisible;
        togglePasswordFields(isConfirmVisible, confirmPasswordField, confirmPasswordVisibleField, confirmToggleIcon);
    }

    private void togglePasswordFields(boolean isVisible, PasswordField pField, TextField tField, SVGPath icon) {
        if (isVisible) {
            tField.setVisible(true);
            tField.setManaged(true);
            pField.setVisible(false);
            pField.setManaged(false);
            icon.setContent(EYE_OPEN);
        } else {
            pField.setVisible(true);
            pField.setManaged(true);
            tField.setVisible(false);
            tField.setManaged(false);
            icon.setContent(EYE_CLOSED);
        }
    }
}
