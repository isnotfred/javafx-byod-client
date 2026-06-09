package com.pup.byod.javafxbyodclient.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReportService {
    private final ApiClient apiClient = ApiClient.getInstance();

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getIncidentsReport(String fromDate, String toDate) throws Exception {
        String from = URLEncoder.encode(fromDate, StandardCharsets.UTF_8.toString());
        String to = URLEncoder.encode(toDate, StandardCharsets.UTF_8.toString());
        Map[] reports = apiClient.get("/reports/incidents?from=" + from + "&to=" + to, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getReport(String path, String fromDate, String toDate) throws Exception {
        String from = URLEncoder.encode(fromDate, StandardCharsets.UTF_8.toString());
        String to = URLEncoder.encode(toDate, StandardCharsets.UTF_8.toString());
        Map[] reports = apiClient.get("/reports/" + path + "?from=" + from + "&to=" + to, Map[].class);
        return (List<Map<String, Object>>) (List) Arrays.asList(reports);
    }

    public List<Map<String, Object>> getDailyTrafficReport(String fromDate, String toDate) throws Exception {
        try {
            return getReport("daily", fromDate, toDate);
        } catch (Exception e) {
            try {
                return getReport("traffic/daily", fromDate, toDate);
            } catch (Exception e2) {
                try {
                    return getReport("daily-traffic", fromDate, toDate);
                } catch (Exception e3) {
                    throw new Exception("Could not fetch daily traffic report from any endpoint: " + e3.getMessage());
                }
            }
        }
    }

    public List<Map<String, Object>> getMonthlyTrafficReport(String fromDate, String toDate) throws Exception {
        try {
            return getReport("monthly", fromDate, toDate);
        } catch (Exception e) {
            try {
                return getReport("traffic/monthly", fromDate, toDate);
            } catch (Exception e2) {
                try {
                    return getReport("monthly-traffic", fromDate, toDate);
                } catch (Exception e3) {
                    throw new Exception("Could not fetch monthly traffic report from any endpoint: " + e3.getMessage());
                }
            }
        }
    }
}
