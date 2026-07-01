package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.Request;
import com.pup.byod.javafxbyodclient.model.RequestDevice;
import com.pup.byod.javafxbyodclient.model.DeviceCampusStatus;

import java.util.Arrays;
import java.util.List;

public class RequestService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<Request> getRequestsByStudentId(String studentId) throws Exception {
        Request[] requests = apiClient.get("/api/requests/student/" + studentId, Request[].class);
        return Arrays.asList(requests);
    }

    public List<RequestDevice> getDevicesForRequest(int requestId) throws Exception {
        RequestDevice[] devices = apiClient.get("/api/requests/" + requestId + "/devices", RequestDevice[].class);
        return Arrays.asList(devices);
    }

    public List<DeviceCampusStatus> getCampusStatus() throws Exception {
        DeviceCampusStatus[] status = apiClient.get("/api/requests/campus-status", DeviceCampusStatus[].class);
        return Arrays.asList(status);
    }

    public DeviceCampusStatus getCampusStatusBySerial(String serialNumber) throws Exception {
        return apiClient.get("/api/requests/campus-status/" + serialNumber, DeviceCampusStatus.class);
    }

    public List<Request> getAllRequests() throws Exception {
        Request[] requests = apiClient.get("/api/requests", Request[].class);
        return Arrays.asList(requests);
    }

    public Request createRequest(Request request) throws Exception {
        return apiClient.post("/api/requests", request, Request.class);
    }

    public Request updateRequest(int requestId, Request request) throws Exception {
        return apiClient.put("/api/requests/" + requestId, request, Request.class);
    }

    public void approveRequest(int requestId, int reviewerId) throws Exception {
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("reviewerUserId", reviewerId);
        apiClient.put("/api/requests/" + requestId + "/approve", body, Void.class);
    }

    public void rejectRequest(int requestId, int reviewerId, String remarks) throws Exception {
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("reviewerUserId", reviewerId);
        body.put("remarks", remarks);
        apiClient.put("/api/requests/" + requestId + "/reject", body, Void.class);
    }

    public void returnRequest(int requestId, int reviewerId, String remarks) throws Exception {
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("reviewerUserId", reviewerId);
        body.put("remarks", remarks);
        apiClient.put("/api/requests/" + requestId + "/return", body, Void.class);
    }

    public void cancelRequest(int requestId, int modifierId, String remarks) throws Exception {
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("modifierUserId", modifierId);
        body.put("remarks", remarks);
        apiClient.put("/api/requests/" + requestId + "/cancel", body, Void.class);
    }
}
