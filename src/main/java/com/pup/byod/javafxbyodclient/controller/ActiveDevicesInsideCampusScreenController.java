package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.DeviceCampusStatus;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import com.pup.byod.javafxbyodclient.util.CsvExportHelper;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.PromptTextHelper;

import java.util.List;

public class ActiveDevicesInsideCampusScreenController {
    @FXML private TableView<DeviceCampusStatus> statusTable;
    @FXML private TableColumn<DeviceCampusStatus, Integer> colId;
    @FXML private TableColumn<DeviceCampusStatus, String> colStudentId;
    @FXML private TableColumn<DeviceCampusStatus, String> colName;
    @FXML private TableColumn<DeviceCampusStatus, String> colSerialNumber;
    @FXML private TableColumn<DeviceCampusStatus, String> colStatus;
    @FXML private TableColumn<DeviceCampusStatus, String> colLastTime;

    @FXML private TextField searchField;

    private final DeviceService deviceService = new DeviceService();
    private final ObservableList<DeviceCampusStatus> statusList = FXCollections.observableArrayList();
    private FilteredList<DeviceCampusStatus> filteredList;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("deviceId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("campusStatus"));
        colLastTime.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimestamp(cellData.getValue().getLastEventTime())
        ));

        filteredList = new FilteredList<>(statusList, p -> true);
        SortedList<DeviceCampusStatus> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(statusTable.comparatorProperty());
        statusTable.setItems(sortedData);

        // Restrict sorting strictly to Device ID
        colId.setSortable(true);
        colStudentId.setSortable(false);
        colName.setSortable(false);
        colSerialNumber.setSortable(false);
        colStatus.setSortable(false);
        colLastTime.setSortable(false);

        // Apply right-aligned arrow CSS class
        colId.getStyleClass().add("right-arrow-header");

        // Set default sort to Device ID
        colId.setSortType(TableColumn.SortType.ASCENDING);
        statusTable.getSortOrder().add(colId);

        // Prevent the "no arrow" state by defaulting back to colId
        statusTable.setOnSort(event -> {
            if (statusTable.getSortOrder().isEmpty()) {
                colId.setSortType(TableColumn.SortType.ASCENDING);
                javafx.application.Platform.runLater(() -> {
                    statusTable.getSortOrder().add(colId);
                });
            }
        });

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                applyFilter(newVal);
            });
            PromptTextHelper.setup(searchField);
        }

        loadStatus();
    }

    private void applyFilter(String query) {
        if (filteredList == null) return;
        filteredList.setPredicate(status -> {
            if (query == null || query.trim().isEmpty()) {
                return true;
            }
            String lowerCaseFilter = query.toLowerCase().trim();

            // Check Device ID (id)
            if (status.getDeviceId() != null && String.valueOf(status.getDeviceId()).contains(lowerCaseFilter)) {
                return true;
            }
            // Check Student Number (studentId)
            if (status.getStudentId() != null && status.getStudentId().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            // Check Device Serial Number (serialNumber)
            if (status.getSerialNumber() != null && status.getSerialNumber().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            return false;
        });
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
