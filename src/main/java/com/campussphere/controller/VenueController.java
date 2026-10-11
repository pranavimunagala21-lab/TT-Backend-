package com.campussphere.controller;

import com.campussphere.dto.VenueDto;
import com.campussphere.service.VenueService;
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
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "Campus venue inventory and availability")
public class VenueController {
    private final VenueService venueService;
    public VenueController(VenueService venueService) { this.venueService = venueService; }

    @GetMapping
    @Operation(summary = "Search venues", description = "Find venues by keyword, building, capacity, and status")
    public ResponseEntity<List<VenueDto>> search(@RequestParam(required = false) String query,
                                                  @RequestParam(required = false) String building,
                                                  @RequestParam(required = false) Integer minCapacity,
                                                  @RequestParam(required = false) String status) {
        return ResponseEntity.ok(venueService.findVenues(query, building, minCapacity, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get venue", description = "Return a venue by identifier")
    public ResponseEntity<VenueDto> get(@PathVariable Long id) { return ResponseEntity.ok(venueService.getVenue(id)); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'RESOURCE_MANAGER')")
    @Operation(summary = "Create venue", description = "Create a venue managed by the authenticated administrator or resource manager")
    public ResponseEntity<VenueDto> create(@Valid @RequestBody VenueDto dto, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.createVenue(dto, authentication.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'RESOURCE_MANAGER')")
    @Operation(summary = "Update venue", description = "Update venue details and operational status")
    public ResponseEntity<VenueDto> update(@PathVariable Long id, @Valid @RequestBody VenueDto dto) { return ResponseEntity.ok(venueService.updateVenue(id, dto)); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_MANAGER')")
    @Operation(summary = "Delete venue", description = "Delete a venue only when it has no booking history")
    public ResponseEntity<Void> delete(@PathVariable Long id) { venueService.deleteVenue(id); return ResponseEntity.noContent().build(); }
}
