package com.campussphere.service.impl;

import com.campussphere.dto.AddProjectMemberRequest;
import com.campussphere.dto.DocumentDto;
import com.campussphere.dto.ProjectDto;
import com.campussphere.dto.ProjectMemberDto;
import com.campussphere.entity.Club;
import com.campussphere.entity.Project;
import com.campussphere.entity.ProjectMembership;
import com.campussphere.entity.User;
import com.campussphere.entity.enums.MembershipStatus;
import com.campussphere.entity.enums.ProjectMemberRole;
import com.campussphere.entity.enums.ProjectStatus;
import com.campussphere.entity.enums.Role;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ForbiddenException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.exception.ForbiddenException;
import com.campussphere.repository.ClubRepository;
import com.campussphere.repository.DocumentRepository;
import com.campussphere.repository.ProjectMembershipRepository;
import com.campussphere.repository.ProjectRepository;
import com.campussphere.repository.UserRepository;
import com.campussphere.service.ProjectService;
import com.campussphere.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final DocumentRepository documentRepository;
    private final NotificationService notifications;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ProjectMembershipRepository membershipRepository,
                              UserRepository userRepository,
                              ClubRepository clubRepository,
                              DocumentRepository documentRepository,
                              NotificationService notifications) {
        this.projectRepository = projectRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.clubRepository = clubRepository;
        this.documentRepository = documentRepository;
        this.notifications = notifications;
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return new ProjectDto(project);
    }

    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto dto, String currentUserEmail) {
        User owner = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));

        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setProblemStatement(dto.getProblemStatement());
        project.setCategory(dto.getCategory());
        if (dto.getTechnologies() != null) project.setTechnologies(dto.getTechnologies());
        if (dto.getRequiredSkills() != null) project.setRequiredSkills(dto.getRequiredSkills());
        if (dto.getRequiredEquipment() != null) project.setRequiredEquipment(dto.getRequiredEquipment());
        project.setRequiredVenue(dto.getRequiredVenue());
        // Every student proposal enters the review queue; client-provided statuses are ignored.
        project.setStatus(ProjectStatus.IDEA);
        project.setOwner(owner);
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setAchievements(dto.getAchievements());
        project.setLessonsLearned(dto.getLessonsLearned());

        if (dto.getClubId() != null) {
            Club club = clubRepository.findById(dto.getClubId()).orElse(null);
            project.setClub(club);
        }

        Project savedProject = projectRepository.save(project);

        // Add owner as Lead Project Member automatically
        ProjectMembership membership = new ProjectMembership();
        membership.setProject(savedProject);
        membership.setUser(owner);
        membership.setRole(ProjectMemberRole.LEAD);
        membership.setStatus(MembershipStatus.APPROVED);
        membershipRepository.save(membership);

        return new ProjectDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectDto updateProject(Long id, ProjectDto dto, String currentUserEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));

        // Check ownership or ADMIN role
        if (!project.getOwner().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only the project owner or an administrator can update this project");
        }

        if (dto.getTitle() != null) project.setTitle(dto.getTitle());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getProblemStatement() != null) project.setProblemStatement(dto.getProblemStatement());
        if (dto.getCategory() != null) project.setCategory(dto.getCategory());
        if (dto.getTechnologies() != null) project.setTechnologies(dto.getTechnologies());
        if (dto.getRequiredSkills() != null) project.setRequiredSkills(dto.getRequiredSkills());
        if (dto.getRequiredEquipment() != null) project.setRequiredEquipment(dto.getRequiredEquipment());
        if (dto.getRequiredVenue() != null) project.setRequiredVenue(dto.getRequiredVenue());
        // Status transitions are handled only by the review operation, never by general profile edits.
        if (dto.getStartDate() != null) project.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) project.setEndDate(dto.getEndDate());
        if (dto.getAchievements() != null) project.setAchievements(dto.getAchievements());
        if (dto.getLessonsLearned() != null) project.setLessonsLearned(dto.getLessonsLearned());

        return new ProjectDto(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectDto reviewProject(Long id, boolean approved, String reason, String reviewerEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        if (project.getStatus() != ProjectStatus.IDEA) throw new BadRequestException("Only project proposals awaiting review can be decided");
        if (!approved && (reason == null || reason.isBlank())) throw new BadRequestException("A rejection reason is required");
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        project.setStatus(approved ? ProjectStatus.PLANNING : ProjectStatus.REJECTED);
        project.setReviewedBy(reviewer);
        project.setReviewedAt(java.time.LocalDateTime.now());
        project.setReviewReason(reason == null || reason.isBlank() ? null : reason.trim());
        Project saved = projectRepository.save(project);
        notifications.notifyOnce(project.getOwner(), approved ? "Project proposal approved" : "Project proposal declined",
                approved ? "Your project proposal “" + project.getTitle() + "” was approved for planning."
                        : "Your project proposal “" + project.getTitle() + "” was declined: " + reason.trim(),
                "PROJECT", "/api/projects/" + id, "project-review:" + id + ":" + saved.getStatus().name());
        return new ProjectDto(saved);
    }

    @Override
    @Transactional
    public void deleteProject(Long id, String currentUserEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + currentUserEmail));

        if (!project.getOwner().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only the project owner or an administrator can delete this project");
        }

        projectRepository.delete(project);
    }

    @Override
    public List<ProjectDto> searchProjects(String query, String category, ProjectStatus status) {
        return projectRepository.searchProjects(query, category, status).stream()
                .map(ProjectDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectMemberDto> getProjectMembers(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        return membershipRepository.findByProjectId(projectId).stream()
                .map(ProjectMemberDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectMemberDto addProjectMember(Long projectId, AddProjectMemberRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (membershipRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new BadRequestException("User is already a member of this project");
        }

        ProjectMembership membership = new ProjectMembership();
        membership.setProject(project);
        membership.setUser(user);
        membership.setRole(request.getRole());
        membership.setStatus(MembershipStatus.APPROVED);

        ProjectMembership saved = membershipRepository.save(membership);
        notifications.notifyOnce(user, "Added to project", "You were added to project " + project.getTitle() + ".", "PROJECT", "/api/projects/" + projectId, "project-membership:" + saved.getId() + ":added");
        return new ProjectMemberDto(saved);
    }

    @Override
    @Transactional
    public ProjectMemberDto requestProjectMembership(Long projectId, String requesterEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        if (project.getStatus() != ProjectStatus.PLANNING && project.getStatus() != ProjectStatus.ACTIVE) {
            throw new BadRequestException("Only approved planning or active projects accept team requests");
        }
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + requesterEmail));
        if (requester.getRole() != Role.STUDENT) throw new ForbiddenException("Only student accounts can request project membership");
        var existing = membershipRepository.findByProjectIdAndUserId(projectId, requester.getId());
        if (existing.isPresent()) {
            ProjectMembership membership = existing.get();
            if (membership.getStatus() != MembershipStatus.REJECTED) throw new BadRequestException("A project membership request or membership already exists");
            membership.setStatus(MembershipStatus.PENDING);
            membership.setRole(ProjectMemberRole.MEMBER);
            return new ProjectMemberDto(membershipRepository.save(membership));
        }
        ProjectMembership membership = new ProjectMembership(project, requester, ProjectMemberRole.MEMBER, MembershipStatus.PENDING);
        ProjectMembership saved = membershipRepository.save(membership);
        notifications.notifyOnce(project.getOwner(), "Project team request", requester.getName() + " requested to join " + project.getTitle() + ".", "PROJECT", "/api/projects/" + projectId, "project-join-request:" + saved.getId());
        return new ProjectMemberDto(saved);
    }

    @Override
    @Transactional
    public ProjectMemberDto reviewProjectMembership(Long membershipId, boolean approved, String reviewerEmail) {
        ProjectMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new ResourceNotFoundException("Project membership not found with id: " + membershipId));
        if (membership.getStatus() != MembershipStatus.PENDING) throw new BadRequestException("Only pending project team requests can be reviewed");
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        membership.setStatus(approved ? MembershipStatus.APPROVED : MembershipStatus.REJECTED);
        ProjectMembership saved = membershipRepository.save(membership);
        notifications.notifyOnce(membership.getUser(), approved ? "Project team request approved" : "Project team request declined",
                approved ? "Your request to join " + membership.getProject().getTitle() + " was approved."
                        : "Your request to join " + membership.getProject().getTitle() + " was declined.",
                "PROJECT", "/api/projects/" + membership.getProject().getId(), "project-join-decision:" + membershipId + ":" + saved.getStatus().name());
        return new ProjectMemberDto(saved);
    }

    @Override
    @Transactional
    public void removeProjectMember(Long projectId, Long userId) {
        ProjectMembership membership = membershipRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found for projectId " + projectId + " and userId " + userId));
        notifications.notifyOnce(membership.getUser(), "Removed from project", "You were removed from project " + membership.getProject().getTitle() + ".", "PROJECT", "/api/projects/" + projectId, "project-membership:" + membership.getId() + ":removed");
        membershipRepository.delete(membership);
    }

    @Override
    public List<DocumentDto> getProjectDocuments(Long projectId, String actorEmail) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        User actor = userRepository.findByEmail(actorEmail).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + actorEmail));
        boolean privileged = actor.getRole() == Role.ADMIN || actor.getRole() == Role.FACULTY || actor.getRole() == Role.RESOURCE_MANAGER;
        boolean member = membershipRepository.findByProjectIdAndUserId(projectId, actor.getId()).filter(m -> m.getStatus() == MembershipStatus.APPROVED).isPresent();
        if (!privileged && !project.getOwner().getId().equals(actor.getId()) && !member) throw new ForbiddenException("Not authorized to view project documents");
        return documentRepository.findByProjectId(projectId).stream()
                .map(DocumentDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getSimilarProjects(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        return projectRepository.findAll().stream()
                .filter(p -> !p.getId().equals(projectId))
                .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(project.getCategory()))
                .map(ProjectDto::new)
                .collect(Collectors.toList());
    }
}
