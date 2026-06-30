package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.service.ReportService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.CsvExportHelper;
import com.pup.byod.javafxbyodclient.util.PromptTextHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.*;

public class OnCampusDevicesScreenController {
    @FXML private TableView<Map<String, Object>> statusTable;
    @FXML private TableColumn<Map<String, Object>, Object> colStudentId;
    @FXML private TableColumn<Map<String, Object>, Object> colStudentName;
    @FXML private TableColumn<Map<String, Object>, Object> colCourseYearLevel;
    @FXML private TableColumn<Map<String, Object>, Object> colDeviceName;
    @FXML private TableColumn<Map<String, Object>, Object> colExpectedExitTime;
    @FXML private TableColumn<Map<String, Object>, Object> colCheckedInTime;
    @FXML private TableColumn<Map<String, Object>, Void> colDetails;

    @FXML private TextField searchField;

    private final ReportService reportService = new ReportService();
    private final ObservableList<Map<String, Object>> statusList = FXCollections.observableArrayList();
    private FilteredList<Map<String, Object>> filteredList;

    @FXML
    public void initialize() {
        // Setup columns dynamically based on keys
        setupColumn(colStudentId, "studentId", "student_id");
        setupColumn(colStudentName, "studentName", "student_name");
        setupColumn(colCourseYearLevel, "courseYearLevel", "course_year_level");
        setupColumn(colDeviceName, "deviceName", "device_name");
        if (colExpectedExitTime != null) {
            colExpectedExitTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimeOnly(
                    getValueFromMapRaw(cellData.getValue(), "expectedExitTime", "expected_exit_time", "expectedEgressTime", "expected_egress_time")
                )
            ));
        }
        if (colCheckedInTime != null) {
            colCheckedInTime.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimeOnly(
                    getValueFromMapRaw(cellData.getValue(), "enteredAt", "entered_at", "lastEventTime", "last_event_time")
                )
            ));
        }

        // Action Details column - View Button Cell Factory
        if (colDetails != null) {
            colDetails.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("View");

                {
                    btn.getStyleClass().addAll("action-btn", "action-btn-primary");
                    btn.setStyle("-fx-font-size: 11px; -fx-padding: 4px 8px; -fx-cursor: hand;");
                    btn.setOnAction(event -> {
                        Map<String, Object> device = getTableView().getItems().get(getIndex());
                        showDeviceDetails(device);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }
                }
            });
        }

        filteredList = new FilteredList<>(statusList, p -> true);
        SortedList<Map<String, Object>> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(statusTable.comparatorProperty());
        statusTable.setItems(sortedData);

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));
            PromptTextHelper.setup(searchField);
        }

        loadStatus();
    }

    private void setupColumn(TableColumn<Map<String, Object>, Object> column, String... keys) {
        if (column != null) {
            column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), keys)
            ));
        }
    }

    private void showDeviceDetails(Map<String, Object> device) {
        javafx.scene.text.TextFlow textFlow = new javafx.scene.text.TextFlow();
        textFlow.setPrefWidth(380);
        
        appendBoldField(textFlow, "Student Name: ", getValueString(device, "studentName", "student_name"));
        appendBoldField(textFlow, "Student ID: ", getValueString(device, "studentId", "student_id"));
        appendBoldField(textFlow, "Course/Year: ", getValueString(device, "courseYearLevel", "course_year_level"));
        textFlow.getChildren().add(new javafx.scene.text.Text("\n"));
        
        appendBoldField(textFlow, "Device Name: ", getValueString(device, "deviceName", "device_name"));
        appendBoldField(textFlow, "Brand: ", getValueString(device, "brand"));
        appendBoldField(textFlow, "Model: ", getValueString(device, "model"));
        appendBoldField(textFlow, "Device Type: ", getValueString(device, "deviceType", "device_type"));
        appendBoldField(textFlow, "Serial Number: ", getValueString(device, "serialNumber", "serial_number"));
        
        AlertHelper.showInfo("Device Specifications", "Specifications for " + getValueString(device, "deviceName", "Device"), textFlow);
    }

    private void appendBoldField(javafx.scene.text.TextFlow flow, String label, String value) {
        javafx.scene.text.Text boldText = new javafx.scene.text.Text(label);
        boldText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-fill: #1E293B;"); // Using standard slate-800 color to match the text
        
        javafx.scene.text.Text normalText = new javafx.scene.text.Text(value + "\n");
        normalText.setStyle("-fx-font-size: 14px; -fx-fill: #475569;"); // Slate-600
        
        flow.getChildren().addAll(boldText, normalText);
    }

    private Object getValueFromMap(Map<String, Object> map, String... keys) {
        if (map == null) return "";
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                Object val = map.get(key);
                if (val instanceof String && isTimestampKey(key)) {
                    return com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimestamp((String) val);
                }
                return val;
            }
        }
        for (String mapKey : map.keySet()) {
            for (String key : keys) {
                if (mapKey.equalsIgnoreCase(key) && map.get(mapKey) != null) {
                    Object val = map.get(mapKey);
                    if (val instanceof String && isTimestampKey(key)) {
                        return com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimestamp((String) val);
                    }
                    return val;
                }
            }
        }
        return "";
    }

    private String getValueString(Map<String, Object> map, String... keys) {
        if (map == null) return "-";
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                return map.get(key).toString();
            }
        }
        for (String mapKey : map.keySet()) {
            for (String key : keys) {
                if (mapKey.equalsIgnoreCase(key) && map.get(mapKey) != null) {
                    return map.get(mapKey).toString();
                }
            }
        }
        return "-";
    }

    private boolean isTimestampKey(String key) {
        if (key == null) return false;
        String lower = key.toLowerCase();
        return lower.contains("time") || lower.contains("at") || lower.contains("seen") || lower.equals("timestamp");
    }

    private void applyFilter(String query) {
        if (filteredList == null) return;
        filteredList.setPredicate(map -> {
            if (query == null || query.trim().isEmpty()) {
                return true;
            }
            String lowerCaseFilter = query.toLowerCase().trim();

            String studentId = getValueString(map, "studentId", "student_id");
            String studentName = getValueString(map, "studentName", "student_name");
            String serialNumber = getValueString(map, "serialNumber", "serial_number");

            return studentId.toLowerCase().contains(lowerCaseFilter) ||
                   studentName.toLowerCase().contains(lowerCaseFilter) ||
                   serialNumber.toLowerCase().contains(lowerCaseFilter);
        });
    }

    @FXML
    public void loadStatus() {
        try {
            List<Map<String, Object>> activeDevices = reportService.getActiveDevicesReport();
            statusList.setAll(activeDevices);
        } catch (Exception e) {
            System.err.println("Could not load on-campus device status: " + e.getMessage());
            AlertHelper.showError("Loading Error", "Could not load campus device status", e.getMessage());
        }
    }

    @FXML
    public void handleExportPresence() {
        if (statusTable.getItems().isEmpty()) {
            AlertHelper.showWarning("Export Warning", "No Data", "There is no active presence data to export.");
            return;
        }
        javafx.stage.Window window = statusTable.getScene().getWindow();
        CsvExportHelper.exportToCsv(statusTable, window, "on_campus_devices.csv");
    }

    private String getValueFromMapRaw(Map<String, Object> map, String... keys) {
        if (map == null) return "";
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                return map.get(key).toString();
            }
        }
        for (String mapKey : map.keySet()) {
            for (String key : keys) {
                if (mapKey.equalsIgnoreCase(key) && map.get(mapKey) != null) {
                    return map.get(mapKey).toString();
                }
            }
        }
        return "";
    }
}
