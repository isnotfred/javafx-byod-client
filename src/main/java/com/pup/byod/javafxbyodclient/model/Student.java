package com.pup.byod.javafxbyodclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    private String studentId;
    private String firstName;
    private String lastName;
    private String courseYearLevel;
    private String course;
    private Integer yearLevel;
    private String status;
    private String createdAt;
    private String updatedAt;

    // Helper for table view
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCourseYearLevel() { return courseYearLevel; }
    public void setCourseYearLevel(String courseYearLevel) { this.courseYearLevel = courseYearLevel; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public Integer getYearLevel() { return yearLevel; }
    public void setYearLevel(Integer yearLevel) { this.yearLevel = yearLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
