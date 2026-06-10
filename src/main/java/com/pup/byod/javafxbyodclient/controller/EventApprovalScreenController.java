package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.EventRequest;
import com.pup.byod.javafxbyodclient.model.EventRequestDevice;
import com.pup.byod.javafxbyodclient.service.EventRequestService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventApprovalScreenController {
    // Left side
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterBox;
    @FXML private TableView<EventRequest> eventsTable;
    @FXML private TableColumn<EventRequest, Integer> colEventId;
    @FXML private TableColumn<EventRequest, String> colStudentId;
    @FXML private TableColumn<EventRequest, String> colEventName;
    @FXML private TableColumn<EventRequest, String> colEventStatus;

    // Right side - form (read-only details)
    @FXML private TextField formStudentIdField;
    @FXML private TextField responsiblePersonField;
    @FXML private TextField contactField;
    @FXML private TextField eventNameField;
    @FXML private TextField organizationField;
    @FXML private TextField purposeField;
    @FXML private TextField startDateField;
    @FXML private TextField endDateField;
    @FXML private TextField docTypeField;
    @FXML private TextField docRefField;

    // Right side - devices table
    @FXML private TableView<EventRequestDevice> itemsTable;
    @FXML private TableColumn<EventRequestDevice, Integer> colItemId;
    @FXML private TableColumn<EventRequestDevice, String> colItemName;
    @FXML private TableColumn<EventRequestDevice, String> colSerialNumber;
    @FXML private TableColumn<EventRequestDevice, String> colType;
    @FXML private TableColumn<EventRequestDevice, String> colStatus;

    // Remarks and buttons
    @FXML private TextArea remarksArea;
    @FXML private Button approveBtn;
    @FXML private Button rejectBtn;
    @FXML private Button returnBtn;

    private final EventRequestService eventRequestService = new EventRequestService();
    private final ObservableList<EventRequest> eventsList = FXCollections.observableArrayList();
    private final ObservableList<EventRequestDevice> deviceList = FXCollections.observableArrayList();
    private List<EventRequest> allRequests = new ArrayList<>();

    @FXML
    public void initialize() {
        // Table Columns Config
        colEventId.setCellValueFactory(new PropertyValueFactory<>("eventRequestId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        colEventStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        eventsTable.setItems(eventsList);

        colItemId.setCellValueFactory(new PropertyValueFactory<>("eventDeviceId"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("deviceType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("deviceStatus"));
        itemsTable.setItems(deviceList);

        // Status filter values
        statusFilterBox.getItems().addAll("All", "Pending", "Approved", "Returned", "Rejected");
        statusFilterBox.setValue("All");

        // Listeners
        eventsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showEventRequestDetails(newVal);
            } else {
                clearDetailsPane();
            }
        });

        // Load initially
        loadEventRequests();
    }

    @FXML
    public void loadEventRequests() {
        try {
            allRequests = eventRequestService.getAllEventRequests();
            applyFilters();
        } catch (Exception e) {
            AlertHelper.showError("Load Error", "Failed to load event requests", e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        applyFilters();
    }

    private void applyFilters() {
        String query = searchField.getText();
        String statusFilter = statusFilterBox.getValue();

        List<EventRequest> filtered = allRequests.stream()
            .filter(r -> {
                // Search query
                if (ValidationHelper.isEmpty(query)) {
                    return true;
                }
                String lower = query.toLowerCase();
                return (r.getStudentId() != null && r.getStudentId().toLowerCase().contains(lower)) ||
                       (r.getEventName() != null && r.getEventName().toLowerCase().contains(lower)) ||
                       (r.getEventRequestId() != null && String.valueOf(r.getEventRequestId()).contains(query));
            })
            .filter(r -> {
                // Status Filter
                if (statusFilter == null || "All".equalsIgnoreCase(statusFilter)) {
                    return true;
                }
                return statusFilter.equalsIgnoreCase(r.getStatus());
            })
            .collect(Collectors.toList());

        eventsList.setAll(filtered);
    }

    private void showEventRequestDetails(EventRequest request) {
        formStudentIdField.setText(request.getStudentId());
        responsiblePersonField.setText(request.getResponsiblePerson());
        eventNameField.setText(request.getEventName());
        organizationField.setText(request.getOrganization());
        purposeField.setText(request.getEventPurpose());
        startDateField.setText(request.getStartDate());
        endDateField.setText(request.getEndDate());
        docTypeField.setText(request.getApprovalDocType());
        docRefField.setText(request.getApprovalDocRef());

        // Parse contact info from remarks
        String remarks = request.getRemarks();
        if (remarks != null && remarks.startsWith("Contact: ")) {
            int endIdx = remarks.indexOf("\n");
            if (endIdx == -1) {
                contactField.setText(remarks.substring(9).trim());
            } else {
                contactField.setText(remarks.substring(9, endIdx).trim());
            }
            // If there's more after Contact, set to remarks area, otherwise clear it
            if (endIdx != -1 && endIdx + 1 < remarks.length()) {
                remarksArea.setText(remarks.substring(endIdx + 1).trim());
            } else {
                remarksArea.clear();
            }
        } else {
            contactField.clear();
            remarksArea.setText(remarks);
        }

        // Fetch associated devices
        try {
            List<EventRequestDevice> devices = eventRequestService.getEventRequestDevices(request.getEventRequestId());
            deviceList.setAll(devices);
        } catch (Exception e) {
            AlertHelper.showError("Load Error", "Failed to load request devices", e.getMessage());
            deviceList.clear();
        }

        // Enable or disable actions depending on whether it is pending
        boolean isPending = "pending".equalsIgnoreCase(request.getStatus());
        remarksArea.setEditable(isPending);
        approveBtn.setDisable(!isPending);
        rejectBtn.setDisable(!isPending);
        returnBtn.setDisable(!isPending);
    }

    private void clearDetailsPane() {
        formStudentIdField.clear();
        responsiblePersonField.clear();
        contactField.clear();
        eventNameField.clear();
        organizationField.clear();
        purposeField.clear();
        startDateField.clear();
        endDateField.clear();
        docTypeField.clear();
        docRefField.clear();
        remarksArea.clear();
        deviceList.clear();

        approveBtn.setDisable(true);
        rejectBtn.setDisable(true);
        returnBtn.setDisable(true);
    }

    @FXML
    public void handleApprove() {
        EventRequest request = eventsTable.getSelectionModel().getSelectedItem();
        if (request == null) return;

        try {
            int reviewerId = SessionManager.getInstance().getCurrentUser().getUserId();
            eventRequestService.approveEventRequest(request.getEventRequestId(), reviewerId);
            AlertHelper.showInfo("Success", "Approved", "Event Request ID " + request.getEventRequestId() + " has been approved.");
            loadEventRequests();
        } catch (Exception e) {
            AlertHelper.showError("Approval Failed", "Error processing approval", e.getMessage());
        }
    }

    @FXML
    public void handleReject() {
        EventRequest request = eventsTable.getSelectionModel().getSelectedItem();
        if (request == null) return;

        String remarks = remarksArea.getText();
        if (ValidationHelper.isEmpty(remarks)) {
            AlertHelper.showWarning("Remarks Required", "Missing Remarks", "Please provide remarks explaining why the request is rejected.");
            return;
        }

        try {
            int reviewerId = SessionManager.getInstance().getCurrentUser().getUserId();
            eventRequestService.rejectEventRequest(request.getEventRequestId(), reviewerId, remarks);
            AlertHelper.showInfo("Success", "Rejected", "Event Request ID " + request.getEventRequestId() + " has been rejected.");
            loadEventRequests();
        } catch (Exception e) {
            AlertHelper.showError("Rejection Failed", "Error processing rejection", e.getMessage());
        }
    }

    @FXML
    public void handleReturn() {
        EventRequest request = eventsTable.getSelectionModel().getSelectedItem();
        if (request == null) return;

        String remarks = remarksArea.getText();
        if (ValidationHelper.isEmpty(remarks)) {
            AlertHelper.showWarning("Remarks Required", "Missing Remarks", "Please provide remarks detailing what needs revision.");
            return;
        }

        try {
            int reviewerId = SessionManager.getInstance().getCurrentUser().getUserId();
            eventRequestService.returnEventRequest(request.getEventRequestId(), reviewerId, remarks);
            AlertHelper.showInfo("Success", "Returned", "Event Request ID " + request.getEventRequestId() + " has been returned to the submitter.");
            loadEventRequests();
        } catch (Exception e) {
            AlertHelper.showError("Action Failed", "Error returning request", e.getMessage());
        }
    }
}
