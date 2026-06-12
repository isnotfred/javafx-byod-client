package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.SystemSetting;
import com.pup.byod.javafxbyodclient.service.SystemSettingService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class SystemConfigurationScreenController {
    @FXML private TableView<SystemSetting> settingsTable;
    @FXML private TableColumn<SystemSetting, String> colKey;
    @FXML private TableColumn<SystemSetting, String> colValue;
    @FXML private TableColumn<SystemSetting, String> colDescription;

    @FXML private Label keyLabel;
    @FXML private TextField valueField;
    @FXML private Label descriptionLabel;

    private final SystemSettingService settingService = new SystemSettingService();
    private final ObservableList<SystemSetting> settingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colKey.setCellValueFactory(new PropertyValueFactory<>("settingKey"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("settingValue"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        settingsTable.setItems(settingList);

        // Restrict sorting strictly to the Description column with a 2-state sort (ASCENDING <-> DESCENDING)
        colKey.setSortable(false);
        colValue.setSortable(false);
        colDescription.setSortable(true);

        colDescription.setSortType(TableColumn.SortType.ASCENDING);
        settingsTable.getSortOrder().add(colDescription);
        
        settingsTable.setOnSort(event -> {
            if (settingsTable.getSortOrder().isEmpty()) {
                colDescription.setSortType(TableColumn.SortType.ASCENDING);
                javafx.application.Platform.runLater(() -> {
                    settingsTable.getSortOrder().add(colDescription);
                });
            }
        });

        // Selection listener
        settingsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                keyLabel.setText(newVal.getSettingKey());
                valueField.setText(newVal.getSettingValue());
                descriptionLabel.setText(newVal.getDescription());
            } else {
                clearFields();
            }
        });

        loadSettings();
    }

    private void loadSettings() {
        try {
            List<SystemSetting> settings = settingService.getAllSettings();
            settingList.setAll(settings);
        } catch (Exception e) {
            System.err.println("Could not load system settings: " + e.getMessage());
            AlertHelper.showError("Error", "Load Failed", "Could not fetch settings: " + e.getMessage());
        }
    }

    @FXML
    public void handleSaveSetting() {
        SystemSetting selected = settingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("System Config", "No Selection", "Please select a setting to modify.");
            return;
        }

        String newValue = valueField.getText();
        if (ValidationHelper.isEmpty(newValue)) {
            AlertHelper.showWarning("System Config", "Missing Value", "Setting value cannot be empty.");
            return;
        }

        if (!AlertHelper.showConfirmation("System Config", "Confirm Setting Change", "Are you sure you want to change system configuration parameter " + selected.getSettingKey() + " to: " + newValue + "?")) {
            return;
        }

        try {
            settingService.updateSetting(selected.getSettingKey(), newValue);
            AlertHelper.showInfo("System Config", "Update Success", "Setting '" + selected.getSettingKey() + "' updated successfully.");
            loadSettings();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Update Failed", e.getMessage());
        }
    }

    private void clearFields() {
        keyLabel.setText("(Select a setting)");
        valueField.clear();
        descriptionLabel.setText("");
    }
}
