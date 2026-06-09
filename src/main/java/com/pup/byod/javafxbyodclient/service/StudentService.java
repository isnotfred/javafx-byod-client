package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.Student;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class StudentService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<Student> getAllStudents() throws Exception {
        Student[] students = apiClient.get("/api/v1/students", Student[].class);
        return Arrays.asList(students);
    }

    public List<Student> searchStudents(String keyword) throws Exception {
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.toString());
        Student[] students = apiClient.get("/api/v1/students/search?keyword=" + encodedKeyword, Student[].class);
        return Arrays.asList(students);
    }

    public Student createStudent(Student student) throws Exception {
        return apiClient.post("/api/v1/students", student, Student.class);
    }

    public Student updateStudent(String id, Student student) throws Exception {
        return apiClient.put("/api/v1/students/" + id, student, Student.class);
    }

    public void deactivateStudent(String id) throws Exception {
        apiClient.put("/api/v1/students/" + id + "/deactivate", null, Void.class);
    }

    public java.util.Map<String, Object> importStudentsCsv(java.io.File file) throws Exception {
        return apiClient.postMultipart("/api/v1/students/import", file, java.util.Map.class);
    }
}
