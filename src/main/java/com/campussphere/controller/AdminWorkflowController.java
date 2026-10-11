package com.campussphere.controller;

import com.campussphere.dto.*;
import com.campussphere.entity.enums.EquipmentRequestStatus;
import com.campussphere.entity.enums.MembershipStatus;
import com.campussphere.entity.enums.ProjectStatus;
import com.campussphere.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name="Admin workflows", description="Administrator-only review queues and decisions")
public class AdminWorkflowController {
    private final BookingService bookings;
    private final EquipmentRequestService equipment;
    private final ProjectService projects;
    private final ClubService clubs;
    private final com.campussphere.repository.ClubMembershipRepository memberships;
    public AdminWorkflowController(BookingService bookings, EquipmentRequestService equipment, ProjectService projects,
                                   ClubService clubs, com.campussphere.repository.ClubMembershipRepository memberships) {
        this.bookings=bookings; this.equipment=equipment; this.projects=projects; this.clubs=clubs; this.memberships=memberships;
    }
    @GetMapping("/queues")
    public Map<String, List<?>> queues(Authentication auth) {
        return Map.of(
                "bookings", bookings.getBookings(auth.getName(), true).stream().filter(x -> x.getStatus().name().equals("PENDING")).toList(),
                "equipmentRequests", equipment.getRequests(auth.getName(), true, EquipmentRequestStatus.PENDING),
                "projectProposals", projects.getAllProjects().stream().filter(x -> x.getStatus() == ProjectStatus.IDEA).toList(),
                "projectMemberships", projects.getAllProjects().stream().flatMap(p -> projects.getProjectMembers(p.getId()).stream()).filter(m -> m.getStatus() == com.campussphere.entity.enums.MembershipStatus.PENDING).toList(),
                "clubMemberships", memberships.findByStatus(MembershipStatus.PENDING).stream().map(ClubMemberDto::new).collect(Collectors.toList()));
    }
    @PostMapping("/bookings/{id}/decision")
    public BookingDto decideBooking(@PathVariable Long id, @Valid @RequestBody WorkflowDecisionRequest decision, Authentication auth) {
        if (Boolean.TRUE.equals(decision.getApproved())) return bookings.approveBooking(id, auth.getName());
        requireReason(decision); return bookings.rejectBooking(id, auth.getName(), decision.getReason());
    }
    @PostMapping("/equipment-requests/{id}/decision")
    public EquipmentRequestDto decideEquipment(@PathVariable Long id, @Valid @RequestBody WorkflowDecisionRequest decision, Authentication auth) {
        if (Boolean.TRUE.equals(decision.getApproved())) return equipment.approve(id, auth.getName());
        requireReason(decision); return equipment.reject(id, auth.getName(), decision.getReason());
    }
    @PostMapping("/projects/{id}/decision")
    public ProjectDto decideProject(@PathVariable Long id, @Valid @RequestBody WorkflowDecisionRequest decision, Authentication auth) {
        return projects.reviewProject(id, Boolean.TRUE.equals(decision.getApproved()), decision.getReason(), auth.getName());
    }
    @PostMapping("/project-memberships/{id}/decision")
    public ProjectMemberDto decideProjectMembership(@PathVariable Long id, @Valid @RequestBody WorkflowDecisionRequest decision, Authentication auth) {
        return projects.reviewProjectMembership(id, Boolean.TRUE.equals(decision.getApproved()), auth.getName());
    }
    @PostMapping("/club-memberships/{id}/decision")
    public ClubMemberDto decideMembership(@PathVariable Long id, @Valid @RequestBody WorkflowDecisionRequest decision, Authentication auth) {
        return clubs.reviewMembership(id, Boolean.TRUE.equals(decision.getApproved()), auth.getName());
    }
    private void requireReason(WorkflowDecisionRequest decision) {
        if (decision.getReason() == null || decision.getReason().isBlank()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "A rejection reason is required");
    }
}
