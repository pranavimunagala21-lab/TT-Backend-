package com.campussphere.controller;

import com.campussphere.dto.*;
import com.campussphere.entity.enums.Role;
import com.campussphere.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name="Recommendations and Discovery", description="Explainable local recommendations from existing campus data")
@PreAuthorize("isAuthenticated()")
public class RecommendationController {
    private final RecommendationService service;
    public RecommendationController(RecommendationService service) { this.service=service; }

    @GetMapping("/projects/{projectId}/people")
    @Operation(summary="Rank possible student collaborators and faculty mentors for a project")
    public Page<RankedPersonMatchDto> people(@PathVariable Long projectId,@RequestParam(required=false) Role role,@RequestParam(required=false) String query,
            @PageableDefault(size=20) Pageable pageable) { return service.peopleForProject(projectId,role,query,pageable); }

    @GetMapping("/projects/{projectId}/venues")
    @Operation(summary="Recommend available venues for the requested time, capacity, and facilities")
    public List<RecommendationDto> venues(@PathVariable Long projectId,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required=false) Integer attendees,@RequestParam(required=false) Set<String> facilities) {
        return service.venuesForProject(projectId,startTime,endTime,attendees,facilities);
    }

    @GetMapping("/projects/{projectId}/equipment")
    @Operation(summary="Recommend matching equipment that is currently available")
    public List<RecommendationDto> equipment(@PathVariable Long projectId) { return service.equipmentForProject(projectId); }

    @GetMapping("/projects/{projectId}/related-projects")
    @Operation(summary="Discover related projects by category, skills, technologies, and text")
    public Page<DiscoveryRecommendationDto> relatedProjects(@PathVariable Long projectId,
            @PageableDefault(size=20) Pageable pageable) { return service.relatedProjects(projectId,pageable); }

    @GetMapping("/projects/{projectId}/knowledge")
    @Operation(summary="Discover related knowledge records for a project")
    public Page<DiscoveryRecommendationDto> relatedKnowledge(@PathVariable Long projectId,
            @PageableDefault(size=20) Pageable pageable) { return service.relatedKnowledge(projectId,pageable); }
}
