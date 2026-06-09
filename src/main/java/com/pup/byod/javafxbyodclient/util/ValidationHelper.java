package com.pup.byod.javafxbyodclient.util;

import java.util.regex.Pattern;

public class ValidationHelper {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^\\d{4}-\\d{5}$");

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidStudentId(String studentId) {
        if (isEmpty(studentId)) return false;
        // Allows both general and specific formats (e.g. 2021-10023 or raw numbers if needed)
        return studentId.length() >= 5;
    }

    public static boolean isValidSerialNumber(String serialNumber) {
        return !isEmpty(serialNumber) && serialNumber.trim().length() >= 3;
    }
}
