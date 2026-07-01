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

        // Disable search bar & show loading state to eliminate UI freeze/lag
        studentIdField.setDisable(true);
        statusLabel.setText("STATUS: SEARCHING...");
        statusLabel.setStyle("-fx-text-fill: #3B82F6; -fx-font-weight: bold;");

        new Thread(() -> {
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
                    final String errMsg = "No registered student found with ID: " + studentId;
                    javafx.application.Platform.runLater(() -> {
                        studentIdField.setDisable(false);
                        resetView();
                        statusLabel.setText("STATUS: STUDENT NOT FOUND");
                        AlertHelper.showWarning("Search Result", "Student Not Found", errMsg);
                    });
                    return;
                }

                final Student student = found;

                // Local cache maps to prevent sequential N+1 HTTP calls in loop
                Map<Integer, List<RequestDevice>> reqDevicesCache = new HashMap<>();
                Map<Integer, List<DeviceTransaction>> devTxsCache = new HashMap<>();

                // Fetch requests and check if student has any ongoing requests today or missed checkouts
                LocalDate today = LocalDate.now();
                List<Request> allRequests = requestService.getRequestsByStudentId(student.getStudentId());
                List<Request> approvedRequests = new ArrayList<>();

                for (Request r : allRequests) {
                    if ("approved".equalsIgnoreCase(r.getStatus())) {
                        LocalDate start = LocalDate.parse(r.getStartDate());
                        LocalDate end = LocalDate.parse(r.getEndDate());
                        boolean isActiveToday = !today.isBefore(start) && !today.isAfter(end);

                        boolean hasUnclosedMissedCheckout = false;
                        List<RequestDevice> devices = requestService.getDevicesForRequest(r.getRequestId());
                        reqDevicesCache.put(r.getRequestId(), devices);

                        // Only query transaction history if NOT active today to detect missed checkouts
                        if (!isActiveToday) {
                            for (RequestDevice d : devices) {
                                List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                                devTxsCache.put(d.getRequestDeviceId(), txs);
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
                        } else {
                            // Pre-cache transactions for active requests to speed up calculateExpectedActionType
                            for (RequestDevice d : devices) {
                                List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                                devTxsCache.put(d.getRequestDeviceId(), txs);
                            }
                        }

                        if (isActiveToday || hasUnclosedMissedCheckout) {
                            approvedRequests.add(r);
                        }
                    } else if ("cancelled".equalsIgnoreCase(r.getStatus())) {
                        List<RequestDevice> devices = requestService.getDevicesForRequest(r.getRequestId());
                        reqDevicesCache.put(r.getRequestId(), devices);
                        
                        boolean hasOpen = false;
                        for (RequestDevice d : devices) {
                            List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                            devTxsCache.put(d.getRequestDeviceId(), txs);
                            for (DeviceTransaction tx : txs) {
                                if (tx.getEgressTime() == null) {
                                    hasOpen = true;
                                    break;
                                }
                            }
                            if (hasOpen) break;
                        }
                        
                        if (hasOpen) {
                            approvedRequests.add(r);
                        }
                    }
                }

                // Pre-calculate action type for each request using cached data, filtering out fully completed ones
                Map<Integer, String> calculatedActions = new HashMap<>();
                List<Request> filteredRequests = new ArrayList<>();
                for (Request req : approvedRequests) {
                    String actionType = calculateExpectedActionTypeCached(req, reqDevicesCache, devTxsCache);
                    if (!"Completed".equalsIgnoreCase(actionType)) {
                        calculatedActions.put(req.getRequestId(), actionType);
                        filteredRequests.add(req);
                    }
                }

                javafx.application.Platform.runLater(() -> {
                    studentIdField.setDisable(false);
                    currentStudent = student;
                    studentSnLabel.setText(student.getStudentId());
                    studentNameLabel.setText(student.getFullName());
                    studentCourseLabel.setText(student.getCourseYearLevel());
                    studentCard.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-width: 1px; -fx-border-radius: 12px; -fx-background-radius: 12px; -fx-padding: 16px;");

                    if (filteredRequests.isEmpty()) {
                        requestsList.clear();
                        requestActionTypeMap.clear();
                        statusLabel.setText("STATUS: NO ACTIVE OR MISSED REQUESTS");
                        statusLabel.setStyle("-fx-text-fill: #E11D48; -fx-font-weight: bold;");
                        AlertHelper.showWarning("Search Result", "No Active/Missed Requests", "This student has no ongoing or missed checkout requests.");
                        return;
                    }

                    requestActionTypeMap.clear();
                    requestActionTypeMap.putAll(calculatedActions);
                    statusLabel.setText("STATUS: STUDENT FOUND");
                    statusLabel.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                    requestsList.setAll(filteredRequests);
                    requestsTable.refresh();
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    studentIdField.setDisable(false);
                    resetView();
                    statusLabel.setText("STATUS: SEARCH ERROR");
                    AlertHelper.showError("Search Error", "Search Failed", e.getMessage());
                });
            }
        }).start();
    }

    private String calculateExpectedActionTypeCached(Request req, Map<Integer, List<RequestDevice>> reqDevicesCache, Map<Integer, List<DeviceTransaction>> devTxsCache) {
        try {
            LocalDate today = LocalDate.now();
            List<RequestDevice> devices = reqDevicesCache.get(req.getRequestId());
            if (devices == null) {
                devices = requestService.getDevicesForRequest(req.getRequestId());
            }
            
            // 1. Check if there are any unclosed missed checkouts
            boolean hasUnclosedMissed = false;
            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = devTxsCache.get(d.getRequestDeviceId());
                if (txs == null) {
                    txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                }
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
            
            // 1.5 Determine if any device has check-in today
            boolean hasAnyCheckInToday = false;
            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = devTxsCache.get(d.getRequestDeviceId());
                if (txs == null) {
                    txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                }
                for (DeviceTransaction tx : txs) {
                    if (isTransactionToday(tx)) {
                        hasAnyCheckInToday = true;
                        break;
                    }
                }
                if (hasAnyCheckInToday) break;
            }

            // 2. Otherwise calculate regular entry/exit action
            boolean anyInside = false;
            boolean anyOutside = false;
            
            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = devTxsCache.get(d.getRequestDeviceId());
                if (txs == null) {
                    txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                }
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

            if (hasAnyCheckInToday) {
                if (anyInside) {
                    return "Exit: " + formatTime12hr(req.getExpectedEgressTime());
                } else {
                    return "Completed";
                }
            } else {
                if (anyOutside) {
                    if (isExpired) {
                        return "Expired";
                    }
                    return "Entry: " + formatTime12hr(req.getExpectedIngressTime());
                } else {
                    return "Completed";
                }
            }
        } catch (Exception e) {
            return "Entry"; // fallback
        }
    }

    private String calculateExpectedActionType(Request req) {
        return calculateExpectedActionTypeCached(req, new HashMap<>(), new HashMap<>());
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

            // Determine if any device has check-in today
            boolean hasAnyCheckInToday = false;
            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                for (DeviceTransaction tx : txs) {
                    if (isTransactionToday(tx)) {
                        hasAnyCheckInToday = true;
                        break;
                    }
                }
                if (hasAnyCheckInToday) break;
            }

            if (hasAnyCheckInToday) {
                expectedAction = "Exit";
            }

            List<RequestDevice> filteredDevices = new ArrayList<>();
            List<String> statuses = new ArrayList<>();

            for (RequestDevice d : devices) {
                List<DeviceTransaction> txs = logService.getDeviceTransactions(d.getRequestDeviceId());
                boolean isInside = false;
                boolean isCompletedToday = false;
                boolean hasTxToday = false;

                for (DeviceTransaction tx : txs) {
                    if (isTransactionToday(tx)) {
                        hasTxToday = true;
                    }
                    if (tx.getEgressTime() == null) {
                        isInside = true;
                    } else if (isTransactionToday(tx)) {
                        isCompletedToday = true;
                    }
                }

                String status = isInside ? "Inside" : (isCompletedToday ? "Completed" : "Outside");

                if (hasAnyCheckInToday) {
                    // Only show devices that were logged in today
                    if (hasTxToday) {
                        filteredDevices.add(d);
                        statuses.add(status);
                    }
                } else {
                    // Show all devices
                    filteredDevices.add(d);
                    statuses.add(status);
                }
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/pup/byod/javafxbyodclient/fxml/DevicesModalScreen.fxml"));
            javafx.scene.Parent root = loader.load();
            
            DevicesModalScreenController modalController = loader.getController();
            
            modalController.initData(req, expectedAction, currentStudent, filteredDevices, statuses, () -> {
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
