package com.campussphere.dto;

import com.campussphere.entity.ProjectMembership;
import com.campussphere.entity.enums.MembershipStatus;
import com.campussphere.entity.enums.ProjectMemberRole;
import java.time.LocalDateTime;

public class ProjectMemberDto {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private UserDto user;
    private ProjectMemberRole role;
    private MembershipStatus status;
    private LocalDateTime joinedAt;

    public ProjectMemberDto() {}

    public ProjectMemberDto(ProjectMembership membership) {
        if (membership != null) {
            this.id = membership.getId();
            if (membership.getProject() != null) {
                this.projectId = membership.getProject().getId();
                this.projectTitle = membership.getProject().getTitle();
            }
            if (membership.getUser() != null) {
                this.user = new UserDto(membership.getUser());
            }
            this.role = membership.getRole();
            this.status = membership.getStatus();
            this.joinedAt = membership.getJoinedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public ProjectMemberRole getRole() { return role; }
    public void setRole(ProjectMemberRole role) { this.role = role; }

    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
