package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.User;
import com.pup.byod.javafxbyodclient.service.SuperAdminService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.List;
import java.util.Optional;

public class UserManagementScreenController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colStatus;

    @FXML private TextField searchField;
    @FXML private Button editBtn;
    @FXML private Button deactivateBtn;

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

        // Initialize state of buttons
        editBtn.setDisable(true);
        deactivateBtn.setDisable(true);

        // Table selection listener to handle dynamic enable/disable and reactivate/deactivate styling
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                editBtn.setDisable(false);
                deactivateBtn.setDisable(false);

                boolean isInactive = "inactive".equalsIgnoreCase(newVal.getStatus()) || "deactivated".equalsIgnoreCase(newVal.getStatus());
                if (isInactive) {
                    deactivateBtn.setText("Reactivate Operator");
                    deactivateBtn.getStyleClass().removeAll("action-btn-danger");
                    if (!deactivateBtn.getStyleClass().contains("action-btn-success")) {
                        deactivateBtn.getStyleClass().add("action-btn-success");
                    }
                } else {
                    deactivateBtn.setText("Deactivate Operator");
                    deactivateBtn.getStyleClass().removeAll("action-btn-success");
                    if (!deactivateBtn.getStyleClass().contains("action-btn-danger")) {
                        deactivateBtn.getStyleClass().add("action-btn-danger");
                    }
                }
            } else {
                editBtn.setDisable(true);
                deactivateBtn.setDisable(true);

                deactivateBtn.setText("Deactivate Operator");
                deactivateBtn.getStyleClass().removeAll("action-btn-success");
                if (!deactivateBtn.getStyleClass().contains("action-btn-danger")) {
                    deactivateBtn.getStyleClass().add("action-btn-danger");
                }
            }
        });
        
        // Setup Search/Filter
        FilteredList<User> filteredData = new FilteredList<>(userList, p -> true);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(user -> {
                if (newVal == null || newVal.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newVal.toLowerCase().trim();
                
                if (user.getFullName() != null && user.getFullName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (user.getRole() != null && user.getRole().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (user.getStatus() != null && user.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        userTable.setItems(filteredData);

        // Highlight inactive operators in bright/pinkish red
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<User>() {
                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        getStyleClass().removeAll("inactive-row");
                    } else if ("inactive".equalsIgnoreCase(item.getStatus()) || "deactivated".equalsIgnoreCase(item.getStatus())) {
                        if (!getStyleClass().contains("inactive-row")) {
                            getStyleClass().add("inactive-row");
                        }
                    } else {
                        getStyleClass().removeAll("inactive-row");
                    }
                }
            };
            return row;
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
    public void handleAddOperator() {
        showAddOperatorDialog();
    }

    @FXML
    public void handleEditOperator() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Edit Operator", "No Selection", "Please select an operator from the table.");
            return;
        }
        showEditOperatorDialog(selected);
    }

    @FXML
    public void handleDeactivate() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("User update", "No Selection", "Please select a user.");
            return;
        }

        // Prevent self-deactivation
        int currentUserId = SessionManager.getInstance().getCurrentUser().getUserId();
        if (selected.getUserId() == currentUserId) {
            AlertHelper.showWarning("Deactivation", "Self-Deactivation Blocked", "You cannot deactivate your own account.");
            return;
        }

        boolean isInactive = "inactive".equalsIgnoreCase(selected.getStatus()) || "deactivated".equalsIgnoreCase(selected.getStatus());
        int actingUserId = currentUserId;

        if (isInactive) {
            if (!AlertHelper.showConfirmation("Reactivate Operator", "Confirm Reactivation", "Are you sure you want to reactivate operator " + selected.getUsername() + "?")) {
                return;
            }
            try {
                superAdminService.updateUser(selected.getUserId(), actingUserId, selected.getFullName(), "active");
                AlertHelper.showInfo("Reactivated", "Success", "User account reactivated successfully.");
                loadUsers();
            } catch (Exception e) {
                AlertHelper.showError("Error", "Action Failed", e.getMessage());
            }
        } else {
            if (!AlertHelper.showConfirmation("Deactivate Operator", "Confirm Deactivation", "Are you sure you want to deactivate operator " + selected.getUsername() + "? They will lose access to the system.")) {
                return;
            }
            try {
                superAdminService.deactivateUser(selected.getUserId(), actingUserId);
                AlertHelper.showInfo("Deactivated", "Success", "User account deactivated successfully.");
                loadUsers();
            } catch (Exception e) {
                AlertHelper.showError("Error", "Action Failed", e.getMessage());
            }
        }
    }

    private void showAddOperatorDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Onboard New Operator");
        dialog.setHeaderText("Enter new operator details below.");

        DialogPane dialogPane = dialog.getDialogPane();
        try {
            dialogPane.getStylesheets().clear();
            dialogPane.getStylesheets().add(getClass().getResource("/com/pup/byod/javafxbyodclient/css/dialog_styles.css").toExternalForm());
            dialogPane.getStylesheets().add(getClass().getResource("/com/pup/byod/javafxbyodclient/css/admin_dashboard_styles.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load dialog styles: " + e.getMessage());
        }
        dialogPane.getStyleClass().add("custom-dialog-pane");
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(450);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(new ColumnConstraints(), col2);

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. John Doe");
        nameField.getStyleClass().add("modern-textfield");
        nameField.setPrefWidth(250);
        nameField.setMaxWidth(Double.MAX_VALUE);
        nameField.setPrefHeight(38);

        TextField emailField = new TextField();
        emailField.setPromptText("e.g. john.doe@example.com");
        emailField.getStyleClass().add("modern-textfield");
        emailField.setPrefWidth(250);
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setPrefHeight(38);

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("admin", "guard");
        roleCombo.setPromptText("Select Role");
        roleCombo.getStyleClass().add("modern-combo-box");
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.setPrefHeight(38);

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email Address:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("System Role:"), 0, 2);
        grid.add(roleCombo, 1, 2);

        dialogPane.setContent(grid);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showingProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Button okBtn = (Button) dialogPane.lookupButton(ButtonType.OK);
                if (okBtn != null) {
                    okBtn.getStyleClass().clear();
                    okBtn.getStyleClass().addAll("button", "dialog-btn", "dialog-primary-btn");
                    okBtn.setText("Onboard");
                }
                Button cancelBtn = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
                if (cancelBtn != null) {
                    cancelBtn.getStyleClass().clear();
                    cancelBtn.getStyleClass().addAll("button", "dialog-btn", "dialog-cancel-btn");
                }
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText();
            String email = emailField.getText();
            String role = roleCombo.getValue();

            if (ValidationHelper.isEmpty(name) || ValidationHelper.isEmpty(email) || role == null) {
                AlertHelper.showWarning("Form Validation", "Missing fields", "Please fill in all onboard credentials.");
                return;
            }

            try {
                int actingUserId = SessionManager.getInstance().getCurrentUser().getUserId();
                superAdminService.onboardUser(actingUserId, name, email, role);
                AlertHelper.showInfo("Onboarded", "Success", "Onboarding email sent, account in pending status.");
                loadUsers();
            } catch (Exception e) {
                AlertHelper.showError("Error", "Onboard Failed", e.getMessage());
            }
        }
    }

    private void showEditOperatorDialog(User selected) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Operator");
        dialog.setHeaderText("Modify details for operator: " + selected.getUsername());

        DialogPane dialogPane = dialog.getDialogPane();
        try {
            dialogPane.getStylesheets().clear();
            dialogPane.getStylesheets().add(getClass().getResource("/com/pup/byod/javafxbyodclient/css/dialog_styles.css").toExternalForm());
            dialogPane.getStylesheets().add(getClass().getResource("/com/pup/byod/javafxbyodclient/css/admin_dashboard_styles.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load dialog styles: " + e.getMessage());
        }
        dialogPane.getStyleClass().add("custom-dialog-pane");
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(450);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new javafx.geometry.Insets(20, 10, 10, 10));

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(new ColumnConstraints(), col2);

        TextField nameField = new TextField(selected.getFullName());
        nameField.getStyleClass().add("modern-textfield");
        nameField.setPrefWidth(250);
        nameField.setMaxWidth(Double.MAX_VALUE);
        nameField.setPrefHeight(38);

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("admin", "guard");
        roleCombo.setValue(selected.getRole());
        roleCombo.getStyleClass().add("modern-combo-box");
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.setPrefHeight(38);

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("active", "inactive");
        statusCombo.setValue(selected.getStatus());
        statusCombo.getStyleClass().add("modern-combo-box");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        statusCombo.setPrefHeight(38);

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("System Role:"), 0, 1);
        grid.add(roleCombo, 1, 1);
        grid.add(new Label("Account Status:"), 0, 2);
        grid.add(statusCombo, 1, 2);

        dialogPane.setContent(grid);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showingProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Button okBtn = (Button) dialogPane.lookupButton(ButtonType.OK);
                if (okBtn != null) {
                    okBtn.getStyleClass().clear();
                    okBtn.getStyleClass().addAll("button", "dialog-btn", "dialog-primary-btn");
                    okBtn.setText("Save Changes");
                }
                Button cancelBtn = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
                if (cancelBtn != null) {
                    cancelBtn.getStyleClass().clear();
                    cancelBtn.getStyleClass().addAll("button", "dialog-btn", "dialog-cancel-btn");
                }
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String name = nameField.getText();
            String status = statusCombo.getValue();
            String role = roleCombo.getValue();

            if (ValidationHelper.isEmpty(name) || status == null || role == null) {
                AlertHelper.showWarning("Form Validation", "Missing fields", "Please fill in all fields.");
                return;
            }

            try {
                int actingUserId = SessionManager.getInstance().getCurrentUser().getUserId();
                int updates = 0;

                if (!name.equals(selected.getFullName()) || !status.equals(selected.getStatus())) {
                    superAdminService.updateUser(selected.getUserId(), actingUserId, name, status);
                    updates++;
                }

                if (!role.equals(selected.getRole())) {
                    superAdminService.changeRole(selected.getUserId(), actingUserId, role);
                    updates++;
                }

                if (updates > 0) {
                    AlertHelper.showInfo("Updated", "Success", "Operator information updated successfully.");
                    loadUsers();
                }
            } catch (Exception e) {
                AlertHelper.showError("Error", "Update Failed", e.getMessage());
            }
        }
    }
}
