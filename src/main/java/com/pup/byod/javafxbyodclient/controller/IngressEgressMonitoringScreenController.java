package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Device;
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
        // Initialize Device Columns
        colSelect.setCellValueFactory(f -> f.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colDeviceName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("campusStatus"));

        deviceTable.setItems(deviceList);
        deviceTable.setEditable(true);

        deviceTable.setRowFactory(tv -> new TableRow<DeviceSelection>() {
            @Override
            protected void updateItem(DeviceSelection item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                    getStyleClass().removeAll("warning-row", "overstay-row");
                } else {
                    Device d = item.getDevice();
                    boolean isFlagged = !"approved".equalsIgnoreCase(d.getRegistrationStatus()) 
                                        || "inactive".equalsIgnoreCase(d.getDeviceStatus());
                    if (isFlagged) {
                        setStyle("-fx-background-color: #FEE2E2;"); // light red/rose background
                        getStyleClass().remove("overstay-row");
                        if (!getStyleClass().contains("warning-row")) {
                            getStyleClass().add("warning-row");
                        }
                    } else if (item.isOverstay()) {
                        setStyle("-fx-background-color: #FEF3C7;"); // light amber/orange background
                        getStyleClass().remove("warning-row");
                        if (!getStyleClass().contains("overstay-row")) {
                            getStyleClass().add("overstay-row");
                        }
                    } else {
                        setStyle("");
                        getStyleClass().removeAll("warning-row", "overstay-row");
                    }
                }
            }
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

            deviceList.clear();
            for (Device d : devices) {
                DeviceCampusStatus cs = statusMap.get(d.getSerialNumber());
                String campusStatus = cs != null ? cs.getCampusStatus() : "exit";
                String lastTime = cs != null ? cs.getLastEventTime() : null;

                DeviceSelection selection = new DeviceSelection(d, campusStatus, lastTime);
                
                // Deselect automatically if inactive or not approved
                if ("inactive".equalsIgnoreCase(d.getDeviceStatus()) || !"approved".equalsIgnoreCase(d.getRegistrationStatus())) {
                    selection.setSelected(false);
                }
                
                deviceList.add(selection);
            }
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
            // Log exit/egress for all devices that are currently inside (entry/inside status)
            for (DeviceSelection selection : deviceList) {
                if ("entry".equalsIgnoreCase(selection.getCampusStatus()) || "inside".equalsIgnoreCase(selection.getCampusStatus())) {
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
