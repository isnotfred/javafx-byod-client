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
    @FXML private TableColumn<Request, String> colReqVenue;
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

        colReqVenue.setCellValueFactory(cellData -> {
            Request req = cellData.getValue();
            return new SimpleStringProperty(req.getVenue() != null ? req.getVenue() : "N/A");
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

        // Highlight Missed Checkout request rows in pastel red via CSS style class
        requestsTable.setRowFactory(tv -> new TableRow<Request>() {
            @Override
            protected void updateItem(Request item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    getStyleClass().remove("missed-row");
                } else {
                    String action = requestActionTypeMap.getOrDefault(item.getRequestId(), "Entry");
                    if ("Missed".equalsIgnoreCase(action)) {
                        if (!getStyleClass().contains("missed-row")) {
                            getStyleClass().add("missed-row");
                        }
                    } else {
                        getStyleClass().remove("missed-row");
                    }
                }
            }
        });

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

            // Fetch requests and check if student has any ongoing requests today or missed checkouts
            LocalDate today = LocalDate.now();
            List<Request> allRequests = requestService.getRequestsByStudentId(found.getStudentId());
            List<Request> approvedRequests = new ArrayList<>();
            for (Request r : allRequests) {
                if ("approved".equalsIgnoreCase(r.getStatus())) {
                    try {
                        LocalDate start = LocalDate.parse(r.getStartDate());
                        LocalDate end = LocalDate.parse(r.getEndDate());
                        boolean isActiveToday = !today.isBefore(start) && !today.isAfter(end);
                        
                        boolean hasUnclosedMissedCheckout = false;
                        List<RequestDevice> devices = requestService.getDevicesForRequest(r.getRequestId());
                        for (RequestDevice d : devices) {
                            List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                            for (DeviceTransaction tx : txs) {
                                if (tx.getEgressTime() == null) {
                                    LocalDate logDate = LocalDate.parse(tx.getLogDate().substring(0, 10));
                                    if (tx.isNoEgressMarked() || logDate.isBefore(today)) {
                                        hasUnclosedMissedCheckout = true;
                                        break;
                                    }
                                }
                            }
                            if (hasUnclosedMissedCheckout) break;
                        }

                        if (isActiveToday || hasUnclosedMissedCheckout) {
                            approvedRequests.add(r);
                        }
                    } catch (Exception e) {
                        approvedRequests.add(r);
                    }
                }
            }

            // Student has been found - update profile details first
            currentStudent = found;
            studentSnLabel.setText(found.getStudentId());
            studentNameLabel.setText(found.getFullName());
            studentCourseLabel.setText(found.getCourseYearLevel());
            studentCard.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 16px;");

            if (approvedRequests.isEmpty()) {
                requestsList.clear();
                requestActionTypeMap.clear();
                statusLabel.setText("STATUS: NO ACTIVE OR MISSED REQUESTS");
                statusLabel.setStyle("-fx-text-fill: #E11D48; -fx-font-weight: bold;");
                AlertHelper.showWarning("Search Result", "No Active/Missed Requests", "This student has no ongoing or missed checkout requests.");
                return;
            }

            // Pre-calculate action type for each request to display on the table
            requestActionTypeMap.clear();
            for (Request req : approvedRequests) {
                String actionType = calculateExpectedActionType(req);
                requestActionTypeMap.put(req.getRequestId(), actionType);
            }

            statusLabel.setText("STATUS: STUDENT FOUND");
            statusLabel.setStyle("-fx-text-fill: #64748B; -fx-font-weight: bold;");

            requestsList.setAll(approvedRequests);
            requestsTable.refresh();

        } catch (Exception e) {
            resetView();
            statusLabel.setText("STATUS: SEARCH ERROR");
            AlertHelper.showError("Search Error", "Search Failed", e.getMessage());
        }
    }

    private String calculateExpectedActionType(Request req) {
        try {
            LocalDate today = LocalDate.now();
            List<RequestDevice> devices = requestService.getDevicesForRequest(req.getRequestId());
            
            // 1. Check if there are any unclosed missed checkouts
            boolean hasUnclosedMissed = false;
            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                for (DeviceTransaction tx : txs) {
                    if (tx.getEgressTime() == null) {
                        LocalDate logDate = LocalDate.parse(tx.getLogDate().substring(0, 10));
                        if (tx.isNoEgressMarked() || logDate.isBefore(today)) {
                            hasUnclosedMissed = true;
                            break;
                        }
                    }
                }
                if (hasUnclosedMissed) break;
            }
            
            if (hasUnclosedMissed) {
                return "Missed";
            }
            
            // 2. Otherwise calculate regular entry/exit action
            boolean anyInside = false;
            boolean anyOutside = false;
            
            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                boolean isInside = false;
                boolean isCompletedToday = false;
                
                for (DeviceTransaction tx : txs) {
                    if (tx.getEgressTime() == null) {
                        isInside = true;
                    } else if (isTransactionToday(tx)) {
                        isCompletedToday = true;
                    }
                }
                
                if (isInside) {
                    anyInside = true;
                } else if (!isCompletedToday) {
                    anyOutside = true;
                }
            }
            
            LocalDate end = null;
            try {
                end = LocalDate.parse(req.getEndDate());
            } catch (Exception ignored) {}
            boolean isExpired = end != null && today.isAfter(end);

            if (anyInside) {
                return "Exit: " + formatTime12hr(req.getExpectedEgressTime());
            } else if (anyOutside) {
                if (isExpired) {
                    return "Expired";
                }
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
            String expectedAction = requestActionTypeMap.getOrDefault(req.getRequestId(), "Entry");
            boolean isNormalExit = "normal".equalsIgnoreCase(req.getRequestType()) && expectedAction != null && expectedAction.startsWith("Exit");

            if (isNormalExit) {
                List<Integer> insideDeviceIds = new ArrayList<>();
                for (RequestDevice d : devices) {
                    List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                    boolean isInside = false;
                    for (DeviceTransaction tx : txs) {
                        if (tx.getEgressTime() == null) {
                            isInside = true;
                            break;
                        }
                    }
                    if (isInside) {
                        insideDeviceIds.add(d.getRequestDeviceId());
                    }
                }

                if (insideDeviceIds.isEmpty()) {
                    AlertHelper.showWarning("Log Exit", "No Devices Inside", "No currently checked-in devices found to egress.");
                    return;
                }

                int guardId = SessionManager.getInstance().getCurrentUser().getUserId();
                logService.processBatchEgress(insideDeviceIds, guardId);
                AlertHelper.showInfo("Gate Check Success", "Egress Logged", "Log Exit processed successfully for all (" + insideDeviceIds.size() + ") devices in entry.");
                handleStudentSearch();
                return;
            }

            List<String> statuses = new ArrayList<>();
            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                boolean isInside = false;
                boolean isCompletedToday = false;
                for (DeviceTransaction tx : txs) {
                    if (tx.getEgressTime() == null) {
                        isInside = true;
                    } else if (isTransactionToday(tx)) {
                        isCompletedToday = true;
                    }
                }
                statuses.add(isInside ? "Inside" : (isCompletedToday ? "Completed" : "Outside"));
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/pup/byod/javafxbyodclient/fxml/DevicesModalScreen.fxml"));
            javafx.scene.Parent root = loader.load();
            
            DevicesModalScreenController modalController = loader.getController();
            
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
