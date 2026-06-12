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
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ReportsScreenController {
    @FXML private ComboBox<String> reportTypeBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Map<String, Object>> reportsTable;
    @FXML private TabPane reportsTabPane;
    @FXML private StackPane chartContainer;

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
                getValueFromMap(cellData.getValue(), "date", "trafficDate", "logDate", "day", "reportDate", "eventTime")
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
                getValueFromMap(cellData.getValue(), "total", "totalTraffic", "totalCount", "total_count", "totalEvents", "total_events")
            ));
            colTotal.setPrefWidth(180.0);

            reportsTable.getColumns().addAll(colDate, colEntries, colExits, colTotal);

        } else if (MONTHLY_TRAFFIC.equals(reportType)) {
            TableColumn<Map<String, Object>, Object> colMonth = new TableColumn<>("Month");
            colMonth.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                getValueFromMap(cellData.getValue(), "month", "trafficMonth", "logMonth", "yearMonth", "reportMonth")
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
                getValueFromMap(cellData.getValue(), "total", "totalTraffic", "totalCount", "total_count", "totalEvents", "total_events")
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
            updateVisualAnalytics(reportType, records);
            if (records.isEmpty()) {
                AlertHelper.showInfo("Report Info", "Empty Dataset", "No matching report logs found within the selected dates.");
            }
        } catch (Exception e) {
            AlertHelper.showError("Generation Error", "Report failed", e.getMessage());
        }
    }

    private void updateVisualAnalytics(String reportType, List<Map<String, Object>> records) {
        chartContainer.getChildren().clear();

        if (records == null || records.isEmpty()) {
            Label placeholder = new Label("No data available for the selected range.");
            placeholder.setStyle("-fx-text-fill: #7284A8; -fx-font-size: 14px; -fx-font-family: 'Inter', sans-serif;");
            chartContainer.getChildren().add(placeholder);
            return;
        }

        if (DAILY_TRAFFIC.equals(reportType) || MONTHLY_TRAFFIC.equals(reportType)) {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel(DAILY_TRAFFIC.equals(reportType) ? "Date" : "Month");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Traffic Count");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle(reportType + " Overview");
            barChart.setAnimated(false);

            XYChart.Series<String, Number> entrySeries = new XYChart.Series<>();
            entrySeries.setName("Entries");

            XYChart.Series<String, Number> exitSeries = new XYChart.Series<>();
            exitSeries.setName("Exits");

            for (Map<String, Object> record : records) {
                String timeKey;
                if (DAILY_TRAFFIC.equals(reportType)) {
                    timeKey = String.valueOf(getValueFromMap(record, "date", "trafficDate", "logDate", "day", "reportDate", "eventTime"));
                } else {
                    timeKey = String.valueOf(getValueFromMap(record, "month", "trafficMonth", "logMonth", "yearMonth", "reportMonth"));
                }

                if (timeKey == null || timeKey.trim().isEmpty() || "null".equals(timeKey)) {
                    continue;
                }

                double entries = getNumericValue(record, "entries", "entryCount", "checkIns", "entry_count");
                double exits = getNumericValue(record, "exits", "exitCount", "checkOuts", "exit_count");

                entrySeries.getData().add(new XYChart.Data<>(timeKey, entries));
                exitSeries.getData().add(new XYChart.Data<>(timeKey, exits));
            }

            barChart.getData().addAll(entrySeries, exitSeries);
            chartContainer.getChildren().add(barChart);

        } else if (INCIDENT_OVERRIDES.equals(reportType)) {
            Map<String, Integer> freqMap = new HashMap<>();
            for (Map<String, Object> record : records) {
                Object val = getValueFromMap(record, "actionType", "action", "type");
                String action = val != null ? val.toString() : "Unknown";
                if (action.trim().isEmpty()) {
                    action = "Unknown";
                }
                freqMap.put(action, freqMap.getOrDefault(action, 0) + 1);
            }

            PieChart pieChart = new PieChart();
            pieChart.setTitle("Incident / Override Action Types");
            pieChart.setAnimated(false);

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
                pieData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
            }

            pieChart.setData(pieData);
            chartContainer.getChildren().add(pieChart);
        }
    }

    private double getNumericValue(Map<String, Object> map, String... keys) {
        Object val = getValueFromMap(map, keys);
        if (val == null) return 0;
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        try {
            return Double.parseDouble(val.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
