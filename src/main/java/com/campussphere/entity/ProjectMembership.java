package com.campussphere.entity;

import com.campussphere.entity.enums.MembershipStatus;
import com.campussphere.entity.enums.ProjectMemberRole;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_memberships")
public class ProjectMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectMemberRole role = ProjectMemberRole.MEMBER;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status = MembershipStatus.APPROVED;

    private LocalDateTime joinedAt = LocalDateTime.now();

    public ProjectMembership() {}

    public ProjectMembership(Project project, User user, ProjectMemberRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public ProjectMembership(Project project, User user, ProjectMemberRole role, MembershipStatus status) {
        this.project = project;
        this.user = user;
        this.role = role;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ProjectMemberRole getRole() { return role; }
    public void setRole(ProjectMemberRole role) { this.role = role; }

    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
