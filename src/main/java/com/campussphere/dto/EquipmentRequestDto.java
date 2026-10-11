package com.campussphere.dto;

import com.campussphere.entity.EquipmentRequest;
import com.campussphere.entity.enums.EquipmentRequestStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EquipmentRequestDto {
    private Long id;
    private EquipmentDto equipment;
    private UserDto requester;
    private Long projectId;
    private String projectTitle;
    private Integer quantity;
    private LocalDateTime requestDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private EquipmentRequestStatus status;
    private String conditionOnReturn;
    private String notes;
    private UserDto approvedBy;
    private LocalDateTime approvedAt;
    private UserDto checkedOutBy;
    private LocalDateTime checkedOutAt;
    private UserDto returnedTo;
    private UserDto reviewedBy;
    private LocalDateTime reviewedAt;

    public EquipmentRequestDto() {}

    public EquipmentRequestDto(EquipmentRequest req) {
        if (req != null) {
            this.id = req.getId();
            if (req.getEquipment() != null) {
                this.equipment = new EquipmentDto(req.getEquipment());
            }
            if (req.getRequester() != null) {
                this.requester = new UserDto(req.getRequester());
            }
            if (req.getProject() != null) {
                this.projectId = req.getProject().getId();
                this.projectTitle = req.getProject().getTitle();
            }
            this.quantity = req.getQuantity();
            this.requestDate = req.getRequestDate();
            this.expectedReturnDate = req.getExpectedReturnDate();
            this.actualReturnDate = req.getActualReturnDate();
            this.status = req.getStatus();
            this.conditionOnReturn = req.getConditionOnReturn();
            this.notes = req.getNotes();
            if (req.getApprovedBy() != null) this.approvedBy = new UserDto(req.getApprovedBy());
            this.approvedAt = req.getApprovedAt();
            if (req.getCheckedOutBy() != null) this.checkedOutBy = new UserDto(req.getCheckedOutBy());
            this.checkedOutAt = req.getCheckedOutAt();
            if (req.getReturnedTo() != null) this.returnedTo = new UserDto(req.getReturnedTo());
            if (req.getReviewedBy() != null) this.reviewedBy = new UserDto(req.getReviewedBy());
            this.reviewedAt = req.getReviewedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EquipmentDto getEquipment() { return equipment; }
    public void setEquipment(EquipmentDto equipment) { this.equipment = equipment; }

    public UserDto getRequester() { return requester; }
    public void setRequester(UserDto requester) { this.requester = requester; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }

    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public EquipmentRequestStatus getStatus() { return status; }
    public void setStatus(EquipmentRequestStatus status) { this.status = status; }

    public String getConditionOnReturn() { return conditionOnReturn; }
    public void setConditionOnReturn(String conditionOnReturn) { this.conditionOnReturn = conditionOnReturn; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public UserDto getApprovedBy() { return approvedBy; }
    public void setApprovedBy(UserDto approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public UserDto getCheckedOutBy() { return checkedOutBy; }
    public void setCheckedOutBy(UserDto checkedOutBy) { this.checkedOutBy = checkedOutBy; }
    public LocalDateTime getCheckedOutAt() { return checkedOutAt; }
    public void setCheckedOutAt(LocalDateTime checkedOutAt) { this.checkedOutAt = checkedOutAt; }
    public UserDto getReturnedTo() { return returnedTo; }
    public void setReturnedTo(UserDto returnedTo) { this.returnedTo = returnedTo; }
    public UserDto getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(UserDto reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
