package com.campussphere.controller;

import com.campussphere.dto.AddClubMemberRequest;
import com.campussphere.dto.ClubDto;
import com.campussphere.dto.ClubMemberDto;
import com.campussphere.dto.ProjectDto;
import com.campussphere.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@Tag(name = "Club & Organization Hub", description = "Endpoints for managing campus clubs, memberships, and club projects")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping
    @Operation(summary = "Get all clubs or search with filters", description = "Retrieves all clubs or filters by search query, department, and category")
    public ResponseEntity<List<ClubDto>> getClubs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String category) {
        if (query != null || department != null || category != null) {
            return ResponseEntity.ok(clubService.searchClubs(query, department, category));
        }
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get club profile by ID", description = "Retrieves detailed information about a specific club")
    public ResponseEntity<ClubDto> getClubById(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getClubById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'CLUB_ADMIN')")
    @Operation(summary = "Create a new club", description = "Registers a new campus club")
    public ResponseEntity<ClubDto> createClub(@Valid @RequestBody ClubDto clubDto) {
        ClubDto created = clubService.createClub(clubDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'CLUB_ADMIN')")
    @Operation(summary = "Update club details", description = "Updates profile information for an existing club")
    public ResponseEntity<ClubDto> updateClub(@PathVariable Long id, @Valid @RequestBody ClubDto clubDto) {
        return ResponseEntity.ok(clubService.updateClub(id, clubDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Delete a club", description = "Removes a club by ID")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "Get club members", description = "Lists all registered members of a club")
    public ResponseEntity<List<ClubMemberDto>> getClubMembers(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getClubMembers(id));
    }

    @PostMapping("/{id}/membership-requests")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClubMemberDto> requestMembership(@PathVariable Long id, org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clubService.requestMembership(id, authentication.getName()));
    }

    @GetMapping("/memberships/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ClubMemberDto>> myMemberships(org.springframework.security.core.Authentication authentication) {
        return ResponseEntity.ok(clubService.getMyMemberships(authentication.getName()));
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'CLUB_ADMIN')")
    @Operation(summary = "Add member to club", description = "Adds a user to a club with a specific role")
    public ResponseEntity<ClubMemberDto> addClubMember(@PathVariable Long id, @Valid @RequestBody AddClubMemberRequest request) {
        if (request.getRole() == com.campussphere.entity.enums.ClubRole.ADMIN
                && org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            throw new org.springframework.security.access.AccessDeniedException("Only an administrator can assign the club ADMIN role");
        }
        ClubMemberDto member = clubService.addClubMember(id, request);
        return new ResponseEntity<>(member, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'CLUB_ADMIN')")
    @Operation(summary = "Remove member from club", description = "Removes a member from a club by user ID")
    public ResponseEntity<Void> removeClubMember(@PathVariable Long id, @PathVariable Long userId) {
        clubService.removeClubMember(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/projects")
    @Operation(summary = "Get club projects", description = "Retrieves all projects belonging to a club")
    public ResponseEntity<List<ProjectDto>> getClubProjects(@PathVariable Long id, org.springframework.security.core.Authentication authentication) {
        boolean admin = authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(clubService.getClubProjects(id).stream().filter(p -> admin || (p.getStatus() != com.campussphere.entity.enums.ProjectStatus.IDEA && p.getStatus() != com.campussphere.entity.enums.ProjectStatus.REJECTED)).toList());
    }
}
