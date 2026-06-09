package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.DeviceLog;
import com.pup.byod.javafxbyodclient.service.LogService;
import com.pup.byod.javafxbyodclient.session.SessionManager;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class IngressEgressMonitoringScreenController {
    @FXML private TextField serialNumberField;
    @FXML private TextArea notesArea;
    @FXML private Label statusLabel;
    @FXML private Label lastScannedLabel;

    private final LogService logService = new LogService();

    @FXML
    public void handleEntry() {
        String sn = serialNumberField.getText();
        String notes = notesArea.getText();

        if (ValidationHelper.isEmpty(sn)) {
            AlertHelper.showWarning("Gate Scan", "Missing SN", "Please enter/scan the device serial number.");
            return;
        }

        try {
            int guardId = SessionManager.getInstance().getCurrentUser().getUserId();
            DeviceLog log = logService.logEntry(sn, guardId, notes);
            statusLabel.setText("STATUS: ENTRY LOGGED SUCCESSFULLY");
            lastScannedLabel.setText("Last Scan: " + sn + " (Entry at " + log.getEventTime() + ")");
            clearFields();
        } catch (Exception e) {
            statusLabel.setText("STATUS: SCAN BLOCKED!");
            AlertHelper.showError("Scan Error", "Ingress Blocked", e.getMessage());
        }
    }

    @FXML
    public void handleExit() {
        String sn = serialNumberField.getText();
        String notes = notesArea.getText();

        if (ValidationHelper.isEmpty(sn)) {
            AlertHelper.showWarning("Gate Scan", "Missing SN", "Please enter/scan the device serial number.");
            return;
        }

        try {
            int guardId = SessionManager.getInstance().getCurrentUser().getUserId();
            DeviceLog log = logService.logExit(sn, guardId, notes);
            statusLabel.setText("STATUS: EXIT LOGGED SUCCESSFULLY");
            lastScannedLabel.setText("Last Scan: " + sn + " (Exit at " + log.getEventTime() + ")");
            clearFields();
        } catch (Exception e) {
            statusLabel.setText("STATUS: SCAN BLOCKED!");
            AlertHelper.showError("Scan Error", "Egress Blocked", e.getMessage());
        }
    }

    private void clearFields() {
        serialNumberField.clear();
        notesArea.clear();
    }
}
