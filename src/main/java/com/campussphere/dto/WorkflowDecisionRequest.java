package com.campussphere.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class WorkflowDecisionRequest {
    @NotNull
    private Boolean approved;
    @Size(max = 1000)
    private String reason;
    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
