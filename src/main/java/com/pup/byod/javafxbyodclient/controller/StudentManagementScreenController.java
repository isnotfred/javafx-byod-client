package com.pup.byod.javafxbyodclient.controller;

import com.pup.byod.javafxbyodclient.model.Student;
import com.pup.byod.javafxbyodclient.service.StudentService;
import com.pup.byod.javafxbyodclient.util.AlertHelper;
import com.pup.byod.javafxbyodclient.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentManagementScreenController {
    @FXML private TextField searchField;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colCourse;
    @FXML private TableColumn<Student, String> colStatus;

    @FXML private TextField studentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField courseYearLevelField;

    private final StudentService studentService = new StudentService();
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("courseYearLevel"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        studentTable.setItems(studentList);
        loadStudents();
    }

    private void loadStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            studentList.setAll(students);
        } catch (Exception e) {
            System.err.println("Could not load students: " + e.getMessage());
        }
    }

    @FXML
    public void handleSearch() {
        String keyword = searchField.getText();
        if (ValidationHelper.isEmpty(keyword)) {
            loadStudents();
            return;
        }
        try {
            List<Student> students = studentService.searchStudents(keyword);
            studentList.setAll(students);
        } catch (Exception e) {
            AlertHelper.showError("Search failed", "Error", e.getMessage());
        }
    }

    @FXML
    public void handleAddStudent() {
        String id = studentIdField.getText();
        String first = firstNameField.getText();
        String last = lastNameField.getText();
        String course = courseYearLevelField.getText();

        if (ValidationHelper.isEmpty(id) || ValidationHelper.isEmpty(first) || 
            ValidationHelper.isEmpty(last) || ValidationHelper.isEmpty(course)) {
            AlertHelper.showWarning("Form Warning", "Missing inputs", "Please fill in all student registry fields.");
            return;
        }

        Student s = new Student();
        s.setStudentId(id);
        s.setFirstName(first);
        s.setLastName(last);
        s.setCourseYearLevel(course);

        try {
            studentService.createStudent(s);
            AlertHelper.showInfo("Student Added", "Success", "Student registered successfully.");
            clearInputs();
            loadStudents();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Create Failed", e.getMessage());
        }
    }

    @FXML
    @SuppressWarnings("unchecked")
    public void handleImportCsv() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Import Students CSV");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv")
        );
        
        javafx.stage.Window window = studentTable.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);
        
        if (selectedFile == null) {
            return;
        }

        // Validate CSV headers first
        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            String headerLine = br.readLine();
            if (headerLine == null || headerLine.trim().isEmpty()) {
                AlertHelper.showError("CSV Validation Failed", "Empty File", "The selected CSV file is empty.");
                return;
            }
            
            String[] headers = headerLine.split(",");
            List<String> headersList = new ArrayList<>();
            for (String h : headers) {
                headersList.add(h.trim().toLowerCase());
            }
            
            List<String> requiredHeaders = List.of("student_id", "first_name", "last_name", "course_year_level");
            List<String> missingHeaders = new ArrayList<>();
            for (String req : requiredHeaders) {
                if (!headersList.contains(req)) {
                    missingHeaders.add(req);
                }
            }
            
            if (!missingHeaders.isEmpty()) {
                AlertHelper.showError(
                    "CSV Validation Failed",
                    "Missing Headers",
                    "The selected CSV file is missing required headers: " + String.join(", ", missingHeaders) +
                    "\n\nExpected headers: student_id,first_name,last_name,course_year_level"
                );
                return;
            }
        } catch (Exception e) {
            AlertHelper.showError("CSV Validation Failed", "Error reading file", e.getMessage());
            return;
        }

        // Proceed to call API
        try {
            Map<String, Object> result = studentService.importStudentsCsv(selectedFile);
            
            int inserted = result.containsKey("inserted") ? (int) result.get("inserted") : 0;
            int skipped = result.containsKey("skipped") ? (int) result.get("skipped") : 0;
            List<Map<String, Object>> errors = result.containsKey("errors") ? (List<Map<String, Object>>) result.get("errors") : new ArrayList<>();

            StringBuilder summary = new StringBuilder();
            summary.append("Import completed:\n");
            summary.append("- Successfully inserted: ").append(inserted).append("\n");
            summary.append("- Skipped (duplicates): ").append(skipped).append("\n");
            summary.append("- Failed (errors): ").append(errors.size()).append("\n");

            if (!errors.isEmpty()) {
                summary.append("\nErrors:\n");
                for (Map<String, Object> error : errors) {
                    int row = error.containsKey("row") ? (int) error.get("row") : 0;
                    String id = error.containsKey("studentId") ? (String) error.get("studentId") : "unknown";
                    List<String> reasons = error.containsKey("reasons") ? (List<String>) error.get("reasons") : new ArrayList<>();
                    summary.append("• Row ").append(row)
                           .append(" (ID: ").append(id).append("): ")
                           .append(String.join(", ", reasons)).append("\n");
                }
                AlertHelper.showWarning("Import Complete with Errors", "Bulk Upload Summary", summary.toString());
            } else {
                AlertHelper.showInfo("Import Successful", "Bulk Upload Summary", summary.toString());
            }
            
            loadStudents();
        } catch (Exception e) {
            AlertHelper.showError("Import Failed", "API Request Failed", e.getMessage());
        }
    }

    @FXML
    public void handleDeactivate() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Soft Delete", "No selection", "Please select a student record.");
            return;
        }
        try {
            studentService.deactivateStudent(selected.getStudentId());
            AlertHelper.showInfo("Deactivated", "Success", "Student status marked inactive.");
            loadStudents();
        } catch (Exception e) {
            AlertHelper.showError("Error", "Deactivate Failed", e.getMessage());
        }
    }

    private void clearInputs() {
        studentIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        courseYearLevelField.clear();
    }
}
