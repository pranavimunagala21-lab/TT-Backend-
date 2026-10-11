package com.campussphere.dto;

import com.campussphere.entity.MaintenanceRecord;
import com.campussphere.entity.enums.EquipmentCondition;
import java.time.LocalDate;

public class MaintenanceRecordDto {
    private Long id;
    private EquipmentDto equipment;
    private String issue;
    private EquipmentCondition conditionStatus;
    private LocalDate maintenanceDate;
    private LocalDate completionDate;
    private String notes;
    private String status;
    private Double cost;
    private String performedBy;

    public MaintenanceRecordDto() {}

    public MaintenanceRecordDto(MaintenanceRecord record) {
        if (record != null) {
            this.id = record.getId();
            if (record.getEquipment() != null) {
                this.equipment = new EquipmentDto(record.getEquipment());
            }
            this.issue = record.getIssue();
            this.conditionStatus = record.getConditionStatus();
            this.maintenanceDate = record.getMaintenanceDate();
            this.completionDate = record.getCompletionDate();
            this.notes = record.getNotes();
            this.status = record.getStatus();
            this.cost = record.getCost();
            this.performedBy = record.getPerformedBy();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EquipmentDto getEquipment() { return equipment; }
    public void setEquipment(EquipmentDto equipment) { this.equipment = equipment; }

    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }

    public EquipmentCondition getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(EquipmentCondition conditionStatus) { this.conditionStatus = conditionStatus; }

    public LocalDate getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(LocalDate maintenanceDate) { this.maintenanceDate = maintenanceDate; }

    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
}
