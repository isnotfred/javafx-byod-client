package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Request;
import com.pup.byod.javafxbyodclient.model.RequestDevice;
import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.LogService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class DevicesModalScreenController {
    @FXML private TableView<DeviceSelectionModel> devicesTable;
    @FXML private TableColumn<DeviceSelectionModel, Boolean> colSelect;
    @FXML private TableColumn<DeviceSelectionModel, String> colDevName;
    @FXML private TableColumn<DeviceSelectionModel, String> colDevBrand;
    @FXML private TableColumn<DeviceSelectionModel, String> colDevModel;
    @FXML private TableColumn<DeviceSelectionModel, String> colDevSerial;
    @FXML private TableColumn<DeviceSelectionModel, Integer> colDevQuantity;

    @FXML private Button btnAction;

    private final LogService logService = new LogService();
    private final ObservableList<DeviceSelectionModel> selectionList = FXCollections.observableArrayList();

    private Request request;
    private String expectedAction; // "Entry" or "Exit"
    private Runnable onCompleteCallback;

    @FXML
    public void initialize() {
        // Table Columns Binding
        colSelect.setCellValueFactory(f -> f.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        
        colDevName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colDevBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colDevModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colDevSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colDevQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        devicesTable.setItems(selectionList);
        devicesTable.setEditable(true);

        // Row factory for blue row highlighting on selected rows
        devicesTable.setRowFactory(tv -> {
            TableRow<DeviceSelectionModel> row = new TableRow<DeviceSelectionModel>() {
                @Override
                protected void updateItem(DeviceSelectionModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                        getStyleClass().remove("selected-row");
                    } else {
                        if (item.isSelected()) {
                            setStyle("-fx-background-color: #E1F5FE; -fx-text-fill: black;"); // pastel blue
                            if (!getStyleClass().contains("selected-row")) {
                                getStyleClass().add("selected-row");
                            }
                        } else {
                            setStyle("");
                            getStyleClass().remove("selected-row");
                        }
                    }
                }
            };

            // Toggle selected state on single row click
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    DeviceSelectionModel item = row.getItem();
                    item.setSelected(!item.isSelected());
                    devicesTable.refresh();
                }
            });

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    newItem.selectedProperty().addListener((o, oldVal, newVal) -> {
                        devicesTable.refresh();
                    });
                }
            });

            return row;
        });

        // Prevent standard focus row selection highlight
        devicesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Platform.runLater(() -> devicesTable.getSelectionModel().clearSelection());
            }
        });
    }

    public void initData(Request request, String expectedAction, Student student, List<RequestDevice> devices, List<String> deviceStatuses, Runnable onCompleteCallback) {
        this.request = request;
        this.expectedAction = expectedAction;
        this.onCompleteCallback = onCompleteCallback;

        // Setup Action button text
        if ("Entry".equalsIgnoreCase(expectedAction) || (expectedAction != null && expectedAction.startsWith("Entry"))) {
            btnAction.setText("Log Entry");
            btnAction.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;");
            btnAction.setDisable(false);
        } else if ("Exit".equalsIgnoreCase(expectedAction) || (expectedAction != null && expectedAction.startsWith("Exit"))) {
            btnAction.setText("Log Exit");
            btnAction.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold;");
            btnAction.setDisable(false);
        } else {
            btnAction.setText("Transaction Completed for Today");
            btnAction.setStyle("-fx-background-color: #94A3B8; -fx-text-fill: white; -fx-font-weight: bold;");
            btnAction.setDisable(true);
        }

        // Wrap devices and default to selected = true
        selectionList.clear();
        for (int i = 0; i < devices.size(); i++) {
            RequestDevice dev = devices.get(i);
            String campusStatus = deviceStatuses.size() > i ? deviceStatuses.get(i) : "Outside";
            
            DeviceSelectionModel model = new DeviceSelectionModel(dev, campusStatus);
            // Default select behavior: Check all devices by default
            model.setSelected(true);
            selectionList.add(model);
        }
    }

    @FXML
    public void handleAction() {
        List<Integer> deviceIds = new ArrayList<>();
        List<String> serials = new ArrayList<>();
        
        for (DeviceSelectionModel model : selectionList) {
            if (model.isSelected()) {
                deviceIds.add(model.getDevice().getRequestDeviceId());
                serials.add(model.getDevice().getSerialNumber());
            }
        }

        if (deviceIds.isEmpty()) {
            AlertHelper.showWarning("Log Action", "No Devices Selected", "Please select at least one device to log.");
            return;
        }

        int guardId = SessionManager.getInstance().getCurrentUser().getUserId();

        try {
            if ("Entry".equalsIgnoreCase(expectedAction) || (expectedAction != null && expectedAction.startsWith("Entry"))) {
                // Perform check-in / batch ingress
                logService.processBatchIngress(deviceIds, guardId);
                AlertHelper.showInfo("Gate Check Success", "Ingress Logged", "Log Entry processed successfully for " + deviceIds.size() + " device(s).");
            } else {
                // Perform check-out / batch egress
                logService.processBatchEgress(deviceIds, guardId);
                AlertHelper.showInfo("Gate Check Success", "Egress Logged", "Log Exit processed successfully for " + deviceIds.size() + " device(s).");
            }

            // Close modal stage
            closeModal();

            // Refresh parent screen
            if (onCompleteCallback != null) {
                onCompleteCallback.run();
            }

        } catch (Exception e) {
            AlertHelper.showError("Transaction Error", "Failed to log transaction", e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) btnAction.getScene().getWindow();
        stage.close();
    }

    public static class DeviceSelectionModel {
        private final BooleanProperty selected = new SimpleBooleanProperty(true);
        private final RequestDevice device;
        private final String campusStatus;

        public DeviceSelectionModel(RequestDevice device, String campusStatus) {
            this.device = device;
            this.campusStatus = campusStatus;
        }

        public BooleanProperty selectedProperty() { return selected; }
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean val) { selected.set(val); }

        public RequestDevice getDevice() { return device; }

        public String getDeviceName() { return device.getDeviceName(); }
        public String getBrand() { return device.getBrand(); }
        public String getModel() { return device.getModel(); }
        public String getSerialNumber() { return device.getSerialNumber(); }
        public int getQuantity() { return device.getQuantity(); }
        public String getCampusStatus() { return campusStatus; }
    }
}
