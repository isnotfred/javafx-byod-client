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
    @FXML private ComboBox<String> monthSelectBox;
    @FXML private ComboBox<Integer> yearSelectBox;
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
    private static final String MISSED_CHECKOUTS = "Missed Checkouts Report";
    private static final String ACTIVE_DEVICES = "Active Devices Report";
    private static final String DEVICE_FREQUENCY = "Device Frequency Report";
    private static final String INCIDENT_OVERRIDES = "Incident Overrides Report";
    private static final String PURPOSE_BREAKDOWN = "Purpose Breakdown Report";

    @FXML
    public void initialize() {
        reportTypeBox.getItems().addAll(
            DAILY_TRAFFIC,
            MONTHLY_TRAFFIC,
            MISSED_CHECKOUTS,
            ACTIVE_DEVICES,
            DEVICE_FREQUENCY,
            INCIDENT_OVERRIDES,
            PURPOSE_BREAKDOWN
        );
        reportsTable.setItems(reportList);

        // Clear table, setup columns and adjust filters when selection changes
        reportTypeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            reportList.clear();
            setupTableColumns(newVal);
            adjustFilters(newVal);
        });

        // Set default selection and values
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now());
        reportsTable.setPlaceholder(new Label("No report records found. Try adjusting filter dates."));

        // Disable future dates in date pickers
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });
        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        // Initialize Month and Year ComboBoxes
        monthSelectBox.getItems().addAll(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        );
        LocalDate now = LocalDate.now();
        monthSelectBox.getSelectionModel().select(now.getMonthValue() - 1);

        int currentYear = now.getYear();
        for (int y = currentYear - 5; y <= currentYear + 2; y++) {
            yearSelectBox.getItems().add(y);
        }
        yearSelectBox.getSelectionModel().select(Integer.valueOf(currentYear));

        reportTypeBox.getSelectionModel().select(DAILY_TRAFFIC);
        generateReport(true);
    }

    private void adjustFilters(String reportType) {
        if (startDateContainer == null || endDateContainer == null || startDateLabel == null) {
            return;
        }
        if (DAILY_TRAFFIC.equals(reportType)) {
            startDateContainer.setVisible(true);
            startDateContainer.setManaged(true);
            startDateLabel.setText("Date:");
            startDatePicker.setVisible(true);
            startDatePicker.setManaged(true);
            monthSelectBox.setVisible(false);
            monthSelectBox.setManaged(false);
            yearSelectBox.setVisible(false);
            yearSelectBox.setManaged(false);
            
            endDateContainer.setVisible(false);
            endDateContainer.setManaged(false);
        } else if (MONTHLY_TRAFFIC.equals(reportType)) {
            startDateContainer.setVisible(true);
            startDateContainer.setManaged(true);
            startDateLabel.setText("Select Month/Year:");
            startDatePicker.setVisible(false);
            startDatePicker.setManaged(false);
            monthSelectBox.setVisible(true);
            monthSelectBox.setManaged(true);
            yearSelectBox.setVisible(true);
            yearSelectBox.setManaged(true);
            
            endDateContainer.setVisible(false);
            endDateContainer.setManaged(false);
        } else if (ACTIVE_DEVICES.equals(reportType) || PURPOSE_BREAKDOWN.equals(reportType)) {
            startDateContainer.setVisible(false);
            startDateContainer.setManaged(false);
            
            endDateContainer.setVisible(false);
            endDateContainer.setManaged(false);
        } else { // DEVICE_FREQUENCY, INCIDENT_OVERRIDES, MISSED_CHECKOUTS
            startDateContainer.setVisible(true);
            startDateContainer.setManaged(true);
            startDateLabel.setText("From:");
            startDatePicker.setVisible(true);
            startDatePicker.setManaged(true);
            monthSelectBox.setVisible(false);
            monthSelectBox.setManaged(false);
            yearSelectBox.setVisible(false);
            yearSelectBox.setManaged(false);
            
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
                    createColumn("Log ID", "logId", "transactionId", "transaction_id"),
                    createColumn("Student ID", "studentId", "student_id"),
                    createColumn("Student Name", "studentName", "student_name"),
                    createColumn("Course/Year", "courseYearLevel", "course_year_level"),
                    createColumn("Device Name", "deviceName", "device_name"),
                    createColumn("Serial Number", "serialNumber", "serial_number"),
                    createColumn("Checked-In", "ingressTime", "ingress_time"),
                    createColumn("Checked-Out", "egressTime", "egress_time")
                );
                break;
            case MONTHLY_TRAFFIC:
                reportsTable.getColumns().addAll(
                    createColumn("Month", "reportMonth", "report_month"),
                    createColumn("Device Category", "deviceCategory", "device_category"),
                    createColumn("Student ID", "studentId", "student_id"),
                    createColumn("Student Name", "studentName", "student_name"),
                    createColumn("Course/Year", "courseYearLevel", "course_year_level"),
                    createColumn("Entries/Total", "totalEvents", "total_events"),
                    createColumn("Exits", "exitCount", "exit_count"),
                    createColumn("Missed Checkouts", "missedCount", "missed_count")
                );
                break;
            case MISSED_CHECKOUTS:
                reportsTable.getColumns().addAll(
                    createColumn("Log ID", "transactionId", "transaction_id"),
                    createColumn("Student ID", "studentId", "student_id"),
                    createColumn("Student Name", "studentName", "student_name"),
                    createColumn("Device Name", "deviceName", "device_name"),
                    createColumn("Serial Number", "serialNumber", "serial_number"),
                    createColumn("Log Date", "logDate", "log_date"),
                    createColumn("Ingress Time", "ingressTime", "ingress_time"),
                    createColumn("Notes", "notes")
                );
                break;
            case ACTIVE_DEVICES:
                reportsTable.getColumns().addAll(
                    createColumn("Device ID", "requestDeviceId", "request_device_id", "deviceId"),
                    createColumn("Student ID", "studentId", "student_id"),
                    createColumn("Student Name", "studentName", "student_name"),
                    createColumn("Course/Year", "courseYearLevel", "course_year_level"),
                    createColumn("Device Name", "deviceName", "device_name"),
                    createColumn("Brand", "brand"),
                    createColumn("Model", "model"),
                    createColumn("Serial Number", "serialNumber", "serial_number"),
                    createColumn("Device Type", "deviceType", "device_type"),
                    createColumn("Entered At", "enteredAt", "lastEventTime", "last_event_time")
                );
                break;
            case DEVICE_FREQUENCY:
                reportsTable.getColumns().addAll(
                    createColumn("Device ID", "requestDeviceId", "request_device_id", "deviceId"),
                    createColumn("Student ID", "studentId", "student_id"),
                    createColumn("Student Name", "studentName", "student_name"),
                    createColumn("Course/Year", "courseYearLevel", "course_year_level"),
                    createColumn("Device Name", "deviceName", "device_name"),
                    createColumn("Device Type", "deviceType", "device_type"),
                    createColumn("Brand", "brand"),
                    createColumn("Model", "model"),
                    createColumn("Entry Count", "entryCount", "entry_count"),
                    createColumn("Exit Count", "exitCount", "exit_count"),
                    createColumn("First Seen", "firstSeen", "first_seen"),
                    createColumn("Last Seen", "lastSeen", "last_seen")
                );
                break;
            case INCIDENT_OVERRIDES:
                reportsTable.getColumns().addAll(
                    createColumn("Audit ID", "auditId", "audit_id"),
                    createColumn("Timestamp", "createdAt", "created_at"),
                    createColumn("Incident Action", "actionType", "action_type"),
                    createColumn("Target Table", "targetTable", "target_table"),
                    createColumn("Target ID", "targetId", "target_id"),
                    createColumn("IP Address", "ipAddress", "ip_address")
                );
                break;
            case PURPOSE_BREAKDOWN:
                reportsTable.getColumns().addAll(
                    createColumn("Request Purpose", "purpose"),
                    createColumn("Request Count", "requestCount", "request_count"),
                    createColumn("Total Approved Devices", "totalDevicesApproved", "total_devices_approved"),
                    createColumn("Percentage Volume", "percentage")
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
                Object val = map.get(key);
                if (val instanceof String && isTimestampKey(key)) {
                    if (isTimeOnlyKey(key)) {
                        return formatTimeOnly((String) val);
                    }
                    return com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimestamp((String) val);
                }
                return val;
            }
        }
        // Fallback: try case-insensitive check
        for (String mapKey : map.keySet()) {
            for (String key : keys) {
                if (mapKey.equalsIgnoreCase(key) && map.get(mapKey) != null) {
                    Object val = map.get(mapKey);
                    if (val instanceof String && isTimestampKey(key)) {
                        if (isTimeOnlyKey(key)) {
                            return formatTimeOnly((String) val);
                        }
                        return com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimestamp((String) val);
                    }
                    return map.get(mapKey);
                }
            }
        }
        return "";
    }

    private boolean isTimestampKey(String key) {
        if (key == null) return false;
        String lower = key.toLowerCase();
        return lower.contains("time") || lower.contains("at") || lower.contains("seen") || lower.equals("timestamp");
    }

    private boolean isTimeOnlyKey(String key) {
        if (key == null) return false;
        String lower = key.toLowerCase();
        return lower.contains("ingress") || lower.contains("egress");
    }

    private String formatTimeOnly(String timestampStr) {
        if (timestampStr == null || timestampStr.isEmpty() || "null".equalsIgnoreCase(timestampStr)) return "-";
        try {
            java.time.temporal.TemporalAccessor temporal;
            if (timestampStr.contains("Z") || timestampStr.contains("+") || (timestampStr.lastIndexOf("-") > 10)) {
                temporal = java.time.OffsetDateTime.parse(timestampStr);
            } else if (timestampStr.contains("T")) {
                temporal = java.time.LocalDateTime.parse(timestampStr);
            } else {
                return timestampStr;
            }
            
            java.time.LocalTime time;
            if (temporal instanceof java.time.OffsetDateTime) {
                time = ((java.time.OffsetDateTime) temporal).atZoneSameInstant(java.time.ZoneId.systemDefault()).toLocalTime();
            } else {
                time = ((java.time.LocalDateTime) temporal).toLocalTime();
            }
            
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a");
            return time.format(formatter);
        } catch (Exception e) {
            return timestampStr;
        }
    }

    private String getRawValueFromMap(Map<String, Object> map, String... keys) {
        if (map == null) return "";
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                return map.get(key).toString();
            }
        }
        for (String mapKey : map.keySet()) {
            for (String key : keys) {
                if (mapKey.equalsIgnoreCase(key) && map.get(mapKey) != null) {
                    return map.get(mapKey).toString();
                }
            }
        }
        return "";
    }

    @FXML
    public void handleGenerateReport() {
        generateReport(false);
    }

    private void generateReport(boolean silent) {
        String reportType = reportTypeBox.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (reportType == null) {
            if (!silent) {
                AlertHelper.showWarning("Report Generator", "Report Type Required", "Please select a report type first.");
            }
            return;
        }

        // Validate dates dynamically based on report selection
        if (DAILY_TRAFFIC.equals(reportType)) {
            if (start == null) {
                if (!silent) {
                    AlertHelper.showWarning("Report Generator", "Date Required", "Please specify a date.");
                }
                return;
            }
            if (start.isAfter(LocalDate.now())) {
                if (!silent) {
                    AlertHelper.showWarning("Report Generator", "Invalid Date", "Future dates are not allowed.");
                }
                return;
            }
        } else if (MONTHLY_TRAFFIC.equals(reportType)) {
            if (monthSelectBox.getValue() == null || yearSelectBox.getValue() == null) {
                if (!silent) {
                    AlertHelper.showWarning("Report Generator", "Month/Year Required", "Please select a month and a year.");
                }
                return;
            }
        } else if (DEVICE_FREQUENCY.equals(reportType) || INCIDENT_OVERRIDES.equals(reportType) || MISSED_CHECKOUTS.equals(reportType)) {
            if (start == null || end == null) {
                if (!silent) {
                    AlertHelper.showWarning("Report Generator", "Date Range Required", "Please specify both starting and ending dates.");
                }
                return;
            }
            if (start.isAfter(LocalDate.now()) || end.isAfter(LocalDate.now())) {
                if (!silent) {
                    AlertHelper.showWarning("Report Generator", "Invalid Date", "Future dates are not allowed.");
                }
                return;
            }
        }

        try {
            List<Map<String, Object>> records;
            if (DAILY_TRAFFIC.equals(reportType)) {
                records = reportService.getDailyTrafficReport(start.toString(), null, null, null);
            } else if (MONTHLY_TRAFFIC.equals(reportType)) {
                int month = monthSelectBox.getSelectionModel().getSelectedIndex() + 1;
                Integer year = yearSelectBox.getValue();
                records = reportService.getMonthlyTrafficReport(year != null ? year : LocalDate.now().getYear(), month);
            } else if (MISSED_CHECKOUTS.equals(reportType)) {
                records = reportService.getMissedCheckoutReport(start.toString(), end.toString());
            } else if (ACTIVE_DEVICES.equals(reportType)) {
                records = reportService.getActiveDevicesReport();
            } else if (DEVICE_FREQUENCY.equals(reportType)) {
                records = reportService.getDeviceFrequencyReport(start.toString(), end.toString());
            } else if (INCIDENT_OVERRIDES.equals(reportType)) {
                records = reportService.getIncidentsReport(start.toString(), end.toString());
            } else { // PURPOSE_BREAKDOWN
                records = reportService.getPurposeBreakdownReport();
            }

            reportList.setAll(records);
            updateVisualAnalytics(reportType, records);
            if (records.isEmpty() && !silent) {
                AlertHelper.showInfo("Report Info", "Empty Dataset", "No matching report logs found.");
            }
        } catch (Exception e) {
            if (!silent) {
                AlertHelper.showError("Generation Error", "Report failed", e.getMessage());
            }
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
                String eventTime = getRawValueFromMap(record, "ingressTime", "ingress_time");
                String exitTime = getRawValueFromMap(record, "egressTime", "egress_time");
                
                String inHour = parseHourFromTime(eventTime);
                if (!"Unknown".equals(inHour)) {
                    entryCountsByHour.put(inHour, entryCountsByHour.getOrDefault(inHour, 0L) + 1);
                }

                if (exitTime != null && !exitTime.isEmpty() && !"null".equalsIgnoreCase(exitTime)) {
                    String outHour = parseHourFromTime(exitTime);
                    if (!"Unknown".equals(outHour)) {
                        exitCountsByHour.put(outHour, exitCountsByHour.getOrDefault(outHour, 0L) + 1);
                    }
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
                String category = String.valueOf(getValueFromMap(record, "deviceCategory", "device_category"));
                if (category == null || category.trim().isEmpty() || "null".equals(category)) {
                    category = "Unknown Category";
                }
                int total = getNumericInt(record, "totalEvents", "total_events");
                categoryEvents.put(category, categoryEvents.getOrDefault(category, 0) + total);
            }

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            categoryEvents.forEach((category, total) -> {
                pieData.add(new PieChart.Data(category + " (" + total + ")", total));
            });
            pieChart.setData(pieData);
            chartContainer.getChildren().add(pieChart);

        } else if (MISSED_CHECKOUTS.equals(reportType)) {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Date");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Missed Count");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Missed Checkouts by Log Date");
            barChart.setAnimated(false);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Stale Ingress Scans");

            Map<String, Integer> dateCounts = new TreeMap<>();
            for (Map<String, Object> record : records) {
                String date = String.valueOf(getValueFromMap(record, "logDate", "log_date"));
                dateCounts.put(date, dateCounts.getOrDefault(date, 0) + 1);
            }

            dateCounts.forEach((date, count) -> {
                series.getData().add(new XYChart.Data<>(date, count));
            });

            barChart.getData().add(series);
            chartContainer.getChildren().add(barChart);

        } else if (ACTIVE_DEVICES.equals(reportType)) {
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Live Campus Device Category Share");
            pieChart.setAnimated(false);

            Map<String, Integer> typeCounts = new HashMap<>();
            for (Map<String, Object> record : records) {
                String devType = String.valueOf(getValueFromMap(record, "deviceType", "device_type"));
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
                int count1 = getNumericInt(r1, "entryCount", "entry_count");
                int count2 = getNumericInt(r2, "entryCount", "entry_count");
                return Integer.compare(count2, count1);
            });

            int limit = Math.min(10, sortedRecords.size());
            for (int i = 0; i < limit; i++) {
                Map<String, Object> record = sortedRecords.get(i);
                String studentName = String.valueOf(getValueFromMap(record, "studentName", "student_name"));
                String deviceName = String.valueOf(getValueFromMap(record, "deviceName", "device_name"));
                String label = studentName + " (" + deviceName + ")";
                int entryCount = getNumericInt(record, "entryCount", "entry_count");
                series.getData().add(new XYChart.Data<>(label, entryCount));
            }

            barChart.getData().add(series);
            chartContainer.getChildren().add(barChart);

        } else if (INCIDENT_OVERRIDES.equals(reportType)) {
            Map<String, Integer> freqMap = new HashMap<>();
            for (Map<String, Object> record : records) {
                Object val = getValueFromMap(record, "actionType", "action_type");
                String action = val != null ? val.toString() : "Unknown";
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
            
        } else if (PURPOSE_BREAKDOWN.equals(reportType)) {
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Request Purpose Breakdown");
            pieChart.setAnimated(false);

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            for (Map<String, Object> record : records) {
                String purpose = String.valueOf(getValueFromMap(record, "purpose"));
                int count = getNumericInt(record, "requestCount", "request_count");
                pieData.add(new PieChart.Data(purpose + " (" + count + ")", count));
            }

            pieChart.setData(pieData);
            chartContainer.getChildren().add(pieChart);
        }
    }

    private String parseHourFromTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty() || "null".equalsIgnoreCase(timeStr)) return "Unknown";
        if (timeStr.contains("T")) {
            try {
                int tIdx = timeStr.indexOf('T');
                String timePart = timeStr.substring(tIdx + 1);
                // Strip timezone info if any
                int zIdx = timePart.indexOf('Z');
                if (zIdx < 0) zIdx = timePart.indexOf('+');
                if (zIdx < 0) zIdx = timePart.lastIndexOf('-'); // if offset like -08:00
                if (zIdx > 0) {
                    timePart = timePart.substring(0, zIdx);
                }
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
        } else if (timeStr.contains(" ")) {
            try {
                String[] spaceParts = timeStr.split("\\s+");
                String timePart = null;
                String ampm = null;
                for (String part : spaceParts) {
                    if (part.contains(":")) {
                        timePart = part;
                    } else if (part.equalsIgnoreCase("AM") || part.equalsIgnoreCase("PM")) {
                        ampm = part.toUpperCase();
                    }
                }
                if (timePart != null) {
                    String[] parts = timePart.split(":");
                    if (parts.length >= 1) {
                        int hour = Integer.parseInt(parts[0]);
                        if (ampm == null) {
                            ampm = hour >= 12 ? "PM" : "AM";
                        } else {
                            hour = hour % 12;
                            if (hour == 0) hour = 12;
                            return String.format("%02d:00 %s", hour, ampm);
                        }
                        int displayHour = hour % 12;
                        if (displayHour == 0) displayHour = 12;
                        return String.format("%02d:00 %s", displayHour, ampm);
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        } else if (timeStr.contains(":")) {
            try {
                String[] parts = timeStr.split(":");
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
