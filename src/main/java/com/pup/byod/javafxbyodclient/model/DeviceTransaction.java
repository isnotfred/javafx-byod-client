package com.pup.byod.javafxbyodclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceTransaction {
    private Integer transactionId;
    private Integer requestDeviceId;
    private String logDate;
    
    private String ingressTime;
    private Integer ingressHandledBy;
    
    private String egressTime;
    private Integer egressHandledBy;
    
    private boolean noEgressMarked;
    private String notes;
    
    private String createdAt;
    private String updatedAt;

    // Getters and Setters
    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }

    public Integer getRequestDeviceId() { return requestDeviceId; }
    public void setRequestDeviceId(Integer requestDeviceId) { this.requestDeviceId = requestDeviceId; }

    public String getLogDate() { return logDate; }
    public void setLogDate(String logDate) { this.logDate = logDate; }

    public String getIngressTime() { return ingressTime; }
    public void setIngressTime(String ingressTime) { this.ingressTime = ingressTime; }

    public Integer getIngressHandledBy() { return ingressHandledBy; }
    public void setIngressHandledBy(Integer ingressHandledBy) { this.ingressHandledBy = ingressHandledBy; }

    public String getEgressTime() { return egressTime; }
    public void setEgressTime(String egressTime) { this.egressTime = egressTime; }

    public Integer getEgressHandledBy() { return egressHandledBy; }
    public void setEgressHandledBy(Integer egressHandledBy) { this.egressHandledBy = egressHandledBy; }

    public boolean isNoEgressMarked() { return noEgressMarked; }
    public void setNoEgressMarked(boolean noEgressMarked) { this.noEgressMarked = noEgressMarked; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
