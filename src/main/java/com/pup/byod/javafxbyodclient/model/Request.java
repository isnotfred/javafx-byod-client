package com.pup.byod.javafxbyodclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {
    private Integer requestId;
    private String requestType;
    private String studentId;
    
    // Event-specific details
    private String eventName;
    private String venue;
    private String organization;
    private String responsiblePerson;
    private String purpose;
    private String startDate;
    private String endDate;
    private String expectedIngressTime;
    private String expectedEgressTime;
    
    private String status;
    private Boolean isSubmitted;
    private Boolean isAccommodated;
    private Integer reviewedBy;
    private String reviewedAt;
    private String remarks;
    
    private String createdAt;
    private String updatedAt;

    // Getters and Setters
    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getExpectedIngressTime() { return expectedIngressTime; }
    public void setExpectedIngressTime(String expectedIngressTime) { this.expectedIngressTime = expectedIngressTime; }

    public String getExpectedEgressTime() { return expectedEgressTime; }
    public void setExpectedEgressTime(String expectedEgressTime) { this.expectedEgressTime = expectedEgressTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsSubmitted() { return isSubmitted; }
    public void setIsSubmitted(Boolean isSubmitted) { this.isSubmitted = isSubmitted; }

    public Boolean getIsAccommodated() { return isAccommodated; }
    public void setIsAccommodated(Boolean isAccommodated) { this.isAccommodated = isAccommodated; }

    public Integer getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Integer reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    private java.util.List<RequestDevice> lineItems;
    public java.util.List<RequestDevice> getLineItems() { return lineItems; }
    public void setLineItems(java.util.List<RequestDevice> lineItems) { this.lineItems = lineItems; }
}
