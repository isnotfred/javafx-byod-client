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
        EventRequest[] requests = apiClient.get("/api/requests", EventRequest[].class);
        return Arrays.asList(requests);
    }

    public EventRequest getEventRequestById(int id) throws Exception {
        return apiClient.get("/api/requests/" + id, EventRequest.class);
    }

    public List<ActiveEventRequest> getActiveEventRequests() throws Exception {
        ActiveEventRequest[] requests = apiClient.get("/api/requests/active", ActiveEventRequest[].class);
        return Arrays.asList(requests);
    }

    public List<EventRequestDevice> getEventRequestDevices(int requestId) throws Exception {
        EventRequestDevice[] devices = apiClient.get("/api/requests/" + requestId + "/devices", EventRequestDevice[].class);
        return Arrays.asList(devices);
    }

    public EventRequest createEventRequest(EventRequest request) throws Exception {
        return apiClient.post("/api/requests", request, EventRequest.class);
    }

    public EventRequest updateEventRequest(int id, EventRequest request) throws Exception {
        return apiClient.put("/api/requests/" + id, request, EventRequest.class);
    }

    public void approveEventRequest(int id, int reviewerId) throws Exception {
        Map<String, Integer> body = new HashMap<>();
        body.put("reviewerUserId", reviewerId);
        apiClient.put("/api/requests/" + id + "/approve", body, Void.class);
    }

    public void returnEventRequest(int id, int reviewerId, String remarks) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("reviewerUserId", reviewerId);
        body.put("remarks", remarks);
        apiClient.put("/api/requests/" + id + "/return", body, Void.class);
    }

    public void rejectEventRequest(int id, int reviewerId, String remarks) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("reviewerUserId", reviewerId);
        body.put("remarks", remarks);
        apiClient.put("/api/requests/" + id + "/reject", body, Void.class);
    }

    public void verifyEventDevice(int eventDeviceId, int verifiedBy, String status) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("verifiedBy", verifiedBy);
        body.put("deviceStatus", status);
        apiClient.put("/api/requests/devices/" + eventDeviceId + "/verify", body, Void.class);
    }

    public List<ActiveEventRequest> getGuardEventRequests() throws Exception {
        ActiveEventRequest[] requests = apiClient.get("/api/requests/active", ActiveEventRequest[].class);
        return Arrays.asList(requests);
    }

    public void logDeviceEntry(List<Integer> deviceIds, int guardId) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("requestDeviceIds", deviceIds);
        body.put("handledBy", guardId);
        apiClient.post("/api/transactions/batch-ingress", body, Void.class);
    }

    public void logDeviceExit(List<Integer> deviceIds, int guardId) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("requestDeviceIds", deviceIds);
        body.put("handledBy", guardId);
        apiClient.post("/api/transactions/batch-egress", body, Void.class);
    }

    public List<EventRequestDevice> getReconciliationReport() throws Exception {
        EventRequestDevice[] devices = apiClient.get("/api/requests/campus-status", EventRequestDevice[].class);
        return Arrays.asList(devices);
    }
}
