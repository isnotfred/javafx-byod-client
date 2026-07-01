package com.pup.byod.javafxbyodclient.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReportService {
    private final ApiClient apiClient = ApiClient.getInstance();

    // 1. Daily Traffic Report (Accepts single date)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDailyTrafficReport(String dateString, String studentId, String deviceType, String status) throws Exception {
        StringBuilder url = new StringBuilder("/api/reports/daily-traffic?date=").append(URLEncoder.encode(dateString, StandardCharsets.UTF_8));
        if (studentId != null) url.append("&studentId=").append(URLEncoder.encode(studentId, StandardCharsets.UTF_8));
        if (deviceType != null) url.append("&deviceType=").append(URLEncoder.encode(deviceType, StandardCharsets.UTF_8));
        if (status != null) url.append("&status=").append(URLEncoder.encode(status, StandardCharsets.UTF_8));
        
        Map[] reports = apiClient.get(url.toString(), Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 2. Monthly Traffic Report (Accepts year and month)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMonthlyTrafficReport(int year, int month) throws Exception {
        Map[] reports = apiClient.get("/api/reports/monthly-traffic?year=" + year + "&month=" + month, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 3. Active Devices On Campus
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getActiveDevicesReport() throws Exception {
        Map[] reports = apiClient.get("/api/reports/active-devices", Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 4. Device Frequency Report
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDeviceFrequencyReport(String fromDate, String toDate) throws Exception {
        String from = URLEncoder.encode(fromDate, StandardCharsets.UTF_8);
        String to = URLEncoder.encode(toDate, StandardCharsets.UTF_8);
        Map[] reports = apiClient.get("/api/reports/device-frequency?from=" + from + "&to=" + to, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 5. Incidents Report
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getIncidentsReport(String fromDate, String toDate) throws Exception {
        String from = URLEncoder.encode(fromDate, StandardCharsets.UTF_8);
        String to = URLEncoder.encode(toDate, StandardCharsets.UTF_8);
        Map[] reports = apiClient.get("/api/reports/incidents?from=" + from + "&to=" + to, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 6. Missed Checkouts Report
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMissedCheckoutReport(String fromDate, String toDate) throws Exception {
        String from = URLEncoder.encode(fromDate, StandardCharsets.UTF_8);
        String to = URLEncoder.encode(toDate, StandardCharsets.UTF_8);
        Map[] reports = apiClient.get("/api/reports/missed-checkouts?from=" + from + "&to=" + to, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 7. Purpose Breakdown Report
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getPurposeBreakdownReport() throws Exception {
        Map[] reports = apiClient.get("/api/reports/purpose-breakdown", Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 8. Late Check-ins & Check-outs Report
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getLateScansReport(String fromDate, String toDate) throws Exception {
        String from = URLEncoder.encode(fromDate, StandardCharsets.UTF_8);
        String to = URLEncoder.encode(toDate, StandardCharsets.UTF_8);
        Map[] reports = apiClient.get("/api/reports/late-scans?from=" + from + "&to=" + to, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    // 9. Unused & Cancelled Requests Report
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUnusedCancelledReport(String fromDate, String toDate) throws Exception {
        String from = URLEncoder.encode(fromDate, StandardCharsets.UTF_8);
        String to = URLEncoder.encode(toDate, StandardCharsets.UTF_8);
        Map[] reports = apiClient.get("/api/reports/unused-cancelled?from=" + from + "&to=" + to, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }
}

