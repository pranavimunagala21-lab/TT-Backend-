package com.campussphere.dto;

import com.campussphere.entity.ResourceRequest;
import com.campussphere.entity.enums.ResourceRequestStatus;
import com.campussphere.entity.enums.ResourceType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ResourceRequestDto {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private UserDto requester;
    private ResourceType resourceType;
    private String resourceName;
    private Integer quantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private ResourceRequestStatus status;
    private LocalDateTime createdAt;

    public ResourceRequestDto() {}

    public ResourceRequestDto(ResourceRequest request) {
        if (request != null) {
            this.id = request.getId();
            if (request.getProject() != null) {
                this.projectId = request.getProject().getId();
                this.projectTitle = request.getProject().getTitle();
            }
            if (request.getRequester() != null) {
                this.requester = new UserDto(request.getRequester());
            }
            this.resourceType = request.getResourceType();
            this.resourceName = request.getResourceName();
            this.quantity = request.getQuantity();
            this.startDate = request.getStartDate();
            this.endDate = request.getEndDate();
            this.reason = request.getReason();
            this.status = request.getStatus();
            this.createdAt = request.getCreatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public UserDto getRequester() { return requester; }
    public void setRequester(UserDto requester) { this.requester = requester; }

    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public ResourceRequestStatus getStatus() { return status; }
    public void setStatus(ResourceRequestStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
