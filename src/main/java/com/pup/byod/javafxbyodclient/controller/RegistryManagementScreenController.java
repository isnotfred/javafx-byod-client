package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Device;
import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import com.pup.byod.javafxbyodclient.util.CsvExportHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.pup.byod.javafxbyodclient.session.SessionManager;

public class RegistryManagementScreenController {
    // Student directory controls
    @FXML private TextField searchField;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colStudentName;
    @FXML private TableColumn<Student, String> colStudentCourse;
    @FXML private TableColumn<Student, String> colStudentStatus;
    @FXML private Button editSelectedBtn;
    @FXML private Button clearFormBtn;
    @FXML private Button deactivateRecordBtn;
    @FXML private Label studentIdLabel;

    // Overlay control
    @FXML private StackPane formOverlay;

    // Form inputs
    @FXML private TextField studentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField courseYearLevelField;

    // Device controls
    @FXML private TableView<Device> deviceTable;
    @FXML private TableColumn<Device, Integer> colDeviceId;
    @FXML private TableColumn<Device, String> colDeviceName;
    @FXML private TableColumn<Device, String> colDeviceType;
    @FXML private TableColumn<Device, String> colDeviceBrand;
    @FXML private TableColumn<Device, String> colDeviceSerialNumber;
    @FXML private TableColumn<Device, String> colDeviceRegStatus;
    @FXML private TableColumn<Device, String> colDeviceStatus;

    @FXML private TextField deviceNameField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField serialNumberField;
    @FXML private ComboBox<String> deviceTypeBox;
    @FXML private ComboBox<String> devicePurposeBox;
    @FXML private ComboBox<String> deviceStatusBox;
    @FXML private Button stageDeviceBtn;
    @FXML private Label serialNumberLabel;

    private final StudentService studentService = new StudentService();
    private final DeviceService deviceService = new DeviceService();

    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final ObservableList<Device> deviceList = FXCollections.observableArrayList();

    private boolean isStudentEditMode = false;
    private boolean isDeviceEditMode = false;

    @FXML
    public void initialize() {
        // Initialize Student Columns
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colStudentCourse.setCellValueFactory(new PropertyValueFactory<>("courseYearLevel"));
        colStudentStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        studentTable.setItems(studentList);

        // Initialize Device Columns
        colDeviceId.setCellValueFactory(new PropertyValueFactory<>("deviceId"));
        colDeviceName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colDeviceType.setCellValueFactory(new PropertyValueFactory<>("deviceType"));
        colDeviceBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colDeviceSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colDeviceRegStatus.setCellValueFactory(new PropertyValueFactory<>("registrationStatus"));
        colDeviceStatus.setCellValueFactory(new PropertyValueFactory<>("deviceStatus"));

        deviceTable.setItems(deviceList);

        // Populate Choice Boxes
        deviceStatusBox.getItems().addAll("active", "inactive");

        deviceTypeBox.getItems().addAll(
                "Personal Computers",
                "Components & Peripherals",
                "Display & Projection",
                "Project Prototypes (Optional SN)",
                "Appliances (TLE)"
        );

        devicePurposeBox.getItems().addAll(
                "Academic BYOD",
                "School Event",
                "Organization Activity",
                "Temporary Equipment",
                "Other Approved Purpose",
                "PROTOTYPE",
                "APPLIANCE"
        );

        // Setup UI Validation
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(studentIdField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(firstNameField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(lastNameField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(courseYearLevelField, "Input required");

        // Listeners for selection & double click
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editSelectedBtn.setDisable(newVal == null);
        });

        studentTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && studentTable.getSelectionModel().getSelectedItem() != null) {
                openEditOverlay();
            }
        });

        deviceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            onDeviceSelected(newVal);
        });

        // Red row styling for inactive students
        studentTable.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<Student>() {
                @Override
                protected void updateItem(Student item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        getStyleClass().removeAll("inactive-row");
                    } else if ("inactive".equalsIgnoreCase(item.getStatus())) {
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

        // Enter key triggers search
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleSearch();
            }
        });

        // Search dynamically as the user types
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            handleSearch();
        });

        // Setup PromptTextHelpers
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(searchField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(studentIdField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(firstNameField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(lastNameField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(courseYearLevelField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(deviceNameField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(brandField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(modelField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(serialNumberField);

        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(studentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(firstNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(lastNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(courseYearLevelField);
        
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(deviceNameField);
        serialNumberField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String type = deviceTypeBox.getValue();
                if (type == null || !type.equals("Project Prototypes (Optional SN)")) {
                    com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(serialNumberField, "Input needed");
                } else {
                    com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(serialNumberField);
                }
            }
        });
        serialNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!com.pup.byod.javafxbyodclient.util.ValidationHelper.isEmpty(newVal)) {
                com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(serialNumberField);
            }
        });

        deviceTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Project Prototypes (Optional SN)".equals(newVal)) {
                serialNumberField.setPromptText("Optional Serial Number");
                com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(serialNumberField);
                if (serialNumberLabel != null) {
                    serialNumberLabel.setGraphic(null);
                }
            } else {
                serialNumberField.setPromptText("e.g. SN-DELL-12345");
                if (serialNumberLabel != null) {
                    javafx.scene.control.Label ast = new javafx.scene.control.Label("*");
                    ast.setStyle("-fx-text-fill: red;");
                    serialNumberLabel.setGraphic(ast);
                }
            }
        });
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(deviceTypeBox);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(devicePurposeBox);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(deviceStatusBox);

        // Load students
        loadStudents();
    }

    private void loadStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            studentList.setAll(students);
        } catch (Exception e) {
            System.err.println("Could not load students: " + e.getMessage());
            AlertHelper.showError("Error", "Load Failed", "Could not fetch students from backend: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        if (ValidationHelper.isEmpty(keyword)) {
            loadStudents();
            return;
        }
        try {
            List<Student> students = studentService.searchStudents(keyword);
            studentList.setAll(students);
        } catch (Exception e) {
            AlertHelper.showError("Search failed", "Error", e.getMessage());
        }
    }

    // --- Overlay opening and closing triggers ---

    @FXML
    public void openAddOverlay() {
        handleClearForm(); // Clears all inputs and selections
        isStudentEditMode = false;
        studentIdField.setDisable(false);
        studentTable.getSelectionModel().clearSelection();
        deviceList.clear();

        // Hide deactivate button in add mode, show clear form
        if (deactivateRecordBtn != null) {
            deactivateRecordBtn.setVisible(false);
            deactivateRecordBtn.setManaged(false);
            deactivateRecordBtn.setText("Deactivate Record");
            deactivateRecordBtn.getStyleClass().removeAll("action-btn-success");
            if (!deactivateRecordBtn.getStyleClass().contains("action-btn-danger")) {
                deactivateRecordBtn.getStyleClass().add("action-btn-danger");
            }
        }
        if (clearFormBtn != null) {
            clearFormBtn.setVisible(true);
            clearFormBtn.setManaged(true);
        }

        formOverlay.setVisible(true);
    }

    @FXML
    public void openEditOverlay() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Edit Profile", "No Selection", "Please select a student record to edit.");
            return;
        }
        isStudentEditMode = true;
        studentIdField.setText(selected.getStudentId());
        studentIdField.setDisable(true); // Student ID is immutable on edit
        if (studentIdLabel != null) {
            studentIdLabel.setGraphic(null);
        }
        firstNameField.setText(selected.getFirstName());
        lastNameField.setText(selected.getLastName());
        courseYearLevelField.setText(selected.getCourseYearLevel());

        // Clear device forms, then load devices for student
        handleClearDevice();
        deviceList.clear();
        loadDevicesForStudent(selected.getStudentId());

        // Show deactivate/reactivate button in edit mode, hide clear form
        if (deactivateRecordBtn != null) {
            boolean isInactive = "inactive".equalsIgnoreCase(selected.getStatus());
            deactivateRecordBtn.setVisible(true);
            deactivateRecordBtn.setManaged(true);
            if (isInactive) {
                deactivateRecordBtn.setText("Reactivate Student");
                deactivateRecordBtn.getStyleClass().removeAll("action-btn-danger");
                if (!deactivateRecordBtn.getStyleClass().contains("action-btn-success")) {
                    deactivateRecordBtn.getStyleClass().add("action-btn-success");
                }
            } else {
                deactivateRecordBtn.setText("Deactivate Record");
                deactivateRecordBtn.getStyleClass().removeAll("action-btn-success");
                if (!deactivateRecordBtn.getStyleClass().contains("action-btn-danger")) {
                    deactivateRecordBtn.getStyleClass().add("action-btn-danger");
                }
            }
        }
        if (clearFormBtn != null) {
            clearFormBtn.setVisible(false);
            clearFormBtn.setManaged(false);
        }

        formOverlay.setVisible(true);
    }

    @FXML
    public void handleCloseOverlay() {
        formOverlay.setVisible(false);
        handleClearForm();
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(studentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(firstNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(lastNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(courseYearLevelField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(deviceNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(serialNumberField);
    }

    @FXML
    private void handleCancelBtn() {
        formOverlay.setVisible(false);
        isStudentEditMode = false;
        
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(studentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(firstNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(lastNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(courseYearLevelField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(deviceNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(serialNumberField);
    }

    private void loadDevicesForStudent(String studentId) {
        new Thread(() -> {
            try {
                List<Device> devices = deviceService.getDevicesByStudentId(studentId);
                Platform.runLater(() -> deviceList.setAll(devices));
            } catch (Exception e) {
                System.err.println("Could not load devices for student: " + e.getMessage());
            }
        }).start();
    }

    private void selectStudentById(String studentId) {
        for (Student s : studentList) {
            if (s.getStudentId().equals(studentId)) {
                studentTable.getSelectionModel().select(s);
                break;
            }
        }
    }

    // Removed old handleImportCsv

    @FXML
    public void handleImportDevicesCsv() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Import Unified Registry CSV");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv")
        );
        
        javafx.stage.Window window = studentTable.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);
        
        if (selectedFile == null) {
            return;
        }

        // Validate CSV headers first
        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            String headerLine = br.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                AlertHelper.showError("CSV Validation Failed", "Empty File", "The selected CSV file is empty.");
                return;
            }
            
            String[] headers = headerLine.split(",");
            List<String> headersList = new ArrayList<>();
            for (String h : headers) {
                headersList.add(h.trim().toLowerCase());
            }
            
            List<String> requiredHeaders = List.of("student_id", "first_name", "last_name", "course_year_level", "device_name", "serial_number", "device_type", "device_purpose");
            List<String> missingHeaders = new ArrayList<>();
            for (String req : requiredHeaders) {
                if (!headersList.contains(req)) {
                    missingHeaders.add(req);
                }
            }
            
            if (!missingHeaders.isEmpty()) {
                AlertHelper.showError(
                    "CSV Validation Failed",
                    "Missing Headers",
                    "The selected CSV file is missing required headers: " + String.join(", ", missingHeaders) +
                    "\n\nExpected headers: student_id, first_name, last_name, course_year_level, device_name, serial_number, device_type, device_purpose"
                );
                return;
            }
        } catch (Exception e) {
            AlertHelper.showError("CSV Validation Failed", "Error reading file", e.getMessage());
            return;
        }

        if (!AlertHelper.showConfirmation("Import CSV", "Confirm Import", 
                "Are you sure you want to import student and device records from the selected CSV file?")) {
            return;
        }

        int currentAdminId = SessionManager.getInstance().getCurrentUser().getUserId();

        new Thread(() -> {
            int inserted = 0;
            int errorsCount = 0;
            List<String> errorsList = new ArrayList<>();
            
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                String headerLine = br.readLine();
                String[] headers = headerLine.split(",");
                int sIdx=-1, fIdx=-1, lIdx=-1, cIdx=-1, dnIdx=-1, snIdx=-1, dtIdx=-1, dpIdx=-1;
                for(int i=0; i<headers.length; i++){
                    String h = headers[i].trim().toLowerCase();
                    if(h.equals("student_id")) sIdx = i;
                    if(h.equals("first_name")) fIdx = i;
                    if(h.equals("last_name")) lIdx = i;
                    if(h.equals("course_year_level")) cIdx = i;
                    if(h.equals("device_name")) dnIdx = i;
                    if(h.equals("serial_number")) snIdx = i;
                    if(h.equals("device_type")) dtIdx = i;
                    if(h.equals("device_purpose")) dpIdx = i;
                }
                
                String line;
                int rowNum = 1;
                while ((line = br.readLine()) != null) {
                    rowNum++;
                    String[] cols = line.split(",", -1);
                    if(cols.length < headers.length) continue;
                    
                    String sId = cols[sIdx].trim();
                    String fName = cols[fIdx].trim();
                    String lName = cols[lIdx].trim();
                    String cYear = cols[cIdx].trim();
                    
                    String dName = cols[dnIdx].trim();
                    String sNum = cols[snIdx].trim();
                    String dType = cols[dtIdx].trim();
                    String dPurp = cols[dpIdx].trim();
                    
                    if(sId.isEmpty()) continue;
                    
                    try {
                        // Create student silently if not exists
                        Student s = new Student();
                        s.setStudentId(sId);
                        s.setFirstName(fName);
                        s.setLastName(lName);
                        s.setCourseYearLevel(cYear);
                        s.setStatus("active");
                        try {
                            studentService.createStudent(s);
                        } catch(Exception ignored) { }
                        
                        boolean isSNRequired = !"Project Prototypes (Optional SN)".equals(dType);
                        if ((!isSNRequired || !sNum.isEmpty()) && !dName.isEmpty()) {
                            Device d = new Device();
                            d.setStudentId(sId);
                            d.setDeviceName(dName);
                            d.setSerialNumber(sNum);
                            d.setDeviceType(dType);
                            d.setDevicePurpose(dPurp);
                            d.setDeviceStatus("active");
                            
                            Device registered = deviceService.registerDevice(d);
                            // Auto-approve instantly
                            deviceService.approveDevice(registered.getDeviceId(), currentAdminId);
                            inserted++;
                        }
                    } catch(Exception e) {
                        errorsCount++;
                        errorsList.add("Row " + rowNum + " (SN: " + sNum + "): " + e.getMessage());
                    }
                }
                
                final int finalInserted = inserted;
                final int finalErrorsCount = errorsCount;
                final List<String> finalErrorsList = new ArrayList<>(errorsList);
                
                Platform.runLater(() -> {
                    StringBuilder summary = new StringBuilder();
                    summary.append("Import completed:\n");
                    summary.append("- Successfully registered & approved devices: ").append(finalInserted).append("\n");
                    summary.append("- Failed (errors): ").append(finalErrorsCount).append("\n");

                    if (!finalErrorsList.isEmpty()) {
                        summary.append("\nErrors:\n");
                        for (int i=0; i<Math.min(10, finalErrorsList.size()); i++) {
                            summary.append("• ").append(finalErrorsList.get(i)).append("\n");
                        }
                        if(finalErrorsList.size() > 10) summary.append("...and more.");
                        AlertHelper.showWarning("Import Complete with Errors", "Bulk Upload Summary", summary.toString());
                    } else {
                        AlertHelper.showInfo("Import Successful", "Bulk Upload Summary", summary.toString());
                    }

                    loadStudents();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Import Failed", "File Reading Failed", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void handleExportStudents() {
        if (studentTable.getItems().isEmpty()) {
            AlertHelper.showWarning("Export Warning", "No Data", "There is no student registry data to export.");
            return;
        }
        javafx.stage.Window window = studentTable.getScene().getWindow();
        CsvExportHelper.exportToCsv(studentTable, window, "student_registry.csv");
    }

    private void onDeviceSelected(Device device) {
        if (device == null) {
            handleClearDevice();
        } else {
            isDeviceEditMode = true;
            deviceNameField.setText(device.getDeviceName());
            brandField.setText(device.getBrand());
            modelField.setText(device.getModel());
            serialNumberField.setText(device.getSerialNumber());
            serialNumberField.setDisable(true); // Serial Number is unique and immutable once registered
            deviceTypeBox.setValue(device.getDeviceType());
            devicePurposeBox.setValue(device.getDevicePurpose());
            deviceStatusBox.setValue(device.getDeviceStatus());
            
            if (stageDeviceBtn != null) {
                stageDeviceBtn.setText("Update Device in List");
                stageDeviceBtn.getStyleClass().remove("action-btn-success");
                if (!stageDeviceBtn.getStyleClass().contains("action-btn-primary")) {
                    stageDeviceBtn.getStyleClass().add("action-btn-primary");
                }
            }
        }
    }

    // --- Form Action Handlers ---

    @FXML
    public void handleClearForm() {
        handleClearStudent();
        handleClearDevice();
    }
    @FXML
    public void handleDeactivateRecord() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        Device selectedDevice = deviceTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null && selectedDevice == null) {
            AlertHelper.showWarning("Deactivation", "No Selection", "Please select a student or device to deactivate/reactivate.");
            return;
        }

        StringBuilder successMsg = new StringBuilder();
        boolean changedAny = false;

        try {
            if (selectedDevice != null) {
                if (AlertHelper.showConfirmation("Deactivation", "Confirm Deactivation", "Are you sure you want to deactivate the selected device record?")) {
                    deviceService.deactivateDevice(selectedDevice.getDeviceId());
                    successMsg.append("Device '").append(selectedDevice.getSerialNumber()).append("' deactivated (status marked inactive).\n");
                    changedAny = true;
                }
            }
            if (selectedStudent != null) {
                boolean isInactive = "inactive".equalsIgnoreCase(selectedStudent.getStatus());
                if (isInactive) {
                    if (AlertHelper.showConfirmation("Reactivation", "Confirm Reactivation", "Are you sure you want to reactivate the selected student record?")) {
                        Student s = new Student();
                        s.setStudentId(selectedStudent.getStudentId());
                        s.setFirstName(selectedStudent.getFirstName());
                        s.setLastName(selectedStudent.getLastName());
                        s.setCourseYearLevel(selectedStudent.getCourseYearLevel());
                        s.setStatus("active");
                        studentService.updateStudent(selectedStudent.getStudentId(), s);
                        successMsg.append("Student '").append(selectedStudent.getStudentId()).append("' reactivated successfully.\n");
                        changedAny = true;
                    }
                } else {
                    if (AlertHelper.showConfirmation("Deactivation", "Confirm Deactivation", "Are you sure you want to deactivate the selected student record?")) {
                        studentService.deactivateStudent(selectedStudent.getStudentId());
                        successMsg.append("Student '").append(selectedStudent.getStudentId()).append("' deactivated (status marked inactive).\n");
                        changedAny = true;
                    }
                }
            }

            if (changedAny) {
                AlertHelper.showInfo("Operation Completed", "Success", successMsg.toString().trim());
                formOverlay.setVisible(false); // Close overlay after successful update
                loadStudents();
                handleClearForm();
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Action Failed", e.getMessage());
        }
    }

    @FXML
    public void handleStageDevice() {
        String deviceName = deviceNameField.getText();
        String sn = serialNumberField.getText();
        String brand = brandField.getText();
        String model = modelField.getText();
        String type = deviceTypeBox.getValue();
        String purpose = devicePurposeBox.getValue();
        String deviceStatusVal = deviceStatusBox.getValue();

        boolean v1 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(deviceNameField, "Input needed");
        boolean v2 = true;
        if (type == null || !type.equals("Project Prototypes (Optional SN)")) {
            v2 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(serialNumberField, "Input needed");
        } else {
            com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(serialNumberField);
        }
        boolean v3 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateComboBox(deviceTypeBox);
        boolean v4 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateComboBox(devicePurposeBox);
        boolean v5 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateComboBox(deviceStatusBox);

        if (!v1 || !v2 || !v3 || !v4 || !v5) {
            AlertHelper.showWarning("Device Warning", "Missing Device Fields", 
                "Please complete all required device fields before proceeding.");
            return;
        }

        if (isDeviceEditMode) {
            Device selectedDevice = deviceTable.getSelectionModel().getSelectedItem();
            if (selectedDevice != null) {
                selectedDevice.setDeviceName(deviceName);
                selectedDevice.setBrand(brand);
                selectedDevice.setModel(model);
                selectedDevice.setDeviceType(type);
                selectedDevice.setDevicePurpose(purpose);
                selectedDevice.setDeviceStatus(deviceStatusVal);
                
                deviceTable.refresh();
                AlertHelper.showInfo("Update Successful", "Device Updated", "The device record has been successfully updated in the list.");
                
                deviceTable.getSelectionModel().clearSelection();
                handleClearDevice();
            }
        } else {
            Device d = new Device();
            d.setDeviceName(deviceName);
            d.setBrand(brand);
            d.setModel(model);
            d.setSerialNumber(sn);
            d.setDeviceType(type);
            d.setDevicePurpose(purpose);
            d.setDeviceStatus(deviceStatusVal);
            d.setRegistrationStatus("Staged"); 
            d.setDeviceId(0); 

            deviceList.add(d);
            handleClearDevice();
        }
    }

    @FXML
    public void handleClearDevice() {
        deviceNameField.clear();
        brandField.clear();
        modelField.clear();
        serialNumberField.clear();
        serialNumberField.setDisable(false);
        deviceTypeBox.setValue(null);
        devicePurposeBox.setValue(null);
        deviceStatusBox.setValue(null);
        
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(deviceNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(serialNumberField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(deviceTypeBox);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(devicePurposeBox);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(deviceStatusBox);

        isDeviceEditMode = false;
        
        deviceTable.getSelectionModel().clearSelection();
        
        if (stageDeviceBtn != null) {
            stageDeviceBtn.setText("+ Add Device to List");
            stageDeviceBtn.getStyleClass().remove("action-btn-primary");
            if (!stageDeviceBtn.getStyleClass().contains("action-btn-success")) {
                stageDeviceBtn.getStyleClass().add("action-btn-success");
            }
        }
    }

    @FXML
    public void handleSaveRecord() {
        // 1. Validate & Save Student details
        String studentId = studentIdField.getText();
        String first = firstNameField.getText();
        String last = lastNameField.getText();
        String course = courseYearLevelField.getText();

        boolean v1 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(studentIdField, "Input needed");
        boolean v2 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(firstNameField, "Input needed");
        boolean v3 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(lastNameField, "Input needed");
        boolean v4 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(courseYearLevelField, "Input needed");

        if (!v1 || !v2 || !v3 || !v4) {
            AlertHelper.showWarning("Validation Error", "Missing Fields", "Please complete all required fields (*).");
            return;
        }

        Student s = new Student();
        s.setStudentId(studentId);
        s.setFirstName(first);
        s.setLastName(last);
        s.setCourseYearLevel(course);
        if (isStudentEditMode) {
            Student selected = studentTable.getSelectionModel().getSelectedItem();
            s.setStatus(selected != null ? selected.getStatus() : "active");
        } else {
            s.setStatus("active");
        }

        if (!AlertHelper.showConfirmation("Save Registry Record", "Confirm Save", "Are you sure you want to save the registry changes for student ID: " + studentId + "?")) {
            return;
        }

        try {
            if (isStudentEditMode) {
                studentService.updateStudent(studentId, s);
            } else {
                studentService.createStudent(s);
            }
            
            // 2. Register and auto-approve all staged devices
            int devicesSaved = 0;
            int currentAdminId = SessionManager.getInstance().getCurrentUser().getUserId();
            
            // Note: If they typed something but forgot to click "+ Add to List", let's be nice and add it
            String typedSn = serialNumberField.getText();
            String typedName = deviceNameField.getText();
            if (!ValidationHelper.isEmpty(typedSn) && !ValidationHelper.isEmpty(typedName) && deviceTypeBox.getValue() != null) {
                handleStageDevice();
            }

            for (Device stagedDevice : deviceList) {
                if (stagedDevice.getDeviceId() == 0 || "Staged".equals(stagedDevice.getRegistrationStatus())) {
                    stagedDevice.setStudentId(studentId);
                    stagedDevice.setRegistrationStatus("pending"); // Fix: Reset to valid enum before API call
                    Device registered = deviceService.registerDevice(stagedDevice);
                    deviceService.approveDevice(registered.getDeviceId(), currentAdminId);
                    devicesSaved++;
                } else if (isDeviceEditMode) {
                    // Update existing device if modified
                    deviceService.updateDevice(stagedDevice.getDeviceId(), stagedDevice);
                    devicesSaved++;
                }
            }

            String msg = "Student record saved successfully." + (devicesSaved > 0 ? "\n" + devicesSaved + " Device(s) registered and auto-approved successfully." : "");
            AlertHelper.showInfo("Registry Saved", "Success", msg);

            formOverlay.setVisible(false); // Close overlay after successful save
            loadStudents();
            handleClearForm();
        } catch (Exception e) {
            AlertHelper.showError("Save Failed", "API Request Failed", e.getMessage());
        }
    }

    private void handleClearStudent() {
        studentIdField.clear();
        studentIdField.setDisable(false);
        firstNameField.clear();
        lastNameField.clear();
        courseYearLevelField.clear();
        
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(studentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(firstNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(lastNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(courseYearLevelField);
        
        if (studentIdLabel != null) {
            javafx.scene.control.Label ast = new javafx.scene.control.Label("*");
            ast.setStyle("-fx-text-fill: red;");
            studentIdLabel.setGraphic(ast);
        }

        isStudentEditMode = false;
    }

}
