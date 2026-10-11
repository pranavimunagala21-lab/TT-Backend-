package com.campussphere.dto;

import com.campussphere.entity.Booking;
import com.campussphere.entity.enums.BookingStatus;
import java.time.LocalDateTime;

public class BookingDto {
    private Long id;
    private VenueDto venue;
    private UserDto user;
    private Long projectId;
    private String projectTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private Integer attendeesCount;
    private BookingStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private UserDto reviewedBy;
    private LocalDateTime reviewedAt;

    public BookingDto() {}

    public BookingDto(Booking booking) {
        if (booking != null) {
            this.id = booking.getId();
            if (booking.getVenue() != null) {
                this.venue = new VenueDto(booking.getVenue());
            }
            if (booking.getUser() != null) {
                this.user = new UserDto(booking.getUser());
            }
            if (booking.getProject() != null) {
                this.projectId = booking.getProject().getId();
                this.projectTitle = booking.getProject().getTitle();
            }
            this.startTime = booking.getStartTime();
            this.endTime = booking.getEndTime();
            this.purpose = booking.getPurpose();
            this.attendeesCount = booking.getAttendeesCount();
            this.status = booking.getStatus();
            this.rejectionReason = booking.getRejectionReason();
            this.createdAt = booking.getCreatedAt();
            if (booking.getReviewedBy() != null) this.reviewedBy = new UserDto(booking.getReviewedBy());
            this.reviewedAt = booking.getReviewedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public VenueDto getVenue() { return venue; }
    public void setVenue(VenueDto venue) { this.venue = venue; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public Integer getAttendeesCount() { return attendeesCount; }
    public void setAttendeesCount(Integer attendeesCount) { this.attendeesCount = attendeesCount; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public UserDto getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(UserDto reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
