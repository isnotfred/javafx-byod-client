package com.pup.byod.javafxbyodclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventRequestDevice {
    @JsonAlias({"requestDeviceId", "request_device_id", "event_device_id", "eventDeviceId"})
    private Integer eventDeviceId;

    @JsonAlias({"requestId", "request_id", "event_request_id", "eventRequestId"})
    private Integer eventRequestId;
    private String deviceName;
    private String brand;
    private String model;
    private String deviceType;
    private String serialNumber;
    private Integer quantity;
    private Integer verifiedBy;
    private String verifiedAt;
    private String deviceStatus;
    private String remarks;
    private String createdAt;
    private String updatedAt;

    // Getters and Setters
    public Integer getEventDeviceId() { return eventDeviceId; }
    public void setEventDeviceId(Integer eventDeviceId) { this.eventDeviceId = eventDeviceId; }

    public Integer getEventRequestId() { return eventRequestId; }
    public void setEventRequestId(Integer eventRequestId) { this.eventRequestId = eventRequestId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(Integer verifiedBy) { this.verifiedBy = verifiedBy; }

    public String getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(String verifiedAt) { this.verifiedAt = verifiedAt; }

    public String getDeviceStatus() { return deviceStatus; }
    public void setDeviceStatus(String deviceStatus) { this.deviceStatus = deviceStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    // Derived fields
    private String currentDayStatus;
    private String lastEventTime;

    public String getCurrentDayStatus() { return currentDayStatus; }
    public void setCurrentDayStatus(String currentDayStatus) { this.currentDayStatus = currentDayStatus; }

    public String getLastEventTime() { return lastEventTime; }
    public void setLastEventTime(String lastEventTime) { this.lastEventTime = lastEventTime; }
}

