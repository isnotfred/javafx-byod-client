package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Device;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class DeviceManagementScreenController {
    @FXML private TextField searchField;
    @FXML private TableView<Device> deviceTable;
    @FXML private TableColumn<Device, Integer> colId;
    @FXML private TableColumn<Device, String> colStudentId;
    @FXML private TableColumn<Device, String> colName;
    @FXML private TableColumn<Device, String> colBrand;
    @FXML private TableColumn<Device, String> colModel;
    @FXML private TableColumn<Device, String> colSerialNumber;
    @FXML private TableColumn<Device, String> colStatus;

    private final DeviceService deviceService = new DeviceService();
    private final ObservableList<Device> deviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("deviceId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("deviceStatus"));

        deviceTable.setItems(deviceList);
        loadDevices();
    }

    private void loadDevices() {
        try {
            List<Device> devices = deviceService.getAllDevices();
            deviceList.setAll(devices);
        } catch (Exception e) {
            // Log or show error silently for now (or alert)
            System.err.println("Could not load devices: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadDevices();
            return;
        }
        // Filter local list as mockup/simple search
        ObservableList<Device> filtered = FXCollections.observableArrayList();
        for (Device d : deviceList) {
            if (d.getSerialNumber().toLowerCase().contains(keyword.toLowerCase()) || 
                d.getStudentId().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(d);
            }
        }
        deviceTable.setItems(filtered);
    }

    @FXML
    public void handleDeactivate() {
        Device selected = deviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Deactivation", "No Selection", "Please select a device to deactivate.");
            return;
        }
        try {
            deviceService.deactivateDevice(selected.getDeviceId());
            AlertHelper.showInfo("Success", "Device Deactivated", "Device with Serial " + selected.getSerialNumber() + " has been soft-deleted.");
            loadDevices();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Deactivation Failed", e.getMessage());
        }
    }
}
