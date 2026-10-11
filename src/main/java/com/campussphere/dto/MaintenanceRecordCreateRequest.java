package com.campussphere.dto;

import com.campussphere.entity.enums.EquipmentCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class MaintenanceRecordCreateRequest {
    @NotNull(message = "Equipment ID is required")
    private Long equipmentId;
    @NotBlank(message = "Issue description is required")
    private String issue;
    private EquipmentCondition conditionStatus;
    @PositiveOrZero(message = "Cost cannot be negative")
    private Double cost;
    private String notes;

    public Long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }
    public EquipmentCondition getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(EquipmentCondition conditionStatus) { this.conditionStatus = conditionStatus; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
