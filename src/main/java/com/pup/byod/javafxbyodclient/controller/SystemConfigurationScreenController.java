package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.SystemSetting;
import com.pup.byod.javafxbyodclient.service.SystemSettingService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class SystemConfigurationScreenController {
    @FXML private Spinner<Integer> maxDevicesSpinner;
    @FXML private Spinner<Integer> eventDurationSpinner;
    @FXML private Button saveButton;

    private final SystemSettingService settingService = new SystemSettingService();

    private String origMaxDevices = "3";
    private String origEventDuration = "7";

    @FXML
    public void initialize() {
        // Setup initial Spinners with value factories
        maxDevicesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 10, 3));
        eventDurationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(7, 365, 7));

        // Make spinners editable (typable)
        maxDevicesSpinner.setEditable(true);
        eventDurationSpinner.setEditable(true);

        // Commit spinner values on focus lost
        maxDevicesSpinner.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                try {
                    int val = Integer.parseInt(maxDevicesSpinner.getEditor().getText());
                    maxDevicesSpinner.getValueFactory().setValue(Math.max(3, Math.min(10, val)));
                } catch (NumberFormatException e) {
                    maxDevicesSpinner.getValueFactory().setValue(3);
                }
            }
        });
        eventDurationSpinner.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                try {
                    int val = Integer.parseInt(eventDurationSpinner.getEditor().getText());
                    eventDurationSpinner.getValueFactory().setValue(Math.max(7, Math.min(365, val)));
                } catch (NumberFormatException e) {
                    eventDurationSpinner.getValueFactory().setValue(7);
                }
            }
        });

        loadSettings();
    }

    private void loadSettings() {
        try {
            List<SystemSetting> settings = settingService.getAllSettings();
            for (SystemSetting setting : settings) {
                String key = setting.getSettingKey();
                String val = setting.getSettingValue();
                if ("max_devices_per_student".equals(key)) {
                    origMaxDevices = val;
                } else if ("event_request_max_duration_days".equals(key)) {
                    origEventDuration = val;
                }
            }

            // Bind values to UI elements
            int maxDevices = 3;
            try {
                maxDevices = Integer.parseInt(origMaxDevices);
            } catch (NumberFormatException e) { }
            maxDevicesSpinner.getValueFactory().setValue(maxDevices);

            int eventDuration = 7;
            try {
                eventDuration = Integer.parseInt(origEventDuration);
            } catch (NumberFormatException e) { }
            eventDurationSpinner.getValueFactory().setValue(eventDuration);

        } catch (Exception e) {
            System.err.println("Could not load system settings: " + e.getMessage());
            AlertHelper.showError("Error", "Load Failed", "Could not fetch settings: " + e.getMessage());
        }
    }

    @FXML
    public void handleSaveSettings() {
        int maxDevices = maxDevicesSpinner.getValue();
        int eventDuration = eventDurationSpinner.getValue();

        // Validate range limitations programmatically (spinners inherently restrict these, but let's double check)
        if (maxDevices < 3 || maxDevices > 10) {
            AlertHelper.showWarning("System Config", "Validation Error", "Max Devices per student must be between 3 and 10.");
            return;
        }
        if (eventDuration < 7) {
            AlertHelper.showWarning("System Config", "Validation Error", "Event request max duration must be at least 7 days.");
            return;
        }

        if (!AlertHelper.showConfirmation("System Config", "Confirm Changes", "Are you sure you want to save the system configuration changes?")) {
            return;
        }

        int updates = 0;
        try {
            if (!origMaxDevices.equals(String.valueOf(maxDevices))) {
                settingService.updateSetting("max_devices_per_student", String.valueOf(maxDevices));
                origMaxDevices = String.valueOf(maxDevices);
                updates++;
            }
            if (!origEventDuration.equals(String.valueOf(eventDuration))) {
                settingService.updateSetting("event_request_max_duration_days", String.valueOf(eventDuration));
                origEventDuration = String.valueOf(eventDuration);
                updates++;
            }

            if (updates > 0) {
                AlertHelper.showInfo("System Config", "Save Success", "Configuration settings updated successfully (" + updates + " change(s)).");
            } else {
                AlertHelper.showInfo("System Config", "No Changes", "No configuration settings were modified.");
            }
            loadSettings();
        } catch (Exception e) {
            AlertHelper.showError("Save Failed", "Error saving settings", e.getMessage());
        }
    }
}
