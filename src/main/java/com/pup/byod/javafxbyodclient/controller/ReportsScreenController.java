package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.service.ReportService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.chart.*;

import java.time.LocalDate;
import java.util.*;

public class ReportsScreenController {
    @FXML private ComboBox<String> reportTypeBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TableView<Map<String, Object>> reportsTable;
    @FXML private TabPane reportsTabPane;
    @FXML private StackPane chartContainer;

    @FXML private HBox startDateContainer;
    @FXML private HBox endDateContainer;
    @FXML private Label startDateLabel;

    private final ReportService reportService = new ReportService();
    private final ObservableList<Map<String, Object>> reportList = FXCollections.observableArrayList();

    private static final String DAILY_TRAFFIC = "Daily Traffic Report";
    private static final String MONTHLY_TRAFFIC = "Monthly Traffic Report";
    private static final String PENDING_REGISTRATIONS = "Pending Registrations Report";
    private static final String ACTIVE_DEVICES = "Active Devices Report";
    private static final String DEVICE_FREQUENCY = "Device Frequency Report";
    private static final String INCIDENT_OVERRIDES = "Incident Overrides Report";

    @FXML
    public void initialize() {
        reportTypeBox.getItems().addAll(
            DAILY_TRAFFIC,
            MONTHLY_TRAFFIC,
            PENDING_REGISTRATIONS,
            ACTIVE_DEVICES,
            DEVICE_FREQUENCY,
            INCIDENT_OVERRIDES
        );
        reportsTable.setItems(reportList);

        // Clear table, setup columns and adjust filters when selection changes
        reportTypeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            reportList.clear();
            setupTableColumns(newVal);
            adjustFilters(newVal);
        });

        // Set default selection
        reportTypeBox.getSelectionModel().select(INCIDENT_OVERRIDES);
    }

    private void adjustFilters(String reportType) {
        if (startDateContainer == null || endDateContainer == null || startDateLabel == null) {
            return;
        }
        if (DAILY_TRAFFIC.equals(reportType)) {
            startDateContainer.setVisible(true);
            startDateContainer.setManaged(true);
            startDateLabel.setText("Date:");
            
            endDateContainer.setVisible(false);
            endDateContainer.setManaged(false);
        } else if (MONTHLY_TRAFFIC.equals(reportType)) {
            startDateContainer.setVisible(true);
            startDateContainer.setManaged(true);
            startDateLabel.setText("Select Month/Year:");
            
            endDateContainer.setVisible(false);
            endDateContainer.setManaged(false);
        } else if (PENDING_REGISTRATIONS.equals(reportType) || ACTIVE_DEVICES.equals(reportType)) {
            startDateContainer.setVisible(false);
            startDateContainer.setManaged(false);
            
            endDateContainer.setVisible(false);
            endDateContainer.setManaged(false);
        } else { // DEVICE_FREQUENCY and INCIDENT_OVERRIDES
            startDateContainer.setVisible(true);
            startDateContainer.setManaged(true);
            startDateLabel.setText("From:");
            
            endDateContainer.setVisible(true);
            endDateContainer.setManaged(true);
        }
    }

    private TableColumn<Map<String, Object>, Object> createColumn(String header, String... keys) {
        TableColumn<Map<String, Object>, Object> column = new TableColumn<>(header);
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            getValueFromMap(cellData.getValue(), keys)
        ));
        return column;
    }

    private void setupTableColumns(String reportType) {
        reportsTable.getColumns().clear();
        if (reportType == null) return;

        switch (reportType) {
            case DAILY_TRAFFIC:
                reportsTable.getColumns().addAll(
                    createColumn("Log ID", "logId"),
                    createColumn("Event Type", "eventType"),
                    createColumn("Event Time", "eventTime"),
                    createColumn("Student ID", "studentId"),
                    createColumn("Student Name", "studentName"),
                    createColumn("Course/Year", "courseYearLevel"),
                    createColumn("Device Name", "deviceName"),
                    createColumn("Serial Number", "serialNumber"),
                    createColumn("Device Type", "deviceType"),
                    createColumn("Reg. Status", "registrationStatus"),
                    createColumn("Handled By", "handledByName"),
                    createColumn("Notes", "notes")
                );
                break;
            case MONTHLY_TRAFFIC:
                reportsTable.getColumns().addAll(
                    createColumn("Month", "reportMonth"),
                    createColumn("Device Category", "deviceCategory"),
                    createColumn("Student ID", "studentId"),
                    createColumn("Student Name", "studentName"),
                    createColumn("Course/Year", "courseYearLevel"),
                    createColumn("Entries", "entryCount"),
                    createColumn("Exits", "exitCount"),
                    createColumn("Total Events", "totalEvents")
                );
                break;
            case PENDING_REGISTRATIONS:
                reportsTable.getColumns().addAll(
                    createColumn("Device ID", "deviceId"),
                    createColumn("Student ID", "studentId"),
                    createColumn("Student Name", "studentName"),
                    createColumn("Course/Year", "courseYearLevel"),
                    createColumn("Device Name", "deviceName"),
                    createColumn("Brand", "brand"),
                    createColumn("Model", "model"),
                    createColumn("Serial Number", "serialNumber"),
                    createColumn("Device Type", "deviceType"),
                    createColumn("Purpose", "devicePurpose"),
                    createColumn("Submitted At", "submittedAt"),
                    createColumn("Submitted By", "submittedBy")
                );
                break;
            case ACTIVE_DEVICES:
                reportsTable.getColumns().addAll(
                    createColumn("Device ID", "deviceId"),
                    createColumn("Student ID", "studentId"),
                    createColumn("Student Name", "studentName"),
                    createColumn("Course/Year", "courseYearLevel"),
                    createColumn("Device Name", "deviceName"),
                    createColumn("Brand", "brand"),
                    createColumn("Model", "model"),
                    createColumn("Serial Number", "serialNumber"),
                    createColumn("Device Type", "deviceType"),
                    createColumn("Entered At", "enteredAt")
                );
                break;
            case DEVICE_FREQUENCY:
                reportsTable.getColumns().addAll(
                    createColumn("Device ID", "deviceId"),
                    createColumn("Student ID", "studentId"),
                    createColumn("Student Name", "studentName"),
                    createColumn("Course/Year", "courseYearLevel"),
                    createColumn("Device Name", "deviceName"),
                    createColumn("Device Type", "deviceType"),
                    createColumn("Brand", "brand"),
                    createColumn("Model", "model"),
                    createColumn("Entry Count", "entryCount"),
                    createColumn("Exit Count", "exitCount"),
                    createColumn("First Seen", "firstSeen"),
                    createColumn("Last Seen", "lastSeen")
                );
                break;
            case INCIDENT_OVERRIDES:
                reportsTable.getColumns().addAll(
                    createColumn("Audit ID", "auditId"),
                    createColumn("Timestamp", "createdAt"),
                    createColumn("Incident Action", "actionType"),
                    createColumn("Target Table", "targetTable"),
                    createColumn("Target ID", "targetId"),
                    createColumn("IP Address", "ipAddress"),
                    createColumn("Operator Name", "performedBy"),
                    createColumn("Role", "performerRole"),
                    createColumn("Old Values", "oldValues"),
                    createColumn("New Values", "newValues")
                );
                break;
        }

        // Set reasonable default widths
        for (TableColumn<Map<String, Object>, ?> col : reportsTable.getColumns()) {
            col.setPrefWidth(130.0);
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

        // Validate dates dynamically based on report selection
        if (DAILY_TRAFFIC.equals(reportType) || MONTHLY_TRAFFIC.equals(reportType)) {
            if (start == null) {
                AlertHelper.showWarning("Report Generator", "Date Required", "Please specify a date.");
                return;
            }
        } else if (DEVICE_FREQUENCY.equals(reportType) || INCIDENT_OVERRIDES.equals(reportType)) {
            if (start == null || end == null) {
                AlertHelper.showWarning("Report Generator", "Date Range Required", "Please specify both starting and ending dates.");
                return;
            }
        }

        try {
            List<Map<String, Object>> records;
            if (DAILY_TRAFFIC.equals(reportType)) {
                records = reportService.getDailyTrafficReport(start.toString(), null, null, null);
            } else if (MONTHLY_TRAFFIC.equals(reportType)) {
                records = reportService.getMonthlyTrafficReport(start.getYear(), start.getMonthValue());
            } else if (PENDING_REGISTRATIONS.equals(reportType)) {
                records = reportService.getPendingRegistrationReport();
            } else if (ACTIVE_DEVICES.equals(reportType)) {
                records = reportService.getActiveDevicesReport();
            } else if (DEVICE_FREQUENCY.equals(reportType)) {
                records = reportService.getDeviceFrequencyReport(start.toString(), end.toString());
            } else {
                records = reportService.getIncidentsReport(start.toString(), end.toString());
            }

            reportList.setAll(records);
            updateVisualAnalytics(reportType, records);
            if (records.isEmpty()) {
                AlertHelper.showInfo("Report Info", "Empty Dataset", "No matching report logs found.");
            }
        } catch (Exception e) {
            AlertHelper.showError("Generation Error", "Report failed", e.getMessage());
            e.printStackTrace();
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

        if (DAILY_TRAFFIC.equals(reportType)) {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Hour of Day");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Event Count");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Hourly Campus Gate Load");
            barChart.setAnimated(false);

            XYChart.Series<String, Number> entriesSeries = new XYChart.Series<>();
            entriesSeries.setName("Entries");

            XYChart.Series<String, Number> exitsSeries = new XYChart.Series<>();
            exitsSeries.setName("Exits");

            Map<String, Long> entryCountsByHour = new TreeMap<>();
            Map<String, Long> exitCountsByHour = new TreeMap<>();

            for (Map<String, Object> record : records) {
                String eventTime = String.valueOf(getValueFromMap(record, "eventTime"));
                String eventType = String.valueOf(getValueFromMap(record, "eventType"));
                String hourStr = parseHourFromTime(eventTime);
                if ("Unknown".equals(hourStr)) continue;

                if ("entry".equalsIgnoreCase(eventType)) {
                    entryCountsByHour.put(hourStr, entryCountsByHour.getOrDefault(hourStr, 0L) + 1);
                } else if ("exit".equalsIgnoreCase(eventType)) {
                    exitCountsByHour.put(hourStr, exitCountsByHour.getOrDefault(hourStr, 0L) + 1);
                }
            }

            Set<String> allHours = new HashSet<>();
            allHours.addAll(entryCountsByHour.keySet());
            allHours.addAll(exitCountsByHour.keySet());
            List<String> sortedHours = new ArrayList<>(allHours);
            
            sortedHours.sort((h1, h2) -> {
                try {
                    int hr1 = Integer.parseInt(h1.substring(0, 2));
                    int hr2 = Integer.parseInt(h2.substring(0, 2));
                    boolean pm1 = h1.contains("PM");
                    boolean pm2 = h2.contains("PM");
                    if (pm1 != pm2) {
                        return pm1 ? 1 : -1;
                    }
                    if (hr1 == 12 && hr2 != 12) return -1;
                    if (hr2 == 12 && hr1 != 12) return 1;
                    return Integer.compare(hr1, hr2);
                } catch (Exception e) {
                    return h1.compareTo(h2);
                }
            });

            for (String hour : sortedHours) {
                long entries = entryCountsByHour.getOrDefault(hour, 0L);
                long exits = exitCountsByHour.getOrDefault(hour, 0L);
                entriesSeries.getData().add(new XYChart.Data<>(hour, entries));
                exitsSeries.getData().add(new XYChart.Data<>(hour, exits));
            }

            barChart.getData().addAll(entriesSeries, exitsSeries);
            chartContainer.getChildren().add(barChart);

        } else if (MONTHLY_TRAFFIC.equals(reportType)) {
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Monthly Device Category Share");
            pieChart.setAnimated(false);

            Map<String, Integer> categoryEvents = new HashMap<>();
            for (Map<String, Object> record : records) {
                String category = String.valueOf(getValueFromMap(record, "deviceCategory"));
                if (category == null || category.trim().isEmpty() || "null".equals(category)) {
                    category = "Unknown Category";
                }
                int total = getNumericInt(record, "totalEvents", "total_events", "total", "totalTraffic");
                categoryEvents.put(category, categoryEvents.getOrDefault(category, 0) + total);
            }

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            categoryEvents.forEach((category, total) -> {
                pieData.add(new PieChart.Data(category + " (" + total + ")", total));
            });
            pieChart.setData(pieData);
            chartContainer.getChildren().add(pieChart);

        } else if (PENDING_REGISTRATIONS.equals(reportType)) {
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Pending Approvals by Device Type");
            pieChart.setAnimated(false);

            Map<String, Integer> typeCounts = new HashMap<>();
            for (Map<String, Object> record : records) {
                String devType = String.valueOf(getValueFromMap(record, "deviceType"));
                if (devType == null || devType.trim().isEmpty() || "null".equals(devType)) {
                    devType = "Unknown Type";
                }
                typeCounts.put(devType, typeCounts.getOrDefault(devType, 0) + 1);
            }

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            typeCounts.forEach((devType, count) -> {
                pieData.add(new PieChart.Data(devType + " (" + count + ")", count));
            });
            pieChart.setData(pieData);
            chartContainer.getChildren().add(pieChart);

        } else if (ACTIVE_DEVICES.equals(reportType)) {
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Live Campus Device Category Share");
            pieChart.setAnimated(false);

            Map<String, Integer> typeCounts = new HashMap<>();
            for (Map<String, Object> record : records) {
                String devType = String.valueOf(getValueFromMap(record, "deviceType"));
                if (devType == null || devType.trim().isEmpty() || "null".equals(devType)) {
                    devType = "Unknown Type";
                }
                typeCounts.put(devType, typeCounts.getOrDefault(devType, 0) + 1);
            }

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            typeCounts.forEach((devType, count) -> {
                pieData.add(new PieChart.Data(devType + " (" + count + ")", count));
            });
            pieChart.setData(pieData);
            chartContainer.getChildren().add(pieChart);

        } else if (DEVICE_FREQUENCY.equals(reportType)) {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Student (Device)");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Entry Count");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Top 10 Most Frequent Devices");
            barChart.setAnimated(false);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Entries");

            List<Map<String, Object>> sortedRecords = new ArrayList<>(records);
            sortedRecords.sort((r1, r2) -> {
                int count1 = getNumericInt(r1, "entryCount");
                int count2 = getNumericInt(r2, "entryCount");
                return Integer.compare(count2, count1);
            });

            int limit = Math.min(10, sortedRecords.size());
            for (int i = 0; i < limit; i++) {
                Map<String, Object> record = sortedRecords.get(i);
                String studentName = String.valueOf(getValueFromMap(record, "studentName"));
                String deviceName = String.valueOf(getValueFromMap(record, "deviceName"));
                String label = studentName + " (" + deviceName + ")";
                int entryCount = getNumericInt(record, "entryCount");
                series.getData().add(new XYChart.Data<>(label, entryCount));
            }

            barChart.getData().add(series);
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

    private String parseHourFromTime(String timeStr) {
        if (timeStr == null || !timeStr.contains("T")) return "Unknown";
        try {
            int tIdx = timeStr.indexOf('T');
            String timePart = timeStr.substring(tIdx + 1);
            String[] parts = timePart.split(":");
            if (parts.length >= 1) {
                int hour = Integer.parseInt(parts[0]);
                String ampm = hour >= 12 ? "PM" : "AM";
                int displayHour = hour % 12;
                if (displayHour == 0) displayHour = 12;
                return String.format("%02d:00 %s", displayHour, ampm);
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    private int getNumericInt(Map<String, Object> map, String... keys) {
        Object val = getValueFromMap(map, keys);
        if (val == null) return 0;
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

