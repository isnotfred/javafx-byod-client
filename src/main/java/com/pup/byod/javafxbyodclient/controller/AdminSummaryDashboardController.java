package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Device;
import com.pup.byod.javafxbyodclient.model.DeviceCampusStatus;
import com.pup.byod.javafxbyodclient.model.PendingDevice;
import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.DeviceService;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.List;

public class AdminSummaryDashboardController {
    @FXML private Label activeStudentsLabel;
    @FXML private Label registeredDevicesLabel;
    @FXML private Label pendingApprovalsLabel;
    @FXML private Label devicesOnCampusLabel;

    private final StudentService studentService = new StudentService();
    private final DeviceService deviceService = new DeviceService();

    @FXML
    public void initialize() {
        refreshStats();
    }

    @FXML
    public void refreshStats() {
        activeStudentsLabel.setText("...");
        registeredDevicesLabel.setText("...");
        pendingApprovalsLabel.setText("...");
        devicesOnCampusLabel.setText("...");

        new Thread(() -> {
            try {
                // Fetch students count
                List<Student> students = studentService.getAllStudents();
                long activeStudents = students.stream().filter(s -> "active".equalsIgnoreCase(s.getStatus())).count();

                // Fetch devices count
                List<Device> devices = deviceService.getAllDevices();
                long registeredDevices = devices.size();

                // Fetch pending approvals
                List<PendingDevice> pending = deviceService.getPendingDevices();
                long pendingCount = pending.size();

                // Fetch devices currently on campus (where campusStatus = "entry")
                List<DeviceCampusStatus> statuses = deviceService.getDeviceCampusStatus();
                long devicesOnCampus = statuses.stream().filter(s -> "entry".equalsIgnoreCase(s.getCampusStatus())).count();

                Platform.runLater(() -> {
                    activeStudentsLabel.setText(String.valueOf(activeStudents));
                    registeredDevicesLabel.setText(String.valueOf(registeredDevices));
                    pendingApprovalsLabel.setText(String.valueOf(pendingCount));
                    devicesOnCampusLabel.setText(String.valueOf(devicesOnCampus));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    activeStudentsLabel.setText("Error");
                    registeredDevicesLabel.setText("Error");
                    pendingApprovalsLabel.setText("Error");
                    devicesOnCampusLabel.setText("Error");
                });
            }
        }).start();
    }

    @FXML
    public void goToStudents() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "StudentManagementScreen.fxml");
    }

    @FXML
    public void goToDevices() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "DeviceManagementScreen.fxml");
    }

    @FXML
    public void goToApprovals() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "PendingRegistrationApprovalScreen.fxml");
    }

    @FXML
    public void goToReports() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "ReportsScreen.fxml");
    }
}
