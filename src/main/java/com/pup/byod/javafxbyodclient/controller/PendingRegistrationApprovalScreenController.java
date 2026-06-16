package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.PendingDevice;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class PendingRegistrationApprovalScreenController {
    @FXML private TableView<PendingDevice> pendingTable;
    @FXML private TableColumn<PendingDevice, Integer> colId;
    @FXML private TableColumn<PendingDevice, String> colStudentName;
    @FXML private TableColumn<PendingDevice, String> colDeviceName;
    @FXML private TableColumn<PendingDevice, String> colSerialNumber;
    @FXML private TableColumn<PendingDevice, String> colPurpose;
    @FXML private TextArea remarksArea;

    private final DeviceService deviceService = new DeviceService();
    private final ObservableList<PendingDevice> pendingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("deviceId"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentFullName"));
        colDeviceName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colPurpose.setCellValueFactory(new PropertyValueFactory<>("devicePurpose"));

        pendingTable.setItems(pendingList);
        loadPending();
    }

    private void loadPending() {
        try {
            List<PendingDevice> pending = deviceService.getPendingDevices();
            pendingList.setAll(pending);
        } catch (Exception e) {
            System.err.println("Could not load pending queue: " + e.getMessage());
        }
    }

    @FXML
    public void handleApprove() {
        PendingDevice selected = pendingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Approval", "No Selection", "Please select a pending device.");
            return;
        }
        if (!AlertHelper.showConfirmation("Approve Device", "Confirm Approval", "Are you sure you want to approve this device registration request?")) {
            return;
        }
        try {
            int reviewerId = SessionManager.getInstance().getCurrentUser().getUserId();
            deviceService.approveDevice(selected.getDeviceId(), reviewerId);
            AlertHelper.showInfo("Approved", "Success", "Device registration approved successfully.");
            loadPending();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Approval Failed", e.getMessage());
        }
    }

    @FXML
    public void handleReject() {
        PendingDevice selected = pendingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Rejection", "No Selection", "Please select a pending device.");
            return;
        }
        String remarks = remarksArea.getText();
        if (remarks == null || remarks.trim().isEmpty()) {
            AlertHelper.showWarning("Rejection", "Remarks Required", "Please provide a reason/remarks for rejection.");
            return;
        }
        if (!AlertHelper.showConfirmation("Reject Device", "Confirm Rejection", "Are you sure you want to reject this device registration request?")) {
            return;
        }
        try {
            int reviewerId = SessionManager.getInstance().getCurrentUser().getUserId();
            deviceService.rejectDevice(selected.getDeviceId(), reviewerId, remarks);
            AlertHelper.showInfo("Rejected", "Success", "Device registration rejected.");
            remarksArea.clear();
            loadPending();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Rejection Failed", e.getMessage());
        }
    }
}
