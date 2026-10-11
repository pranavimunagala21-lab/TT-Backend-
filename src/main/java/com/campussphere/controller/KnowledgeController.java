package com.campussphere.controller;

import com.campussphere.dto.KnowledgeRecordDto;
import com.campussphere.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController @RequestMapping("/api/knowledge") @Tag(name="Knowledge Repository")
public class KnowledgeController {
    private final KnowledgeService service;
    public KnowledgeController(KnowledgeService service){this.service=service;}
    @GetMapping @Operation(summary="Search knowledge records with pagination and filters")
    public Page<KnowledgeRecordDto> search(@RequestParam(required=false) String query,@RequestParam(required=false) String category,@RequestParam(required=false) Long projectId,@RequestParam(required=false) Long clubId,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fromDate,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate toDate,@PageableDefault(size=20,sort="createdAt",direction=Sort.Direction.DESC) Pageable pageable){return service.search(query,category,projectId,clubId,fromDate,toDate,pageable);}
    @GetMapping("/{id}") @Operation(summary="Get a knowledge record") public KnowledgeRecordDto get(@PathVariable Long id){return service.get(id);}
    @PostMapping @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") @Operation(summary="Create a knowledge record") public ResponseEntity<KnowledgeRecordDto> create(@Valid @RequestBody KnowledgeRecordDto body,Authentication auth){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body,auth.getName()));}
    @PutMapping("/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") @Operation(summary="Update an authorized knowledge record") public KnowledgeRecordDto update(@PathVariable Long id,@Valid @RequestBody KnowledgeRecordDto body,Authentication auth){return service.update(id,body,auth.getName());}
    @DeleteMapping("/{id}") @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')") @Operation(summary="Delete an authorized knowledge record") public ResponseEntity<Void> delete(@PathVariable Long id,Authentication auth){service.delete(id,auth.getName());return ResponseEntity.noContent().build();}
}
