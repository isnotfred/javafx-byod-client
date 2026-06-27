package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Request;
import com.pup.byod.javafxbyodclient.model.RequestDevice;
import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.RequestService;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.model.enums.DeviceType;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RequestsScreenController {
    // Main components
    @FXML private TextField searchField;
    @FXML private TableView<Request> requestsTable;
    @FXML private Button btnEditRequest;

    private boolean isEditMode = false;
    private int editingRequestId = -1;
    private boolean isUpdatingFields = false;
    @FXML private TableColumn<Request, String> colStudentId;
    @FXML private TableColumn<Request, String> colReqType;
    @FXML private TableColumn<Request, String> colPurpose;
    @FXML private TableColumn<Request, String> colDates;
    @FXML private TableColumn<Request, String> colTimes;
    @FXML private TableColumn<Request, Void> colAction;

    // Overlays
    @FXML private StackPane newRequestOverlay;
    @FXML private StackPane newEventRequestOverlay;
    @FXML private StackPane reviewOverlay;

    // Standard Request fields
    @FXML private TextField reqStudentIdField;
    @FXML private TextField reqPurposeField;
    @FXML private TextField reqVenueField;
    @FXML private DatePicker reqStartDatePicker;
    @FXML private DatePicker reqEndDatePicker;
    @FXML private ComboBox<String> reqIngressHour;
    @FXML private ComboBox<String> reqIngressMinute;
    @FXML private ComboBox<String> reqIngressAmpm;
    @FXML private ComboBox<String> reqEgressHour;
    @FXML private ComboBox<String> reqEgressMinute;
    @FXML private ComboBox<String> reqEgressAmpm;
    @FXML private TextField reqRemarksField;
    // Standard Device Inline Entry
    @FXML private TextField devNameField;
    @FXML private TextField devBrandField;
    @FXML private TextField devModelField;
    @FXML private TextField devSerialField;
    @FXML private TextField devQtyField;
    @FXML private ComboBox<String> devTypeBox;
    @FXML private Label lblStandardRequestTitle;
    @FXML private Button btnSaveRequestNormal;
    @FXML private Button btnAddDeviceNormal;
    @FXML private TableView<RequestDevice> stagedNormalTable;
    @FXML private TableColumn<RequestDevice, String> colStgName;
    @FXML private TableColumn<RequestDevice, String> colStgBrand;
    @FXML private TableColumn<RequestDevice, String> colStgModel;
    @FXML private TableColumn<RequestDevice, String> colStgSerial;
    @FXML private TableColumn<RequestDevice, Integer> colStgQty;
    @FXML private TableColumn<RequestDevice, String> colStgType;
    @FXML private TableColumn<RequestDevice, Void> colStgAction;

    @FXML private Button btnCancelDeviceNormal;
    @FXML private Button btnCancelDeviceEvent;

    private RequestDevice editingStagedDeviceNormal = null;
    private RequestDevice editingStagedDeviceEvent = null;

    // Event Request fields
    @FXML private TextField evtStudentIdField;
    @FXML private TextField evtEventNameField;
    @FXML private TextField evtVenueField;
    @FXML private TextField evtOrgField;
    @FXML private TextField evtRespPersonField;
    @FXML private TextField evtPurposeField;
    @FXML private DatePicker evtStartDatePicker;
    @FXML private DatePicker evtEndDatePicker;
    @FXML private ComboBox<String> evtIngressHour;
    @FXML private ComboBox<String> evtIngressMinute;
    @FXML private ComboBox<String> evtIngressAmpm;
    @FXML private ComboBox<String> evtEgressHour;
    @FXML private ComboBox<String> evtEgressMinute;
    @FXML private ComboBox<String> evtEgressAmpm;
    @FXML private TextField evtRemarksField;
    // Event Device Inline Entry
    @FXML private TextField evtDevNameField;
    @FXML private TextField evtDevBrandField;
    @FXML private TextField evtDevModelField;
    @FXML private TextField evtDevSerialField;
    @FXML private TextField evtDevQtyField;
    @FXML private ComboBox<String> evtDevTypeBox;
    @FXML private Label lblEventRequestTitle;
    @FXML private Button btnSaveRequestEvent;
    @FXML private Button btnAddDeviceEvent;
    @FXML private TableView<RequestDevice> stagedEventTable;
    @FXML private TableColumn<RequestDevice, String> colEvtStgName;
    @FXML private TableColumn<RequestDevice, String> colEvtStgBrand;
    @FXML private TableColumn<RequestDevice, String> colEvtStgModel;
    @FXML private TableColumn<RequestDevice, String> colEvtStgSerial;
    @FXML private TableColumn<RequestDevice, Integer> colEvtStgQty;
    @FXML private TableColumn<RequestDevice, String> colEvtStgType;
    @FXML private TableColumn<RequestDevice, Void> colEvtStgAction;

    // Review Modal Fields
    @FXML private Label reviewTitleLabel;
    @FXML private Label lblReviewStudentId;
    @FXML private Label lblReviewType;
    @FXML private Label lblReviewPurpose;
    @FXML private Label lblReviewTimes;
    @FXML private TableView<RequestDevice> reviewDevicesTable;
    @FXML private TableColumn<RequestDevice, String> colRevName;
    @FXML private TableColumn<RequestDevice, String> colRevBrand;
    @FXML private TableColumn<RequestDevice, String> colRevModel;
    @FXML private TableColumn<RequestDevice, String> colRevSerial;
    @FXML private TableColumn<RequestDevice, Integer> colRevQty;
    @FXML private Button btnApprove;
    @FXML private Button btnReject;
    @FXML private Button btnReturn;

    private final RequestService requestService = new RequestService();
    private final StudentService studentService = new StudentService();

    private final ObservableList<Request> requestList = FXCollections.observableArrayList();
    private FilteredList<Request> filteredRequestList;

    private final ObservableList<RequestDevice> normalStagedList = FXCollections.observableArrayList();
    private final ObservableList<RequestDevice> eventStagedList = FXCollections.observableArrayList();
    private final ObservableList<RequestDevice> reviewDevicesList = FXCollections.observableArrayList();

    private Request selectedRequest = null;

    @FXML
    public void initialize() {
        // Main Requests Table Columns
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colReqType.setCellValueFactory(new PropertyValueFactory<>("requestType"));
        colPurpose.setCellValueFactory(cellData -> {
            Request r = cellData.getValue();
            if ("EVENT".equalsIgnoreCase(r.getRequestType())) {
                return new SimpleStringProperty(r.getEventName());
            } else {
                return new SimpleStringProperty(r.getPurpose());
            }
        });
        colDates.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getStartDate() + " to " + cellData.getValue().getEndDate()
        ));
        colTimes.setCellValueFactory(cellData -> new SimpleStringProperty(
                formatTime12hr(cellData.getValue().getExpectedIngressTime()) + " - " + formatTime12hr(cellData.getValue().getExpectedEgressTime())
        ));

        // Setup row View button programmatically
        colAction.setCellFactory(col -> new TableCell<Request, Void>() {
            private final Button btn = new Button("View");
            {
                btn.getStyleClass().addAll("action-btn", "action-btn-primary");
                btn.setStyle("-fx-font-size: 11px; -fx-padding: 4px 12px; -fx-cursor: hand;");
                btn.setOnAction(event -> {
                    Request req = getTableView().getItems().get(getIndex());
                    openReviewOverlay(req);
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

        // Search Bar Filtering
        filteredRequestList = new FilteredList<>(requestList, p -> true);
        requestsTable.setItems(filteredRequestList);
        btnEditRequest.disableProperty().bind(requestsTable.getSelectionModel().selectedItemProperty().isNull());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredRequestList.setPredicate(req -> {
                if (newVal == null || newVal.trim().isEmpty()) {
                    return true;
                }
                String lower = newVal.toLowerCase().trim();
                return req.getStudentId().toLowerCase().contains(lower);
            });
        });

        // Initialize Combobox device types
        List<String> devTypes = new java.util.ArrayList<>();
        for (DeviceType type : DeviceType.values()) {
            devTypes.add(type.getDisplayName());
        }
        devTypeBox.getItems().addAll(devTypes);
        evtDevTypeBox.getItems().addAll(devTypes);

        // Populate time pickers
        setupTimeComboBoxes(reqIngressHour, reqIngressMinute, reqIngressAmpm);
        setupTimeComboBoxes(reqEgressHour, reqEgressMinute, reqEgressAmpm);
        setupTimeComboBoxes(evtIngressHour, evtIngressMinute, evtIngressAmpm);
        setupTimeComboBoxes(evtEgressHour, evtEgressMinute, evtEgressAmpm);

        // Date validation: Start date must be today or future (excluding Sundays & Holidays)
        reqStartDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean disable = empty || date.isBefore(LocalDate.now()) || isSundayOrHoliday(date);
                setDisable(disable);
                if (!empty && isSundayOrHoliday(date)) {
                    setStyle("-fx-background-color: #ffe4e6; -fx-text-fill: #991b1b;");
                }
            }
        });
        evtStartDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean disable = empty || date.isBefore(LocalDate.now()) || isSundayOrHoliday(date);
                setDisable(disable);
                if (!empty && isSundayOrHoliday(date)) {
                    setStyle("-fx-background-color: #ffe4e6; -fx-text-fill: #991b1b;");
                }
            }
        });

        // Initialize End Date pickers factories
        reqEndDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean disable = empty || date.isBefore(LocalDate.now()) || isSundayOrHoliday(date);
                setDisable(disable);
                if (!empty && isSundayOrHoliday(date)) {
                    setStyle("-fx-background-color: #ffe4e6; -fx-text-fill: #991b1b;");
                }
            }
        });
        evtEndDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean disable = empty || date.isBefore(LocalDate.now()) || isSundayOrHoliday(date);
                setDisable(disable);
                if (!empty && isSundayOrHoliday(date)) {
                    setStyle("-fx-background-color: #ffe4e6; -fx-text-fill: #991b1b;");
                }
            }
        });

        // Dynamic End Date constraints based on Start Date selection
        reqStartDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            reqEndDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate minDate = (newVal != null) ? newVal : LocalDate.now();
                    boolean disable = empty || date.isBefore(minDate) || isSundayOrHoliday(date);
                    setDisable(disable);
                    if (!empty && isSundayOrHoliday(date)) {
                        setStyle("-fx-background-color: #ffe4e6; -fx-text-fill: #991b1b;");
                    }
                }
            });
            if (reqEndDatePicker.getValue() != null && newVal != null && reqEndDatePicker.getValue().isBefore(newVal)) {
                reqEndDatePicker.setValue(null);
            }
        });

        evtStartDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            evtEndDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate minDate = (newVal != null) ? newVal : LocalDate.now();
                    boolean disable = empty || date.isBefore(minDate) || isSundayOrHoliday(date);
                    setDisable(disable);
                    if (!empty && isSundayOrHoliday(date)) {
                        setStyle("-fx-background-color: #ffe4e6; -fx-text-fill: #991b1b;");
                    }
                }
            });
            if (evtEndDatePicker.getValue() != null && newVal != null && evtEndDatePicker.getValue().isBefore(newVal)) {
                evtEndDatePicker.setValue(null);
            }
        });

        // Bind Staging tables columns
        colStgName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colStgBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colStgModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colStgSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colStgQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStgType.setCellValueFactory(new PropertyValueFactory<>("deviceType"));
        colStgAction.setCellFactory(col -> new TableCell<RequestDevice, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Remove");
            private final javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(5.0, editBtn, deleteBtn);
            {
                container.setAlignment(javafx.geometry.Pos.CENTER);
                editBtn.getStyleClass().addAll("action-btn", "action-btn-primary");
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3px 8px; -fx-cursor: hand;");
                editBtn.setOnAction(event -> {
                    RequestDevice d = getTableView().getItems().get(getIndex());
                    editingStagedDeviceNormal = d;
                    devNameField.setText(d.getDeviceName());
                    devBrandField.setText(d.getBrand() != null ? d.getBrand() : "");
                    devModelField.setText(d.getModel() != null ? d.getModel() : "");
                    devSerialField.setText(d.getSerialNumber());
                    devQtyField.setText(String.valueOf(d.getQuantity()));
                    devTypeBox.setValue(d.getDeviceType());
                    
                    btnAddDeviceNormal.setText("Update Device");
                    if (btnCancelDeviceNormal != null) {
                        btnCancelDeviceNormal.setVisible(true);
                        btnCancelDeviceNormal.setManaged(true);
                    }
                });

                deleteBtn.getStyleClass().addAll("action-btn", "action-btn-danger");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3px 8px; -fx-cursor: hand;");
                deleteBtn.setOnAction(event -> {
                    RequestDevice d = getTableView().getItems().get(getIndex());
                    normalStagedList.remove(d);
                    if (editingStagedDeviceNormal == d) {
                        editingStagedDeviceNormal = null;
                        clearStagedDeviceNormalForm();
                        btnAddDeviceNormal.setText("+ Add Device To Staging");
                        if (btnCancelDeviceNormal != null) {
                            btnCancelDeviceNormal.setVisible(false);
                            btnCancelDeviceNormal.setManaged(false);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });
        stagedNormalTable.setItems(normalStagedList);

        colEvtStgName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colEvtStgBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colEvtStgModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colEvtStgSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colEvtStgQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colEvtStgType.setCellValueFactory(new PropertyValueFactory<>("deviceType"));
        colEvtStgAction.setCellFactory(col -> new TableCell<RequestDevice, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Remove");
            private final javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(5.0, editBtn, deleteBtn);
            {
                container.setAlignment(javafx.geometry.Pos.CENTER);
                editBtn.getStyleClass().addAll("action-btn", "action-btn-primary");
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3px 8px; -fx-cursor: hand;");
                editBtn.setOnAction(event -> {
                    RequestDevice d = getTableView().getItems().get(getIndex());
                    editingStagedDeviceEvent = d;
                    evtDevNameField.setText(d.getDeviceName());
                    evtDevBrandField.setText(d.getBrand() != null ? d.getBrand() : "");
                    evtDevModelField.setText(d.getModel() != null ? d.getModel() : "");
                    evtDevSerialField.setText(d.getSerialNumber());
                    evtDevQtyField.setText(String.valueOf(d.getQuantity()));
                    evtDevTypeBox.setValue(d.getDeviceType());
                    
                    btnAddDeviceEvent.setText("Update Device");
                    if (btnCancelDeviceEvent != null) {
                        btnCancelDeviceEvent.setVisible(true);
                        btnCancelDeviceEvent.setManaged(true);
                    }
                });

                deleteBtn.getStyleClass().addAll("action-btn", "action-btn-danger");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 3px 8px; -fx-cursor: hand;");
                deleteBtn.setOnAction(event -> {
                    RequestDevice d = getTableView().getItems().get(getIndex());
                    eventStagedList.remove(d);
                    if (editingStagedDeviceEvent == d) {
                        editingStagedDeviceEvent = null;
                        clearStagedDeviceEventForm();
                        btnAddDeviceEvent.setText("+ Add Device To Staging");
                        if (btnCancelDeviceEvent != null) {
                            btnCancelDeviceEvent.setVisible(false);
                            btnCancelDeviceEvent.setManaged(false);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });
        stagedEventTable.setItems(eventStagedList);

        // Bind Review table columns
        colRevName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colRevBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colRevModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colRevSerial.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colRevQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        reviewDevicesTable.setItems(reviewDevicesList);

        // Setup Validation
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqStudentIdField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqPurposeField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqVenueField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqStartDatePicker);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqEndDatePicker);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqIngressHour);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqIngressMinute);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqIngressAmpm);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqEgressHour);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqEgressMinute);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(reqEgressAmpm);

        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtStudentIdField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtEventNameField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtOrgField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtRespPersonField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtPurposeField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtVenueField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtStartDatePicker);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtEndDatePicker);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtIngressHour);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtIngressMinute);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtIngressAmpm);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtEgressHour);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtEgressMinute);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(evtEgressAmpm);

        // Partial Search for Student ID
        com.pup.byod.javafxbyodclient.util.StudentSearchDropdown.attach(reqStudentIdField, null);
        com.pup.byod.javafxbyodclient.util.StudentSearchDropdown.attach(evtStudentIdField, null);

        // Load data async
        loadRequests();
    }

    private void loadRequests() {
        new Thread(() -> {
            try {
                List<Request> list = requestService.getAllRequests();
                Platform.runLater(() -> {
                    requestList.setAll(list);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Loading Error", "Could not fetch access requests list", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void openNewRequestOverlay() {
        isEditMode = false;
        editingRequestId = -1;
        lblStandardRequestTitle.setText("New Standard Academic BYOD Request");
        btnSaveRequestNormal.setText("Submit Request");

        isUpdatingFields = true;
        newRequestOverlay.setVisible(true);
        reqStudentIdField.clear();
        reqPurposeField.clear();
        reqVenueField.clear();
        reqStartDatePicker.setValue(null);
        reqEndDatePicker.setValue(null);
        resetTimeComboBoxes(reqIngressHour, reqIngressMinute, reqIngressAmpm);
        resetTimeComboBoxes(reqEgressHour, reqEgressMinute, reqEgressAmpm);
        reqRemarksField.clear();
        normalStagedList.clear();
        editingStagedDeviceNormal = null;
        if (btnAddDeviceNormal != null) {
            btnAddDeviceNormal.setText("+ Add Device To Staging");
        }
        if (btnCancelDeviceNormal != null) {
            btnCancelDeviceNormal.setVisible(false);
            btnCancelDeviceNormal.setManaged(false);
        }
        clearStagedDeviceNormalForm();
        isUpdatingFields = false;
    }

    @FXML
    public void openNewEventRequestOverlay() {
        isEditMode = false;
        editingRequestId = -1;
        lblEventRequestTitle.setText("New Event BYOD Access Request");
        btnSaveRequestEvent.setText("Submit Event Request");

        isUpdatingFields = true;
        newEventRequestOverlay.setVisible(true);
        evtStudentIdField.clear();
        evtEventNameField.clear();
        evtVenueField.clear();
        evtOrgField.clear();
        evtRespPersonField.clear();
        evtPurposeField.clear();
        evtStartDatePicker.setValue(null);
        evtEndDatePicker.setValue(null);
        resetTimeComboBoxes(evtIngressHour, evtIngressMinute, evtIngressAmpm);
        resetTimeComboBoxes(evtEgressHour, evtEgressMinute, evtEgressAmpm);
        evtRemarksField.clear();
        eventStagedList.clear();
        editingStagedDeviceEvent = null;
        if (btnAddDeviceEvent != null) {
            btnAddDeviceEvent.setText("+ Add Device To Staging");
        }
        if (btnCancelDeviceEvent != null) {
            btnCancelDeviceEvent.setVisible(false);
            btnCancelDeviceEvent.setManaged(false);
        }
        clearStagedDeviceEventForm();
        isUpdatingFields = false;
    }

    @FXML
    public void handleEditRequest() {
        Request req = requestsTable.getSelectionModel().getSelectedItem();
        if (req == null) return;

        isEditMode = true;
        editingRequestId = req.getRequestId();

        if ("event".equalsIgnoreCase(req.getRequestType())) {
            lblEventRequestTitle.setText("Edit Event BYOD Access Request (Req #" + editingRequestId + ")");
            btnSaveRequestEvent.setText("Save Changes");

            isUpdatingFields = true;
            newEventRequestOverlay.setVisible(true);
            evtStudentIdField.setText(req.getStudentId());
            evtEventNameField.setText(req.getEventName());
            evtOrgField.setText(req.getOrganization());
            evtRespPersonField.setText(req.getResponsiblePerson());
            evtPurposeField.setText(req.getPurpose());
            evtVenueField.setText(req.getVenue() != null ? req.getVenue() : "");
            evtStartDatePicker.setValue(parseLocalDate(req.getStartDate()));
            evtEndDatePicker.setValue(parseLocalDate(req.getEndDate()));
            populateTimeFields(req.getExpectedIngressTime(), evtIngressHour, evtIngressMinute, evtIngressAmpm);
            populateTimeFields(req.getExpectedEgressTime(), evtEgressHour, evtEgressMinute, evtEgressAmpm);
            evtRemarksField.setText(req.getRemarks());
            isUpdatingFields = false;
            
            editingStagedDeviceEvent = null;
            if (btnAddDeviceEvent != null) {
                btnAddDeviceEvent.setText("+ Add Device To Staging");
            }
            if (btnCancelDeviceEvent != null) {
                btnCancelDeviceEvent.setVisible(false);
                btnCancelDeviceEvent.setManaged(false);
            }
            eventStagedList.clear();
            new Thread(() -> {
                try {
                    List<RequestDevice> devices = requestService.getDevicesForRequest(req.getRequestId());
                    Platform.runLater(() -> eventStagedList.setAll(devices));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            lblStandardRequestTitle.setText("Edit Standard Academic BYOD Request (Req #" + editingRequestId + ")");
            btnSaveRequestNormal.setText("Save Changes");

            isUpdatingFields = true;
            newRequestOverlay.setVisible(true);
            reqStudentIdField.setText(req.getStudentId());
            reqPurposeField.setText(req.getPurpose());
            reqStartDatePicker.setValue(parseLocalDate(req.getStartDate()));
            reqEndDatePicker.setValue(parseLocalDate(req.getEndDate()));
            reqVenueField.setText(req.getVenue() != null ? req.getVenue() : "");
            populateTimeFields(req.getExpectedIngressTime(), reqIngressHour, reqIngressMinute, reqIngressAmpm);
            populateTimeFields(req.getExpectedEgressTime(), reqEgressHour, reqEgressMinute, reqEgressAmpm);
            reqRemarksField.setText(req.getRemarks());
            isUpdatingFields = false;

            editingStagedDeviceNormal = null;
            if (btnAddDeviceNormal != null) {
                btnAddDeviceNormal.setText("+ Add Device To Staging");
            }
            if (btnCancelDeviceNormal != null) {
                btnCancelDeviceNormal.setVisible(false);
                btnCancelDeviceNormal.setManaged(false);
            }
            normalStagedList.clear();
            new Thread(() -> {
                try {
                    List<RequestDevice> devices = requestService.getDevicesForRequest(req.getRequestId());
                    Platform.runLater(() -> normalStagedList.setAll(devices));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            if (dateStr.contains("T")) {
                dateStr = dateStr.split("T")[0];
            }
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private void populateTimeFields(String timeStr, ComboBox<String> hourBox, ComboBox<String> minuteBox, ComboBox<String> ampmBox) {
        if (timeStr == null || !timeStr.contains(":")) {
            hourBox.setValue(null);
            minuteBox.setValue(null);
            ampmBox.setValue(null);
            return;
        }
        try {
            String[] parts = timeStr.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            
            String ampm = "AM";
            if (h >= 12) {
                ampm = "PM";
                if (h > 12) {
                    h -= 12;
                }
            } else if (h == 0) {
                h = 12;
            }

            int roundedMin = ((m + 2) / 5) * 5;
            if (roundedMin >= 60) {
                roundedMin = 55;
            }
            
            isUpdatingFields = true;
            try {
                ampmBox.setValue(ampm);
                if ("AM".equals(ampm)) {
                    hourBox.getItems().setAll("07", "08", "09", "10", "11");
                } else {
                    hourBox.getItems().setAll("12", "01", "02", "03", "04", "05", "06", "07", "08", "09");
                }
                hourBox.setValue(String.format("%02d", h));
                
                if ("PM".equals(ampm) && h == 9) {
                    minuteBox.getItems().setAll("00");
                    minuteBox.setValue("00");
                } else {
                    List<String> mins = new ArrayList<>();
                    for (int minVal = 0; minVal < 60; minVal += 5) {
                        mins.add(String.format("%02d", minVal));
                    }
                    minuteBox.getItems().setAll(mins);
                    minuteBox.setValue(String.format("%02d", roundedMin));
                }
            } finally {
                isUpdatingFields = false;
            }
        } catch (Exception e) {
            hourBox.setValue(null);
            minuteBox.setValue(null);
            ampmBox.setValue(null);
        }
    }

    private void openReviewOverlay(Request req) {
        selectedRequest = req;
        lblReviewStudentId.setText(req.getStudentId());
        lblReviewType.setText(req.getRequestType());
        if ("EVENT".equalsIgnoreCase(req.getRequestType())) {
            lblReviewPurpose.setText(req.getEventName() + " (" + req.getPurpose() + ")");
        } else {
            lblReviewPurpose.setText(req.getPurpose());
        }
        lblReviewTimes.setText(formatTime12hr(req.getExpectedIngressTime()) + " - " + formatTime12hr(req.getExpectedEgressTime()));
        
        reviewTitleLabel.setText("Review Access Request #" + req.getRequestId() + " (" + req.getStatus().toUpperCase() + ")");

        // Manage action buttons visibility based on status
        boolean isPending = "pending".equalsIgnoreCase(req.getStatus());
        btnApprove.setVisible(isPending);
        btnReject.setVisible(isPending);
        btnReturn.setVisible(isPending);

        reviewDevicesList.clear();
        reviewOverlay.setVisible(true);

        new Thread(() -> {
            try {
                List<RequestDevice> devices = requestService.getDevicesForRequest(req.getRequestId());
                Platform.runLater(() -> {
                    reviewDevicesList.setAll(devices);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Loading Error", "Could not fetch request devices", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void handleCloseOverlays() {
        newRequestOverlay.setVisible(false);
        newEventRequestOverlay.setVisible(false);
        reviewOverlay.setVisible(false);
        selectedRequest = null;

        editingStagedDeviceNormal = null;
        clearStagedDeviceNormalForm();
        if (btnAddDeviceNormal != null) {
            btnAddDeviceNormal.setText("+ Add Device To Staging");
        }
        if (btnCancelDeviceNormal != null) {
            btnCancelDeviceNormal.setVisible(false);
            btnCancelDeviceNormal.setManaged(false);
        }

        editingStagedDeviceEvent = null;
        clearStagedDeviceEventForm();
        if (btnAddDeviceEvent != null) {
            btnAddDeviceEvent.setText("+ Add Device To Staging");
        }
        if (btnCancelDeviceEvent != null) {
            btnCancelDeviceEvent.setVisible(false);
            btnCancelDeviceEvent.setManaged(false);
        }

        // Reset validations
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqStudentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqPurposeField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqVenueField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqStartDatePicker);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqEndDatePicker);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqIngressHour);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqIngressMinute);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqIngressAmpm);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqEgressHour);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqEgressMinute);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(reqEgressAmpm);

        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtStudentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtEventNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtOrgField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtRespPersonField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtPurposeField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtVenueField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtStartDatePicker);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtEndDatePicker);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtIngressHour);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtIngressMinute);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtIngressAmpm);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtEgressHour);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtEgressMinute);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(evtEgressAmpm);
    }

    // --- Device Staging Methods ---
    @FXML
    public void addStagedDeviceNormal() {
        String name = devNameField.getText().trim();
        String brand = devBrandField.getText().trim();
        String model = devModelField.getText().trim();
        String serial = devSerialField.getText().trim();
        String qtyStr = devQtyField.getText().trim();
        String type = devTypeBox.getValue();

        if (ValidationHelper.isEmpty(name) || ValidationHelper.isEmpty(serial) || type == null) {
            AlertHelper.showWarning("Device Form", "Required Fields", "Name, Serial, and Device Type are required.");
            return;
        }

        int qty = 1;
        if (!qtyStr.isEmpty()) {
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                AlertHelper.showWarning("Device Form", "Invalid Quantity", "Quantity must be a valid integer.");
                return;
            }
        }

        if (editingStagedDeviceNormal != null) {
            editingStagedDeviceNormal.setDeviceName(name);
            editingStagedDeviceNormal.setBrand(brand);
            editingStagedDeviceNormal.setModel(model);
            editingStagedDeviceNormal.setSerialNumber(serial);
            editingStagedDeviceNormal.setQuantity(qty);
            editingStagedDeviceNormal.setDeviceType(type);
            stagedNormalTable.refresh();
            editingStagedDeviceNormal = null;
            btnAddDeviceNormal.setText("+ Add Device To Staging");
            if (btnCancelDeviceNormal != null) {
                btnCancelDeviceNormal.setVisible(false);
                btnCancelDeviceNormal.setManaged(false);
            }
            clearStagedDeviceNormalForm();
        } else {
            RequestDevice d = new RequestDevice();
            d.setDeviceName(name);
            d.setBrand(brand);
            d.setModel(model);
            d.setSerialNumber(serial);
            d.setQuantity(qty);
            d.setDeviceType(type);

            normalStagedList.add(d);
            clearStagedDeviceNormalForm();
        }
    }

    @FXML
    public void addStagedDeviceEvent() {
        String name = evtDevNameField.getText().trim();
        String brand = evtDevBrandField.getText().trim();
        String model = evtDevModelField.getText().trim();
        String serial = evtDevSerialField.getText().trim();
        String qtyStr = evtDevQtyField.getText().trim();
        String type = evtDevTypeBox.getValue();

        if (ValidationHelper.isEmpty(name) || ValidationHelper.isEmpty(serial) || type == null) {
            AlertHelper.showWarning("Device Form", "Required Fields", "Name, Serial, and Device Type are required.");
            return;
        }

        int qty = 1;
        if (!qtyStr.isEmpty()) {
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException e) {
                AlertHelper.showWarning("Device Form", "Invalid Quantity", "Quantity must be a valid integer.");
                return;
            }
        }

        if (editingStagedDeviceEvent != null) {
            editingStagedDeviceEvent.setDeviceName(name);
            editingStagedDeviceEvent.setBrand(brand);
            editingStagedDeviceEvent.setModel(model);
            editingStagedDeviceEvent.setSerialNumber(serial);
            editingStagedDeviceEvent.setQuantity(qty);
            editingStagedDeviceEvent.setDeviceType(type);
            stagedEventTable.refresh();
            editingStagedDeviceEvent = null;
            btnAddDeviceEvent.setText("+ Add Device To Staging");
            if (btnCancelDeviceEvent != null) {
                btnCancelDeviceEvent.setVisible(false);
                btnCancelDeviceEvent.setManaged(false);
            }
            clearStagedDeviceEventForm();
        } else {
            RequestDevice d = new RequestDevice();
            d.setDeviceName(name);
            d.setBrand(brand);
            d.setModel(model);
            d.setSerialNumber(serial);
            d.setQuantity(qty);
            d.setDeviceType(type);

            eventStagedList.add(d);
            clearStagedDeviceEventForm();
        }
    }

    private void clearStagedDeviceNormalForm() {
        devNameField.clear();
        devBrandField.clear();
        devModelField.clear();
        devSerialField.clear();
        devQtyField.setText("1");
        devTypeBox.setValue(null);
    }

    private void clearStagedDeviceEventForm() {
        evtDevNameField.clear();
        evtDevBrandField.clear();
        evtDevModelField.clear();
        evtDevSerialField.clear();
        evtDevQtyField.setText("1");
        evtDevTypeBox.setValue(null);
    }

    @FXML
    public void cancelEditDeviceNormal() {
        editingStagedDeviceNormal = null;
        clearStagedDeviceNormalForm();
        btnAddDeviceNormal.setText("+ Add Device To Staging");
        if (btnCancelDeviceNormal != null) {
            btnCancelDeviceNormal.setVisible(false);
            btnCancelDeviceNormal.setManaged(false);
        }
    }

    @FXML
    public void cancelEditDeviceEvent() {
        editingStagedDeviceEvent = null;
        clearStagedDeviceEventForm();
        btnAddDeviceEvent.setText("+ Add Device To Staging");
        if (btnCancelDeviceEvent != null) {
            btnCancelDeviceEvent.setVisible(false);
            btnCancelDeviceEvent.setManaged(false);
        }
    }

    // --- Access Requests Saving ---
    @FXML
    public void handleSaveRequestNormal() {
        String studentId = reqStudentIdField.getText().trim();
        String purpose = reqPurposeField.getText().trim();
        String venue = reqVenueField.getText().trim();
        LocalDate start = reqStartDatePicker.getValue();
        LocalDate end = reqEndDatePicker.getValue();
        java.time.LocalTime ingressTime = parseTime(reqIngressHour.getValue(), reqIngressMinute.getValue(), reqIngressAmpm.getValue());
        java.time.LocalTime egressTime = parseTime(reqEgressHour.getValue(), reqEgressMinute.getValue(), reqEgressAmpm.getValue());

        if (ValidationHelper.isEmpty(studentId) || ValidationHelper.isEmpty(purpose) || ValidationHelper.isEmpty(venue) ||
            start == null || end == null || ingressTime == null || egressTime == null) {
            AlertHelper.showWarning("Submit Error", "Required Fields", "Please fill in all request fields (including Venue).");
            return;
        }

        if (!isEditMode && start.isBefore(LocalDate.now())) {
            AlertHelper.showWarning("Submit Error", "Invalid Start Date", "Start date cannot be in the past.");
            return;
        }

        if (end.isBefore(start)) {
            AlertHelper.showWarning("Submit Error", "Invalid End Date", "End date must be same or after start date.");
            return;
        }

        if (!egressTime.isAfter(ingressTime)) {
            AlertHelper.showWarning("Submit Error", "Invalid Times", "Egress time must be after Ingress time.");
            return;
        }

        if (start.equals(LocalDate.now())) {
            java.time.LocalTime nowTime = java.time.LocalTime.now();
            if (ingressTime.isBefore(nowTime)) {
                AlertHelper.showWarning("Submit Error", "Invalid Ingress Time", "Ingress time cannot be in the past for today's request.");
                return;
            }
        }

        if (normalStagedList.isEmpty()) {
            AlertHelper.showWarning("Submit Error", "No Devices", "You must add at least one device line item to this request.");
            return;
        }

        String ingress = formatLocalTime(ingressTime);
        String egress = formatLocalTime(egressTime);
        String remarks = reqRemarksField.getText().trim();

        Request req = new Request();
        req.setRequestType("normal");
        req.setStudentId(studentId);
        req.setPurpose(purpose);
        req.setVenue(venue);
        req.setStartDate(start.toString());
        req.setEndDate(end.toString());
        req.setExpectedIngressTime(ingress);
        req.setExpectedEgressTime(egress);
        req.setRemarks(remarks);
        req.setIsSubmitted(true);
        req.setIsAccommodated(false);
        req.setLineItems(new ArrayList<>(normalStagedList));

        new Thread(() -> {
            try {
                // Verify student exists first
                List<Student> searchResult = studentService.searchStudents(studentId);
                boolean exists = searchResult.stream().anyMatch(s -> s.getStudentId().equalsIgnoreCase(studentId));
                if (!exists) {
                    Platform.runLater(() -> AlertHelper.showWarning("Submit Error", "Student Not Found", "No active student found with ID: " + studentId));
                    return;
                }

                if (isEditMode) {
                    requestService.updateRequest(editingRequestId, req);
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Request Updated", "Standard access request successfully updated.");
                        newRequestOverlay.setVisible(false);
                        loadRequests();
                    });
                } else {
                    requestService.createRequest(req);
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Request Submitted", "Standard access request successfully submitted.");
                        newRequestOverlay.setVisible(false);
                        loadRequests();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> AlertHelper.showError("Submit Failed", "Error saving request", e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleSaveRequestEvent() {
        String studentId = evtStudentIdField.getText().trim();
        String eventName = evtEventNameField.getText().trim();
        String venue = evtVenueField.getText().trim();
        String org = evtOrgField.getText().trim();
        String respPerson = evtRespPersonField.getText().trim();
        String purpose = evtPurposeField.getText().trim();
        LocalDate start = evtStartDatePicker.getValue();
        LocalDate end = evtEndDatePicker.getValue();
        java.time.LocalTime ingressTime = parseTime(evtIngressHour.getValue(), evtIngressMinute.getValue(), evtIngressAmpm.getValue());
        java.time.LocalTime egressTime = parseTime(evtEgressHour.getValue(), evtEgressMinute.getValue(), evtEgressAmpm.getValue());

        if (ValidationHelper.isEmpty(studentId) || ValidationHelper.isEmpty(eventName) || ValidationHelper.isEmpty(venue) ||
            ValidationHelper.isEmpty(org) || ValidationHelper.isEmpty(respPerson) || ValidationHelper.isEmpty(purpose) ||
            start == null || end == null || ingressTime == null || egressTime == null) {
            AlertHelper.showWarning("Submit Error", "Required Fields", "Please fill in all event request fields (including Venue).");
            return;
        }

        if (!isEditMode && start.isBefore(LocalDate.now())) {
            AlertHelper.showWarning("Submit Error", "Invalid Start Date", "Start date cannot be in the past.");
            return;
        }

        if (end.isBefore(start)) {
            AlertHelper.showWarning("Submit Error", "Invalid End Date", "End date must be same or after start date.");
            return;
        }

        if (!egressTime.isAfter(ingressTime)) {
            AlertHelper.showWarning("Submit Error", "Invalid Times", "Egress time must be after Ingress time.");
            return;
        }

        if (start.equals(LocalDate.now())) {
            java.time.LocalTime nowTime = java.time.LocalTime.now();
            if (ingressTime.isBefore(nowTime)) {
                AlertHelper.showWarning("Submit Error", "Invalid Ingress Time", "Ingress time cannot be in the past for today's request.");
                return;
            }
        }

        if (eventStagedList.isEmpty()) {
            AlertHelper.showWarning("Submit Error", "No Devices", "You must add at least one device line item to this request.");
            return;
        }

        String ingress = formatLocalTime(ingressTime);
        String egress = formatLocalTime(egressTime);
        String remarks = evtRemarksField.getText().trim();

        Request req = new Request();
        req.setRequestType("event");
        req.setStudentId(studentId);
        req.setEventName(eventName);
        req.setVenue(venue);
        req.setOrganization(org);
        req.setResponsiblePerson(respPerson);
        req.setPurpose(purpose);
        req.setStartDate(start.toString());
        req.setEndDate(end.toString());
        req.setExpectedIngressTime(ingress);
        req.setExpectedEgressTime(egress);
        req.setRemarks(remarks);
        req.setIsSubmitted(true);
        req.setIsAccommodated(true);
        req.setLineItems(new ArrayList<>(eventStagedList));

        new Thread(() -> {
            try {
                // Verify student exists first
                List<Student> searchResult = studentService.searchStudents(studentId);
                boolean exists = searchResult.stream().anyMatch(s -> s.getStudentId().equalsIgnoreCase(studentId));
                if (!exists) {
                    Platform.runLater(() -> AlertHelper.showWarning("Submit Error", "Student Not Found", "No active student found with ID: " + studentId));
                    return;
                }

                if (isEditMode) {
                    requestService.updateRequest(editingRequestId, req);
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Event Request Updated", "Event access request successfully updated.");
                        newEventRequestOverlay.setVisible(false);
                        loadRequests();
                    });
                } else {
                    requestService.createRequest(req);
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Success", "Event Request Submitted", "Event access request successfully submitted.");
                        newEventRequestOverlay.setVisible(false);
                        loadRequests();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> AlertHelper.showError("Submit Failed", "Error saving request", e.getMessage()));
            }
        }).start();
    }

    // --- Review Execution ---
    @FXML
    public void handleApproveRequest() {
        if (selectedRequest == null) return;
        int reviewerId = SessionManager.getInstance().getCurrentUser().getUserId();

        if (!AlertHelper.showConfirmation("Approve Request", "Confirm Approval", 
                "Are you sure you want to approve request #" + selectedRequest.getRequestId() + "?")) {
            return;
        }

        new Thread(() -> {
            try {
                requestService.approveRequest(selectedRequest.getRequestId(), reviewerId);
                Platform.runLater(() -> {
                    AlertHelper.showInfo("Approved", "Request Approved", "Successfully approved access request.");
                    reviewOverlay.setVisible(false);
                    loadRequests();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> AlertHelper.showError("Approval Error", "Failed to approve request", e.getMessage()));
            }
        }).start();
    }

    @FXML
    public void handleRejectRequest() {
        if (selectedRequest == null) return;
        int reviewerId = SessionManager.getInstance().getCurrentUser().getUserId();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Request");
        dialog.setHeaderText("Reason for Rejection");
        dialog.setContentText("Enter remarks:");
        dialog.showAndWait().ifPresent(remarks -> {
            if (remarks.trim().isEmpty()) {
                AlertHelper.showWarning("Reject Request", "Remarks Required", "Please enter remarks for rejecting the request.");
                return;
            }

            new Thread(() -> {
                try {
                    requestService.rejectRequest(selectedRequest.getRequestId(), reviewerId, remarks.trim());
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Rejected", "Request Rejected", "The request has been rejected.");
                        reviewOverlay.setVisible(false);
                        loadRequests();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> AlertHelper.showError("Rejection Error", "Failed to reject request", e.getMessage()));
                }
            }).start();
        });
    }

    @FXML
    public void handleReturnRequest() {
        if (selectedRequest == null) return;
        int reviewerId = SessionManager.getInstance().getCurrentUser().getUserId();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Return Request");
        dialog.setHeaderText("Reason for Returning Request");
        dialog.setContentText("Enter correction details:");
        dialog.showAndWait().ifPresent(remarks -> {
            if (remarks.trim().isEmpty()) {
                AlertHelper.showWarning("Return Request", "Remarks Required", "Please enter return remarks.");
                return;
            }

            new Thread(() -> {
                try {
                    requestService.returnRequest(selectedRequest.getRequestId(), reviewerId, remarks.trim());
                    Platform.runLater(() -> {
                        AlertHelper.showInfo("Returned", "Request Returned", "The request has been sent back for corrections.");
                        reviewOverlay.setVisible(false);
                        loadRequests();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> AlertHelper.showError("Return Error", "Failed to return request", e.getMessage()));
                }
            }).start();
        });
    }

    private void setupTimeComboBoxes(ComboBox<String> hourBox, ComboBox<String> minuteBox, ComboBox<String> ampmBox) {
        ampmBox.getItems().setAll("AM", "PM");
        ampmBox.setValue("AM");
        List<String> hours = new ArrayList<>();
        for (int h = 1; h <= 12; h++) {
            hours.add(String.format("%02d", h));
        }
        hourBox.getItems().setAll(hours);
        List<String> defaultMins = new ArrayList<>();
        for (int m = 0; m < 60; m += 5) {
            defaultMins.add(String.format("%02d", m));
        }
        minuteBox.getItems().setAll(defaultMins);
    }

    private void resetTimeComboBoxes(ComboBox<String> hourBox, ComboBox<String> minuteBox, ComboBox<String> ampmBox) {
        ampmBox.setValue("AM");
        List<String> hours = new ArrayList<>();
        for (int h = 1; h <= 12; h++) {
            hours.add(String.format("%02d", h));
        }
        hourBox.getItems().setAll(hours);
        hourBox.setValue(null);
        List<String> mins = new ArrayList<>();
        for (int m = 0; m < 60; m += 5) {
            mins.add(String.format("%02d", m));
        }
        minuteBox.getItems().setAll(mins);
        minuteBox.setValue(null);
    }

    private java.time.LocalTime parseTime(String hour, String minute, String ampm) {
        if (hour == null || minute == null || ampm == null) return null;
        try {
            int h = Integer.parseInt(hour);
            int m = Integer.parseInt(minute);
            if ("PM".equalsIgnoreCase(ampm) && h < 12) {
                h += 12;
            } else if ("AM".equalsIgnoreCase(ampm) && h == 12) {
                h = 0;
            }
            return java.time.LocalTime.of(h, m);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatLocalTime(java.time.LocalTime time) {
        if (time == null) return null;
        return String.format("%02d:%02d:00", time.getHour(), time.getMinute());
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

    private boolean isSundayOrHoliday(LocalDate date) {
        if (date == null) return false;
        
        // 1. Check if Sunday
        if (date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            return true;
        }
        
        // 2. Check if fixed Public Holiday (Philippine standard list)
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        
        if (month == 1 && day == 1) return true; // New Year's Day
        if (month == 4 && day == 9) return true; // Araw ng Kagitingan
        if (month == 5 && day == 1) return true; // Labor Day
        if (month == 6 && day == 12) return true; // Independence Day
        if (month == 8 && day == 21) return true; // Ninoy Aquino Day
        if (month == 11 && day == 1) return true; // All Saints' Day
        if (month == 11 && day == 2) return true; // All Souls' Day
        if (month == 11 && day == 30) return true; // Bonifacio Day
        if (month == 12 && day == 8) return true; // Feast of the Immaculate Conception
        if (month == 12 && (day == 24 || day == 25)) return true; // Christmas Eve & Christmas Day
        if (month == 12 && (day == 30 || day == 31)) return true; // Rizal Day & New Year's Eve
        
        return false;
    }

}
