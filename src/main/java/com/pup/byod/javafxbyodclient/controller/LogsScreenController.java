package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.DeviceLog;
import com.pup.byod.javafxbyodclient.service.LogService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class LogsScreenController {
    @FXML private TextField studentIdSearchField;
    @FXML private TableView<DeviceLog> logsTable;
    @FXML private TableColumn<DeviceLog, Integer> colLogId;
    @FXML private TableColumn<DeviceLog, String> colStudentId;
    @FXML private TableColumn<DeviceLog, String> colType;
    @FXML private TableColumn<DeviceLog, String> colTime;
    @FXML private TableColumn<DeviceLog, String> colNotes;

    private final LogService logService = new LogService();
    private final ObservableList<DeviceLog> logsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colType.setCellValueFactory(new PropertyValueFactory<>("eventType"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("eventTime"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        logsTable.setItems(logsList);
    }

    @FXML
    public void handleSearch() {
        String studentId = studentIdSearchField.getText();
        if (ValidationHelper.isEmpty(studentId)) {
            AlertHelper.showWarning("Search Warning", "Student ID Required", "Please enter a student ID to retrieve recent gate logs.");
            return;
        }

        try {
            List<DeviceLog> logs = logService.getStudentLogs(studentId);
            logsList.setAll(logs);
            if (logs.isEmpty()) {
                AlertHelper.showInfo("Search Logs", "No Records Found", "No entry/exit logs found for student: " + studentId);
            }
        } catch (Exception e) {
            AlertHelper.showError("Search failed", "Error", e.getMessage());
        }
    }
}
