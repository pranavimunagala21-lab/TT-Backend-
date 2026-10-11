package com.campussphere.dto;

import com.campussphere.entity.enums.ProjectMemberRole;
import jakarta.validation.constraints.NotNull;

public class AddProjectMemberRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    private ProjectMemberRole role = ProjectMemberRole.MEMBER;

    public AddProjectMemberRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public ProjectMemberRole getRole() { return role; }
    public void setRole(ProjectMemberRole role) { this.role = role; }
}
