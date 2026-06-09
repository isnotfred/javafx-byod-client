package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.DeviceLog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<DeviceLog> getDeviceLogs(int deviceId) throws Exception {
        DeviceLog[] logs = apiClient.get("/api/v1/device-logs/devices/" + deviceId, DeviceLog[].class);
        return Arrays.asList(logs);
    }

    public List<DeviceLog> getStudentLogs(String studentId) throws Exception {
        DeviceLog[] logs = apiClient.get("/api/v1/device-logs/students/" + studentId, DeviceLog[].class);
        return Arrays.asList(logs);
    }

    public DeviceLog logEntry(String serialNumber, int handledBy, String notes) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("serialNumber", serialNumber);
        body.put("handledBy", handledBy);
        body.put("notes", notes);
        return apiClient.post("/api/v1/device-logs/entry", body, DeviceLog.class);
    }

    public DeviceLog logExit(String serialNumber, int handledBy, String notes) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("serialNumber", serialNumber);
        body.put("handledBy", handledBy);
        body.put("notes", notes);
        return apiClient.post("/api/v1/device-logs/exit", body, DeviceLog.class);
    }
}
