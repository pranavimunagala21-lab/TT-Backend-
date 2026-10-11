package com.campussphere.controller;

import com.campussphere.dto.HistoricalRecordDto;
import com.campussphere.service.HistoricalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController @RequestMapping("/api/historical-records") @Tag(name="Historical Campus Records")
public class HistoricalRecordController {
    private final HistoricalRecordService service;
    public HistoricalRecordController(HistoricalRecordService service){this.service=service;}
    @GetMapping @Operation(summary="Search historical activities and milestones") public Page<HistoricalRecordDto> search(@RequestParam(required=false) String query,@RequestParam(required=false) String category,@RequestParam(required=false) Long projectId,@RequestParam(required=false) Long clubId,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fromDate,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate toDate,@PageableDefault(size=20,sort="createdAt",direction=Sort.Direction.DESC) Pageable pageable){return service.search(query,category,projectId,clubId,fromDate,toDate,pageable);}
    @GetMapping("/{id}") @Operation(summary="Get a historical record") public HistoricalRecordDto get(@PathVariable Long id){return service.get(id);}
    @PostMapping @PreAuthorize("hasRole('ADMIN')") @Operation(summary="Record a significant campus activity") public ResponseEntity<HistoricalRecordDto> create(@Valid @RequestBody HistoricalRecordDto body,Authentication auth){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body,auth.getName()));}
}
