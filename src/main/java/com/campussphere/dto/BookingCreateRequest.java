package com.campussphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class BookingCreateRequest {
    @NotNull(message = "Venue ID is required")
    private Long venueId;

    private Long projectId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotBlank(message = "Purpose is required")
    @Size(max = 1000, message = "Purpose must be at most 1000 characters")
    private String purpose;

    @NotNull(message = "Attendee count is required")
    @Min(value = 1, message = "Attendee count must be at least 1")
    private Integer attendeesCount;

    public BookingCreateRequest() {}

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public Integer getAttendeesCount() { return attendeesCount; }
    public void setAttendeesCount(Integer attendeesCount) { this.attendeesCount = attendeesCount; }
}
