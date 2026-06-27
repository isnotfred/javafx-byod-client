package com.pup.byod.javafxbyodclient.util;

import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.StudentService;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class StudentSearchDropdown {
    private static final StudentService studentService = new StudentService();

    public static void attach(TextField textField, Consumer<Student> onStudentSelected) {
        attach(textField, null, onStudentSelected);
    }

    public static void attach(TextField textField, java.util.function.Predicate<Student> filter, Consumer<Student> onStudentSelected) {
        ContextMenu autocompleteMenu = new ContextMenu();
        autocompleteMenu.getStyleClass().add("student-search-popup");
        autocompleteMenu.setMaxHeight(300);
        autocompleteMenu.setStyle("-fx-background-radius: 8px; -fx-border-radius: 8px; -fx-padding: 5px;");

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String currentText = newValue.trim();
            if (currentText.length() >= 3) {
                new Thread(() -> {
                    try {
                        List<Student> matches = studentService.searchStudents(currentText);
                        Platform.runLater(() -> {
                            if (!textField.getText().trim().equals(currentText)) return;
                            
                            autocompleteMenu.getItems().clear();
                            boolean hasItems = false;
                            if (!matches.isEmpty()) {
                                for (Student s : matches) {
                                    if (filter != null && !filter.test(s)) {
                                        continue;
                                    }
                                    MenuItem item = new MenuItem(s.getStudentId() + " - " + s.getFirstName() + " " + s.getLastName());
                                    item.setOnAction(e -> {
                                        textField.setText(s.getStudentId());
                                        if (onStudentSelected != null) {
                                            onStudentSelected.accept(s);
                                        }
                                    });
                                    autocompleteMenu.getItems().add(item);
                                    hasItems = true;
                                }
                                if (hasItems && textField.isFocused() && textField.getScene() != null && textField.getScene().getWindow() != null) {
                                    javafx.geometry.Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                                    autocompleteMenu.show(textField, bounds.getMinX(), bounds.getMaxY());
                                } else {
                                    autocompleteMenu.hide();
                                }
                            } else {
                                autocompleteMenu.hide();
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(autocompleteMenu::hide);
                    }
                }).start();
            } else {
                autocompleteMenu.hide();
            }
        });

        // Hide autocomplete when focus is lost
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                autocompleteMenu.hide();
            }
        });


    }
}
