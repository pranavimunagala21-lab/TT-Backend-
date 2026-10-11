package com.campussphere.service;

import com.campussphere.dto.EquipmentDto;
import com.campussphere.entity.enums.EquipmentStatus;
import java.util.List;

public interface EquipmentService {
    List<EquipmentDto> searchEquipment(String query, String category, EquipmentStatus status, boolean availableOnly);
    EquipmentDto getEquipment(Long id);
    EquipmentDto createEquipment(EquipmentDto dto);
    EquipmentDto updateEquipment(Long id, EquipmentDto dto);
    void deleteEquipment(Long id);
}
