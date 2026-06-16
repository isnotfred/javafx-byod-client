package com.pup.byod.javafxbyodclient.util;

import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class CsvExportHelper {
    public static <T> void exportToCsv(TableView<T> tableView, Window ownerWindow, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to CSV");
        fileChooser.setInitialFileName(defaultFileName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        
        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file == null) {
            return;
        }
        
        try (PrintWriter writer = new PrintWriter(file)) {
            // Write headers
            List<javafx.scene.control.TableColumn<T, ?>> columns = tableView.getColumns();
            StringBuilder headerRow = new StringBuilder();
            for (int i = 0; i < columns.size(); i++) {
                String colText = columns.get(i).getText();
                // Skip selection checkbox columns or blank headers
                if ("Select".equalsIgnoreCase(colText) || colText == null || colText.trim().isEmpty()) {
                    continue;
                }
                if (headerRow.length() > 0) {
                    headerRow.append(",");
                }
                headerRow.append(escapeCsv(colText));
            }
            writer.println(headerRow.toString());
            
            // Write data rows
            for (T item : tableView.getItems()) {
                StringBuilder dataRow = new StringBuilder();
                for (javafx.scene.control.TableColumn<T, ?> col : columns) {
                    String colText = col.getText();
                    if ("Select".equalsIgnoreCase(colText) || colText == null || colText.trim().isEmpty()) {
                        continue;
                    }
                    if (dataRow.length() > 0) {
                        dataRow.append(",");
                    }
                    Object cellValue = col.getCellData(item);
                    dataRow.append(escapeCsv(cellValue != null ? cellValue.toString() : ""));
                }
                writer.println(dataRow.toString());
            }
            
            AlertHelper.showInfo("Export Complete", "Success", "Table data exported successfully to " + file.getName());
        } catch (Exception e) {
            AlertHelper.showError("Export Failed", "Error writing file", e.getMessage());
        }
    }
    
    private static String escapeCsv(String val) {
        if (val == null) return "";
        String escaped = val.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
