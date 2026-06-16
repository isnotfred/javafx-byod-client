package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.AuditLog;
import com.pup.byod.javafxbyodclient.model.User;
import com.pup.byod.javafxbyodclient.service.AuditLogService;
import com.pup.byod.javafxbyodclient.service.SuperAdminService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemAuditLogsScreenController {
    @FXML private TextField usernameFilterField;
    @FXML private ComboBox<String> actionTypeBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private TableView<AuditLog> auditLogsTable;
    @FXML private TableColumn<AuditLog, Integer> colAuditId;
    @FXML private TableColumn<AuditLog, String> colPerformedBy;
    @FXML private TableColumn<AuditLog, String> colActionType;
    @FXML private TableColumn<AuditLog, String> colTargetTable;
    @FXML private TableColumn<AuditLog, String> colTargetId;
    @FXML private TableColumn<AuditLog, String> colOldValues;
    @FXML private TableColumn<AuditLog, String> colNewValues;
    @FXML private TableColumn<AuditLog, String> colIpAddress;
    @FXML private TableColumn<AuditLog, String> colTimestamp;

    private final AuditLogService auditLogService = new AuditLogService();
    private final SuperAdminService superAdminService = new SuperAdminService();
    private final ObservableList<AuditLog> auditLogsList = FXCollections.observableArrayList();
    private final Map<Integer, String> userMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Configure Table Columns
        colAuditId.setCellValueFactory(new PropertyValueFactory<>("auditId"));
        colActionType.setCellValueFactory(new PropertyValueFactory<>("actionType"));
        colTargetTable.setCellValueFactory(new PropertyValueFactory<>("targetTable"));
        colTargetId.setCellValueFactory(new PropertyValueFactory<>("targetId"));
        colOldValues.setCellValueFactory(new PropertyValueFactory<>("oldValues"));
        colNewValues.setCellValueFactory(new PropertyValueFactory<>("newValues"));
        colIpAddress.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        colTimestamp.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimestamp(cellData.getValue().getActionTime())
        ));

        // Performed By mapping from cached user details
        colPerformedBy.setCellValueFactory(cellData -> {
            Integer uid = cellData.getValue().getUserId();
            if (uid == null) {
                return new SimpleStringProperty("SYSTEM");
            }
            String operator = userMap.get(uid);
            return new SimpleStringProperty(operator != null ? operator : "User ID: " + uid);
        });

        // Configure Action Type dropdown options
        actionTypeBox.getItems().addAll(
            "ALL",
            "DEVICE_REGISTERED",
            "DEVICE_APPROVED",
            "DEVICE_REJECTED",
            "DEVICE_DEACTIVATED",
            "DEVICE_UPDATED",
            "DEVICE_ENTRY",
            "DEVICE_EXIT",
            "DEVICE_AUTO_EXIT",
            "STUDENT_CREATED",
            "STUDENT_UPDATED",
            "STUDENT_DEACTIVATED",
            "USER_CREATED",
            "USER_UPDATED",
            "USER_DEACTIVATED",
            "USER_LOGIN",
            "USER_LOGOUT",
            "USER_LOGIN_FAILED",
            "EVENT_REQUEST_CREATED",
            "EVENT_REQUEST_APPROVED",
            "EVENT_REQUEST_RETURNED",
            "EVENT_REQUEST_REJECTED",
            "SYSTEM_AUTO_EXIT_BATCH",
            "SYSTEM_CONFIG_UPDATED"
        );
        actionTypeBox.setValue("ALL");

        // Load data async
        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                // Fetch users to populate local mapping cache
                List<User> users = superAdminService.getAllUsers();
                userMap.clear();
                for (User u : users) {
                    userMap.put(u.getUserId(), u.getFullName() + " (" + u.getUsername() + ")");
                }

                // Fetch audit logs
                List<AuditLog> logs = auditLogService.getAllAuditLogs();
                
                Platform.runLater(() -> {
                    auditLogsList.setAll(logs);
                    setupFiltering();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Load Error", "Failed to load audit logs", e.getMessage());
                });
            }
        }).start();
    }

    private void setupFiltering() {
        FilteredList<AuditLog> filteredData = new FilteredList<>(auditLogsList, p -> true);

        // Bind filter criteria listeners
        usernameFilterField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));
        actionTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter(filteredData));

        auditLogsTable.setItems(filteredData);
    }

    private void updateFilter(FilteredList<AuditLog> filteredData) {
        filteredData.setPredicate(log -> {
            // Operator filter
            String userSearch = usernameFilterField.getText();
            if (userSearch != null && !userSearch.trim().isEmpty()) {
                String keyword = userSearch.toLowerCase().trim();
                Integer uid = log.getUserId();
                String operatorName = uid != null ? userMap.get(uid) : "system";
                if (operatorName == null) operatorName = "user id: " + uid;
                if (!operatorName.toLowerCase().contains(keyword)) {
                    return false;
                }
            }

            // Action type filter
            String selectedType = actionTypeBox.getValue();
            if (selectedType != null && !selectedType.equals("ALL")) {
                if (log.getActionType() == null || !log.getActionType().equalsIgnoreCase(selectedType)) {
                    return false;
                }
            }

            // Date Range filter (format: "yyyy-MM-ddThh:mm:ss")
            String timeStr = log.getActionTime();
            if (timeStr != null && timeStr.length() >= 10) {
                try {
                    String datePart = timeStr.substring(0, 10);
                    LocalDate logDate = LocalDate.parse(datePart);
                    if (startDatePicker.getValue() != null && logDate.isBefore(startDatePicker.getValue())) {
                        return false;
                    }
                    if (endDatePicker.getValue() != null && logDate.isAfter(endDatePicker.getValue())) {
                        return false;
                    }
                } catch (Exception e) {
                    // skip invalid format
                }
            } else {
                if (startDatePicker.getValue() != null || endDatePicker.getValue() != null) {
                    return false;
                }
            }

            return true;
        });
    }

    @FXML
    public void handleResetFilters() {
        usernameFilterField.clear();
        actionTypeBox.setValue("ALL");
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }
}
