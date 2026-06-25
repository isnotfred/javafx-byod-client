package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.model.Request;
import com.pup.byod.javafxbyodclient.model.RequestDevice;
import com.pup.byod.javafxbyodclient.model.DeviceTransaction;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.service.RequestService;
import com.pup.byod.javafxbyodclient.service.LogService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import com.pup.byod.javafxbyodclient.session.SessionManager;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IngressEgressMonitoringScreenController {
    @FXML private TextField studentIdField;
    @FXML private VBox studentCard;
    @FXML private Label studentSnLabel;
    @FXML private Label studentNameLabel;
    @FXML private Label studentCourseLabel;
    @FXML private Label statusLabel;

    @FXML private TableView<Request> requestsTable;
    @FXML private TableColumn<Request, String> colReqName;
    @FXML private TableColumn<Request, String> colReqActionType;
    @FXML private TableColumn<Request, Void> colReqView;
    @FXML private javafx.scene.layout.Region overlay;

    private final StudentService studentService = new StudentService();
    private final RequestService requestService = new RequestService();
    private final LogService logService = new LogService();

    private final ObservableList<Request> requestsList = FXCollections.observableArrayList();
    private final Map<Integer, String> requestActionTypeMap = new HashMap<>();

    private Student currentStudent = null;

    @FXML
    public void initialize() {
        // Setup ENTER key trigger for search
        studentIdField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleStudentSearch();
            }
        });

        // Setup Requests Table columns
        colReqName.setCellValueFactory(cellData -> {
            Request req = cellData.getValue();
            if ("event".equalsIgnoreCase(req.getRequestType())) {
                return new SimpleStringProperty(req.getEventName());
            } else {
                return new SimpleStringProperty("Academic BYOD");
            }
        });

        colReqActionType.setCellValueFactory(cellData -> {
            Request req = cellData.getValue();
            String actionType = requestActionTypeMap.getOrDefault(req.getRequestId(), "Entry");
            return new SimpleStringProperty(actionType);
        });

        // Setup Programmatic View Button column
        colReqView.setCellFactory(col -> new TableCell<Request, Void>() {
            private final Button btn = new Button("View");

            {
                btn.getStyleClass().addAll("action-btn", "action-btn-primary");
                btn.setStyle("-fx-font-size: 11px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    Request req = getTableView().getItems().get(getIndex());
                    openDevicesModal(req);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        requestsTable.setItems(requestsList);

        // Reset view state
        resetView();
    }

    private void resetView() {
        currentStudent = null;

        studentSnLabel.setText("-");
        studentNameLabel.setText("Student: No Selection");
        studentCourseLabel.setText("-");
        statusLabel.setText("STATUS: READY TO SEARCH");
        statusLabel.setStyle("-fx-text-fill: #64748B; -fx-font-weight: bold;");
        if (studentCard != null) {
            studentCard.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 16px;");
        }

        requestsList.clear();
        requestActionTypeMap.clear();
    }

    @FXML
    public void handleStudentSearch() {
        String studentId = studentIdField.getText();
        if (ValidationHelper.isEmpty(studentId)) {
            AlertHelper.showWarning("Search student", "Input Required", "Please enter a Student ID.");
            return;
        }

        try {
            List<Student> results = studentService.searchStudents(studentId);
            Student found = null;
            for (Student s : results) {
                if (s.getStudentId().equalsIgnoreCase(studentId) && !"inactive".equalsIgnoreCase(s.getStatus())) {
                    found = s;
                    break;
                }
            }

            if (found == null) {
                resetView();
                statusLabel.setText("STATUS: STUDENT NOT FOUND");
                AlertHelper.showWarning("Search Result", "Student Not Found", "No registered student found with ID: " + studentId);
                return;
            }

            // Fetch requests and check if student has any approved requests
            List<Request> allRequests = requestService.getRequestsByStudentId(found.getStudentId());
            List<Request> approvedRequests = new ArrayList<>();
            for (Request r : allRequests) {
                if ("approved".equalsIgnoreCase(r.getStatus())) {
                    String actionType = calculateExpectedActionType(r);
                    if (!"Completed".equalsIgnoreCase(actionType)) {
                        approvedRequests.add(r);
                    }
                }
            }

            if (approvedRequests.isEmpty()) {
                // Do NOT update student details below as per request
                statusLabel.setText("STATUS: NO APPROVED REQUESTS");
                AlertHelper.showWarning("Search Result", "No Approved Requests", "This student has no active or approved requests.");
                return;
            }

            // Pre-calculate action type for each request to display on the table
            requestActionTypeMap.clear();
            for (Request req : approvedRequests) {
                String actionType = calculateExpectedActionType(req);
                requestActionTypeMap.put(req.getRequestId(), actionType);
            }

            // Student has approved requests - update profile and populate table
            currentStudent = found;
            studentSnLabel.setText(found.getStudentId());
            studentNameLabel.setText(found.getFullName());
            studentCourseLabel.setText(found.getCourseYearLevel());
            
            statusLabel.setText("STATUS: STUDENT FOUND");
            statusLabel.setStyle("-fx-text-fill: #64748B; -fx-font-weight: bold;");
            studentCard.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 16px;");

            requestsList.setAll(approvedRequests);

        } catch (Exception e) {
            resetView();
            statusLabel.setText("STATUS: SEARCH ERROR");
            AlertHelper.showError("Search Error", "Search Failed", e.getMessage());
        }
    }

    private String calculateExpectedActionType(Request req) {
        try {
            List<RequestDevice> devices = requestService.getDevicesForRequest(req.getRequestId());
            boolean anyInside = false;
            boolean anyOutside = false;
            
            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                boolean isInsideToday = false;
                boolean isCompletedToday = false;
                
                for (DeviceTransaction tx : txs) {
                    if (isTransactionToday(tx)) {
                        if (tx.getEgressTime() != null) {
                            isCompletedToday = true;
                        } else if (!tx.isNoEgressMarked()) {
                            isInsideToday = true;
                        }
                    }
                }
                
                if (isInsideToday) {
                    anyInside = true;
                } else if (!isCompletedToday) {
                    anyOutside = true;
                }
            }
            
            if (anyInside) {
                return "Exit: " + formatTime12hr(req.getExpectedEgressTime());
            } else if (anyOutside) {
                return "Entry: " + formatTime12hr(req.getExpectedIngressTime());
            } else {
                return "Completed";
            }
        } catch (Exception e) {
            return "Entry"; // fallback
        }
    }

    private boolean isTransactionToday(DeviceTransaction tx) {
        if (tx == null) return false;
        String todayStr = LocalDate.now().toString(); // YYYY-MM-DD
        if (tx.getLogDate() != null && tx.getLogDate().startsWith(todayStr)) {
            return true;
        }
        if (tx.getIngressTime() != null && tx.getIngressTime().startsWith(todayStr)) {
            return true;
        }
        return false;
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

    private void openDevicesModal(Request req) {
        if (currentStudent == null) return;

        try {
            List<RequestDevice> devices = requestService.getDevicesForRequest(req.getRequestId());
            List<String> statuses = new ArrayList<>();
            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                boolean isInside = false;
                boolean isCompleted = false;
                for (DeviceTransaction tx : txs) {
                    if (isTransactionToday(tx)) {
                        if (tx.getEgressTime() != null) {
                            isCompleted = true;
                        } else if (!tx.isNoEgressMarked()) {
                            isInside = true;
                        }
                    }
                }
                statuses.add(isInside ? "Inside" : (isCompleted ? "Completed" : "Outside"));
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/pup/byod/javafxbyodclient/fxml/DevicesModalScreen.fxml"));
            javafx.scene.Parent root = loader.load();
            
            DevicesModalScreenController modalController = loader.getController();
            String expectedAction = requestActionTypeMap.getOrDefault(req.getRequestId(), "Entry");
            
            modalController.initData(req, expectedAction, currentStudent, devices, statuses, () -> {
                // On transaction complete callback: re-fetch and update table values
                handleStudentSearch(); 
            });
            
            // Show overlay
            overlay.setVisible(true);
            overlay.setManaged(true);
            
            Stage stage = new Stage();
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initOwner(requestsTable.getScene().getWindow());
            stage.setTitle("Process Ingress/Egress");
            stage.setScene(new javafx.scene.Scene(root));
            
            // Center stage on owner once shown
            stage.setOnShown(event -> {
                javafx.stage.Window owner = stage.getOwner();
                if (owner != null) {
                    double x = owner.getX() + (owner.getWidth() - stage.getWidth()) / 2.0;
                    double y = owner.getY() + (owner.getHeight() - stage.getHeight()) / 2.0;
                    stage.setX(x);
                    stage.setY(y);
                }
            });

            // Hide overlay when stage is closed
            stage.setOnHidden(event -> {
                overlay.setVisible(false);
                overlay.setManaged(false);
            });
            
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            overlay.setVisible(false);
            overlay.setManaged(false);
            AlertHelper.showError("Modal Error", "Could not open devices modal", e.getMessage());
        }
    }
}
