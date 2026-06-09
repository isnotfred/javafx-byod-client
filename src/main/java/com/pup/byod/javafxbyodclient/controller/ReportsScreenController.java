package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.service.ReportService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ReportsScreenController {
    @FXML private ComboBox<String> reportTypeBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Map<String, Object>> reportsTable;

    private final ReportService reportService = new ReportService();
    private final ObservableList<Map<String, Object>> reportList = FXCollections.observableArrayList();

    private static final String DAILY_TRAFFIC = "Daily Traffic Report";
    private static final String MONTHLY_TRAFFIC = "Monthly Traffic Report";
    private static final String INCIDENT_OVERRIDES = "Incident Overrides Report";

    @FXML
    public void initialize() {
        reportTypeBox.getItems().addAll(DAILY_TRAFFIC, MONTHLY_TRAFFIC, INCIDENT_OVERRIDES);
        reportsTable.setItems(reportList);

        // Clear table and setup columns when selection changes
        reportTypeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            reportList.clear();
            setupTableColumns(newVal);
        });

        // Set default selection
        reportTypeBox.getSelectionModel().select(INCIDENT_OVERRIDES);
    }

    private void setupTableColumns(String reportType) {
        reportsTable.getColumns().clear();

        if (DAILY_TRAFFIC.equals(reportType)) {
            TableColumn<Map<String, Object>, Object> colDate = new TableColumn<>("Date");
            colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "date", "trafficDate", "logDate", "day")
            ));
            colDate.setPrefWidth(180.0);

            TableColumn<Map<String, Object>, Object> colEntries = new TableColumn<>("Total Entries");
            colEntries.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "entries", "entryCount", "checkIns", "entry_count")
            ));
            colEntries.setPrefWidth(150.0);

            TableColumn<Map<String, Object>, Object> colExits = new TableColumn<>("Total Exits");
            colExits.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "exits", "exitCount", "checkOuts", "exit_count")
            ));
            colExits.setPrefWidth(150.0);

            TableColumn<Map<String, Object>, Object> colTotal = new TableColumn<>("Total Traffic");
            colTotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "total", "totalTraffic", "totalCount", "total_count")
            ));
            colTotal.setPrefWidth(180.0);

            reportsTable.getColumns().addAll(colDate, colEntries, colExits, colTotal);

        } else if (MONTHLY_TRAFFIC.equals(reportType)) {
            TableColumn<Map<String, Object>, Object> colMonth = new TableColumn<>("Month");
            colMonth.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "month", "trafficMonth", "logMonth", "yearMonth")
            ));
            colMonth.setPrefWidth(180.0);

            TableColumn<Map<String, Object>, Object> colEntries = new TableColumn<>("Total Entries");
            colEntries.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "entries", "entryCount", "checkIns", "entry_count")
            ));
            colEntries.setPrefWidth(150.0);

            TableColumn<Map<String, Object>, Object> colExits = new TableColumn<>("Total Exits");
            colExits.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "exits", "exitCount", "checkOuts", "exit_count")
            ));
            colExits.setPrefWidth(150.0);

            TableColumn<Map<String, Object>, Object> colTotal = new TableColumn<>("Total Traffic");
            colTotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "total", "totalTraffic", "totalCount", "total_count")
            ));
            colTotal.setPrefWidth(180.0);

            reportsTable.getColumns().addAll(colMonth, colEntries, colExits, colTotal);

        } else { // INCIDENT_OVERRIDES
            TableColumn<Map<String, Object>, Object> colTimestamp = new TableColumn<>("Timestamp");
            colTimestamp.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "timestamp", "eventTime", "logTime")
            ));
            colTimestamp.setPrefWidth(180.0);

            TableColumn<Map<String, Object>, Object> colAction = new TableColumn<>("Incident Action");
            colAction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "actionType", "action", "type")
            ));
            colAction.setPrefWidth(150.0);

            TableColumn<Map<String, Object>, Object> colUser = new TableColumn<>("Operator Name");
            colUser.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "operatorName", "userName", "fullName", "operator")
            ));
            colUser.setPrefWidth(150.0);

            TableColumn<Map<String, Object>, Object> colDescription = new TableColumn<>("Override / Dispute Details");
            colDescription.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "details", "overrideDetails", "description", "remarks")
            ));
            colDescription.setPrefWidth(400.0);

            reportsTable.getColumns().addAll(colTimestamp, colAction, colUser, colDescription);
        }
    }

    private Object getValueFromMap(Map<String, Object> map, String... keys) {
        if (map == null) return "";
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                return map.get(key);
            }
        }
        // Fallback: try case-insensitive check
        for (String mapKey : map.keySet()) {
            for (String key : keys) {
                if (mapKey.equalsIgnoreCase(key) && map.get(mapKey) != null) {
                    return map.get(mapKey);
                }
            }
        }
        return "";
    }

    @FXML
    public void handleGenerateReport() {
        String reportType = reportTypeBox.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (reportType == null) {
            AlertHelper.showWarning("Report Generator", "Report Type Required", "Please select a report type first.");
            return;
        }

        if (start == null || end == null) {
            AlertHelper.showWarning("Report Generator", "Date Range Required", "Please specify both starting and ending dates.");
            return;
        }

        try {
            List<Map<String, Object>> records;
            if (DAILY_TRAFFIC.equals(reportType)) {
                records = reportService.getDailyTrafficReport(start.toString(), end.toString());
            } else if (MONTHLY_TRAFFIC.equals(reportType)) {
                records = reportService.getMonthlyTrafficReport(start.toString(), end.toString());
            } else {
                records = reportService.getIncidentsReport(start.toString(), end.toString());
            }

            reportList.setAll(records);
            if (records.isEmpty()) {
                AlertHelper.showInfo("Report Info", "Empty Dataset", "No matching report logs found within the selected dates.");
            }
        } catch (Exception e) {
            AlertHelper.showError("Generation Error", "Report failed", e.getMessage());
        }
    }
}
