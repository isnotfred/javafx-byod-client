package com.pup.byod.javafxbyodclient.service;

import com.pup.byod.javafxbyodclient.model.Device;
import com.pup.byod.javafxbyodclient.model.PendingDevice;
import com.pup.byod.javafxbyodclient.model.DeviceCampusStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceService {
    private final ApiClient apiClient = ApiClient.getInstance();

    public List<Device> getAllDevices() throws Exception {
        Device[] devices = apiClient.get("/api/v1/devices", Device[].class);
        return Arrays.asList(devices);
    }

    public Device getDeviceById(int id) throws Exception {
        return apiClient.get("/api/v1/devices/" + id, Device.class);
    }

    public Device getDeviceBySerialNumber(String serialNumber) throws Exception {
        return apiClient.get("/api/v1/devices/serial/" + serialNumber, Device.class);
    }

    public List<Device> getDevicesByStudentId(String studentId) throws Exception {
        Device[] devices = apiClient.get("/api/v1/devices/student/" + studentId, Device[].class);
        return Arrays.asList(devices);
    }

    public List<PendingDevice> getPendingDevices() throws Exception {
        PendingDevice[] devices = apiClient.get("/api/v1/devices/pending", PendingDevice[].class);
        return Arrays.asList(devices);
    }

    public List<DeviceCampusStatus> getDeviceCampusStatus() throws Exception {
        DeviceCampusStatus[] status = apiClient.get("/api/v1/devices/campus-status", DeviceCampusStatus[].class);
        return Arrays.asList(status);
    }

    public Device registerDevice(Device device) throws Exception {
        return apiClient.post("/api/v1/devices", device, Device.class);
    }

    public Device updateDevice(int id, Device device) throws Exception {
        return apiClient.put("/api/v1/devices/" + id, device, Device.class);
    }

    public void approveDevice(int id, int reviewerId) throws Exception {
        Map<String, Integer> body = new HashMap<>();
        body.put("reviewedBy", reviewerId);
        apiClient.put("/api/v1/devices/" + id + "/approve", body, Void.class);
    }

    public void rejectDevice(int id, int reviewerId, String remarks) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("reviewedBy", reviewerId);
        body.put("remarks", remarks);
        apiClient.put("/api/v1/devices/" + id + "/reject", body, Void.class);
    }

    public void deactivateDevice(int id) throws Exception {
        apiClient.put("/api/v1/devices/" + id + "/deactivate", null, Void.class);
    }

    public java.util.Map<String, Object> importDevicesCsv(java.io.File file) throws Exception {
        return apiClient.postMultipart("/api/v1/devices/import", file, java.util.Map.class);
    }
}
