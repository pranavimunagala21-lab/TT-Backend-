package com.campussphere.service.impl;

import com.campussphere.dto.EquipmentDto;
import com.campussphere.entity.Equipment;
import com.campussphere.entity.enums.EquipmentCondition;
import com.campussphere.entity.enums.EquipmentStatus;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ConflictException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.EquipmentRepository;
import com.campussphere.repository.EquipmentRequestRepository;
import com.campussphere.repository.MaintenanceRecordRepository;
import com.campussphere.service.EquipmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl implements EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentRequestRepository requestRepository;
    private final MaintenanceRecordRepository maintenanceRepository;

    public EquipmentServiceImpl(EquipmentRepository equipmentRepository, EquipmentRequestRepository requestRepository, MaintenanceRecordRepository maintenanceRepository) {
        this.equipmentRepository = equipmentRepository;
        this.requestRepository = requestRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public List<EquipmentDto> searchEquipment(String query, String category, EquipmentStatus status, boolean availableOnly) {
        return equipmentRepository.searchEquipment(blankToNull(query), blankToNull(category), status).stream()
                .filter(e -> !availableOnly || (e.getStatus() == EquipmentStatus.AVAILABLE && e.getAvailableQuantity() > 0 && e.getConditionStatus() != EquipmentCondition.NEEDS_REPAIR))
                .map(EquipmentDto::new).collect(Collectors.toList());
    }

    @Override
    public EquipmentDto getEquipment(Long id) { return new EquipmentDto(findById(id)); }

    @Override
    @Transactional
    public EquipmentDto createEquipment(EquipmentDto dto) {
        validate(dto);
        if (dto.getSerialNumber() != null && equipmentRepository.findBySerialNumber(dto.getSerialNumber()).isPresent()) throw new ConflictException("Serial number is already in use");
        Equipment equipment = new Equipment();
        copyInventory(dto, equipment);
        equipment.setAvailableQuantity(dto.getQuantity());
        equipment.setConditionStatus(dto.getConditionStatus() == null ? EquipmentCondition.EXCELLENT : dto.getConditionStatus());
        equipment.setStatus(equipment.getConditionStatus() == EquipmentCondition.NEEDS_REPAIR ? EquipmentStatus.MAINTENANCE : EquipmentStatus.AVAILABLE);
        return new EquipmentDto(equipmentRepository.save(equipment));
    }

    @Override
    @Transactional
    public EquipmentDto updateEquipment(Long id, EquipmentDto dto) {
        validate(dto);
        Equipment equipment = equipmentRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));
        if (dto.getSerialNumber() != null && equipmentRepository.findBySerialNumber(dto.getSerialNumber()).filter(other -> !other.getId().equals(id)).isPresent()) throw new ConflictException("Serial number is already in use");
        int allocated = equipment.getQuantity() - equipment.getAvailableQuantity();
        if (dto.getQuantity() < allocated) throw new ConflictException("Quantity cannot be lower than the number of units currently allocated");
        equipment.setAvailableQuantity(dto.getQuantity() - allocated);
        copyInventory(dto, equipment);
        if (dto.getConditionStatus() != null) equipment.setConditionStatus(dto.getConditionStatus());
        if (equipment.getStatus() != EquipmentStatus.MAINTENANCE) {
            equipment.setStatus(equipment.getConditionStatus() == EquipmentCondition.NEEDS_REPAIR ? EquipmentStatus.MAINTENANCE : (equipment.getAvailableQuantity() > 0 ? EquipmentStatus.AVAILABLE : EquipmentStatus.IN_USE));
        }
        return new EquipmentDto(equipmentRepository.save(equipment));
    }

    @Override
    @Transactional
    public void deleteEquipment(Long id) {
        Equipment equipment = equipmentRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));
        if (requestRepository.existsByEquipmentId(id) || !maintenanceRepository.findByEquipmentId(id).isEmpty()) throw new ConflictException("Equipment has lifecycle history and cannot be deleted");
        equipmentRepository.delete(equipment);
    }

    private Equipment findById(Long id) { return equipmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id)); }
    private void validate(EquipmentDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) throw new BadRequestException("Equipment name is required");
        if (dto.getCategory() == null || dto.getCategory().isBlank()) throw new BadRequestException("Equipment category is required");
        if (dto.getQuantity() == null || dto.getQuantity() < 1) throw new BadRequestException("Quantity must be at least 1");
        if (dto.getStatus() == EquipmentStatus.MAINTENANCE) throw new BadRequestException("Use maintenance records to place equipment under maintenance");
    }
    private void copyInventory(EquipmentDto dto, Equipment equipment) {
        equipment.setName(dto.getName().trim());
        equipment.setCategory(dto.getCategory().trim());
        equipment.setSerialNumber(blankToNull(dto.getSerialNumber()));
        equipment.setQuantity(dto.getQuantity());
        equipment.setLocation(dto.getLocation());
        equipment.setDescription(dto.getDescription());
        equipment.setImageUrl(dto.getImageUrl());
    }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
