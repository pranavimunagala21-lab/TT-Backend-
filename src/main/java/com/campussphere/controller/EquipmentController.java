package com.campussphere.controller;

import com.campussphere.dto.EquipmentDto;
import com.campussphere.entity.enums.EquipmentStatus;
import com.campussphere.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@Tag(name = "Equipment Inventory", description = "Campus equipment inventory and availability")
public class EquipmentController {
    private final EquipmentService equipmentService;
    public EquipmentController(EquipmentService equipmentService) { this.equipmentService = equipmentService; }

    @GetMapping
    @Operation(summary = "Search equipment", description = "Filter inventory by keyword, category, status, and current availability")
    public ResponseEntity<List<EquipmentDto>> search(@RequestParam(required = false) String query,
                                                      @RequestParam(required = false) String category,
                                                      @RequestParam(required = false) EquipmentStatus status,
                                                      @RequestParam(defaultValue = "false") boolean availableOnly) {
        return ResponseEntity.ok(equipmentService.searchEquipment(query, category, status, availableOnly));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get equipment", description = "Get inventory details by identifier")
    public ResponseEntity<EquipmentDto> get(@PathVariable Long id) { return ResponseEntity.ok(equipmentService.getEquipment(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(summary = "Create inventory item", description = "Add an equipment item or quantity to the inventory")
    public ResponseEntity<EquipmentDto> create(@Valid @RequestBody EquipmentDto dto) { return ResponseEntity.status(HttpStatus.CREATED).body(equipmentService.createEquipment(dto)); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(summary = "Update inventory item", description = "Update inventory details without overriding allocated quantities")
    public ResponseEntity<EquipmentDto> update(@PathVariable Long id, @Valid @RequestBody EquipmentDto dto) { return ResponseEntity.ok(equipmentService.updateEquipment(id, dto)); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(summary = "Delete inventory item", description = "Delete equipment only when it has no request history")
    public ResponseEntity<Void> delete(@PathVariable Long id) { equipmentService.deleteEquipment(id); return ResponseEntity.noContent().build(); }
}
