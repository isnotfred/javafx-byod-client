package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.service.ReportService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.DateFormatter;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class LogsScreenController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterPeriodComboBox;
    @FXML private TableView<Map<String, Object>> logsTable;
    @FXML private TableColumn<Map<String, Object>, String> colDate;
    @FXML private TableColumn<Map<String, Object>, String> colStudentId;
    @FXML private TableColumn<Map<String, Object>, String> colStudentName;
    @FXML private TableColumn<Map<String, Object>, String> colDeviceName;
    @FXML private TableColumn<Map<String, Object>, String> colSerial;
    @FXML private TableColumn<Map<String, Object>, String> colIngress;
    @FXML private TableColumn<Map<String, Object>, String> colEgress;

    private final ReportService reportService = new ReportService();
    private final ObservableList<Map<String, Object>> logsList = FXCollections.observableArrayList();
    private FilteredList<Map<String, Object>> filteredLogsList;

    private static final String TODAY = "Today";
    private static final String THIS_WEEK = "This Week (Mon-Sat)";
    private static final String MONTHLY = "Monthly";

    @FXML
    public void initialize() {
        // Table Columns Binding
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(
                formatDateOnly(getVal(cellData.getValue(), "ingress_time", "ingressTime"))
        ));
        colStudentId.setCellValueFactory(cellData -> new SimpleStringProperty(getVal(cellData.getValue(), "student_id", "studentId")));
        colStudentName.setCellValueFactory(cellData -> new SimpleStringProperty(getVal(cellData.getValue(), "student_name", "studentName")));
        colDeviceName.setCellValueFactory(cellData -> new SimpleStringProperty(getVal(cellData.getValue(), "device_name", "deviceName")));
        colSerial.setCellValueFactory(cellData -> new SimpleStringProperty(getVal(cellData.getValue(), "serial_number", "serialNumber")));
        
        colIngress.setCellValueFactory(cellData -> new SimpleStringProperty(
                formatTimeOnly(getVal(cellData.getValue(), "ingress_time", "ingressTime"))
        ));
        colEgress.setCellValueFactory(cellData -> new SimpleStringProperty(
                formatTimeOnly(getVal(cellData.getValue(), "egress_time", "egressTime"))
        ));

        // Dropdown settings
        filterPeriodComboBox.getItems().addAll(TODAY, THIS_WEEK, MONTHLY);
        filterPeriodComboBox.setValue(TODAY);

        // Filter and Search bindings
        filteredLogsList = new FilteredList<>(logsList, p -> true);
        logsTable.setItems(filteredLogsList);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredLogsList.setPredicate(log -> {
                if (newVal == null || newVal.trim().isEmpty()) {
                    return true;
                }
                String lower = newVal.toLowerCase().trim();
                return getVal(log, "student_id", "studentId").toLowerCase().contains(lower) ||
                       getVal(log, "serial_number", "serialNumber").toLowerCase().contains(lower) ||
                       getVal(log, "student_name", "studentName").toLowerCase().contains(lower);
            });
        });

        // Load data on filter change
        filterPeriodComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            colDate.setVisible(!TODAY.equals(newVal));
            loadLogs(newVal);
        });

        colDate.setVisible(false);
        loadLogs(TODAY);
    }

    private void loadLogs(String period) {
        logsList.clear();
        logsTable.setPlaceholder(new Label("Querying logs from server..."));

        new Thread(() -> {
            try {
                List<Map<String, Object>> records = new ArrayList<>();
                LocalDate today = LocalDate.now();

                if (TODAY.equals(period)) {
                    records = reportService.getDailyTrafficReport(today.toString(), null, null, null);
                } else if (THIS_WEEK.equals(period)) {
                    LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    for (int i = 0; i < 6; i++) { // Mon, Tue, Wed, Thu, Fri, Sat
                        LocalDate date = monday.plusDays(i);
                        List<Map<String, Object>> daily = reportService.getDailyTrafficReport(date.toString(), null, null, null);
                        records.addAll(daily);
                    }
                } else if (MONTHLY.equals(period)) {
                    // Loop over all days from start of month to today
                    LocalDate startOfMonth = today.withDayOfMonth(1);
                    int lastDay = today.getDayOfMonth();
                    for (int i = 0; i < lastDay; i++) {
                        LocalDate date = startOfMonth.plusDays(i);
                        List<Map<String, Object>> daily = reportService.getDailyTrafficReport(date.toString(), null, null, null);
                        records.addAll(daily);
                    }
                }

                // Sort logs descending by Ingress Time
                records.sort((r1, r2) -> {
                    String t1 = getVal(r1, "ingress_time", "ingressTime");
                    String t2 = getVal(r2, "ingress_time", "ingressTime");
                    return t2.compareTo(t1);
                });

                final List<Map<String, Object>> finalRecords = records;
                Platform.runLater(() -> {
                    logsList.setAll(finalRecords);
                    logsTable.setPlaceholder(new Label("No logs found for this period."));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Logs Error", "Failed to fetch transaction logs", e.getMessage());
                    logsTable.setPlaceholder(new Label("Failed to load logs."));
                });
            }
        }).start();
    }

    private String getVal(Map<String, Object> map, String... keys) {
        if (map == null) return "";
        for (String k : keys) {
            if (map.containsKey(k) && map.get(k) != null) {
                return map.get(k).toString();
            }
        }
        return "";
    }

    private String formatTime(String timestampStr) {
        if (timestampStr == null || timestampStr.isEmpty() || "null".equals(timestampStr)) return "-";
        return DateFormatter.formatTimestamp(timestampStr);
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

    private String formatDateOnly(String timestampStr) {
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
            
            java.time.LocalDate date;
            if (temporal instanceof java.time.OffsetDateTime) {
                date = ((java.time.OffsetDateTime) temporal).atZoneSameInstant(java.time.ZoneId.systemDefault()).toLocalDate();
            } else {
                date = ((java.time.LocalDateTime) temporal).toLocalDate();
            }
            return date.toString();
        } catch (Exception e) {
            return timestampStr;
        }
    }

    private String deriveStatus(Map<String, Object> log) {
        String egress = getVal(log, "egress_time", "egressTime");
        String noEgress = getVal(log, "no_egress_marked", "noEgressMarked");

        if ("true".equalsIgnoreCase(noEgress)) {
            return "Missed Checkout";
        } else if (egress != null && !egress.isEmpty() && !"null".equalsIgnoreCase(egress)) {
            return "Checked Out";
        } else {
            return "Checked In";
        }
    }

    @FXML
    public void handleExportExcel() {
        if (logsList.isEmpty()) {
            AlertHelper.showWarning("Export Warning", "No Data Available", "There are no log records to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Transaction Logs");
        fileChooser.setInitialFileName("BYOD_Gate_Logs_" + filterPeriodComboBox.getValue().replace(" ", "_") + ".xlsx");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"));
        File file = fileChooser.showSaveDialog(logsTable.getScene().getWindow());

        if (file == null) return;

        new Thread(() -> {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Gate Logs");

                // Headers Styling
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font headerFont = workbook.createFont();
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);

                // Row Borders Styling
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);

                // Row Headers creation
                Row headerRow = sheet.createRow(0);
                String[] headers = {
                        "Log ID", "Date", "Student ID", "Student Name", "Device",
                        "Serial Number", "Ingress Time", "Egress Time", "Status", "Notes"
                };
                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell c = headerRow.createCell(i);
                    c.setCellValue(headers[i]);
                    c.setCellStyle(headerStyle);
                }

                // Add Row Data
                int rowIdx = 1;
                for (Map<String, Object> log : filteredLogsList) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(getVal(log, "transaction_id", "transactionId"));
                    row.createCell(1).setCellValue(getVal(log, "log_date", "logDate"));
                    row.createCell(2).setCellValue(getVal(log, "student_id", "studentId"));
                    row.createCell(3).setCellValue(getVal(log, "student_name", "studentName"));
                    row.createCell(4).setCellValue(getVal(log, "device_name", "deviceName"));
                    row.createCell(5).setCellValue(getVal(log, "serial_number", "serialNumber"));
                    row.createCell(6).setCellValue(formatTime(getVal(log, "ingress_time", "ingressTime")));
                    row.createCell(7).setCellValue(formatTime(getVal(log, "egress_time", "egressTime")));
                    row.createCell(8).setCellValue(deriveStatus(log));
                    row.createCell(9).setCellValue(getVal(log, "notes"));

                    for (int i = 0; i < headers.length; i++) {
                        org.apache.poi.ss.usermodel.Cell cell = row.getCell(i);
                        if (cell != null) cell.setCellStyle(cellStyle);
                    }
                }

                // Auto size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                try (FileOutputStream out = new FileOutputStream(file)) {
                    workbook.write(out);
                }

                Platform.runLater(() -> {
                    AlertHelper.showInfo("Success", "Excel Exported", "Successfully exported transaction logs to " + file.getName());
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Export Failure", "Failed to write Excel worksheet", e.getMessage());
                });
            }
        }).start();
    }
}
