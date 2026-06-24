package com.pup.byod.javafxbyodclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceCampusStatus {
    @JsonAlias({"requestDeviceId", "request_device_id", "deviceId", "device_id"})
    private Integer deviceId;
    private String studentId;
    private String deviceName;
    private String serialNumber;
    private String brand;
    private String model;
    private String deviceType;
    private String campusStatus;
    private String lastEventTime;

    // Getters and Setters
    public Integer getDeviceId() { return deviceId; }
    public void setDeviceId(Integer deviceId) { this.deviceId = deviceId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getCampusStatus() { return campusStatus; }
    public void setCampusStatus(String campusStatus) { this.campusStatus = campusStatus; }

    public String getLastEventTime() { return lastEventTime; }
    public void setLastEventTime(String lastEventTime) { this.lastEventTime = lastEventTime; }
}
