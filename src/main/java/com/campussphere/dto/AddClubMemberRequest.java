package com.campussphere.dto;

import com.campussphere.entity.enums.ClubRole;
import jakarta.validation.constraints.NotNull;

public class AddClubMemberRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    private ClubRole role = ClubRole.MEMBER;

    public AddClubMemberRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public ClubRole getRole() { return role; }
    public void setRole(ClubRole role) { this.role = role; }
}
