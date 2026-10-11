package com.campussphere.service.impl;

import com.campussphere.dto.MaintenanceRecordCreateRequest;
import com.campussphere.dto.MaintenanceRecordDto;
import com.campussphere.entity.Equipment;
import com.campussphere.entity.MaintenanceRecord;
import com.campussphere.entity.enums.EquipmentCondition;
import com.campussphere.entity.enums.EquipmentStatus;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ConflictException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.EquipmentRepository;
import com.campussphere.repository.MaintenanceRecordRepository;
import com.campussphere.service.MaintenanceRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceRecordServiceImpl implements MaintenanceRecordService {
    private final MaintenanceRecordRepository maintenanceRepository;
    private final EquipmentRepository equipmentRepository;
    public MaintenanceRecordServiceImpl(MaintenanceRecordRepository maintenanceRepository, EquipmentRepository equipmentRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public List<MaintenanceRecordDto> getRecords(Long equipmentId) {
        List<MaintenanceRecord> records = equipmentId == null ? maintenanceRepository.findAll() : maintenanceRepository.findByEquipmentId(equipmentId);
        return records.stream().map(MaintenanceRecordDto::new).collect(Collectors.toList());
    }

    @Override
    public MaintenanceRecordDto getRecord(Long id) { return new MaintenanceRecordDto(recordById(id)); }

    @Override
    @Transactional
    public MaintenanceRecordDto createRecord(MaintenanceRecordCreateRequest request, String performedBy) {
        Equipment equipment = equipmentRepository.findByIdForUpdate(request.getEquipmentId()).orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + request.getEquipmentId()));
        if (maintenanceRepository.existsByEquipmentIdAndStatus(equipment.getId(), "IN_PROGRESS")) throw new ConflictException("Equipment is already under maintenance");
        if (!equipment.getAvailableQuantity().equals(equipment.getQuantity())) throw new ConflictException("All equipment units must be returned before maintenance can start");
        MaintenanceRecord record = new MaintenanceRecord();
        record.setEquipment(equipment);
        record.setIssueDescription(request.getIssue().trim());
        record.setConditionStatus(request.getConditionStatus() == null ? equipment.getConditionStatus() : request.getConditionStatus());
        record.setCost(request.getCost());
        record.setNotes(request.getNotes());
        record.setPerformedBy(performedBy);
        record.setStatus("IN_PROGRESS");
        equipment.setStatus(EquipmentStatus.MAINTENANCE);
        equipmentRepository.save(equipment);
        return new MaintenanceRecordDto(maintenanceRepository.save(record));
    }

    @Override
    @Transactional
    public MaintenanceRecordDto completeRecord(Long id, EquipmentCondition finalCondition) {
        MaintenanceRecord record = maintenanceRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found with id: " + id));
        Equipment equipment = equipmentRepository.findByIdForUpdate(record.getEquipment().getId()).orElseThrow();
        if (!"IN_PROGRESS".equals(record.getStatus())) throw new ConflictException("Only in-progress maintenance records can be completed");
        if (finalCondition == null) throw new BadRequestException("Final equipment condition is required");
        if (finalCondition == EquipmentCondition.NEEDS_REPAIR) throw new BadRequestException("Equipment needing repair cannot be cleared from maintenance");
        record.setStatus("FIXED");
        record.setCompletionDate(LocalDate.now());
        equipment.setConditionStatus(finalCondition);
        equipment.setStatus(equipment.getAvailableQuantity() > 0 ? EquipmentStatus.AVAILABLE : EquipmentStatus.IN_USE);
        equipmentRepository.save(equipment);
        return new MaintenanceRecordDto(maintenanceRepository.save(record));
    }

    private MaintenanceRecord recordById(Long id) { return maintenanceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found with id: " + id)); }
}
