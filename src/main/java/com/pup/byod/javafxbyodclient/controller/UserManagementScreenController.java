package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.User;
import com.pup.byod.javafxbyodclient.service.SuperAdminService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UserManagementScreenController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleBox;
    @FXML private ComboBox<String> statusBox;

    private final SuperAdminService superAdminService = new SuperAdminService();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        roleBox.getItems().addAll("super_admin", "admin", "guard");
        statusBox.getItems().addAll("active", "pending", "deactivated");
        userTable.setItems(userList);
        colId.getStyleClass().add("right-arrow-header");

        // Restrict sorting strictly to the User ID column with a 2-state sort (ASCENDING <-> DESCENDING)
        colId.setSortable(true);
        colUsername.setSortable(false);
        colEmail.setSortable(false);
        colName.setSortable(false);
        colRole.setSortable(false);
        colStatus.setSortable(false);

        colId.setSortType(TableColumn.SortType.ASCENDING);
        userTable.getSortOrder().add(colId);

        userTable.setOnSort(event -> {
            if (userTable.getSortOrder().isEmpty()) {
                colId.setSortType(TableColumn.SortType.ASCENDING);
                javafx.application.Platform.runLater(() -> {
                    userTable.getSortOrder().add(colId);
                });
            }
        });
        
        // Table selection listener
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fullNameField.setText(newVal.getFullName());
                usernameField.setText(newVal.getUsername());
                emailField.setText(newVal.getEmail());
                roleBox.setValue(newVal.getRole());
                statusBox.setValue(newVal.getStatus());
            }
        });

        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = superAdminService.getAllUsers();
            userList.setAll(users);
        } catch (Exception e) {
            System.err.println("Could not load users: " + e.getMessage());
            AlertHelper.showError("Error", "Load Failed", e.getMessage());
        }
    }

    @FXML
    public void handleOnboard() {
        String name = fullNameField.getText();
        String email = emailField.getText();
        String role = roleBox.getValue();

        if (ValidationHelper.isEmpty(name) || ValidationHelper.isEmpty(email) || role == null) {
            AlertHelper.showWarning("Form Validation", "Missing fields", "Please fill in all onboard credentials.");
            return;
        }

        if (!AlertHelper.showConfirmation("Onboard Operator", "Confirm Onboard", "Are you sure you want to onboard " + email + " as a new operator?")) {
            return;
        }

        try {
            int actingUserId = SessionManager.getInstance().getCurrentUser().getUserId();
            superAdminService.onboardUser(actingUserId, name, email, role);
            AlertHelper.showInfo("Onboarded", "Success", "Onboarding email sent, account in pending status.");
            clearForm();
            loadUsers();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Onboard Failed", e.getMessage());
        }
    }

    @FXML
    public void handleUpdate() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Update Operator", "No Selection", "Please select an operator from the table.");
            return;
        }

        String name = fullNameField.getText();
        String status = statusBox.getValue();

        if (ValidationHelper.isEmpty(name) || status == null) {
            AlertHelper.showWarning("Form Validation", "Missing fields", "Please fill in name and status.");
            return;
        }

        if (!AlertHelper.showConfirmation("Update Operator", "Confirm Update", "Are you sure you want to save updates to operator " + selected.getUsername() + "'s profile?")) {
            return;
        }

        try {
            int actingUserId = SessionManager.getInstance().getCurrentUser().getUserId();
            superAdminService.updateUser(selected.getUserId(), actingUserId, name, status);
            AlertHelper.showInfo("Updated", "Success", "Operator information updated successfully.");
            clearForm();
            loadUsers();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Update Failed", e.getMessage());
        }
    }

    @FXML
    public void handleChangeRole() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Change Role", "No Selection", "Please select an operator from the table.");
            return;
        }

        String role = roleBox.getValue();
        if (role == null) {
            AlertHelper.showWarning("Change Role", "Role Required", "Please select a role.");
            return;
        }

        if (!AlertHelper.showConfirmation("Change Role", "Confirm Role Change", "Are you sure you want to change the role of operator " + selected.getUsername() + " to " + role + "?")) {
            return;
        }

        try {
            int actingUserId = SessionManager.getInstance().getCurrentUser().getUserId();
            superAdminService.changeRole(selected.getUserId(), actingUserId, role);
            AlertHelper.showInfo("Role Updated", "Success", "Operator role changed to: " + role);
            clearForm();
            loadUsers();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Role Update Failed", e.getMessage());
        }
    }

    @FXML
    public void handleDeactivate() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("User update", "No Selection", "Please select a user.");
            return;
        }
        if (!AlertHelper.showConfirmation("Deactivate Operator", "Confirm Deactivation", "Are you sure you want to deactivate operator " + selected.getUsername() + "? They will lose access to the system.")) {
            return;
        }
        try {
            int actingUserId = SessionManager.getInstance().getCurrentUser().getUserId();
            superAdminService.deactivateUser(selected.getUserId(), actingUserId);
            AlertHelper.showInfo("Deactivated", "Success", "User account deactivated successfully.");
            clearForm();
            loadUsers();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Action Failed", e.getMessage());
        }
    }

    private void clearForm() {
        fullNameField.clear();
        usernameField.clear();
        emailField.clear();
        roleBox.setValue(null);
        statusBox.setValue(null);
        userTable.getSelectionModel().clearSelection();
    }
}
