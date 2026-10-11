package com.campussphere.dto;

import com.campussphere.entity.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ResourceRequestCreateRequest {
    private Long projectId;

    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;

    @NotBlank(message = "Resource name is required")
    private String resourceName;

    private Integer quantity = 1;
    private LocalDate startDate;
    private LocalDate endDate;

    @NotBlank(message = "Reason is required")
    private String reason;

    public ResourceRequestCreateRequest() {}

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

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
}
