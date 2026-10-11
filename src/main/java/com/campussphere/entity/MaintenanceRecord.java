package com.campussphere.entity;

import com.campussphere.entity.enums.EquipmentCondition;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(nullable = false, length = 2000)
    private String issueDescription;

    @Enumerated(EnumType.STRING)
    private EquipmentCondition conditionStatus;

    private String status = "IN_PROGRESS"; // IN_PROGRESS, FIXED

    private LocalDate serviceDate = LocalDate.now();
    private LocalDate completionDate;

    private Double cost;
    private String performedBy;

    @Column(length = 2000)
    private String notes;

    public MaintenanceRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public String getIssue() { return issueDescription; }
    public void setIssue(String issue) { this.issueDescription = issue; }

    public EquipmentCondition getConditionStatus() {
        if (conditionStatus != null) return conditionStatus;
        return equipment != null ? equipment.getConditionStatus() : null;
    }
    public void setConditionStatus(EquipmentCondition conditionStatus) { this.conditionStatus = conditionStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public LocalDate getMaintenanceDate() { return serviceDate; }
    public void setMaintenanceDate(LocalDate maintenanceDate) { this.serviceDate = maintenanceDate; }

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
