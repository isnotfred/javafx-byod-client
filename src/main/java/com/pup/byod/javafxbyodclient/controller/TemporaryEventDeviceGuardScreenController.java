package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.EventRequestDevice;
import com.pup.byod.javafxbyodclient.service.EventRequestService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TemporaryEventDeviceGuardScreenController {
    @FXML private TextField eventRequestIdField;
    @FXML private TableView<EventRequestDevice> itemsTable;
    @FXML private TableColumn<EventRequestDevice, Integer> colItemId;
    @FXML private TableColumn<EventRequestDevice, String> colItemName;
    @FXML private TableColumn<EventRequestDevice, String> colSerialNumber;
    @FXML private TableColumn<EventRequestDevice, String> colStatus;

    private final EventRequestService eventRequestService = new EventRequestService();
    private final ObservableList<EventRequestDevice> deviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colItemId.setCellValueFactory(new PropertyValueFactory<>("eventDeviceId"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("deviceStatus"));

        itemsTable.setItems(deviceList);
    }

    @FXML
    public void handleSearchEvent() {
        String reqIdText = eventRequestIdField.getText();
        if (ValidationHelper.isEmpty(reqIdText)) {
            AlertHelper.showWarning("Search Warning", "ID Required", "Please enter the Event Request ID.");
            return;
        }

        try {
            int requestId = Integer.parseInt(reqIdText);
            List<EventRequestDevice> devices = eventRequestService.getEventRequestDevices(requestId);
            deviceList.setAll(devices);
            if (devices.isEmpty()) {
                AlertHelper.showInfo("Search Request", "No Devices Found", "No devices associated with this event request.");
            }
        } catch (NumberFormatException e) {
            AlertHelper.showError("Validation Error", "Invalid ID Format", "Event Request ID must be a number.");
        } catch (Exception e) {
            AlertHelper.showError("Search Error", "Load Failed", e.getMessage());
        }
    }

    @FXML
    public void handleVerify() {
        EventRequestDevice selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Verification", "No Selection", "Please select a device to verify.");
            return;
        }

        try {
            int guardId = SessionManager.getInstance().getCurrentUser().getUserId();
            eventRequestService.verifyEventDevice(selected.getEventDeviceId(), guardId, "approved");
            AlertHelper.showInfo("Success", "Device Verified", "Device " + selected.getDeviceName() + " has been marked verified.");
            handleSearchEvent(); // Reload list
        } catch (Exception e) {
            AlertHelper.showError("Error", "Verification Failed", e.getMessage());
        }
    }
}
