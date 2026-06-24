package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Device;
import com.pup.byod.javafxbyodclient.model.DeviceCampusStatus;
import com.pup.byod.javafxbyodclient.model.ActiveEventRequest;
import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.service.EventRequestService;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import java.util.List;

public class AdminSummaryDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label activeStudentsLabel;
    @FXML private Label registeredDevicesLabel;
    @FXML private Label activeEventsLabel;
    @FXML private Label activeEventsBadge;
    @FXML private Label devicesOnCampusLabel;

    private final StudentService studentService = new StudentService();
    private final DeviceService deviceService = new DeviceService();
    private final EventRequestService eventRequestService = new EventRequestService();

    @FXML
    public void initialize() {
        if (com.pup.byod.javafxbyodclient.session.SessionManager.getInstance().getCurrentUser() != null) {
            String fullName = com.pup.byod.javafxbyodclient.session.SessionManager.getInstance().getCurrentUser().getFullName();
            welcomeLabel.setText("Welcome back, " + fullName + "!");
        } else {
            welcomeLabel.setText("Welcome back!");
        }
        refreshStats();
    }

    @FXML
    public void refreshStats() {
        activeStudentsLabel.setText("...");
        registeredDevicesLabel.setText("...");
        activeEventsLabel.setText("...");
        devicesOnCampusLabel.setText("...");

        new Thread(() -> {
            String activeStudentsStr = "Error";
            try {
                List<Student> students = studentService.getAllStudents();
                long activeStudents = students.stream().filter(s -> "active".equalsIgnoreCase(s.getStatus())).count();
                activeStudentsStr = String.valueOf(activeStudents);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String registeredDevicesStr = "Error";
            try {
                List<Device> devices = deviceService.getAllDevices();
                long registeredDevices = devices.size();
                registeredDevicesStr = String.valueOf(registeredDevices);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String activeEventsCountStr = "Error";
            try {
                List<ActiveEventRequest> activeEvents = eventRequestService.getActiveEventRequests();
                long activeEventsCount = activeEvents.size();
                activeEventsCountStr = String.valueOf(activeEventsCount);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String devicesOnCampusStr = "Error";
            try {
                List<DeviceCampusStatus> statuses = deviceService.getDeviceCampusStatus();
                long devicesOnCampus = statuses.stream().filter(s -> "entry".equalsIgnoreCase(s.getCampusStatus())).count();
                devicesOnCampusStr = String.valueOf(devicesOnCampus);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String fActiveStudents = activeStudentsStr;
            final String fRegisteredDevices = registeredDevicesStr;
            final String fActiveEventsCount = activeEventsCountStr;
            final String fDevicesOnCampus = devicesOnCampusStr;

            Platform.runLater(() -> {
                activeStudentsLabel.setText(fActiveStudents);
                registeredDevicesLabel.setText(fRegisteredDevices);
                activeEventsLabel.setText(fActiveEventsCount);
                devicesOnCampusLabel.setText(fDevicesOnCampus);
                if (activeEventsBadge != null) {
                    activeEventsBadge.setText(fActiveEventsCount);
                    if ("0".equals(fActiveEventsCount) || "Error".equals(fActiveEventsCount)) {
                        activeEventsBadge.setVisible(false);
                    } else {
                        activeEventsBadge.setVisible(true);
                    }
                }
            });
        }).start();
    }

    @FXML
    public void goToStudents() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "RegistryManagementScreen.fxml");
        syncSidebarSelection("Registry Management");
    }

    @FXML
    public void goToDevices() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "TemporaryEventDeviceGuardScreen.fxml");
        syncSidebarSelection("Event Requests");
    }

    @FXML
    public void goToIngressEgress() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "IngressEgressMonitoringScreen.fxml");
        syncSidebarSelection("Gate Ingress/Egress");
    }

    @FXML
    public void goToReports() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "ReportsScreen.fxml");
        syncSidebarSelection("Analytical Reports");
    }

    private void syncSidebarSelection(String buttonText) {
        try {
            javafx.scene.layout.Pane container = NavigationManager.getInstance().getContentArea();
            if (container != null && container.getScene() != null) {
                javafx.scene.Scene scene = container.getScene();
                for (javafx.scene.Node node : scene.getRoot().lookupAll(".sidebar-btn")) {
                    if (node instanceof ToggleButton) {
                        ToggleButton tb = (ToggleButton) node;
                        if (buttonText.equals(tb.getText())) {
                            tb.setSelected(true);
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
