package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.DeviceTransaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<DeviceTransaction> getDeviceTransactions(int requestDeviceId) throws Exception {
        DeviceTransaction[] logs = apiClient.get("/api/transactions/device/" + requestDeviceId, DeviceTransaction[].class);
        return Arrays.asList(logs);
    }

    public List<DeviceTransaction> processBatchIngress(List<Integer> requestDeviceIds, int handledBy) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("requestDeviceIds", requestDeviceIds);
        body.put("handledBy", handledBy);
        DeviceTransaction[] response = apiClient.post("/api/transactions/batch-ingress", body, DeviceTransaction[].class);
        return Arrays.asList(response);
    }

    public List<DeviceTransaction> processBatchEgress(List<Integer> requestDeviceIds, int handledBy) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("requestDeviceIds", requestDeviceIds);
        body.put("handledBy", handledBy);
        DeviceTransaction[] response = apiClient.post("/api/transactions/batch-egress", body, DeviceTransaction[].class);
        return Arrays.asList(response);
    }

    public void processScan(String serialNumber, int handledBy, String notes) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("serialNumber", serialNumber);
        body.put("handledBy", handledBy);
        body.put("notes", notes);
        apiClient.post("/api/transactions/scan", body, Object.class);
    }

    public int reconcileMissedCheckouts() throws Exception {
        Map res = apiClient.post("/api/transactions/reconcile", null, Map.class);
        if (res != null && res.containsKey("markedAsMissed")) {
            return ((Number) res.get("markedAsMissed")).intValue();
        }
        return 0;
    }
}
