package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.EventRequest;
import com.pup.byod.javafxbyodclient.model.EventRequestDevice;
import com.pup.byod.javafxbyodclient.model.ActiveEventRequest;
import com.pup.byod.javafxbyodclient.service.EventRequestService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TemporaryEventDeviceGuardScreenController {
    // Main layouts
    @FXML private VBox adminView;
    @FXML private VBox guardView;
    @FXML private StackPane formOverlay;

    // Admin view
    @FXML private TextField adminSearchField;
    @FXML private TableView<EventRequest> adminEventsTable;
    @FXML private TableColumn<EventRequest, Integer> colAdminEventId;
    @FXML private TableColumn<EventRequest, String> colAdminStudentId;
    @FXML private TableColumn<EventRequest, String> colAdminEventName;
    @FXML private TableColumn<EventRequest, String> colAdminStartDate;
    @FXML private TableColumn<EventRequest, String> colAdminEndDate;
    @FXML private TableColumn<EventRequest, String> colAdminEventStatus;
    @FXML private Button editSelectedBtn;

    // Guard view elements
    @FXML private TextField guardStudentIdField;
    @FXML private TextField guardEventNameField;
    @FXML private TextField guardResponsiblePersonField;
    @FXML private TextField guardOrganizationField;
    @FXML private TextField guardContactField;
    @FXML private ComboBox<String> guardPurposeBox;
    @FXML private DatePicker guardStartDatePicker;
    @FXML private DatePicker guardEndDatePicker;
    @FXML private ComboBox<String> guardDocTypeBox;
    @FXML private TextField guardDocRefField;

    @FXML private TableView<EventDeviceSelection> guardItemsTable;
    @FXML private TableColumn<EventDeviceSelection, Boolean> colGuardSelect;
    @FXML private TableColumn<EventDeviceSelection, Integer> colGuardItemId;
    @FXML private TableColumn<EventDeviceSelection, String> colGuardItemName;
    @FXML private TableColumn<EventDeviceSelection, String> colGuardSerialNumber;
    @FXML private TableColumn<EventDeviceSelection, String> colGuardType;
    @FXML private TableColumn<EventDeviceSelection, String> colGuardStatus;
    @FXML private TableColumn<EventDeviceSelection, String> colGuardCurrentDayStatus;

    // Left Section: Event Logs Table (Guards)
    @FXML private TextField searchField;
    @FXML private TableView<EventRequest> eventsTable;
    @FXML private TableColumn<EventRequest, String> colStudentId;
    @FXML private TableColumn<EventRequest, String> colEventName;

    // Right Section: Event Details Form (Admins Overlay)
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

    // Right Section: Devices Table (Admins Overlay)
    @FXML private TableView<EventDeviceSelection> itemsTable;
    @FXML private TableColumn<EventDeviceSelection, Boolean> colSelect;
    @FXML private TableColumn<EventDeviceSelection, Integer> colItemId;
    @FXML private TableColumn<EventDeviceSelection, String> colItemName;
    @FXML private TableColumn<EventDeviceSelection, String> colSerialNumber;
    @FXML private TableColumn<EventDeviceSelection, String> colType;
    @FXML private TableColumn<EventDeviceSelection, String> colCurrentDayStatus;

    // Right Section: Add Temporary Device inputs (Admins Overlay)
    @FXML private TextField deviceNameField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField serialNumberField;
    @FXML private ComboBox<String> deviceTypeBox;

    // Actions
    @FXML private VBox addDeviceCard;
    @FXML private Button clearFormBtn;
    @FXML private Button verifyBtn;
    @FXML private Button submitBtn;
    @FXML private Button logIngressBtn;
    @FXML private Button logEgressBtn;
    @FXML private Button ingressEgressBtn;
    @FXML private StackPane guardModalOverlay;

    private boolean isEditMode = false;

    private static class DraftEventRequest {
        String studentId = "";
        String eventName = "";
        String org = "";
        String responsible = "";
        String contact = "";
        String purpose = null;
        String docRef = "";
        String docType = null;
        LocalDate start = null;
        LocalDate end = null;
        final List<EventRequestDevice> devices = new ArrayList<>();
        int tempCounter = 1;
        boolean hasDraft = false;

        void clear() {
            studentId = "";
            eventName = "";
            org = "";
            responsible = "";
            contact = "";
            purpose = null;
            docRef = "";
            docType = null;
            start = null;
            end = null;
            devices.clear();
            tempCounter = 1;
            hasDraft = false;
        }
    }

    private static final DraftEventRequest draft = new DraftEventRequest();
    private int tempDeviceIdCounter = 1;

    private final EventRequestService eventRequestService = new EventRequestService();
    private final ObservableList<EventRequest> eventsList = FXCollections.observableArrayList();
    private final ObservableList<EventDeviceSelection> deviceList = FXCollections.observableArrayList();
    private List<EventRequest> allRequests = new ArrayList<>();

    @FXML
    public void initialize() {
        // Configure admin events table
        colAdminEventId.setCellValueFactory(new PropertyValueFactory<>("eventRequestId"));
        colAdminStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colAdminEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        colAdminStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colAdminEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colAdminEventStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        adminEventsTable.setItems(eventsList);

        // Configure guard events table
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        eventsTable.setItems(eventsList);

        // Configure admin devices table in overlay
        colSelect.setVisible(false); // Admin doesn't need select checkboxes
        colItemId.setCellValueFactory(new PropertyValueFactory<>("eventDeviceId"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("deviceType"));
        colCurrentDayStatus.setCellValueFactory(new PropertyValueFactory<>("currentDayStatus"));
        itemsTable.setItems(deviceList);

        // Configure guard devices table
        colGuardSelect.setCellValueFactory(f -> f.getValue().selectedProperty());
        colGuardSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colGuardSelect));
        colGuardItemId.setCellValueFactory(new PropertyValueFactory<>("eventDeviceId"));
        colGuardItemName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colGuardSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colGuardType.setCellValueFactory(new PropertyValueFactory<>("deviceType"));
        colGuardStatus.setCellValueFactory(new PropertyValueFactory<>("deviceStatus"));
        colGuardCurrentDayStatus.setCellValueFactory(new PropertyValueFactory<>("currentDayStatus"));
        guardItemsTable.setItems(deviceList);
        guardItemsTable.setEditable(true);

        // Populate dropdowns
        purposeBox.getItems().addAll(
            "Academic BYOD",
            "School Event",
            "Organization Activity",
            "Temporary Equipment",
            "Other"
        );
        docTypeBox.getItems().addAll("Signed GPOA", "Paper Approval");
        deviceTypeBox.getItems().addAll(
            "Personal Computers",
            "Components & Peripherals",
            "Display & Projection",
            "Project Prototypes (Optional SN)",
            "Appliances (TLE)",
            "Other"
        );

        guardPurposeBox.getItems().addAll(purposeBox.getItems());
        guardDocTypeBox.getItems().addAll(docTypeBox.getItems());

        // Handle role visibility
        boolean isGuard = false;
        if (SessionManager.getInstance().getCurrentUser() != null) {
            String role = SessionManager.getInstance().getCurrentUser().getRole();
            if ("guard".equalsIgnoreCase(role)) {
                isGuard = true;
            }
        }

        adminView.setVisible(!isGuard);
        adminView.setManaged(!isGuard);
        guardView.setVisible(isGuard);
        guardView.setManaged(isGuard);
        formOverlay.setVisible(false);

        // Selection listeners & double click for admin table
        adminEventsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editSelectedBtn.setDisable(newVal == null);
        });

        adminEventsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && adminEventsTable.getSelectionModel().getSelectedItem() != null) {
                openEditOverlay();
            }
        });

        // Load draft if exists (for Admin view)
        if (draft.hasDraft) {
            formStudentIdField.setText(draft.studentId);
            eventNameField.setText(draft.eventName);
            organizationField.setText(draft.org);
            responsiblePersonField.setText(draft.responsible);
            contactField.setText(draft.contact);
            purposeBox.setValue(draft.purpose);
            docRefField.setText(draft.docRef);
            docTypeBox.setValue(draft.docType);
            startDatePicker.setValue(draft.start);
            endDatePicker.setValue(draft.end);
            
            List<EventDeviceSelection> wrapped = draft.devices.stream()
                .map(EventDeviceSelection::new)
                .collect(Collectors.toList());
            deviceList.setAll(wrapped);
            tempDeviceIdCounter = draft.tempCounter;
        } else {
            tempDeviceIdCounter = 1;
        }

        // Setup real-time input listeners to update draft
        setupDraftListeners();

        // Event Logs Selection Listener for Guard table
        eventsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showEventRequestDetails(newVal);
            }
        });

        // Load logs initially
        loadEventLogs();

        // Setup autocomplete, dynamic search filtering, and prompt helpers
        com.pup.byod.javafxbyodclient.util.StudentSearchDropdown.attach(formStudentIdField, null);

        adminSearchField.textProperty().addListener((obs, oldVal, newVal) -> handleAdminSearch());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> handleSearchLogs());

        adminSearchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleAdminSearch();
            }
        });
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleSearchLogs();
            }
        });

        // Setup PromptTextHelpers
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(adminSearchField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(guardStudentIdField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(guardEventNameField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(guardResponsiblePersonField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(guardOrganizationField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(guardContactField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(guardDocRefField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(searchField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(formStudentIdField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(responsiblePersonField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(contactField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(eventNameField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(organizationField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(docRefField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(deviceNameField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(brandField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(modelField);
        com.pup.byod.javafxbyodclient.util.PromptTextHelper.setup(serialNumberField);
    }

    private void setupDraftListeners() {
        formStudentIdField.textProperty().addListener((obs, oldVal, newVal) -> {
            draft.studentId = newVal;
            draft.hasDraft = true;
        });
        eventNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            draft.eventName = newVal;
            draft.hasDraft = true;
        });
        organizationField.textProperty().addListener((obs, oldVal, newVal) -> {
            draft.org = newVal;
            draft.hasDraft = true;
        });
        responsiblePersonField.textProperty().addListener((obs, oldVal, newVal) -> {
            draft.responsible = newVal;
            draft.hasDraft = true;
        });
        contactField.textProperty().addListener((obs, oldVal, newVal) -> {
            draft.contact = newVal;
            draft.hasDraft = true;
        });
        purposeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            draft.purpose = newVal;
            draft.hasDraft = true;
        });
        docRefField.textProperty().addListener((obs, oldVal, newVal) -> {
            draft.docRef = newVal;
            draft.hasDraft = true;
        });
        docTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            draft.docType = newVal;
            draft.hasDraft = true;
        });
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            draft.start = newVal;
            draft.hasDraft = true;
        });
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            draft.end = newVal;
            draft.hasDraft = true;
        });
    }

    @FXML
    public void loadEventLogs() {
        try {
            boolean isGuard = false;
            if (SessionManager.getInstance().getCurrentUser() != null) {
                String role = SessionManager.getInstance().getCurrentUser().getRole();
                if ("guard".equalsIgnoreCase(role)) {
                    isGuard = true;
                }
            }

            if (isGuard) {
                List<ActiveEventRequest> active = eventRequestService.getGuardEventRequests();
                List<EventRequest> mapped = new ArrayList<>();
                for (ActiveEventRequest act : active) {
                    EventRequest req = new EventRequest();
                    req.setEventRequestId(act.getEventRequestId());
                    req.setStudentId(act.getStudentId());
                    req.setEventName(act.getEventName());
                    req.setOrganization(act.getOrganization());
                    req.setStartDate(act.getStartDate());
                    req.setEndDate(act.getEndDate());
                    req.setStatus(act.getStatus());
                    mapped.add(req);
                }
                allRequests = mapped;
            } else {
                allRequests = eventRequestService.getAllEventRequests();
            }
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
        EventRequest fullRequest = request;
        try {
            fullRequest = eventRequestService.getEventRequestById(request.getEventRequestId());
        } catch (Exception e) {
            // fallback if API request fails
        }

        boolean isGuard = false;
        if (SessionManager.getInstance().getCurrentUser() != null) {
            String role = SessionManager.getInstance().getCurrentUser().getRole();
            if ("guard".equalsIgnoreCase(role)) {
                isGuard = true;
            }
        }

        if (isGuard) {
            guardStudentIdField.setText(fullRequest.getStudentId());
            guardResponsiblePersonField.setText(fullRequest.getResponsiblePerson());
            guardEventNameField.setText(fullRequest.getEventName());
            guardOrganizationField.setText(fullRequest.getOrganization());
            guardPurposeBox.setValue(fullRequest.getEventPurpose());
            guardDocTypeBox.setValue(fullRequest.getApprovalDocType());
            guardDocRefField.setText(fullRequest.getApprovalDocRef());

            String remarks = fullRequest.getRemarks();
            if (remarks != null && remarks.startsWith("Contact: ")) {
                int endIdx = remarks.indexOf("\n");
                if (endIdx == -1) {
                    guardContactField.setText(remarks.substring(9).trim());
                } else {
                    guardContactField.setText(remarks.substring(9, endIdx).trim());
                }
            } else {
                guardContactField.clear();
            }

            try {
                if (fullRequest.getStartDate() != null) {
                    guardStartDatePicker.setValue(LocalDate.parse(fullRequest.getStartDate()));
                } else {
                    guardStartDatePicker.setValue(null);
                }
                if (fullRequest.getEndDate() != null) {
                    guardEndDatePicker.setValue(LocalDate.parse(fullRequest.getEndDate()));
                } else {
                    guardEndDatePicker.setValue(null);
                }
            } catch (DateTimeParseException e) {
                guardStartDatePicker.setValue(null);
                guardEndDatePicker.setValue(null);
            }
        } else {
            formStudentIdField.setText(fullRequest.getStudentId());
            responsiblePersonField.setText(fullRequest.getResponsiblePerson());
            eventNameField.setText(fullRequest.getEventName());
            organizationField.setText(fullRequest.getOrganization());
            purposeBox.setValue(fullRequest.getEventPurpose());
            docTypeBox.setValue(fullRequest.getApprovalDocType());
            docRefField.setText(fullRequest.getApprovalDocRef());

            String remarks = fullRequest.getRemarks();
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

            try {
                if (fullRequest.getStartDate() != null) {
                    startDatePicker.setValue(LocalDate.parse(fullRequest.getStartDate()));
                } else {
                    startDatePicker.setValue(null);
                }
                if (fullRequest.getEndDate() != null) {
                    endDatePicker.setValue(LocalDate.parse(fullRequest.getEndDate()));
                } else {
                    endDatePicker.setValue(null);
                }
            } catch (DateTimeParseException e) {
                startDatePicker.setValue(null);
                endDatePicker.setValue(null);
            }
        }

        // Fetch associated devices
        try {
            List<EventRequestDevice> devices = eventRequestService.getEventRequestDevices(request.getEventRequestId());
            deviceList.clear();
            for (EventRequestDevice d : devices) {
                EventDeviceSelection sel = new EventDeviceSelection(d);
                sel.selectedProperty().addListener((obs, oldVal, newVal) -> updateGuardActionButtonsState());
                deviceList.add(sel);
            }
            if (ingressEgressBtn != null) {
                ingressEgressBtn.setDisable(devices.isEmpty());
            }
        } catch (Exception e) {
            AlertHelper.showError("Load Error", "Failed to load devices", e.getMessage());
            deviceList.clear();
            if (ingressEgressBtn != null) {
                ingressEgressBtn.setDisable(true);
            }
        }

        updateGuardActionButtonsState();

        if (!isGuard) {
            setInputFieldsDisabled(true);
            boolean isApproved = "approved".equalsIgnoreCase(request.getStatus());
            verifyBtn.setDisable(!isApproved);
        }
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

    private void setEventDetailsFieldsDisabled(boolean disabled) {
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
    }

    @FXML
    public void handleAddItem() {
        if (formStudentIdField.isDisable() && !isEditMode) {
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
        if (isEditMode) {
            item.setEventDeviceId(0);
        } else {
            item.setEventDeviceId(tempDeviceIdCounter++);
        }
        item.setDeviceName(name);
        item.setBrand(brand);
        item.setModel(model);
        item.setSerialNumber(sn);
        item.setDeviceType(type);
        item.setQuantity(1);
        item.setDeviceStatus("pending"); // Default local status

        EventDeviceSelection selection = new EventDeviceSelection(item);
        deviceList.add(selection);
        draft.devices.add(item);
        draft.tempCounter = tempDeviceIdCounter;
        draft.hasDraft = true;

        // Clear local inputs
        deviceNameField.clear();
        brandField.clear();
        modelField.clear();
        serialNumberField.clear();
        deviceTypeBox.setValue(null);
    }

    @FXML
    public void handleRemoveItem() {
        if (formStudentIdField.isDisable() && !isEditMode) {
            AlertHelper.showWarning("Action Restricted", "Read-only mode", "Cannot remove devices from an already submitted event request.");
            return;
        }

        EventDeviceSelection selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Item Warning", "No selection", "Please select a device from the table to remove.");
            return;
        }
        deviceList.remove(selected);
        draft.devices.remove(selected.getDevice());
        draft.hasDraft = true;
    }

    @FXML
    public void handleClearForm() {
        eventsTable.getSelectionModel().clearSelection();
        if (adminEventsTable != null) {
            adminEventsTable.getSelectionModel().clearSelection();
        }

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

        // Clear draft
        draft.clear();
        tempDeviceIdCounter = 1;

        // Enable inputs
        setInputFieldsDisabled(false);

        verifyBtn.setDisable(true);
        if (ingressEgressBtn != null) {
            ingressEgressBtn.setDisable(true);
        }
    }

    @FXML
    public void handleVerify() {
        EventDeviceSelection selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Verification", "No Selection", "Please select a device to verify.");
            return;
        }

        if ("returned".equalsIgnoreCase(selected.getDeviceStatus())) {
            AlertHelper.showInfo("Verification", "Already Reconciled", "This device has already been reconciled.");
            return;
        }

        try {
            int guardId = SessionManager.getInstance().getCurrentUser().getUserId();
            eventRequestService.verifyEventDevice(selected.getEventDeviceId(), guardId, "returned");
            AlertHelper.showInfo("Success", "Device Reconciled", "Device " + selected.getDeviceName() + " has been marked returned/reconciled.");

            // Reload details to refresh statuses
            EventRequest request = adminEventsTable.getSelectionModel().getSelectedItem();
            if (request != null) {
                showEventRequestDetails(request);
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Reconciliation Failed", e.getMessage());
        }
    }

    @FXML
    public void handleSubmitRequest() {
        if (formStudentIdField.isDisable() && !isEditMode) {
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

        // 7-day max date range validation — prevent submission
        if (start.isAfter(end)) {
            AlertHelper.showWarning("Date Validation", "Invalid Date Range", "Start date must be before or equal to end date.");
            return;
        }
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start, end);
        if (daysBetween > 7) {
            AlertHelper.showWarning("Date Validation", "Duration Exceeds Limit", "Event request date range cannot exceed 7 days. Current range: " + daysBetween + " days.");
            return;
        }

        EventRequest request;
        if (isEditMode) {
            request = adminEventsTable.getSelectionModel().getSelectedItem();
            if (request == null) {
                AlertHelper.showWarning("Update Request", "No Selection", "Please select a request to update.");
                return;
            }
        } else {
            request = new EventRequest();
            request.setStudentId(studentId);
            request.setIsSubmitted(true);
            request.setIsAccommodated(true);
            request.setStatus("approved"); // Approved immediately on creation
        }

        request.setResponsiblePerson(responsible);
        request.setOrganization(org);
        request.setEventName(eventName);
        request.setEventPurpose(purpose);
        request.setApprovalDocRef(docRef);
        request.setApprovalDocType(docType);
        request.setStartDate(start.toString());
        request.setEndDate(end.toString());

        // Prefix contact info in remarks column
        if (!ValidationHelper.isEmpty(contact)) {
            request.setRemarks("Contact: " + contact);
        } else {
            request.setRemarks("");
        }

        List<EventRequestDevice> items = deviceList.stream().map(EventDeviceSelection::getDevice).collect(Collectors.toList());
        request.setLineItems(items);

        if (SessionManager.getInstance().getCurrentUser() != null) {
            request.setCreatorUserId(SessionManager.getInstance().getCurrentUser().getUserId());
        }

        String confirmTitle = isEditMode ? "Save Changes" : "Submit Request";
        String confirmHeader = isEditMode ? "Confirm Saving Changes" : "Confirm Submission";
        String confirmMsg = isEditMode ? "Are you sure you want to save the changes to this event request?" : "Are you sure you want to submit this temporary event registration request?";

        if (!AlertHelper.showConfirmation(confirmTitle, confirmHeader, confirmMsg)) {
            return;
        }

        try {
            if (isEditMode) {
                eventRequestService.updateEventRequest(request.getEventRequestId(), request);
                AlertHelper.showInfo("Success", "Updated", "Event request updated successfully.");
            } else {
                eventRequestService.createEventRequest(request);
                AlertHelper.showInfo("Success", "Submitted", "Event request submitted successfully.");
            }
            loadEventLogs();
            handleCloseOverlay();
        } catch (Exception e) {
            AlertHelper.showError("Submit Error", "Action Failed", e.getMessage());
        }
    }

    @FXML
    public void handleConfirmEntry() {
        List<Integer> selectedDeviceIds = new ArrayList<>();
        for (EventDeviceSelection sel : deviceList) {
            if (sel.isSelected()) {
                selectedDeviceIds.add(sel.getEventDeviceId());
            }
        }

        if (selectedDeviceIds.isEmpty()) {
            AlertHelper.showWarning("Entry Logging", "No Selection", "Please select at least one device to confirm entry.");
            return;
        }

        try {
            int guardId = SessionManager.getInstance().getCurrentUser().getUserId();
            eventRequestService.logDeviceEntry(selectedDeviceIds, guardId);
            AlertHelper.showInfo("Success", "Ingress Logged", "Successfully logged ingress for selected devices.");
            
            // Deselect all devices after logging
            for (EventDeviceSelection sel : deviceList) {
                sel.setSelected(false);
            }
            
            // Refresh table details to get the new status
            EventRequest selectedRequest = eventsTable.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                showEventRequestDetails(selectedRequest);
            }
        } catch (Exception e) {
            AlertHelper.showError("Entry Logging Error", "Action Failed", e.getMessage());
        }
    }

    @FXML
    public void handleConfirmExit() {
        List<Integer> selectedDeviceIds = new ArrayList<>();
        for (EventDeviceSelection sel : deviceList) {
            if (sel.isSelected()) {
                selectedDeviceIds.add(sel.getEventDeviceId());
            }
        }

        if (selectedDeviceIds.isEmpty()) {
            AlertHelper.showWarning("Egress Logging", "No Selection", "Please select at least one device to confirm exit.");
            return;
        }

        try {
            int guardId = SessionManager.getInstance().getCurrentUser().getUserId();
            eventRequestService.logDeviceExit(selectedDeviceIds, guardId);
            AlertHelper.showInfo("Success", "Egress Logged", "Successfully logged egress for selected devices.");
            
            // Deselect all devices after logging
            for (EventDeviceSelection sel : deviceList) {
                sel.setSelected(false);
            }
            
            // Refresh table details to get the new status
            EventRequest selectedRequest = eventsTable.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                showEventRequestDetails(selectedRequest);
            }
        } catch (Exception e) {
            AlertHelper.showError("Egress Logging Error", "Action Failed", e.getMessage());
        }
    }

    @FXML
    public void openAddOverlay() {
        handleClearForm();
        isEditMode = false;
        setInputFieldsDisabled(false);
        
        // Hide verify button when creating a new event
        verifyBtn.setVisible(false);
        verifyBtn.setManaged(false);
        submitBtn.setVisible(true);
        submitBtn.setManaged(true);
        submitBtn.setText("Submit Event Registration");
        
        clearFormBtn.setVisible(true);
        clearFormBtn.setManaged(true);
        
        addDeviceCard.setVisible(true);
        addDeviceCard.setManaged(true);
        
        formOverlay.setVisible(true);
    }

    @FXML
    public void openEditOverlay() {
        EventRequest selected = adminEventsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("View/Reconcile Request", "No Selection", "Please select an event request to view/reconcile.");
            return;
        }
        isEditMode = false;
        
        // Populate overlay details
        showEventRequestDetails(selected);
        
        // Show verify button if it can be reconciled, hide editing options
        verifyBtn.setVisible(true);
        verifyBtn.setManaged(true);
        
        submitBtn.setVisible(false);
        submitBtn.setManaged(false);
        
        clearFormBtn.setVisible(false);
        clearFormBtn.setManaged(false);
        
        addDeviceCard.setVisible(false);
        addDeviceCard.setManaged(false);

        formOverlay.setVisible(true);
    }

    @FXML
    public void handleCloseOverlay() {
        formOverlay.setVisible(false);
        handleClearForm();
    }

    @FXML
    public void handleOpenGuardModal() {
        guardModalOverlay.setVisible(true);
        updateGuardActionButtonsState();
    }

    @FXML
    public void handleCloseGuardModal() {
        guardModalOverlay.setVisible(false);
    }

    private void updateGuardActionButtonsState() {
        if (logIngressBtn == null || logEgressBtn == null) {
            return;
        }

        boolean hasSelectedEntry = false;
        boolean hasSelectedExit = false;
        boolean anySelected = false;

        for (EventDeviceSelection sel : deviceList) {
            if (sel.isSelected()) {
                anySelected = true;
                String status = sel.getCurrentDayStatus();
                if ("entry".equalsIgnoreCase(status)) {
                    hasSelectedEntry = true;
                } else if ("exit".equalsIgnoreCase(status)) {
                    hasSelectedExit = true;
                }
            }
        }

        if (!anySelected) {
            logIngressBtn.setDisable(true);
            logEgressBtn.setDisable(true);
        } else if (hasSelectedEntry && hasSelectedExit) {
            logIngressBtn.setDisable(true);
            logEgressBtn.setDisable(true);
        } else if (hasSelectedExit && !hasSelectedEntry) {
            logIngressBtn.setDisable(false);
            logEgressBtn.setDisable(true);
        } else if (hasSelectedEntry && !hasSelectedExit) {
            logIngressBtn.setDisable(true);
            logEgressBtn.setDisable(false);
        } else {
            logIngressBtn.setDisable(true);
            logEgressBtn.setDisable(true);
        }
    }

    @FXML
    public void handleAdminSearch() {
        String query = adminSearchField.getText();
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

    // Helper Wrapper class for Event Request Device selection with Checkboxes
    public static class EventDeviceSelection {
        private final BooleanProperty selected = new SimpleBooleanProperty(true);
        private final EventRequestDevice device;

        public EventDeviceSelection(EventRequestDevice device) {
            this.device = device;
        }

        public BooleanProperty selectedProperty() { return selected; }
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean val) { selected.set(val); }

        public EventRequestDevice getDevice() { return device; }

        public Integer getEventDeviceId() { return device.getEventDeviceId(); }
        public String getDeviceName() { return device.getDeviceName(); }
        public String getBrand() { return device.getBrand(); }
        public String getModel() { return device.getModel(); }
        public String getSerialNumber() { return device.getSerialNumber(); }
        public String getDeviceType() { return device.getDeviceType(); }
        public String getDeviceStatus() { return device.getDeviceStatus(); }
        public String getCurrentDayStatus() { return device.getCurrentDayStatus(); }
        public String getLastEventTime() { return device.getLastEventTime(); }
    }
}
