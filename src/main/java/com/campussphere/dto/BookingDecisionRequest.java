package com.campussphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookingDecisionRequest {
    @NotBlank(message = "Decision reason is required")
    @Size(max = 1000, message = "Reason must be at most 1000 characters")
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
