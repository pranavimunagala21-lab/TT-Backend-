package com.campussphere.controller;

import com.campussphere.dto.BookingDecisionRequest;
import com.campussphere.dto.EquipmentRequestCreateRequest;
import com.campussphere.dto.EquipmentRequestDto;
import com.campussphere.entity.enums.EquipmentRequestStatus;
import com.campussphere.exception.ForbiddenException;
import com.campussphere.service.EquipmentRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/equipment-requests")
@Tag(name = "Equipment Requests", description = "Equipment request, approval, checkout, return, and cancellation workflows")
public class EquipmentRequestController {
    private final EquipmentRequestService requestService;
    public EquipmentRequestController(EquipmentRequestService requestService) { this.requestService = requestService; }

    @GetMapping
    @Operation(summary = "List equipment requests", description = "List own requests or all requests for resource managers, optionally filtered by status")
    public ResponseEntity<List<EquipmentRequestDto>> list(@RequestParam(required = false) EquipmentRequestStatus status, Authentication authentication) {
        boolean manager = manager(authentication);
        return ResponseEntity.ok(requestService.getRequests(authentication.getName(), manager, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get equipment request", description = "Get a request owned by the caller or accessible to a resource manager")
    public ResponseEntity<EquipmentRequestDto> get(@PathVariable Long id, Authentication authentication) {
        EquipmentRequestDto request = requestService.getRequest(id);
        if (!manager(authentication) && !authentication.getName().equalsIgnoreCase(request.getRequester().getEmail())) throw new ForbiddenException("You can only view your own equipment requests");
        return ResponseEntity.ok(request);
    }

    @PostMapping
    @Operation(summary = "Request equipment", description = "Create a pending request for the authenticated user")
    public ResponseEntity<EquipmentRequestDto> create(@Valid @RequestBody EquipmentRequestCreateRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequest(request, authentication.getName()));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(summary = "Approve equipment request", description = "Reserve inventory for a pending request")
    public ResponseEntity<EquipmentRequestDto> approve(@PathVariable Long id, Authentication authentication) { return ResponseEntity.ok(requestService.approve(id, authentication.getName())); }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(summary = "Reject equipment request", description = "Reject a pending request with a reason")
    public ResponseEntity<EquipmentRequestDto> reject(@PathVariable Long id, @Valid @RequestBody BookingDecisionRequest request, Authentication authentication) {
        return ResponseEntity.ok(requestService.reject(id, authentication.getName(), request.getReason()));
    }

    @PostMapping("/{id}/checkout")
    @Operation(summary = "Check out equipment", description = "Record checkout time and responsible user for an approved allocation")
    public ResponseEntity<EquipmentRequestDto> checkout(@PathVariable Long id, Authentication authentication) {
        EquipmentRequestDto request = requestService.getRequest(id);
        if (!manager(authentication) && !authentication.getName().equalsIgnoreCase(request.getRequester().getEmail())) throw new ForbiddenException("Only the requester or a resource manager can check out this allocation");
        return ResponseEntity.ok(requestService.checkout(id, authentication.getName()));
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Return equipment", description = "Record the return time, recipient, and returned equipment condition")
    public ResponseEntity<EquipmentRequestDto> returnEquipment(@PathVariable Long id, @RequestParam String condition, Authentication authentication) {
        EquipmentRequestDto request = requestService.getRequest(id);
        if (!manager(authentication) && !authentication.getName().equalsIgnoreCase(request.getRequester().getEmail())) throw new ForbiddenException("Only the requester or a resource manager can return this allocation");
        return ResponseEntity.ok(requestService.returnEquipment(id, authentication.getName(), condition));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel equipment request", description = "Cancel a pending or approved request")
    public ResponseEntity<EquipmentRequestDto> cancel(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(requestService.cancel(id, authentication.getName(), manager(authentication)));
    }

    private boolean manager(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_RESOURCE_MANAGER"));
    }
}
