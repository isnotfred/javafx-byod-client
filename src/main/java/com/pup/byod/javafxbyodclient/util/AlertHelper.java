package com.pup.byod.javafxbyodclient.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;

public class AlertHelper {
    public static void showInfo(String title, String header, String content) {
        showAlert(AlertType.INFORMATION, title, header, content);
    }

    public static void showWarning(String title, String header, String content) {
        showAlert(AlertType.WARNING, title, header, content);
    }

    public static void showError(String title, String header, String content) {
        showAlert(AlertType.ERROR, title, header, content);
    }

    private static void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(AlertHelper.class.getResource("/com/pup/byod/javafxbyodclient/css/admin_dashboard_styles.css").toExternalForm());
            dialogPane.getStyleClass().add("module-card");
        } catch (Exception e) {
            System.err.println("Could not load alert styles: " + e.getMessage());
        }
        
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        DialogPane dialogPane = alert.getDialogPane();
        try {
            dialogPane.getStylesheets().add(AlertHelper.class.getResource("/com/pup/byod/javafxbyodclient/css/admin_dashboard_styles.css").toExternalForm());
            dialogPane.getStyleClass().add("module-card");
        } catch (Exception e) {
            System.err.println("Could not load alert styles: " + e.getMessage());
        }
        
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
    }
}
