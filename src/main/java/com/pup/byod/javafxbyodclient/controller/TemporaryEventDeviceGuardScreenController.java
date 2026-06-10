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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TemporaryEventDeviceGuardScreenController {
    // Left Section: Event Logs Table
    @FXML private TextField searchField;
    @FXML private TableView<EventRequest> eventsTable;
    @FXML private TableColumn<EventRequest, Integer> colEventId;
    @FXML private TableColumn<EventRequest, String> colStudentId;
    @FXML private TableColumn<EventRequest, String> colEventName;
    @FXML private TableColumn<EventRequest, String> colEventStatus;

    // Right Section: Event Details Form
    @FXML private TextField formStudentIdField;
    @FXML private TextField responsiblePersonField;
    @FXML private TextField contactField;
    @FXML private TextField eventNameField;
    @FXML private TextField organizationField;
    @FXML private ComboBox<String> purposeBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> docTypeBox;
    @FXML private TextField docRefField;

    // Right Section: Devices Table
    @FXML private TableView<EventRequestDevice> itemsTable;
    @FXML private TableColumn<EventRequestDevice, Integer> colItemId;
    @FXML private TableColumn<EventRequestDevice, String> colItemName;
    @FXML private TableColumn<EventRequestDevice, String> colSerialNumber;
    @FXML private TableColumn<EventRequestDevice, String> colType;
    @FXML private TableColumn<EventRequestDevice, String> colStatus;

    // Right Section: Add Temporary Device inputs
    @FXML private TextField deviceNameField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField serialNumberField;
    @FXML private ComboBox<String> deviceTypeBox;

    // Actions
    @FXML private Button verifyBtn;

    private final EventRequestService eventRequestService = new EventRequestService();
    private final ObservableList<EventRequest> eventsList = FXCollections.observableArrayList();
    private final ObservableList<EventRequestDevice> deviceList = FXCollections.observableArrayList();
    private List<EventRequest> allRequests = new ArrayList<>();

    @FXML
    public void initialize() {
        // Configure event logs table
        colEventId.setCellValueFactory(new PropertyValueFactory<>("eventRequestId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        colEventStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        eventsTable.setItems(eventsList);

        // Configure devices table
        colItemId.setCellValueFactory(new PropertyValueFactory<>("eventDeviceId"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("deviceType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("deviceStatus"));
        itemsTable.setItems(deviceList);

        // Populate dropdowns
        purposeBox.getItems().addAll(
            "Academic BYOD",
            "School Event",
            "Organization Activity",
            "Temporary Equipment",
            "Other"
        );
        docTypeBox.getItems().addAll("Signed GPOA", "Paper Approval", "Other");
        deviceTypeBox.getItems().addAll("laptop", "tablet", "phone", "camera", "projector", "other");

        // Event Logs Selection Listener
        eventsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showEventRequestDetails(newVal);
            }
        });

        // Load logs initially
        loadEventLogs();
    }

    @FXML
    public void loadEventLogs() {
        try {
            allRequests = eventRequestService.getAllEventRequests();
            eventsList.setAll(allRequests);
        } catch (Exception e) {
            AlertHelper.showError("Load Error", "Failed to retrieve event logs", e.getMessage());
        }
    }

    @FXML
    public void handleSearchLogs() {
        String query = searchField.getText();
        if (ValidationHelper.isEmpty(query)) {
            eventsList.setAll(allRequests);
            return;
        }

        List<EventRequest> filtered = allRequests.stream()
            .filter(r -> (r.getStudentId() != null && r.getStudentId().toLowerCase().contains(query.toLowerCase())) ||
                         (r.getEventName() != null && r.getEventName().toLowerCase().contains(query.toLowerCase())) ||
                         (r.getEventRequestId() != null && String.valueOf(r.getEventRequestId()).contains(query)))
            .collect(Collectors.toList());

        eventsList.setAll(filtered);
    }

    private void showEventRequestDetails(EventRequest request) {
        // Populate form details
        formStudentIdField.setText(request.getStudentId());
        responsiblePersonField.setText(request.getResponsiblePerson());
        eventNameField.setText(request.getEventName());
        organizationField.setText(request.getOrganization());
        purposeBox.setValue(request.getEventPurpose());
        docTypeBox.setValue(request.getApprovalDocType());
        docRefField.setText(request.getApprovalDocRef());

        // Parse contact details from remarks
        String remarks = request.getRemarks();
        if (remarks != null && remarks.startsWith("Contact: ")) {
            int endIdx = remarks.indexOf("\n");
            if (endIdx == -1) {
                contactField.setText(remarks.substring(9).trim());
            } else {
                contactField.setText(remarks.substring(9, endIdx).trim());
            }
        } else {
            contactField.clear();
        }

        // Set dates
        try {
            if (request.getStartDate() != null) {
                startDatePicker.setValue(LocalDate.parse(request.getStartDate()));
            } else {
                startDatePicker.setValue(null);
            }
            if (request.getEndDate() != null) {
                endDatePicker.setValue(LocalDate.parse(request.getEndDate()));
            } else {
                endDatePicker.setValue(null);
            }
        } catch (DateTimeParseException e) {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        }

        // Fetch associated devices
        try {
            List<EventRequestDevice> devices = eventRequestService.getEventRequestDevices(request.getEventRequestId());
            deviceList.setAll(devices);
        } catch (Exception e) {
            AlertHelper.showError("Load Error", "Failed to load devices", e.getMessage());
            deviceList.clear();
        }

        // Disable input fields in read-only view
        setInputFieldsDisabled(true);

        // Configure verification action
        boolean isApproved = "approved".equalsIgnoreCase(request.getStatus());
        verifyBtn.setDisable(!isApproved);
    }

    private void setInputFieldsDisabled(boolean disabled) {
        formStudentIdField.setDisable(disabled);
        responsiblePersonField.setDisable(disabled);
        contactField.setDisable(disabled);
        eventNameField.setDisable(disabled);
        organizationField.setDisable(disabled);
        purposeBox.setDisable(disabled);
        startDatePicker.setDisable(disabled);
        endDatePicker.setDisable(disabled);
        docTypeBox.setDisable(disabled);
        docRefField.setDisable(disabled);

        deviceNameField.setDisable(disabled);
        brandField.setDisable(disabled);
        modelField.setDisable(disabled);
        serialNumberField.setDisable(disabled);
        deviceTypeBox.setDisable(disabled);
    }

    @FXML
    public void handleAddItem() {
        if (formStudentIdField.isDisable()) {
            AlertHelper.showWarning("Action Restricted", "Read-only mode", "Clear the form to start a new event request.");
            return;
        }

        String name = deviceNameField.getText();
        String brand = brandField.getText();
        String model = modelField.getText();
        String sn = serialNumberField.getText();
        String type = deviceTypeBox.getValue();

        if (ValidationHelper.isEmpty(name) || type == null) {
            AlertHelper.showWarning("Item Warning", "Missing inputs", "Please enter at least a device name and select a device type.");
            return;
        }

        EventRequestDevice item = new EventRequestDevice();
        item.setDeviceName(name);
        item.setBrand(brand);
        item.setModel(model);
        item.setSerialNumber(sn);
        item.setDeviceType(type);
        item.setQuantity(1);
        item.setDeviceStatus("pending"); // Default local status

        deviceList.add(item);

        // Clear local inputs
        deviceNameField.clear();
        brandField.clear();
        modelField.clear();
        serialNumberField.clear();
        deviceTypeBox.setValue(null);
    }

    @FXML
    public void handleRemoveItem() {
        if (formStudentIdField.isDisable()) {
            AlertHelper.showWarning("Action Restricted", "Read-only mode", "Cannot remove devices from an already submitted event request.");
            return;
        }

        EventRequestDevice selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Item Warning", "No selection", "Please select a device from the table to remove.");
            return;
        }
        deviceList.remove(selected);
    }

    @FXML
    public void handleClearForm() {
        eventsTable.getSelectionModel().clearSelection();

        // Clear all fields
        formStudentIdField.clear();
        responsiblePersonField.clear();
        contactField.clear();
        eventNameField.clear();
        organizationField.clear();
        purposeBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        docTypeBox.setValue(null);
        docRefField.clear();

        // Clear device inputs
        deviceNameField.clear();
        brandField.clear();
        modelField.clear();
        serialNumberField.clear();
        deviceTypeBox.setValue(null);

        // Clear lists
        deviceList.clear();

        // Enable inputs
        setInputFieldsDisabled(false);

        verifyBtn.setDisable(true);
    }

    @FXML
    public void handleVerify() {
        EventRequestDevice selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Verification", "No Selection", "Please select a device to verify.");
            return;
        }

        if ("verified".equalsIgnoreCase(selected.getDeviceStatus()) || "approved".equalsIgnoreCase(selected.getDeviceStatus())) {
            AlertHelper.showInfo("Verification", "Already Verified", "This device has already been verified.");
            return;
        }

        try {
            int guardId = SessionManager.getInstance().getCurrentUser().getUserId();
            eventRequestService.verifyEventDevice(selected.getEventDeviceId(), guardId, "verified");
            AlertHelper.showInfo("Success", "Device Verified", "Device " + selected.getDeviceName() + " has been marked verified.");

            // Reload details to refresh statuses
            EventRequest request = eventsTable.getSelectionModel().getSelectedItem();
            if (request != null) {
                showEventRequestDetails(request);
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Verification Failed", e.getMessage());
        }
    }

    @FXML
    public void handleSubmitRequest() {
        if (formStudentIdField.isDisable()) {
            AlertHelper.showWarning("Action Restricted", "Read-only mode", "Form is in view-only mode. Click Clear Form to start a new registration.");
            return;
        }

        String studentId = formStudentIdField.getText();
        String eventName = eventNameField.getText();
        String org = organizationField.getText();
        String responsible = responsiblePersonField.getText();
        String contact = contactField.getText();
        String purpose = purposeBox.getValue();
        String docRef = docRefField.getText();
        String docType = docTypeBox.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (ValidationHelper.isEmpty(studentId) || ValidationHelper.isEmpty(eventName) ||
            start == null || end == null || deviceList.isEmpty()) {
            AlertHelper.showWarning("Form Validation", "Incomplete fields", "Please fill in student ID, event name, start/end dates, and add at least one device.");
            return;
        }

        EventRequest request = new EventRequest();
        request.setStudentId(studentId);
        request.setEventName(eventName);
        request.setOrganization(org);
        request.setResponsiblePerson(responsible);
        request.setEventPurpose(purpose);
        request.setApprovalDocRef(docRef);
        request.setApprovalDocType(docType);
        request.setStartDate(start.toString());
        request.setEndDate(end.toString());
        request.setIsSubmitted(true);
        request.setIsAccommodated(true);
        request.setStatus("pending");

        // Prefix contact info in remarks column
        if (!ValidationHelper.isEmpty(contact)) {
            request.setRemarks("Contact: " + contact);
        } else {
            request.setRemarks("");
        }

        request.setLineItems(new ArrayList<>(deviceList));

        try {
            eventRequestService.createEventRequest(request);
            AlertHelper.showInfo("Success", "Submitted", "Event request submitted successfully.");
            loadEventLogs();
            handleClearForm();
        } catch (Exception e) {
            AlertHelper.showError("Submit Error", "Submission Failed", e.getMessage());
        }
    }
}
