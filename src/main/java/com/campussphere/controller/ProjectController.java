package com.campussphere.controller;

import com.campussphere.dto.AddProjectMemberRequest;
import com.campussphere.dto.DocumentDto;
import com.campussphere.dto.ProjectDto;
import com.campussphere.dto.ProjectMemberDto;
import com.campussphere.entity.enums.ProjectStatus;
import com.campussphere.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Project Repository & Continuity", description = "Endpoints for managing projects, team memberships, project documents, and similar project discovery")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "Get all projects or search with filters", description = "Retrieves all projects or searches by keyword, category, and status")
    public ResponseEntity<List<ProjectDto>> getProjects(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) ProjectStatus status,
            Authentication authentication) {
        boolean admin = authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        java.util.function.Predicate<ProjectDto> approved = p -> p.getStatus() != ProjectStatus.IDEA && p.getStatus() != ProjectStatus.REJECTED;
        if (query != null || category != null || status != null) {
            return ResponseEntity.ok(projectService.searchProjects(query, category, status).stream().filter(p -> admin || approved.test(p)).toList());
        }
        return ResponseEntity.ok(projectService.getAllProjects().stream().filter(p -> admin || approved.test(p)).toList());
    }

    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectDto>> getMyProjects(Authentication authentication) {
        return ResponseEntity.ok(projectService.getAllProjects().stream()
                .filter(p -> p.getOwner() != null && authentication.getName().equalsIgnoreCase(p.getOwner().getEmail())).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project details by ID", description = "Retrieves details for a specific project")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id, Authentication authentication) {
        ProjectDto project = projectService.getProjectById(id);
        boolean admin = authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean owner = authentication != null && project.getOwner() != null && authentication.getName().equalsIgnoreCase(project.getOwner().getEmail());
        if (!admin && !owner && (project.getStatus() == ProjectStatus.IDEA || project.getStatus() == ProjectStatus.REJECTED)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Project not found");
        }
        return ResponseEntity.ok(project);
    }

    @PostMapping
    @Operation(summary = "Create a new project", description = "Creates a new project owned by the authenticated user")
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto dto, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        ProjectDto created = projectService.createProject(dto, currentUserEmail);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project", description = "Updates details for an existing project (requires ownership or ADMIN role)")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDto dto, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        return ResponseEntity.ok(projectService.updateProject(id, dto, currentUserEmail));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project", description = "Deletes a project by ID (requires ownership or ADMIN role)")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        projectService.deleteProject(id, currentUserEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "Get project members", description = "Lists all team members of a project")
    public ResponseEntity<List<ProjectMemberDto>> getProjectMembers(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectMembers(id));
    }

    @PostMapping("/{id}/membership-requests")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProjectMemberDto> requestMembership(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.requestProjectMembership(id, authentication.getName()));
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Add member to project", description = "Adds a user to a project team with a specific role")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'CLUB_ADMIN', 'STUDENT')")
    public ResponseEntity<ProjectMemberDto> addProjectMember(@PathVariable Long id, @Valid @RequestBody AddProjectMemberRequest request, Authentication authentication) {
        requireOwnerOrAdmin(id, authentication);
        ProjectMemberDto member = projectService.addProjectMember(id, request);
        return new ResponseEntity<>(member, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @Operation(summary = "Remove member from project", description = "Removes a member from a project team by user ID")
    public ResponseEntity<Void> removeProjectMember(@PathVariable Long id, @PathVariable Long userId, Authentication authentication) {
        requireOwnerOrAdmin(id, authentication);
        projectService.removeProjectMember(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/documents")
    @Operation(summary = "Get project documents", description = "Retrieves all documentation attachments associated with a project")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DocumentDto>> getProjectDocuments(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(projectService.getProjectDocuments(id, authentication.getName()));
    }

    @GetMapping("/{id}/similar")
    @Operation(summary = "Find similar projects", description = "Retrieves projects in the same category for knowledge reuse and collaboration")
    public ResponseEntity<List<ProjectDto>> getSimilarProjects(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getSimilarProjects(id).stream()
                .filter(p -> p.getStatus() != ProjectStatus.IDEA && p.getStatus() != ProjectStatus.REJECTED).toList());
    }

    private void requireOwnerOrAdmin(Long id, Authentication authentication) {
        ProjectDto project = projectService.getProjectById(id);
        boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && (project.getOwner() == null || !authentication.getName().equalsIgnoreCase(project.getOwner().getEmail()))) {
            throw new org.springframework.security.access.AccessDeniedException("Only the project owner or an administrator can manage project membership");
        }
    }
}
