package com.campussphere.service;

import com.campussphere.dto.MaintenanceRecordCreateRequest;
import com.campussphere.dto.MaintenanceRecordDto;
import com.campussphere.entity.enums.EquipmentCondition;
import java.util.List;

public interface MaintenanceRecordService {
    List<MaintenanceRecordDto> getRecords(Long equipmentId);
    MaintenanceRecordDto getRecord(Long id);
    MaintenanceRecordDto createRecord(MaintenanceRecordCreateRequest request, String performedBy);
    MaintenanceRecordDto completeRecord(Long id, EquipmentCondition finalCondition);
}
