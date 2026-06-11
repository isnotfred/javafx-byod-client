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
    @FXML private Label welcomeLabel;
    @FXML private Label devicesOnCampusLabel;
    @FXML private Label activeEventsLabel;
    @FXML private Label totalApprovedDevicesLabel;

    private final DeviceService deviceService = new DeviceService();
    private final EventRequestService eventRequestService = new EventRequestService();

    @FXML
    public void initialize() {
        if (com.pup.byod.javafxbyodclient.session.SessionManager.getInstance().getCurrentUser() != null) {
            String fullName = com.pup.byod.javafxbyodclient.session.SessionManager.getInstance().getCurrentUser().getFullName();
            String role = com.pup.byod.javafxbyodclient.session.SessionManager.getInstance().getCurrentUser().getRole();
            welcomeLabel.setText("Welcome back, " + fullName + " (" + role + ")!");
        } else {
            welcomeLabel.setText("Welcome back!");
        }
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
        syncSidebarSelection("Gate Entry/Exit Scan");
    }

    @FXML
    public void goToQuickRegister() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "QuickPendingRegistrationScreen.fxml");
        syncSidebarSelection("Quick Registration");
    }

    @FXML
    public void goToEventScanner() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "TemporaryEventDeviceGuardScreen.fxml");
        syncSidebarSelection("Event Device Verification");
    }

    @FXML
    public void goToActiveDevices() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "ActiveDevicesInsideCampusScreen.fxml");
        syncSidebarSelection("On-Campus Devices");
    }

    private void syncSidebarSelection(String buttonText) {
        try {
            javafx.scene.layout.Pane container = NavigationManager.getInstance().getContentArea();
            if (container != null && container.getScene() != null) {
                for (javafx.scene.Node node : container.getScene().getRoot().lookupAll(".sidebar-btn")) {
                    if (node instanceof javafx.scene.control.ToggleButton) {
                        javafx.scene.control.ToggleButton btn = (javafx.scene.control.ToggleButton) node;
                        if (buttonText.equals(btn.getText())) {
                            btn.setSelected(true);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
