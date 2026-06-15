package com.pup.byod.javafxbyodclient.util;

import javafx.scene.control.TextField;

public class PromptTextHelper {
    public static void setup(TextField textField) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateStyle(textField, newVal);
        });
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            updateStyle(textField, textField.getText());
        });
        updateStyle(textField, textField.getText());
    }

    private static void updateStyle(TextField textField, String text) {
        if (text == null || text.isEmpty()) {
            if (!textField.getStyleClass().contains("showing-prompt")) {
                textField.getStyleClass().add("showing-prompt");
            }
        } else {
            textField.getStyleClass().remove("showing-prompt");
        }
    }
}
