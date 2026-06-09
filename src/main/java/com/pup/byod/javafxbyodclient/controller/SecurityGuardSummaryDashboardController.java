package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.ActiveEventRequest;
import com.pup.byod.javafxbyodclient.model.DeviceCampusStatus;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import com.pup.byod.javafxbyodclient.service.EventRequestService;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

public class SecurityGuardSummaryDashboardController {
    @FXML private Label devicesOnCampusLabel;
    @FXML private Label activeEventsLabel;
    @FXML private Label totalApprovedDevicesLabel;

    private final DeviceService deviceService = new DeviceService();
    private final EventRequestService eventRequestService = new EventRequestService();

    @FXML
    public void initialize() {
        refreshStats();
    }

    @FXML
    public void refreshStats() {
        devicesOnCampusLabel.setText("...");
        activeEventsLabel.setText("...");
        totalApprovedDevicesLabel.setText("...");

        new Thread(() -> {
            try {
                // Fetch devices campus statuses
                List<DeviceCampusStatus> statuses = deviceService.getDeviceCampusStatus();
                long devicesOnCampus = statuses.stream().filter(s -> "entry".equalsIgnoreCase(s.getCampusStatus())).count();
                long totalApproved = statuses.size();

                // Fetch active event requests
                List<ActiveEventRequest> events = eventRequestService.getActiveEventRequests();
                long activeEvents = events.size();

                Platform.runLater(() -> {
                    devicesOnCampusLabel.setText(String.valueOf(devicesOnCampus));
                    totalApprovedDevicesLabel.setText(String.valueOf(totalApproved));
                    activeEventsLabel.setText(String.valueOf(activeEvents));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    devicesOnCampusLabel.setText("Error");
                    totalApprovedDevicesLabel.setText("Error");
                    activeEventsLabel.setText("Error");
                });
            }
        }).start();
    }

    @FXML
    public void goToGateScan() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "IngressEgressMonitoringScreen.fxml");
    }

    @FXML
    public void goToQuickRegister() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "QuickPendingRegistrationScreen.fxml");
    }

    @FXML
    public void goToEventScanner() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "TemporaryEventDeviceGuardScreen.fxml");
    }

    @FXML
    public void goToActiveDevices() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "ActiveDevicesInsideCampusScreen.fxml");
    }
}
