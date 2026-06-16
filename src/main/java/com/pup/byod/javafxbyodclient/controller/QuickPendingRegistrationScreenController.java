package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Device;
import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import java.util.List;

public class QuickPendingRegistrationScreenController {
    @FXML private TextField studentIdField;
    @FXML private TextField deviceNameField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField serialNumberField;
    @FXML private ComboBox<String> deviceTypeBox;
    @FXML private ComboBox<String> purposeBox;
    @FXML private TextField remarksField;
    @FXML private Button registerBtn;
    @FXML private javafx.scene.control.Label serialNumberLabel;

    private final DeviceService deviceService = new DeviceService();
    private final StudentService studentService = new StudentService();
    private boolean submitting = false;

    @FXML
    public void initialize() {
        deviceTypeBox.getItems().addAll(
                "Personal Computers",
                "Components & Peripherals",
                "Display & Projection",
                "Project Prototypes (Optional SN)",
                "Appliances (TLE)"
        );
        purposeBox.getItems().addAll(
                "Academic BYOD",
                "School Event",
                "Organization Use",
                "Other"
        );

        // Setup Student ID Autocomplete & Prompt Text Helpers
        com.pup.byod.javafxbyodclient.util.StudentSearchDropdown.attach(studentIdField, null);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(studentIdField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(deviceNameField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(brandField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(modelField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(serialNumberField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(remarksField);
        
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(studentIdField);
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
                serialNumberField.setPromptText("e.g. SN-12345");
                if (serialNumberLabel != null) {
                    javafx.scene.control.Label ast = new javafx.scene.control.Label("*");
                    ast.setStyle("-fx-text-fill: red;");
                    serialNumberLabel.setGraphic(ast);
                }
            }
        });
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(deviceTypeBox);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(purposeBox);
    }


    @FXML
    public void handleRegister() {
        if (submitting) return;

        String studentId = studentIdField.getText();
        String name = deviceNameField.getText();
        String brand = brandField.getText();
        String model = modelField.getText();
        String sn = serialNumberField.getText();
        String type = deviceTypeBox.getValue();
        String purpose = purposeBox.getValue();
        String remarks = remarksField.getText();

        boolean v1 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(studentIdField, "Input needed");
        boolean v2 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(deviceNameField, "Input needed");
        boolean v3 = true;
        if (type == null || !type.equals("Project Prototypes (Optional SN)")) {
            v3 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateTextInput(serialNumberField, "Input needed");
        } else {
            com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(serialNumberField);
        }
        boolean v4 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateComboBox(deviceTypeBox);
        boolean v5 = com.pup.byod.javafxbyodclient.util.ValidationHelper.validateComboBox(purposeBox);

        if (!v1 || !v2 || !v3 || !v4 || !v5) {
            AlertHelper.showWarning("Form Validation", "Missing Fields", "Please fill in all mandatory fields.");
            return;
        }

        Device device = new Device();
        device.setStudentId(studentId);
        device.setDeviceName(name);
        device.setBrand(brand);
        device.setModel(model);
        device.setSerialNumber(sn);
        device.setDeviceType(type);
        device.setDevicePurpose(purpose);
        device.setRemarks(remarks);

        submitting = true;
        registerBtn.setDisable(true);

        new Thread(() -> {
            try {
                // Check if device with same serial number already exists (prevent duplicate submission)
                try {
                    Device existing = deviceService.getDeviceBySerialNumber(sn);
                    if (existing != null) {
                        Platform.runLater(() -> {
                            AlertHelper.showWarning("Duplicate Serial Number", "Device Already Registered",
                                    "A device with serial number '" + sn + "' is already registered in the system.");
                            submitting = false;
                            registerBtn.setDisable(false);
                        });
                        return;
                    }
                } catch (Exception ignored) {
                    // 404 is expected when device does not exist
                }

                deviceService.registerDevice(device);
                Platform.runLater(() -> {
                    AlertHelper.showInfo("Registered", "Success", "Device registered in pending status.");
                    clearFields();
                    submitting = false;
                    registerBtn.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    AlertHelper.showError("Registration Error", "Submit Failed", e.getMessage());
                    submitting = false;
                    registerBtn.setDisable(false);
                });
            }
        }).start();
    }

    private void clearFields() {
        studentIdField.clear();
        deviceNameField.clear();
        brandField.clear();
        modelField.clear();
        serialNumberField.clear();
        remarksField.clear();
        deviceTypeBox.setValue(null);
        purposeBox.setValue(null);
        
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(studentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(deviceNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(serialNumberField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(deviceTypeBox);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(purposeBox);
    }
}
