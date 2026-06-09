package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Device;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class QuickPendingRegistrationScreenController {
    @FXML private TextField studentIdField;
    @FXML private TextField deviceNameField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField serialNumberField;
    @FXML private ComboBox<String> deviceTypeBox;
    @FXML private ComboBox<String> purposeBox;
    @FXML private TextField remarksField;

    private final DeviceService deviceService = new DeviceService();

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
    }

    @FXML
    public void handleRegister() {
        String studentId = studentIdField.getText();
        String name = deviceNameField.getText();
        String brand = brandField.getText();
        String model = modelField.getText();
        String sn = serialNumberField.getText();
        String type = deviceTypeBox.getValue();
        String purpose = purposeBox.getValue();
        String remarks = remarksField.getText();

        if (ValidationHelper.isEmpty(studentId) || ValidationHelper.isEmpty(name) ||
                ValidationHelper.isEmpty(sn) || type == null || purpose == null) {
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

        try {
            deviceService.registerDevice(device);
            AlertHelper.showInfo("Registered", "Success", "Device registered in pending status.");
            clearFields();
        } catch (Exception e) {
            AlertHelper.showError("Registration Error", "Submit Failed", e.getMessage());
        }
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
    }
}
