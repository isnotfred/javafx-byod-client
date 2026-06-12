package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.DeviceCampusStatus;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.pup.byod.javafxbyodclient.util.CsvExportHelper;
import com.pup.byod.javafxbyodclient.util.AlertHelper;

import java.util.List;

public class ActiveDevicesInsideCampusScreenController {
    @FXML private TableView<DeviceCampusStatus> statusTable;
    @FXML private TableColumn<DeviceCampusStatus, Integer> colId;
    @FXML private TableColumn<DeviceCampusStatus, String> colStudentId;
    @FXML private TableColumn<DeviceCampusStatus, String> colName;
    @FXML private TableColumn<DeviceCampusStatus, String> colSerialNumber;
    @FXML private TableColumn<DeviceCampusStatus, String> colStatus;
    @FXML private TableColumn<DeviceCampusStatus, String> colLastTime;

    private final DeviceService deviceService = new DeviceService();
    private final ObservableList<DeviceCampusStatus> statusList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("deviceId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("campusStatus"));
        colLastTime.setCellValueFactory(new PropertyValueFactory<>("lastEventTime"));

        statusTable.setItems(statusList);
        loadStatus();
    }

    @FXML
    public void loadStatus() {
        try {
            List<DeviceCampusStatus> status = deviceService.getDeviceCampusStatus();
            statusList.setAll(status);
        } catch (Exception e) {
            System.err.println("Could not load campus device status: " + e.getMessage());
        }
    }

    @FXML
    public void handleExportPresence() {
        if (statusTable.getItems().isEmpty()) {
            AlertHelper.showWarning("Export Warning", "No Data", "There is no presence data to export.");
            return;
        }
        javafx.stage.Window window = statusTable.getScene().getWindow();
        CsvExportHelper.exportToCsv(statusTable, window, "campus_presence.csv");
    }
}
