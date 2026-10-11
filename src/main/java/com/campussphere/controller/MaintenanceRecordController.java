package com.campussphere.controller;

import com.campussphere.dto.MaintenanceCompletionRequest;
import com.campussphere.dto.MaintenanceRecordCreateRequest;
import com.campussphere.dto.MaintenanceRecordDto;
import com.campussphere.service.MaintenanceRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance-records")
@Tag(name = "Equipment Maintenance", description = "Maintenance lifecycle and service history for equipment")
public class MaintenanceRecordController {
    private final MaintenanceRecordService maintenanceService;
    public MaintenanceRecordController(MaintenanceRecordService maintenanceService) { this.maintenanceService = maintenanceService; }

    @GetMapping
    @Operation(summary = "List maintenance records", description = "List maintenance history, optionally for one equipment item")
    public ResponseEntity<List<MaintenanceRecordDto>> list(@RequestParam(required = false) Long equipmentId) { return ResponseEntity.ok(maintenanceService.getRecords(equipmentId)); }

    @GetMapping("/{id}")
    @Operation(summary = "Get maintenance record", description = "Get a maintenance record by identifier")
    public ResponseEntity<MaintenanceRecordDto> get(@PathVariable Long id) { return ResponseEntity.ok(maintenanceService.getRecord(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(summary = "Start maintenance", description = "Open a maintenance record and make the equipment unavailable")
    public ResponseEntity<MaintenanceRecordDto> create(@Valid @RequestBody MaintenanceRecordCreateRequest request, org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(maintenanceService.createRecord(request, authentication.getName()));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(summary = "Complete maintenance", description = "Close an in-progress record and restore inventory availability when serviceable")
    public ResponseEntity<MaintenanceRecordDto> complete(@PathVariable Long id, @Valid @RequestBody MaintenanceCompletionRequest request) {
        return ResponseEntity.ok(maintenanceService.completeRecord(id, request.getConditionStatus()));
    }
}
