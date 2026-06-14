package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.EventRequest;
import com.pup.byod.javafxbyodclient.model.EventRequestDevice;
import com.pup.byod.javafxbyodclient.service.EventRequestService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.ArrayList;

public class TemporaryEventDeviceScreenController {
    @FXML private TextField studentIdField;
    @FXML private TextField eventNameField;
    @FXML private TextField organizationField;
    @FXML private TextField responsiblePersonField;
    @FXML private TextField purposeField;
    @FXML private TextField docRefField;
    @FXML private ComboBox<String> docTypeBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private TextField deviceNameField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField serialNumberField;
    @FXML private ComboBox<String> deviceTypeBox;
    @FXML private TableView<EventRequestDevice> itemTable;
    @FXML private TableColumn<EventRequestDevice, String> colDevName;
    @FXML private TableColumn<EventRequestDevice, String> colDevSN;
    @FXML private TableColumn<EventRequestDevice, String> colDevType;

    private final EventRequestService eventRequestService = new EventRequestService();
    private final ObservableList<EventRequestDevice> lineItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        docTypeBox.getItems().addAll("Signed GPOA", "Paper Approval");
        deviceTypeBox.getItems().addAll(
            "Personal Computers",
            "Components & Peripherals",
            "Display & Projection",
            "Project Prototypes (Optional SN)",
            "Appliances (TLE)",
            "Other"
        );

        colDevName.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        colDevSN.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        colDevType.setCellValueFactory(new PropertyValueFactory<>("deviceType"));
        itemTable.setItems(lineItems);
    }

    @FXML
    public void handleAddItem() {
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

        lineItems.add(item);
        clearItemFields();
    }

    @FXML
    public void handleSubmitRequest() {
        String studentId = studentIdField.getText();
        String eventName = eventNameField.getText();
        String org = organizationField.getText();
        String responsible = responsiblePersonField.getText();
        String purpose = purposeField.getText();
        String docRef = docRefField.getText();
        String docType = docTypeBox.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (ValidationHelper.isEmpty(studentId) || ValidationHelper.isEmpty(eventName) || 
            start == null || end == null || lineItems.isEmpty()) {
            AlertHelper.showWarning("Form Validation", "Incomplete wizard", "Please fill in student details, dates, and add at least one device.");
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
        request.setLineItems(new ArrayList<>(lineItems));

        if (!AlertHelper.showConfirmation("Submit Request", "Confirm Submission", "Are you sure you want to submit this temporary event registration request?")) {
            return;
        }

        try {
            eventRequestService.createEventRequest(request);
            AlertHelper.showInfo("Event Request Submitted", "Success", "Your event request with bypass items has been sent for review.");
            clearForm();
        } catch (Exception e) {
            AlertHelper.showError("Submit Error", "Submit Failed", e.getMessage());
        }
    }

    private void clearItemFields() {
        deviceNameField.clear();
        brandField.clear();
        modelField.clear();
        serialNumberField.clear();
        deviceTypeBox.setValue(null);
    }

    private void clearForm() {
        studentIdField.clear();
        eventNameField.clear();
        organizationField.clear();
        responsiblePersonField.clear();
        purposeField.clear();
        docRefField.clear();
        docTypeBox.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        lineItems.clear();
    }
}
