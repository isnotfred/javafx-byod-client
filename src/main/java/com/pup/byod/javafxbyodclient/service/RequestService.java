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
}
