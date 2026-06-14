package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.EventRequest;
import com.pup.byod.javafxbyodclient.model.EventRequestDevice;
import com.pup.byod.javafxbyodclient.model.ActiveEventRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRequestService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<EventRequest> getAllEventRequests() throws Exception {
        EventRequest[] requests = apiClient.get("/api/v1/event-requests", EventRequest[].class);
        return Arrays.asList(requests);
    }

    public EventRequest getEventRequestById(int id) throws Exception {
        return apiClient.get("/api/v1/event-requests/" + id, EventRequest.class);
    }

    public List<ActiveEventRequest> getActiveEventRequests() throws Exception {
        ActiveEventRequest[] requests = apiClient.get("/api/v1/event-requests/active", ActiveEventRequest[].class);
        return Arrays.asList(requests);
    }

    public List<EventRequestDevice> getEventRequestDevices(int requestId) throws Exception {
        EventRequestDevice[] devices = apiClient.get("/api/v1/event-requests/" + requestId + "/devices", EventRequestDevice[].class);
        return Arrays.asList(devices);
    }

    public EventRequest createEventRequest(EventRequest request) throws Exception {
        return apiClient.post("/api/v1/event-requests", request, EventRequest.class);
    }

    public EventRequest updateEventRequest(int id, EventRequest request) throws Exception {
        return apiClient.put("/api/v1/event-requests/" + id, request, EventRequest.class);
    }

    public void approveEventRequest(int id, int reviewerId) throws Exception {
        Map<String, Integer> body = new HashMap<>();
        body.put("reviewerUserId", reviewerId);
        apiClient.put("/api/v1/event-requests/" + id + "/approve", body, Void.class);
    }

    public void returnEventRequest(int id, int reviewerId, String remarks) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("reviewerUserId", reviewerId);
        body.put("remarks", remarks);
        apiClient.put("/api/v1/event-requests/" + id + "/return", body, Void.class);
    }

    public void rejectEventRequest(int id, int reviewerId, String remarks) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("reviewerUserId", reviewerId);
        body.put("remarks", remarks);
        apiClient.put("/api/v1/event-requests/" + id + "/reject", body, Void.class);
    }

    public void verifyEventDevice(int eventDeviceId, int verifiedBy, String status) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("verifiedBy", verifiedBy);
        body.put("deviceStatus", status);
        apiClient.put("/api/v1/event-requests/devices/" + eventDeviceId + "/verify", body, Void.class);
    }

    public List<ActiveEventRequest> getGuardEventRequests() throws Exception {
        ActiveEventRequest[] requests = apiClient.get("/api/v1/event-requests/guard", ActiveEventRequest[].class);
        return Arrays.asList(requests);
    }

    public void logDeviceEntry(List<Integer> deviceIds, int guardId) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("deviceIds", deviceIds);
        body.put("guardId", guardId);
        apiClient.post("/api/v1/event-requests/devices/log-entry", body, Void.class);
    }

    public void logDeviceExit(List<Integer> deviceIds, int guardId) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("deviceIds", deviceIds);
        body.put("guardId", guardId);
        apiClient.post("/api/v1/event-requests/devices/log-exit", body, Void.class);
    }

    public List<EventRequestDevice> getReconciliationReport() throws Exception {
        EventRequestDevice[] devices = apiClient.get("/api/v1/event-requests/devices/reconciliation-report", EventRequestDevice[].class);
        return Arrays.asList(devices);
    }
}
