package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Map;

public class StudentsScreenController {
    @FXML private TextField searchField;
    @FXML private Button editSelectedBtn;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colStudentName;
    @FXML private TableColumn<Student, String> colStudentCourse;
    @FXML private TableColumn<Student, String> colStudentContact;

    // Overlay components
    @FXML private StackPane formOverlay;
    @FXML private Label formTitleLabel;
    @FXML private TextField studentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField courseYearLevelField;
    @FXML private TextField contactNumberField;

    private final StudentService studentService = new StudentService();
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private FilteredList<Student> filteredStudentList;

    private boolean isEditMode = false;
    private Student selectedStudent = null;

    @FXML
    public void initialize() {
        // Table Columns Binding
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStudentName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        colStudentCourse.setCellValueFactory(new PropertyValueFactory<>("courseYearLevel"));
        colStudentContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        // Staging dynamic lists filtering
        filteredStudentList = new FilteredList<>(studentList, p -> true);
        studentTable.setItems(filteredStudentList);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredStudentList.setPredicate(student -> {
                if (newVal == null || newVal.trim().isEmpty()) {
                    return true;
                }
                String lower = newVal.toLowerCase().trim();
                return student.getStudentId().toLowerCase().contains(lower) ||
                       student.getFullName().toLowerCase().contains(lower) ||
                       (student.getCourseYearLevel() != null && student.getCourseYearLevel().toLowerCase().contains(lower)) ||
                       (student.getContactNumber() != null && student.getContactNumber().toLowerCase().contains(lower));
            });
        });

        // Enable/Disable edit button based on table selection
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editSelectedBtn.setDisable(newVal == null);
        });

        // Setup UI Validation
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(studentIdField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(firstNameField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(lastNameField, "Input required");
        com.pup.byod.javafxbyodclient.util.ValidationHelper.setup(courseYearLevelField, "Input required");

        // Load data async
        loadStudents();
    }

    private void loadStudents() {
        new Thread(() -> {
            try {
                List<Student> students = studentService.getAllStudents();
                Platform.runLater(() -> {
                    studentList.setAll(students);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Loading Error", "Could not fetch student registry list", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void openAddOverlay() {
        isEditMode = false;
        selectedStudent = null;
        formTitleLabel.setText("Add New Student Profile");
        
        studentIdField.clear();
        studentIdField.setEditable(true);
        firstNameField.clear();
        lastNameField.clear();
        courseYearLevelField.clear();
        contactNumberField.clear();
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(studentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(firstNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(lastNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(courseYearLevelField);

        formOverlay.setVisible(true);
    }

    @FXML
    public void openEditOverlay() {
        selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) return;

        isEditMode = true;
        formTitleLabel.setText("Edit Student Profile");

        studentIdField.setText(selectedStudent.getStudentId());
        studentIdField.setEditable(false);
        firstNameField.setText(selectedStudent.getFirstName());
        lastNameField.setText(selectedStudent.getLastName());
        courseYearLevelField.setText(selectedStudent.getCourseYearLevel());
        contactNumberField.setText(selectedStudent.getContactNumber());
        
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(studentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(firstNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(lastNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(courseYearLevelField);

        formOverlay.setVisible(true);
    }

    @FXML
    public void handleCloseOverlay() {
        formOverlay.setVisible(false);
        
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(studentIdField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(firstNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(lastNameField);
        com.pup.byod.javafxbyodclient.util.ValidationHelper.resetValidation(courseYearLevelField);
    }

    @FXML
    public void handleSave() {
        String studentId = studentIdField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String courseYearLevel = courseYearLevelField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();

        if (ValidationHelper.isEmpty(studentId) || ValidationHelper.isEmpty(firstName) ||
            ValidationHelper.isEmpty(lastName) || ValidationHelper.isEmpty(courseYearLevel)) {
            AlertHelper.showWarning("Form Validation", "Fields Required", "Please enter all required student details marked with *.");
            return;
        }

        Student s = isEditMode ? selectedStudent : new Student();
        s.setStudentId(studentId);
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setCourseYearLevel(courseYearLevel);
        s.setContactNumber(contactNumber.isEmpty() ? null : contactNumber);
        s.setStatus(isEditMode ? selectedStudent.getStatus() : "active");

        new Thread(() -> {
            try {
                if (isEditMode) {
                    studentService.updateStudent(s.getStudentId(), s);
                } else {
                    studentService.createStudent(s);
                }
                Platform.runLater(() -> {
                    AlertHelper.showInfo("Success", "Profile Saved", "Student registry profile successfully saved.");
                    formOverlay.setVisible(false);
                    loadStudents();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Save Error", "Failed to save student profile", e.getMessage());
                });
            }
        }).start();
    }



    @FXML
    public void handleImportCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Students CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(studentTable.getScene().getWindow());

        if (file == null) return;

        new Thread(() -> {
            try {
                Map<String, Object> result = studentService.importStudentsCsv(file);
                Platform.runLater(() -> {
                    int success = result.containsKey("successCount") ? ((Number) result.get("successCount")).intValue() : 0;
                    int failed = result.containsKey("failedCount") ? ((Number) result.get("failedCount")).intValue() : 0;
                    AlertHelper.showInfo("Import Result", "CSV Import Complete", 
                            "Successfully imported " + success + " student profiles. Failed: " + failed);
                    loadStudents();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertHelper.showError("Import Error", "Failed to import CSV", e.getMessage());
                });
            }
        }).start();
    }
}
