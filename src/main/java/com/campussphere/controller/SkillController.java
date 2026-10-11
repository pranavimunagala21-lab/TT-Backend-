package com.campussphere.controller;

import com.campussphere.dto.SkillDto;
import com.campussphere.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skill Management", description = "Endpoints for managing system-wide skills and categories")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    @Operation(summary = "Get all skills", description = "Retrieves the master list of all available skills")
    public ResponseEntity<List<SkillDto>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get skill by ID", description = "Retrieves skill details by ID")
    public ResponseEntity<SkillDto> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new skill", description = "Registers a new skill in the master skills directory")
    public ResponseEntity<SkillDto> createSkill(@Valid @RequestBody SkillDto skillDto) {
        SkillDto created = skillService.createSkill(skillDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a skill", description = "Updates details of an existing skill")
    public ResponseEntity<SkillDto> updateSkill(@PathVariable Long id, @Valid @RequestBody SkillDto skillDto) {
        return ResponseEntity.ok(skillService.updateSkill(id, skillDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a skill", description = "Removes a skill by ID")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}
