package com.campussphere.entity;

import com.campussphere.entity.enums.EquipmentRequestStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_requests")
public class EquipmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private Integer quantity = 1;

    private LocalDateTime requestDate = LocalDateTime.now();
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checked_out_by_id")
    private User checkedOutBy;

    private LocalDateTime checkedOutAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "returned_to_id")
    private User returnedTo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentRequestStatus status = EquipmentRequestStatus.PENDING;

    private String conditionOnReturn;

    @Column(length = 1000)
    private String notes;

    public EquipmentRequest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public User getRequester() { return requester; }
    public void setRequester(User requester) { this.requester = requester; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }

    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public User getCheckedOutBy() { return checkedOutBy; }
    public void setCheckedOutBy(User checkedOutBy) { this.checkedOutBy = checkedOutBy; }

    public LocalDateTime getCheckedOutAt() { return checkedOutAt; }
    public void setCheckedOutAt(LocalDateTime checkedOutAt) { this.checkedOutAt = checkedOutAt; }

    public User getReturnedTo() { return returnedTo; }
    public void setReturnedTo(User returnedTo) { this.returnedTo = returnedTo; }

    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public EquipmentRequestStatus getStatus() { return status; }
    public void setStatus(EquipmentRequestStatus status) { this.status = status; }

    public String getConditionOnReturn() { return conditionOnReturn; }
    public void setConditionOnReturn(String conditionOnReturn) { this.conditionOnReturn = conditionOnReturn; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
