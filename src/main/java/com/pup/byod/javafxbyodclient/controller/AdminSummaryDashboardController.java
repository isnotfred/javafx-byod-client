package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.DeviceCampusStatus;
import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.model.Request;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.service.RequestService;
import com.pup.byod.javafxbyodclient.service.LogService;
import com.pup.byod.javafxbyodclient.service.ReportService;
import com.pup.byod.javafxbyodclient.util.NavigationManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AdminSummaryDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label activeStudentsLabel;
    @FXML private Label registeredDevicesLabel;
    @FXML private Label pendingApprovalsLabel;
    @FXML private Label pendingApprovalsBadge;
    @FXML private Label devicesOnCampusLabel;

    private final StudentService studentService = new StudentService();
    private final RequestService requestService = new RequestService();
    private final LogService logService = new LogService();
    private final ReportService reportService = new ReportService();

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
        pendingApprovalsLabel.setText("...");
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

            String approvedRequestsStr = "Error";
            try {
                List<Request> requests = requestService.getAllRequests();
                LocalDate today = LocalDate.now();
                long approved = requests.stream()
                        .filter(r -> "approved".equalsIgnoreCase(r.getStatus()))
                        .filter(r -> {
                            try {
                                LocalDate start = LocalDate.parse(r.getStartDate());
                                LocalDate end = LocalDate.parse(r.getEndDate());
                                return !today.isBefore(start) && !today.isAfter(end);
                            } catch (Exception ex) {
                                return false;
                            }
                        })
                        .count();
                approvedRequestsStr = String.valueOf(approved);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String missedCheckoutsStr = "Error";
            try {
                // Fetch missed checkouts from a generic past date up to today
                List<Map<String, Object>> missed = reportService.getMissedCheckoutReport("2020-01-01", LocalDate.now().toString());
                missedCheckoutsStr = String.valueOf(missed.size());
            } catch (Exception e) {
                e.printStackTrace();
            }

            String devicesOnCampusStr = "Error";
            try {
                List<DeviceCampusStatus> statuses = requestService.getCampusStatus();
                long devicesOnCampus = statuses.stream().filter(s -> "entry".equalsIgnoreCase(s.getCampusStatus())).count();
                devicesOnCampusStr = String.valueOf(devicesOnCampus);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String fActiveStudents = activeStudentsStr;
            final String fApprovedRequests = approvedRequestsStr;
            final String fMissedCheckouts = missedCheckoutsStr;
            final String fDevicesOnCampus = devicesOnCampusStr;

            Platform.runLater(() -> {
                activeStudentsLabel.setText(fActiveStudents);
                registeredDevicesLabel.setText(fApprovedRequests);
                pendingApprovalsLabel.setText(fMissedCheckouts);
                devicesOnCampusLabel.setText(fDevicesOnCampus);
                if (pendingApprovalsBadge != null) {
                    pendingApprovalsBadge.setText(fMissedCheckouts);
                    if ("0".equals(fMissedCheckouts) || "Error".equals(fMissedCheckouts)) {
                        pendingApprovalsBadge.setVisible(false);
                    } else {
                        pendingApprovalsBadge.setVisible(true);
                    }
                }
            });
        }).start();
    }



    @FXML
    public void goToStudents() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "StudentsScreen.fxml");
        syncSidebarSelection("Students");
    }

    @FXML
    public void goToRequests() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "RequestsScreen.fxml");
        syncSidebarSelection("Requests");
    }

    @FXML
    public void goToOnCampusDevices() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "OnCampusDevicesScreen.fxml");
        syncSidebarSelection("On-Campus");
    }

    @FXML
    public void goToReports() {
        NavigationManager.getInstance().loadViewIntoContainer(
            NavigationManager.getInstance().getContentArea(), "ReportsScreen.fxml");
        syncSidebarSelection("Reports");
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
