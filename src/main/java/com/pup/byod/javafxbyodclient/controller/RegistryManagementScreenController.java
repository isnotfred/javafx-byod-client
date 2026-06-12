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

public class RegistryManagementScreenController {
    // Student directory controls
    @FXML private TextField searchField;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colStudentName;
    @FXML private TableColumn<Student, String> colStudentCourse;
    @FXML private TableColumn<Student, String> colStudentStatus;
    @FXML private Button editSelectedBtn;

    // Overlay control
    @FXML private StackPane formOverlay;

    // Form inputs
    @FXML private TextField studentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField courseYearLevelField;
    @FXML private ComboBox<String> studentStatusBox;

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
        studentStatusBox.getItems().addAll("active", "inactive");
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
        firstNameField.setText(selected.getFirstName());
        lastNameField.setText(selected.getLastName());
        courseYearLevelField.setText(selected.getCourseYearLevel());
        studentStatusBox.setValue(selected.getStatus());

        // Clear device forms, then load devices for student
        handleClearDevice();
        deviceList.clear();
        loadDevicesForStudent(selected.getStudentId());

        formOverlay.setVisible(true);
    }

    @FXML
    public void handleCloseOverlay() {
        formOverlay.setVisible(false);
        handleClearForm();
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

    @FXML
    @SuppressWarnings("unchecked")
    public void handleImportCsv() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Import Students CSV");
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
            
            List<String> requiredHeaders = List.of("student_id", "first_name", "last_name", "course_year_level");
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
                    "\n\nExpected headers: student_id,first_name,last_name,course_year_level"
                );
                return;
            }
        } catch (Exception e) {
            AlertHelper.showError("CSV Validation Failed", "Error reading file", e.getMessage());
            return;
        }        // Proceed to call API
        if (!AlertHelper.showConfirmation("Import CSV", "Confirm Import", "Are you sure you want to import student records from the selected CSV file?")) {
            return;
        }

        try {
            Map<String, Object> result = studentService.importStudentsCsv(selectedFile);
            
            int inserted = result.containsKey("inserted") ? (int) result.get("inserted") : 0;
            int skipped = result.containsKey("skipped") ? (int) result.get("skipped") : 0;
            List<Map<String, Object>> errors = result.containsKey("errors") ? (List<Map<String, Object>>) result.get("errors") : new ArrayList<>();

            StringBuilder summary = new StringBuilder();
            summary.append("Import completed:\n");
            summary.append("- Successfully inserted: ").append(inserted).append("\n");
            summary.append("- Skipped (duplicates): ").append(skipped).append("\n");
            summary.append("- Failed (errors): ").append(errors.size()).append("\n");

            if (!errors.isEmpty()) {
                summary.append("\nErrors:\n");
                for (Map<String, Object> error : errors) {
                    int row = error.containsKey("row") ? (int) error.get("row") : 0;
                    String id = error.containsKey("studentId") ? (String) error.get("studentId") : "unknown";
                    List<String> reasons = error.containsKey("reasons") ? (List<String>) error.get("reasons") : new ArrayList<>();
                    summary.append("• Row ").append(row)
                           .append(" (ID: ").append(id).append("): ")
                           .append(String.join(", ", reasons)).append("\n");
                }
                AlertHelper.showWarning("Import Complete with Errors", "Bulk Upload Summary", summary.toString());
            } else {
                AlertHelper.showInfo("Import Successful", "Bulk Upload Summary", summary.toString());
            }
            
            loadStudents();
        } catch (Exception e) {
            AlertHelper.showError("Import Failed", "API Request Failed", e.getMessage());
        }
    }

    @FXML
    public void handleImportDevicesCsv() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Import Devices CSV");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv")
        );
        
        javafx.stage.Window window = studentTable.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);
        
        if (selectedFile == null) {
            return;
        }

        // Validate CSV headers first
        List<String[]> rows = new ArrayList<>();
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
            
            List<String> requiredHeaders = List.of("student_id", "device_name", "serial_number", "device_type");
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
                    "\n\nExpected headers: student_id,device_name,serial_number,device_type"
                );
                return;
            }

            // Find indexes of headers
            int studentIdIdx = headersList.indexOf("student_id");
            int deviceNameIdx = headersList.indexOf("device_name");
            int serialNumberIdx = headersList.indexOf("serial_number");
            int deviceTypeIdx = headersList.indexOf("device_type");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(",");
                // Ensure array has enough elements or fill with empty
                String[] parsedRow = new String[4];
                parsedRow[0] = values.length > studentIdIdx ? values[studentIdIdx].trim() : "";
                parsedRow[1] = values.length > deviceNameIdx ? values[deviceNameIdx].trim() : "";
                parsedRow[2] = values.length > serialNumberIdx ? values[serialNumberIdx].trim() : "";
                parsedRow[3] = values.length > deviceTypeIdx ? values[deviceTypeIdx].trim() : "";
                rows.add(parsedRow);
            }
        } catch (Exception e) {
            AlertHelper.showError("CSV Validation Failed", "Error reading file", e.getMessage());
            return;
        }

        if (rows.isEmpty()) {
            AlertHelper.showWarning("Import Warning", "No Data", "No data rows found in the selected CSV file.");
            return;
        }

        if (!AlertHelper.showConfirmation("Import CSV", "Confirm Import", 
                "Are you sure you want to import " + rows.size() + " device records from the selected CSV file?")) {
            return;
        }

        // Run client-side sequential import in a background thread to prevent UI freezing
        new Thread(() -> {
            int inserted = 0;
            int failed = 0;
            List<String> errors = new ArrayList<>();

            for (int i = 0; i < rows.size(); i++) {
                String[] row = rows.get(i);
                String studentId = row[0];
                String deviceName = row[1];
                String serialNumber = row[2];
                String deviceType = row[3];

                int rowNum = i + 2; // row 1 is header

                if (studentId.isEmpty() || deviceName.isEmpty() || serialNumber.isEmpty() || deviceType.isEmpty()) {
                    failed++;
                    errors.add("Row " + rowNum + ": Missing required fields.");
                    continue;
                }

                try {
                    Device d = new Device();
                    d.setStudentId(studentId);
                    d.setDeviceName(deviceName);
                    d.setSerialNumber(serialNumber);
                    d.setDeviceType(deviceType);
                    // Default fields for CSV import
                    d.setBrand("Unknown");
                    d.setModel("Unknown");
                    d.setDevicePurpose("Academic BYOD");
                    d.setDeviceStatus("active");

                    deviceService.registerDevice(d);
                    inserted++;
                } catch (Exception e) {
                    failed++;
                    errors.add("Row " + rowNum + " (SN: " + serialNumber + "): " + e.getMessage());
                }
            }

            final int successCount = inserted;
            final int failCount = failed;
            final List<String> errorList = errors;

            Platform.runLater(() -> {
                StringBuilder summary = new StringBuilder();
                summary.append("Import completed:\n");
                summary.append("- Successfully registered: ").append(successCount).append("\n");
                summary.append("- Failed: ").append(failCount).append("\n");

                if (!errorList.isEmpty()) {
                    summary.append("\nErrors:\n");
                    for (String err : errorList) {
                        summary.append("• ").append(err).append("\n");
                    }
                    AlertHelper.showWarning("Import Complete with Errors", "Bulk Upload Summary", summary.toString());
                } else {
                    AlertHelper.showInfo("Import Successful", "Bulk Upload Summary", summary.toString());
                }

                loadStudents(); // refresh student table to show linked devices if any
            });
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
            isDeviceEditMode = false;
            deviceNameField.clear();
            brandField.clear();
            modelField.clear();
            serialNumberField.clear();
            serialNumberField.setDisable(false);
            deviceTypeBox.setValue(null);
            devicePurposeBox.setValue(null);
            deviceStatusBox.setValue("active");
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
            AlertHelper.showWarning("Deactivation", "No Selection", "Please select a student or device to deactivate.");
            return;
        }

        if (!AlertHelper.showConfirmation("Deactivation", "Confirm Deactivation", "Are you sure you want to deactivate the selected student/device record? This action is soft-destructive.")) {
            return;
        }

        StringBuilder successMsg = new StringBuilder();
        boolean deactivatedAny = false;

        try {
            if (selectedDevice != null) {
                deviceService.deactivateDevice(selectedDevice.getDeviceId());
                successMsg.append("Device '").append(selectedDevice.getSerialNumber()).append("' deactivated (status marked inactive).\n");
                deactivatedAny = true;
            }
            if (selectedStudent != null) {
                studentService.deactivateStudent(selectedStudent.getStudentId());
                successMsg.append("Student '").append(selectedStudent.getStudentId()).append("' deactivated (status marked inactive).\n");
                deactivatedAny = true;
            }

            if (deactivatedAny) {
                AlertHelper.showInfo("Deactivation Completed", "Success", successMsg.toString().trim());
            }
            
            formOverlay.setVisible(false); // Close overlay after successful deactivation
            loadStudents();
            handleClearForm();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Deactivate Failed", e.getMessage());
        }
    }

    @FXML
    public void handleSaveRecord() {
        // 1. Validate & Save Student details
        String studentId = studentIdField.getText();
        String first = firstNameField.getText();
        String last = lastNameField.getText();
        String course = courseYearLevelField.getText();
        String studentStatus = studentStatusBox.getValue();

        if (ValidationHelper.isEmpty(studentId) || ValidationHelper.isEmpty(first) || 
            ValidationHelper.isEmpty(last) || ValidationHelper.isEmpty(course) || studentStatus == null) {
            AlertHelper.showWarning("Form Warning", "Missing Student Fields", "Please fill in all student information fields.");
            return;
        }

        Student s = new Student();
        s.setStudentId(studentId);
        s.setFirstName(first);
        s.setLastName(last);
        s.setCourseYearLevel(course);
        s.setStatus(studentStatus);

        if (!AlertHelper.showConfirmation("Save Registry Record", "Confirm Save", "Are you sure you want to save the registry changes for student ID: " + studentId + "?")) {
            return;
        }

        try {
            if (isStudentEditMode) {
                studentService.updateStudent(studentId, s);
            } else {
                studentService.createStudent(s);
            }
            
            // 2. Validate & Save Device details (if serial number or name is typed)
            String deviceName = deviceNameField.getText();
            String sn = serialNumberField.getText();
            
            boolean deviceSaved = false;
            if (!ValidationHelper.isEmpty(sn) || !ValidationHelper.isEmpty(deviceName)) {
                String brand = brandField.getText();
                String model = modelField.getText();
                String type = deviceTypeBox.getValue();
                String purpose = devicePurposeBox.getValue();
                String deviceStatusVal = deviceStatusBox.getValue();

                if (ValidationHelper.isEmpty(deviceName) || ValidationHelper.isEmpty(sn) || 
                    type == null || purpose == null || deviceStatusVal == null) {
                    AlertHelper.showWarning("Device Warning", "Missing Device Fields", 
                        "You started entering device details. Please complete all device fields (Name, Serial Number, Type, Purpose, Status).");
                    return;
                }

                Device d = new Device();
                d.setStudentId(studentId);
                d.setDeviceName(deviceName);
                d.setBrand(brand);
                d.setModel(model);
                d.setSerialNumber(sn);
                d.setDeviceType(type);
                d.setDevicePurpose(purpose);
                d.setDeviceStatus(deviceStatusVal);

                if (isDeviceEditMode) {
                    Device selectedDevice = deviceTable.getSelectionModel().getSelectedItem();
                    if (selectedDevice != null) {
                        deviceService.updateDevice(selectedDevice.getDeviceId(), d);
                        deviceSaved = true;
                    }
                } else {
                    deviceService.registerDevice(d);
                    deviceSaved = true;
                }
            }

            String msg = "Student record saved successfully." + (deviceSaved ? "\nDevice record registered/saved successfully." : "");
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
        studentStatusBox.setValue("active");
        isStudentEditMode = false;
    }

    private void handleClearDevice() {
        deviceNameField.clear();
        brandField.clear();
        modelField.clear();
        serialNumberField.clear();
        serialNumberField.setDisable(false);
        deviceTypeBox.setValue(null);
        devicePurposeBox.setValue(null);
        deviceStatusBox.setValue("active");
        isDeviceEditMode = false;
    }
}
