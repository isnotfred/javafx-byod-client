package com.pup.byod.javafxbyodclient.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

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
        
        String type = "info";
        if (alertType == AlertType.WARNING) {
            type = "warning";
        } else if (alertType == AlertType.ERROR) {
            type = "error";
        }
        
        setupCustomDialog(alert, title, header, content, type);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        
        setupCustomDialog(alert, title, header, content, "confirm");
        
        java.util.Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void setupCustomDialog(Alert alert, String title, String header, String content, String type) {
        DialogPane dialogPane = alert.getDialogPane();
        
        // Ensure standard text properties are set
        alert.setHeaderText(header != null && !header.isEmpty() ? header : title);
        
        javafx.scene.control.Label contentLabel = new javafx.scene.control.Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        contentLabel.getStyleClass().add("content");
        dialogPane.setContent(contentLabel);
        
        // Load custom dialog stylesheet
        try {
            dialogPane.getStylesheets().clear();
            dialogPane.getStylesheets().add(AlertHelper.class.getResource("/com/pup/byod/javafxbyodclient/css/dialog_styles.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load dialog styles: " + e.getMessage());
        }
        
        dialogPane.getStyleClass().add("custom-dialog-pane");
        dialogPane.setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(450);
        
        if (!"confirm".equals(type)) {
            // Create custom flat SVG graphic
            SVGPath svgIcon = new SVGPath();
            svgIcon.getStyleClass().addAll("dialog-svg-icon", "dialog-svg-" + type);
            
            // Choose SVG content based on type
            if ("info".equals(type)) {
                // Circle with 'i' letter
                svgIcon.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z");
            } else if ("warning".equals(type)) {
                // Triangle with exclamation mark
                svgIcon.setContent("M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z");
            } else if ("error".equals(type)) {
                // Circle with 'X' cross mark
                svgIcon.setContent("M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z");
            }
            
            StackPane graphicContainer = new StackPane(svgIcon);
            graphicContainer.getStyleClass().add("dialog-graphic-container");
            alert.setGraphic(graphicContainer);
        } else {
            alert.setGraphic(null);
        }
        
        // Add a listener to style buttons dynamically when the dialog becomes visible in scene graph
        alert.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Style OK / YES button
                Button okBtn = (Button) dialogPane.lookupButton(ButtonType.OK);
                if (okBtn != null) {
                    okBtn.getStyleClass().clear();
                    okBtn.getStyleClass().addAll("button", "dialog-btn", "confirm".equals(type) ? "dialog-confirm-btn" : "dialog-primary-btn");
                }
                Button yesBtn = (Button) dialogPane.lookupButton(ButtonType.YES);
                if (yesBtn != null) {
                    yesBtn.getStyleClass().clear();
                    yesBtn.getStyleClass().addAll("button", "dialog-btn", "confirm".equals(type) ? "dialog-confirm-btn" : "dialog-primary-btn");
                }
                
                // Style CANCEL / NO button
                Button cancelBtn = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
                if (cancelBtn != null) {
                    cancelBtn.getStyleClass().clear();
                    cancelBtn.getStyleClass().addAll("button", "dialog-btn", "dialog-cancel-btn");
                }
                Button noBtn = (Button) dialogPane.lookupButton(ButtonType.NO);
                if (noBtn != null) {
                    noBtn.getStyleClass().clear();
                    noBtn.getStyleClass().addAll("button", "dialog-btn", "dialog-cancel-btn");
                }
                
                // Style CLOSE button
                Button closeBtn = (Button) dialogPane.lookupButton(ButtonType.CLOSE);
                if (closeBtn != null) {
                    closeBtn.getStyleClass().clear();
                    closeBtn.getStyleClass().addAll("button", "dialog-btn", "dialog-primary-btn");
                }
            }
        });
    }
}

