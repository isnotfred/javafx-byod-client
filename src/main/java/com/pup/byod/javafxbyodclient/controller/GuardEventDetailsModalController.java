package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Request;
import com.pup.byod.javafxbyodclient.model.RequestDevice;
import com.pup.byod.javafxbyodclient.service.RequestService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class GuardEventDetailsModalController {
    @FXML private Label lblEventName;
    @FXML private Label lblVenue;
    @FXML private Label lblOrg;
    @FXML private Label lblCoordinator;
    @FXML private Label lblTime;
    @FXML private Label lblPurpose;

    @FXML private TableView<RequestDevice> devicesTable;
    @FXML private TableColumn<RequestDevice, String> colDevName;
    @FXML private TableColumn<RequestDevice, String> colDevBrand;
    @FXML private TableColumn<RequestDevice, String> colDevModel;
    @FXML private TableColumn<RequestDevice, String> colDevSerial;
    @FXML private TableColumn<RequestDevice, Integer> colDevQuantity;

    private final RequestService requestService = new RequestService();
    private final ObservableList<RequestDevice> devicesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDevName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colDevBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colDevModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colDevSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colDevQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        devicesTable.setItems(devicesList);
    }

    public void initData(Request req) {
        lblEventName.setText(req.getEventName() != null ? req.getEventName() : "-");
        lblVenue.setText(req.getVenue() != null ? req.getVenue() : "-");
        lblOrg.setText(req.getOrganization() != null ? req.getOrganization() : "-");
        lblCoordinator.setText(req.getResponsiblePerson() != null ? req.getResponsiblePerson() : "-");
        
        String timeDisplay = formatTime12hr(req.getExpectedIngressTime()) + " - " + formatTime12hr(req.getExpectedEgressTime());
        lblTime.setText(timeDisplay);
        lblPurpose.setText(req.getPurpose() != null ? req.getPurpose() : "-");

        // Fetch devices list on background thread
        new Thread(() -> {
            try {
                List<RequestDevice> devices = requestService.getDevicesForRequest(req.getRequestId());
                Platform.runLater(() -> {
                    devicesList.setAll(devices);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Loading Error", "Failed to retrieve registered devices", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) lblEventName.getScene().getWindow();
        stage.close();
    }

    private String formatTime12hr(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return "";
        try {
            java.time.LocalTime time = java.time.LocalTime.parse(timeStr);
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a");
            return time.format(formatter);
        } catch (Exception e) {
            return timeStr;
        }
    }
}
