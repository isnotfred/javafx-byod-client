package com.pup.byod.javafxbyodclient.util;

import java.util.regex.Pattern;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class ValidationHelper {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("(?i)^2\\d{3}-\\d{5,6}-SR-0$");
    private static final Pattern COURSE_PATTERN = Pattern.compile("^[A-Za-z\\s.-]+\\s+\\d-\\d$");

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidStudentId(String studentId) {
        if (isEmpty(studentId)) return false;
        return STUDENT_ID_PATTERN.matcher(studentId.trim()).matches();
    }

    public static boolean isValidCourseYearLevel(String courseYearLevel) {
        if (isEmpty(courseYearLevel)) return false;
        return COURSE_PATTERN.matcher(courseYearLevel.trim()).matches();
    }

    public static boolean isValidSerialNumber(String serialNumber) {
        return !isEmpty(serialNumber) && serialNumber.trim().length() >= 3;
    }

    public static boolean validateTextInput(TextInputControl field, String prompt) {
        if (isEmpty(field.getText())) {
            if (field.getProperties().get("originalPrompt") == null) {
                field.getProperties().put("originalPrompt", field.getPromptText());
            }
            field.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px; -fx-prompt-text-fill: red;");
            field.setPromptText(prompt);
            return false;
        } else {
            resetValidation(field);
            return true;
        }
    }

    public static boolean validateComboBox(ComboBox<?> box) {
        if (box.getValue() == null || box.getValue().toString().trim().isEmpty()) {
            box.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px; -fx-prompt-text-fill: red;");
            return false;
        } else {
            resetValidation(box);
            return true;
        }
    }

    public static boolean validateDatePicker(DatePicker picker) {
        if (picker.getValue() == null) {
            picker.setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-radius: 4px; -fx-prompt-text-fill: red;");
            return false;
        } else {
            resetValidation(picker);
            return true;
        }
    }

    public static void setup(TextInputControl field, String prompt) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateTextInput(field, prompt);
            }
        });
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!isEmpty(newVal)) {
                resetValidation(field);
            }
        });
    }

    public static void setup(TextInputControl field) {
        setup(field, "Input needed");
    }

    public static void setup(ComboBox<?> box) {
        box.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateComboBox(box);
            }
        });
        box.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.toString().trim().isEmpty()) {
                box.setStyle("");
            }
        });
    }

    public static void setup(DatePicker picker) {
        picker.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateDatePicker(picker);
            }
        });
        picker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                picker.setStyle("");
            }
        });
    }

    public static void resetValidation(TextInputControl field) {
        field.setStyle("");
        if (field.getProperties().get("originalPrompt") != null) {
            field.setPromptText((String) field.getProperties().get("originalPrompt"));
        }
    }

    public static void resetValidation(ComboBox<?> box) {
        box.setStyle("");
    }

    public static void resetValidation(DatePicker picker) {
        picker.setStyle("");
    }

    public static String cleanDeviceType(String type) {
        if (type == null) return "";
        if ("Project Prototypes (Optional SN)".equalsIgnoreCase(type.trim()) || "Project Prototypes".equalsIgnoreCase(type.trim())) {
            return "Project Prototypes";
        }
        return type;
    }
}
