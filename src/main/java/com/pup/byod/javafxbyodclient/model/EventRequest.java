package com.pup.byod.javafxbyodclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventRequest {
    @JsonAlias({"requestId", "event_request_id", "eventRequestId"})
    private Integer eventRequestId;
    private String studentId;
    private String responsiblePerson;
    private String organization;
    private String eventName;
    private String eventPurpose;
    private String approvalDocType;
    private String approvalDocRef;
    private String startDate;
    private String endDate;
    private String status;
    private Boolean isSubmitted;
    private Boolean isAccommodated;
    private Integer reviewedBy;
    private String reviewedAt;
    private String remarks;
    private String createdAt;
    private String updatedAt;
    private List<EventRequestDevice> lineItems;
    private Integer creatorUserId;

    // Getters and Setters
    public Integer getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(Integer creatorUserId) { this.creatorUserId = creatorUserId; }

    public Integer getEventRequestId() { return eventRequestId; }
    public void setEventRequestId(Integer eventRequestId) { this.eventRequestId = eventRequestId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventPurpose() { return eventPurpose; }
    public void setEventPurpose(String eventPurpose) { this.eventPurpose = eventPurpose; }

    public String getApprovalDocType() { return approvalDocType; }
    public void setApprovalDocType(String approvalDocType) { this.approvalDocType = approvalDocType; }

    public String getApprovalDocRef() { return approvalDocRef; }
    public void setApprovalDocRef(String approvalDocRef) { this.approvalDocRef = approvalDocRef; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

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

    public List<EventRequestDevice> getLineItems() { return lineItems; }
    public void setLineItems(List<EventRequestDevice> lineItems) { this.lineItems = lineItems; }
}
