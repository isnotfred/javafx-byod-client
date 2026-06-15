package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Device;
import com.pup.byod.javafxbyodclient.model.PendingDevice;
import com.pup.byod.javafxbyodclient.model.DeviceCampusStatus;
import com.pup.byod.javafxbyodclient.model.DeviceLog;
import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import com.pup.byod.javafxbyodclient.service.LogService;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import com.pup.byod.javafxbyodclient.util.CsvExportHelper;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngressEgressMonitoringScreenController {
    @FXML private TextField studentIdField;
    @FXML private VBox studentCard;
    @FXML private Label studentNameLabel;
    @FXML private Label studentCourseLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<DeviceSelection> deviceTable;
    @FXML private TableColumn<DeviceSelection, Boolean> colSelect;
    @FXML private TableColumn<DeviceSelection, String> colDeviceName;
    @FXML private TableColumn<DeviceSelection, String> colBrand;
    @FXML private TableColumn<DeviceSelection, String> colModel;
    @FXML private TableColumn<DeviceSelection, String> colSerialNumber;
    @FXML private TableColumn<DeviceSelection, String> colStatus;

    @FXML private TextArea notesArea;
    @FXML private Button btnLogIngress;
    @FXML private Button btnLogEgress;

    @FXML private TableView<DeviceLog> logsTable;
    @FXML private TableColumn<DeviceLog, Integer> colLogId;
    @FXML private TableColumn<DeviceLog, String> colLogType;
    @FXML private TableColumn<DeviceLog, String> colLogTime;
    @FXML private TableColumn<DeviceLog, Integer> colLogHandler;
    @FXML private TableColumn<DeviceLog, String> colLogNotes;

    private final StudentService studentService = new StudentService();
    private final DeviceService deviceService = new DeviceService();
    private final LogService logService = new LogService();

    private final ObservableList<DeviceSelection> deviceList = FXCollections.observableArrayList();
    private final ObservableList<DeviceLog> logsList = FXCollections.observableArrayList();

    private Student currentStudent = null;

    @FXML
    public void initialize() {
        // Setup Student ID Autocomplete & Prompt text behavior
        com.pup.byod.javafxbyodclient.util.StudentSearchDropdown.attach(studentIdField, s -> handleStudentSearch());
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(studentIdField);
        
        studentIdField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleStudentSearch();
            }
        });


        // Initialize Device Columns
        // Prevent native JavaFX row selection to avoid conflicting with our custom highlight colors
        deviceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Platform.runLater(() -> deviceTable.getSelectionModel().clearSelection());
            }
        });

        colSelect.setCellValueFactory(f -> f.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colDeviceName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("campusStatus"));

        deviceTable.setItems(deviceList);
        deviceTable.setEditable(true);

        deviceTable.setRowFactory(tv -> {
            TableRow<DeviceSelection> row = new TableRow<DeviceSelection>() {
                @Override
                protected void updateItem(DeviceSelection item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                        getStyleClass().removeAll("warning-row", "overstay-row", "pending-row", "selected-row");
                    } else {
                        Device d = item.getDevice();
                        boolean isPending = "pending".equalsIgnoreCase(d.getRegistrationStatus());
                        boolean isInactive = "inactive".equalsIgnoreCase(d.getDeviceStatus());
                        boolean isRejected = "rejected".equalsIgnoreCase(d.getRegistrationStatus());
                        
                        getStyleClass().removeAll("warning-row", "overstay-row", "pending-row", "selected-row");
                        
                        if (isInactive || isRejected) {
                            setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: black;");
                            getStyleClass().add("warning-row");
                        } else {
                            if (item.isSelected()) {
                                if (isPending) {
                                    setStyle("-fx-background-color: #FFF9C4; -fx-text-fill: black;"); // Lighter yellow for pending
                                    getStyleClass().add("pending-row");
                                } else {
                                    setStyle("-fx-background-color: #E1F5FE; -fx-text-fill: black;"); // User's requested light pastel blue
                                    getStyleClass().add("selected-row");
                                }
                            } else if (item.isOverstay()) {
                                setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: black;");
                                getStyleClass().add("overstay-row");
                            } else {
                                setStyle("");
                            }
                        }
                    }
                }
            };
            
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    DeviceSelection item = row.getItem();
                    Device d = item.getDevice();
                    if (!"inactive".equalsIgnoreCase(d.getDeviceStatus()) && !"rejected".equalsIgnoreCase(d.getRegistrationStatus())) {
                        item.setSelected(!item.isSelected());
                        deviceTable.refresh();
                        updateActionButtonsVisibility();
                    }
                }
            });

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    newItem.selectedProperty().addListener((o, oldVal, newVal) -> {
                        deviceTable.refresh();
                        updateActionButtonsVisibility();
                    });
                }
            });
            return row;
        });

        // Initialize Log Columns
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colLogType.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        colLogTime.setCellValueFactory(new PropertyValueFactory<>("eventTime"));
        colLogHandler.setCellValueFactory(new PropertyValueFactory<>("handledBy"));
        colLogNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        logsTable.setItems(logsList);

        // Reset view
        resetView();
    }

    private void resetView() {
        currentStudent = null;
        studentNameLabel.setText("Student: No Selection");
        studentCourseLabel.setText("Course & Year: -");
        statusLabel.setText("STATUS: READY TO LOOKUP");
        statusLabel.setStyle("-fx-text-fill: #64748B; -fx-font-weight: bold;");
        if (studentCard != null) {
            studentCard.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 16px;");
        }
        deviceList.clear();
        logsList.clear();
        notesArea.clear();
    }

    @FXML
    public void handleStudentSearch() {
        String studentId = studentIdField.getText();
        if (ValidationHelper.isEmpty(studentId)) {
            AlertHelper.showWarning("Search student", "Input Required", "Please enter a Student ID.");
            return;
        }

        try {
            List<Student> results = studentService.searchStudents(studentId);
            Student found = null;
            for (Student s : results) {
                if (s.getStudentId().equalsIgnoreCase(studentId)) {
                    found = s;
                    break;
                }
            }

            if (found == null) {
                resetView();
                statusLabel.setText("STATUS: STUDENT NOT FOUND");
                AlertHelper.showWarning("Search Result", "Student Not Found", "No registered student found with ID: " + studentId);
                return;
            }

            currentStudent = found;
            studentNameLabel.setText("Student: " + found.getFullName());
            studentCourseLabel.setText("Course & Year: " + found.getCourseYearLevel());
            
            if ("inactive".equalsIgnoreCase(found.getStatus())) {
                statusLabel.setText("STATUS: STUDENT INACTIVE / DEACTIVATED!");
                statusLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                studentCard.setStyle("-fx-background-color: #FEF2F2; -fx-border-color: #F87171; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 16px;");
            } else {
                statusLabel.setText("STATUS: STUDENT FOUND");
                statusLabel.setStyle("-fx-text-fill: #64748B; -fx-font-weight: bold;");
                studentCard.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 16px;");
            }

            // Load student's devices and their campus status
            loadStudentDevicesAndStatus(found.getStudentId());

            // Load student's gate log history
            loadStudentLogHistory(found.getStudentId());

        } catch (Exception e) {
            resetView();
            statusLabel.setText("STATUS: LOOKUP ERROR");
            AlertHelper.showError("Search Error", "Lookup Failed", e.getMessage());
        }
    }

    private void loadStudentDevicesAndStatus(String studentId) {
        try {
            List<Device> devices = deviceService.getDevicesByStudentId(studentId);
            List<DeviceCampusStatus> statuses = deviceService.getDeviceCampusStatus();
            
            Map<String, DeviceCampusStatus> statusMap = new HashMap<>();
            for (DeviceCampusStatus s : statuses) {
                if (s.getStudentId().equalsIgnoreCase(studentId)) {
                    statusMap.put(s.getSerialNumber(), s);
                }
            }

            // Check if any device is currently inside campus
            boolean hasAnyDeviceInside = false;
            for (Device d : devices) {
                DeviceCampusStatus cs = statusMap.get(d.getSerialNumber());
                String campusStatus = cs != null ? cs.getCampusStatus() : "exit";
                if ("entry".equalsIgnoreCase(campusStatus) || "inside".equalsIgnoreCase(campusStatus)) {
                    hasAnyDeviceInside = true;
                    break;
                }
            }

            deviceList.clear();
            for (Device d : devices) {
                DeviceCampusStatus cs = statusMap.get(d.getSerialNumber());
                String campusStatus = cs != null ? cs.getCampusStatus() : "exit";
                String lastTime = cs != null ? cs.getLastEventTime() : null;

                boolean isInside = "entry".equalsIgnoreCase(campusStatus) || "inside".equalsIgnoreCase(campusStatus);

                if (hasAnyDeviceInside) {
                    // Egress flow: only show and pre-check devices that are currently inside
                    if (isInside) {
                        DeviceSelection selection = new DeviceSelection(d, campusStatus, lastTime);
                        selection.setSelected(true);
                        deviceList.add(selection);
                    }
                } else {
                    // Ingress flow: show all devices
                    DeviceSelection selection = new DeviceSelection(d, campusStatus, lastTime);
                    if ("inactive".equalsIgnoreCase(d.getDeviceStatus()) || "rejected".equalsIgnoreCase(d.getRegistrationStatus())) {
                        selection.setSelected(false);
                    } else {
                        selection.setSelected(true);
                    }
                    deviceList.add(selection);
                }
            }
            
            updateActionButtonsVisibility();
        } catch (Exception e) {
            System.err.println("Error loading devices/statuses: " + e.getMessage());
        }
    }

    private void loadStudentLogHistory(String studentId) {
        try {
            List<DeviceLog> logs = logService.getStudentLogs(studentId);
            logsList.setAll(logs);
        } catch (Exception e) {
            System.err.println("Error loading student logs: " + e.getMessage());
        }
    }

    @FXML
    public void handleEntry() {
        if (currentStudent == null) {
            AlertHelper.showWarning("Gate Scan", "No Student Selected", "Please lookup a student first.");
            return;
        }

        String notes = notesArea.getText();
        int loggedCount = 0;
        int guardId = SessionManager.getInstance().getCurrentUser().getUserId();

        try {
            for (DeviceSelection selection : deviceList) {
                if (selection.isSelected()) {
                    logService.logEntry(selection.getSerialNumber(), guardId, notes);
                    loggedCount++;
                }
            }

            if (loggedCount > 0) {
                statusLabel.setText("STATUS: " + loggedCount + " DEVICE(S) ENTRY LOGGED");
                AlertHelper.showInfo("Gate Scan Success", "Entry Logged", loggedCount + " devices checked in successfully.");
            } else {
                AlertHelper.showWarning("Gate Scan", "No Devices Selected", "No devices were ticked to log entry.");
            }

            // Refresh logs and status
            loadStudentDevicesAndStatus(currentStudent.getStudentId());
            loadStudentLogHistory(currentStudent.getStudentId());
            notesArea.clear();
        } catch (Exception e) {
            statusLabel.setText("STATUS: ENTRY BLOCKED!");
            AlertHelper.showError("Scan Error", "Ingress Blocked", e.getMessage());
            // Refresh in case of partial success
            loadStudentDevicesAndStatus(currentStudent.getStudentId());
            loadStudentLogHistory(currentStudent.getStudentId());
        }
    }

    @FXML
    public void handleExit() {
        if (currentStudent == null) {
            AlertHelper.showWarning("Gate Scan", "No Student Selected", "Please lookup a student first.");
            return;
        }

        String notes = notesArea.getText();
        int loggedCount = 0;
        int guardId = SessionManager.getInstance().getCurrentUser().getUserId();

        try {
            // Log exit/egress for all devices that are currently inside (entry/inside status) and are checked
            for (DeviceSelection selection : deviceList) {
                if (selection.isSelected() && ("entry".equalsIgnoreCase(selection.getCampusStatus()) || "inside".equalsIgnoreCase(selection.getCampusStatus()))) {
                    logService.logExit(selection.getSerialNumber(), guardId, notes);
                    loggedCount++;
                }
            }

            if (loggedCount > 0) {
                statusLabel.setText("STATUS: " + loggedCount + " DEVICE(S) EXIT LOGGED");
                AlertHelper.showInfo("Gate Scan Success", "Exit Logged", loggedCount + " devices checked out successfully.");
            } else {
                AlertHelper.showInfo("Gate Scan", "No Devices Logged", "No devices of this student were logged as inside the campus.");
            }

            // Refresh logs and status
            loadStudentDevicesAndStatus(currentStudent.getStudentId());
            loadStudentLogHistory(currentStudent.getStudentId());
            notesArea.clear();
        } catch (Exception e) {
            statusLabel.setText("STATUS: EXIT BLOCKED!");
            AlertHelper.showError("Scan Error", "Egress Failed", e.getMessage());
            loadStudentDevicesAndStatus(currentStudent.getStudentId());
            loadStudentLogHistory(currentStudent.getStudentId());
        }
    }

    @FXML
    public void handleExportLogs() {
        if (logsTable.getItems().isEmpty()) {
            AlertHelper.showWarning("Export Warning", "No Data", "There is no scan log history to export.");
            return;
        }
        javafx.stage.Window window = logsTable.getScene().getWindow();
        String defaultName = currentStudent != null ? "gate_logs_" + currentStudent.getStudentId() + ".csv" : "gate_logs.csv";
        CsvExportHelper.exportToCsv(logsTable, window, defaultName);
    }

    private void updateActionButtonsVisibility() {
        if (btnLogIngress == null || btnLogEgress == null) return;
        
        boolean hasExit = false;
        boolean hasEntry = false;
        
        for (DeviceSelection item : deviceList) {
            if (item.isSelected()) {
                String status = item.getCampusStatus();
                if ("pending".equalsIgnoreCase(item.getDevice().getRegistrationStatus())) {
                    hasExit = true; 
                } else if ("exit".equalsIgnoreCase(status)) {
                    hasExit = true;
                } else if ("entry".equalsIgnoreCase(status) || "inside".equalsIgnoreCase(status)) {
                    hasEntry = true;
                }
            }
        }
        
        if (hasExit && !hasEntry) {
            btnLogIngress.setDisable(false);
            btnLogEgress.setDisable(true);
        } else if (hasEntry && !hasExit) {
            btnLogIngress.setDisable(true);
            btnLogEgress.setDisable(false);
        } else {
            btnLogIngress.setDisable(true);
            btnLogEgress.setDisable(true);
        }
    }

    // Helper Wrapper class for Device selection with Checkboxes
    public static class DeviceSelection {
        private final BooleanProperty selected = new SimpleBooleanProperty(true);
        private final Device device;
        private final String campusStatus;
        private final String lastEventTime;

        public DeviceSelection(Device device, String campusStatus, String lastEventTime) {
            this.device = device;
            this.campusStatus = campusStatus;
            this.lastEventTime = lastEventTime;
        }

        public BooleanProperty selectedProperty() { return selected; }
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean val) { selected.set(val); }

        public Device getDevice() { return device; }

        public int getDeviceId() { return device.getDeviceId(); }
        public String getDeviceName() { return device.getDeviceName(); }
        public String getBrand() { return device.getBrand(); }
        public String getModel() { return device.getModel(); }
        public String getSerialNumber() { return device.getSerialNumber(); }
        public String getCampusStatus() { return campusStatus; }
        public String getLastEventTime() { return lastEventTime; }

        public boolean isOverstay() {
            if (!"entry".equalsIgnoreCase(campusStatus) && !"inside".equalsIgnoreCase(campusStatus)) {
                return false;
            }
            if (lastEventTime == null || lastEventTime.trim().isEmpty()) {
                return false;
            }
            try {
                java.time.Instant lastInstant;
                if (lastEventTime.contains("Z") || lastEventTime.contains("+") || (lastEventTime.lastIndexOf("-") > 10)) {
                    lastInstant = java.time.OffsetDateTime.parse(lastEventTime).toInstant();
                } else {
                    lastInstant = java.time.LocalDateTime.parse(lastEventTime).atZone(java.time.ZoneId.systemDefault()).toInstant();
                }
                long hours = java.time.Duration.between(lastInstant, java.time.Instant.now()).toHours();
                return hours >= 18;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
