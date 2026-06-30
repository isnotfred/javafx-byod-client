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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.application.Platform;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.SnapshotParameters;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import javafx.stage.FileChooser;

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
    @FXML private HBox rangeSelectContainer;
    @FXML private ComboBox<String> rangeSelectBox;

    private final ReportService reportService = new ReportService();
    private final ObservableList<Map<String, Object>> reportList = FXCollections.observableArrayList();

    private static final String DAILY_TRAFFIC = "Daily Traffic Report";
    private static final String MONTHLY_TRAFFIC = "Monthly Traffic Report";
    private static final String MISSED_CHECKOUTS = "Missed Checkouts Report";
    private static final String ACTIVE_DEVICES = "Active Devices Report";
    private static final String DEVICE_FREQUENCY = "Device Frequency Report";
    private static final String INCIDENT_OVERRIDES = "Incident Overrides Report";
    private static final String PURPOSE_BREAKDOWN = "Purpose Breakdown Report";
    private static final String LATE_SCANS = "Late Check-ins & Check-outs Report";

    @FXML
    public void initialize() {
        reportTypeBox.getItems().addAll(
            DAILY_TRAFFIC,
            MONTHLY_TRAFFIC,
            MISSED_CHECKOUTS,
            DEVICE_FREQUENCY,
            INCIDENT_OVERRIDES,
            PURPOSE_BREAKDOWN,
            LATE_SCANS
        );
        reportsTable.setItems(reportList);

        // Clear table, setup columns and adjust filters when selection changes
        reportTypeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            reportList.clear();
            setupTableColumns(newVal);
            adjustFilters(newVal);
            generateReport(true);
        });

        if (rangeSelectBox != null) {
            rangeSelectBox.getItems().addAll("All Time", "Today", "This Week", "Monthly", "Range");
            rangeSelectBox.getSelectionModel().select("Today");
            rangeSelectBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                adjustFilters(reportTypeBox.getValue());
                generateReport(true);
            });
        }

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

        // Add change listeners to auto-generate when other values are selected
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> generateReport(true));
        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> generateReport(true));
        monthSelectBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> generateReport(true));
        yearSelectBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> generateReport(true));

        reportTypeBox.getSelectionModel().select(DAILY_TRAFFIC);
    }

    private void adjustFilters(String reportType) {
        if (startDateContainer == null || endDateContainer == null || startDateLabel == null) {
            return;
        }

        // Hide range container by default
        if (rangeSelectContainer != null) {
            rangeSelectContainer.setVisible(false);
            rangeSelectContainer.setManaged(false);
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
            if (rangeSelectContainer != null && rangeSelectBox != null) {
                rangeSelectContainer.setVisible(true);
                rangeSelectContainer.setManaged(true);
                
                String rangeType = rangeSelectBox.getValue();
                if ("Range".equals(rangeType)) {
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
                } else {
                    startDateContainer.setVisible(false);
                    startDateContainer.setManaged(false);
                    endDateContainer.setVisible(false);
                    endDateContainer.setManaged(false);
                }
            } else {
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
    }

    private TableColumn<Map<String, Object>, Object> createColumn(String header, String... keys) {
        TableColumn<Map<String, Object>, Object> column = new TableColumn<>(header);
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            getValueFromMap(cellData.getValue(), keys)
        ));
        return column;
    }

    private TableColumn<Map<String, Object>, Object> createLateHighlightedColumn(String header, String lateFlagKey, String... keys) {
        TableColumn<Map<String, Object>, Object> column = new TableColumn<>(header);
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
            getValueFromMap(cellData.getValue(), keys)
        ));
        column.setCellFactory(col -> new TableCell<Map<String, Object>, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    Map<String, Object> rowData = getTableView().getItems().get(getIndex());
                    if (rowData != null) {
                        Object flag = rowData.get(lateFlagKey);
                        if (flag == null) {
                            String alternativeKey = lateFlagKey.equals("isLateIngress") ? "is_late_ingress" : "is_late_egress";
                            flag = rowData.get(alternativeKey);
                        }
                        if (Boolean.TRUE.equals(flag) || "true".equalsIgnoreCase(String.valueOf(flag))) {
                            setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
                        } else {
                            setStyle("");
                        }
                    } else {
                        setStyle("");
                    }
                }
            }
        });
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
                    createLateHighlightedColumn("Checked-In", "isLateIngress", "ingressTime", "ingress_time"),
                    createLateHighlightedColumn("Checked-Out", "isLateEgress", "egressTime", "egress_time")
                );
                break;
            case LATE_SCANS:
                reportsTable.getColumns().addAll(
                    createColumn("Log ID", "logId", "transactionId", "transaction_id"),
                    createColumn("Student ID", "studentId", "student_id"),
                    createColumn("Student Name", "studentName", "student_name"),
                    createColumn("Course/Year", "courseYearLevel", "course_year_level"),
                    createColumn("Device Name", "deviceName", "device_name"),
                    createColumn("Serial Number", "serialNumber", "serial_number"),
                    createColumn("Expected Ingress", "expectedIngressTime", "expected_ingress_time"),
                    createColumn("Expected Egress", "expectedEgressTime", "expected_egress_time"),
                    createLateHighlightedColumn("Checked-In", "isLateIngress", "ingressTime", "ingress_time"),
                    createLateHighlightedColumn("Checked-Out", "isLateEgress", "egressTime", "egress_time")
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
                    createColumn("Student ID", "studentId", "student_id"),
                    createColumn("Student Name", "studentName", "student_name"),
                    createColumn("Device Name", "deviceName", "device_name"),
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

        // Set reasonable default widths that fit headers and make Student Name the widest
        for (TableColumn<Map<String, Object>, ?> col : reportsTable.getColumns()) {
            String header = col.getText();
            double calculatedWidth = (header != null ? header.length() : 10) * 9.5 + 40.0;
            
            if ("Student Name".equals(header)) {
                col.setPrefWidth(220.0);
            } else if ("Log ID".equals(header)) {
                col.setPrefWidth(75.0);
            } else {
                col.setPrefWidth(Math.max(calculatedWidth, 120.0));
            }
        }

        // Force table layout pass to distribute columns evenly and eliminate empty ending column space
        reportsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        reportsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private Object getValueFromMap(Map<String, Object> map, String... keys) {
        if (map == null) return "";
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null) {
                Object val = map.get(key);
                if (val instanceof String) {
                    String strVal = (String) val;
                    if ("Project Prototypes (Optional SN)".equalsIgnoreCase(strVal.trim()) || "Project Prototypes".equalsIgnoreCase(strVal.trim())) {
                        return "Project Prototypes";
                    }
                    if (isTimestampKey(key)) {
                        if (isTimeOnlyKey(key)) {
                            return formatTimeOnly(strVal);
                        }
                        return com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimestamp(strVal);
                    }
                }
                return val;
            }
        }
        // Fallback: try case-insensitive check
        for (String mapKey : map.keySet()) {
            for (String key : keys) {
                if (mapKey.equalsIgnoreCase(key) && map.get(mapKey) != null) {
                    Object val = map.get(mapKey);
                    if (val instanceof String) {
                        String strVal = (String) val;
                        if ("Project Prototypes (Optional SN)".equalsIgnoreCase(strVal.trim()) || "Project Prototypes".equalsIgnoreCase(strVal.trim())) {
                            return "Project Prototypes";
                        }
                        if (isTimestampKey(key)) {
                            if (isTimeOnlyKey(key)) {
                                return formatTimeOnly(strVal);
                            }
                            return com.pup.byod.javafxbyodclient.util.DateFormatter.formatTimestamp(strVal);
                        }
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
            // First check if it's a simple local time string like "08:00:00" or "08:00"
            if (!timestampStr.contains("T") && !timestampStr.contains("Z") && !timestampStr.contains("+") && timestampStr.split(":").length >= 2) {
                java.time.LocalTime time = java.time.LocalTime.parse(timestampStr);
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a");
                return time.format(formatter);
            }

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
                String strVal = map.get(key).toString();
                if ("Project Prototypes (Optional SN)".equalsIgnoreCase(strVal.trim()) || "Project Prototypes".equalsIgnoreCase(strVal.trim())) {
                    return "Project Prototypes";
                }
                return strVal;
            }
        }
        for (String mapKey : map.keySet()) {
            for (String key : keys) {
                if (mapKey.equalsIgnoreCase(key) && map.get(mapKey) != null) {
                    String strVal = map.get(mapKey).toString();
                    if ("Project Prototypes (Optional SN)".equalsIgnoreCase(strVal.trim()) || "Project Prototypes".equalsIgnoreCase(strVal.trim())) {
                        return "Project Prototypes";
                    }
                    return strVal;
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

        if (reportType == null) {
            if (!silent) {
                AlertHelper.showWarning("Report Generator", "Report Type Required", "Please select a report type first.");
            }
            return;
        }

        LocalDate start = null;
        LocalDate end = null;

        // Validate/extract dates dynamically based on report selection
        if (DAILY_TRAFFIC.equals(reportType)) {
            start = startDatePicker.getValue();
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
        } else if (DEVICE_FREQUENCY.equals(reportType) || INCIDENT_OVERRIDES.equals(reportType) || MISSED_CHECKOUTS.equals(reportType) || LATE_SCANS.equals(reportType)) {
            String rangeType = rangeSelectBox != null ? rangeSelectBox.getValue() : "Today";
            if (rangeType == null) rangeType = "Today";
            
            switch (rangeType) {
                case "All Time":
                    start = LocalDate.of(1970, 1, 1);
                    end = LocalDate.now();
                    break;
                case "Today":
                    if (MISSED_CHECKOUTS.equals(reportType)) {
                        start = LocalDate.now().minusDays(1);
                        end = LocalDate.now().minusDays(1);
                    } else {
                        start = LocalDate.now();
                        end = LocalDate.now();
                    }
                    break;
                case "This Week":
                    LocalDate now = LocalDate.now();
                    start = now.minusDays(now.getDayOfWeek().getValue() - 1);
                    end = now;
                    break;
                case "Monthly":
                    start = LocalDate.now().withDayOfMonth(1);
                    end = LocalDate.now();
                    break;
                case "Range":
                    start = startDatePicker.getValue();
                    end = endDatePicker.getValue();
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
                    if (start.isAfter(end)) {
                        if (!silent) {
                            AlertHelper.showWarning("Report Generator", "Invalid Range", "Start date must be before or equal to End date.");
                        }
                        return;
                    }
                    break;
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
            } else if (LATE_SCANS.equals(reportType)) {
                records = reportService.getLateScansReport(start.toString(), end.toString());
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
        } else if (LATE_SCANS.equals(reportType)) {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Scan Type");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Late Scans Count");

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Late Check-ins vs Check-outs");
            barChart.setAnimated(false);

            long lateCheckIns = 0;
            long lateCheckOuts = 0;

            for (Map<String, Object> record : records) {
                Object inFlag = record.get("isLateIngress");
                if (inFlag == null) inFlag = record.get("is_late_ingress");
                if (Boolean.TRUE.equals(inFlag) || "true".equalsIgnoreCase(String.valueOf(inFlag))) {
                    lateCheckIns++;
                }

                Object outFlag = record.get("isLateEgress");
                if (outFlag == null) outFlag = record.get("is_late_egress");
                if (Boolean.TRUE.equals(outFlag) || "true".equalsIgnoreCase(String.valueOf(outFlag))) {
                    lateCheckOuts++;
                }
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Late Transactions");
            series.getData().add(new XYChart.Data<>("Late Check-in", lateCheckIns));
            series.getData().add(new XYChart.Data<>("Late Check-out", lateCheckOuts));

            barChart.getData().add(series);
            chartContainer.getChildren().add(barChart);
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

    @FXML
    public void handleExportExcel() {
        if (reportList.isEmpty()) {
            AlertHelper.showWarning("Export Warning", "No Data Available", "There are no report records to export.");
            return;
        }

        // Get active chart if visible
        Chart activeChart = null;
        if (!chartContainer.getChildren().isEmpty()) {
            javafx.scene.Node firstNode = chartContainer.getChildren().get(0);
            if (firstNode instanceof Chart) {
                activeChart = (Chart) firstNode;
            }
        }

        // Capture snapshot on JavaFX thread
        byte[] chartSnapshotBytes = null;
        if (activeChart != null) {
            chartSnapshotBytes = getChartSnapshotBytes(activeChart);
        }

        // Capture columns and data on JavaFX thread
        List<String> headers = new ArrayList<>();
        for (TableColumn<Map<String, Object>, ?> col : reportsTable.getColumns()) {
            headers.add(col.getText());
        }

        List<List<String>> rowDataList = new ArrayList<>();
        for (int r = 0; r < reportsTable.getItems().size(); r++) {
            List<String> row = new ArrayList<>();
            for (TableColumn<Map<String, Object>, ?> col : reportsTable.getColumns()) {
                Object cellVal = col.getCellData(r);
                row.add(cellVal != null ? cellVal.toString() : "");
            }
            rowDataList.add(row);
        }

        String reportType = reportTypeBox.getValue();
        String dateInfo = "";
        String fileSuffix = "";
        if (DAILY_TRAFFIC.equals(reportType) && startDatePicker.getValue() != null) {
            dateInfo = "Date: " + startDatePicker.getValue().toString();
            fileSuffix = "_" + startDatePicker.getValue().toString();
        } else if (MONTHLY_TRAFFIC.equals(reportType) && monthSelectBox.getValue() != null && yearSelectBox.getValue() != null) {
            dateInfo = "Period: " + monthSelectBox.getValue() + " " + yearSelectBox.getValue();
            fileSuffix = "_" + monthSelectBox.getValue() + "_" + yearSelectBox.getValue();
        } else if (rangeSelectBox != null && !"All Time".equals(rangeSelectBox.getValue())) {
            if ("Today".equals(rangeSelectBox.getValue()) && MISSED_CHECKOUTS.equals(reportType)) {
                dateInfo = "Range: Yesterday (" + LocalDate.now().minusDays(1).toString() + ")";
                fileSuffix = "_Yesterday_" + LocalDate.now().minusDays(1).toString();
            } else {
                dateInfo = "Range: " + rangeSelectBox.getValue();
                if ("Range".equals(rangeSelectBox.getValue()) && startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                    dateInfo += " (" + startDatePicker.getValue() + " to " + endDatePicker.getValue() + ")";
                    fileSuffix = "_" + startDatePicker.getValue() + "_to_" + endDatePicker.getValue();
                } else {
                    fileSuffix = "_" + rangeSelectBox.getValue().replace(" ", "_") + "_" + LocalDate.now().toString();
                }
            }
        } else {
            dateInfo = "All Time";
            fileSuffix = "_All_Time_" + LocalDate.now().toString();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Analytical Report");
        fileChooser.setInitialFileName("BYOD_" + reportType.replace(" ", "_") + fileSuffix + ".xlsx");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"));
        File file = fileChooser.showSaveDialog(reportsTable.getScene().getWindow());

        if (file == null) return;

        final byte[] finalChartBytes = chartSnapshotBytes;
        final String finalDateInfo = dateInfo;
        final String finalReportType = reportType;

        new Thread(() -> {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Analytics Report");
                sheet.setDisplayGridlines(true);

                // Colors
                org.apache.poi.xssf.usermodel.XSSFColor navyColor = new org.apache.poi.xssf.usermodel.XSSFColor(new java.awt.Color(16, 42, 67), null);
                org.apache.poi.xssf.usermodel.XSSFColor stripeColor = new org.apache.poi.xssf.usermodel.XSSFColor(new java.awt.Color(248, 250, 252), null);

                // Fonts
                Font titleFont = workbook.createFont();
                titleFont.setFontName("Segoe UI");
                titleFont.setFontHeightInPoints((short) 16);
                titleFont.setBold(true);

                Font subtitleFont = workbook.createFont();
                subtitleFont.setFontName("Segoe UI");
                subtitleFont.setFontHeightInPoints((short) 11);
                subtitleFont.setItalic(true);

                Font headerFont = workbook.createFont();
                headerFont.setFontName("Segoe UI");
                headerFont.setFontHeightInPoints((short) 11);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerFont.setBold(true);

                Font bodyFont = workbook.createFont();
                bodyFont.setFontName("Segoe UI");
                bodyFont.setFontHeightInPoints((short) 11);

                // Styles
                CellStyle titleStyle = workbook.createCellStyle();
                titleStyle.setFont(titleFont);

                CellStyle subtitleStyle = workbook.createCellStyle();
                subtitleStyle.setFont(subtitleFont);

                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFont(headerFont);
                ((org.apache.poi.xssf.usermodel.XSSFCellStyle) headerStyle).setFillForegroundColor(navyColor);
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);

                CellStyle bodyStyle = workbook.createCellStyle();
                bodyStyle.setFont(bodyFont);
                bodyStyle.setBorderTop(BorderStyle.THIN);
                bodyStyle.setBorderBottom(BorderStyle.THIN);
                bodyStyle.setBorderLeft(BorderStyle.THIN);
                bodyStyle.setBorderRight(BorderStyle.THIN);
                bodyStyle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
                bodyStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
                bodyStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
                bodyStyle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

                CellStyle zebraStyle = workbook.createCellStyle();
                zebraStyle.setFont(bodyFont);
                ((org.apache.poi.xssf.usermodel.XSSFCellStyle) zebraStyle).setFillForegroundColor(stripeColor);
                zebraStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                zebraStyle.setBorderTop(BorderStyle.THIN);
                zebraStyle.setBorderBottom(BorderStyle.THIN);
                zebraStyle.setBorderLeft(BorderStyle.THIN);
                zebraStyle.setBorderRight(BorderStyle.THIN);
                zebraStyle.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
                zebraStyle.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
                zebraStyle.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
                zebraStyle.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());

                // Title rows
                Row r0 = sheet.createRow(0);
                org.apache.poi.ss.usermodel.Cell c0 = r0.createCell(0);
                c0.setCellValue("Polytechnic University of the Philippines — BYOD System");
                c0.setCellStyle(titleStyle);
                if (headers.size() > 1) {
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, headers.size() - 1));
                }

                Row r1 = sheet.createRow(1);
                org.apache.poi.ss.usermodel.Cell c1 = r1.createCell(0);
                c1.setCellValue("Report: " + finalReportType + " (" + finalDateInfo + ")");
                c1.setCellStyle(subtitleStyle);
                if (headers.size() > 1) {
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, headers.size() - 1));
                }

                // Headers row
                Row headerRow = sheet.createRow(3);
                for (int i = 0; i < headers.size(); i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers.get(i));
                    cell.setCellStyle(headerStyle);
                }

                // Data rows
                int rowIdx = 4;
                for (List<String> rowData : rowDataList) {
                    Row row = sheet.createRow(rowIdx++);
                    CellStyle currentStyle = (rowIdx % 2 == 0) ? zebraStyle : bodyStyle;
                    for (int i = 0; i < rowData.size(); i++) {
                        org.apache.poi.ss.usermodel.Cell cell = row.createCell(i);
                        cell.setCellValue(rowData.get(i));
                        cell.setCellStyle(currentStyle);
                    }
                }

                // Auto size columns
                for (int i = 0; i < headers.size(); i++) {
                    sheet.autoSizeColumn(i);
                }

                // Insert Chart Image
                if (finalChartBytes != null) {
                    int pictureIdx = workbook.addPicture(finalChartBytes, Workbook.PICTURE_TYPE_PNG);
                    CreationHelper helper = workbook.getCreationHelper();
                    Drawing<?> drawing = sheet.createDrawingPatriarch();
                    ClientAnchor anchor = helper.createClientAnchor();

                    // Place chart to the right of the table
                    anchor.setCol1(headers.size() + 1);
                    anchor.setRow1(3);

                    Picture pict = drawing.createPicture(anchor, pictureIdx);
                    pict.resize(); // Standard sizing
                }

                // Write file
                try (FileOutputStream out = new FileOutputStream(file)) {
                    workbook.write(out);
                }

                        Platform.runLater(() -> {
                    AlertHelper.showInfo("Success", "Report Exported", "Successfully exported report and charts to " + file.getName());
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Export Failure", "Failed to write Excel worksheet", e.getMessage());
                });
            }
        }).start();
    }

    private byte[] getChartSnapshotBytes(Chart chart) {
        if (chart == null) return null;
        try {
            WritableImage image = chart.snapshot(new SnapshotParameters(), null);
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            PixelReader pixelReader = image.getPixelReader();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bufferedImage.setRGB(x, y, pixelReader.getArgb(x, y));
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
