package com.campussphere.dto;

import com.campussphere.entity.enums.EquipmentCondition;
import jakarta.validation.constraints.NotNull;

public class MaintenanceCompletionRequest {
    @NotNull(message = "Final equipment condition is required")
    private EquipmentCondition conditionStatus;
    public EquipmentCondition getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(EquipmentCondition conditionStatus) { this.conditionStatus = conditionStatus; }
}
