package com.campussphere.dto;

import com.campussphere.entity.ClubMembership;
import com.campussphere.entity.enums.ClubRole;
import com.campussphere.entity.enums.MembershipStatus;
import java.time.LocalDateTime;

public class ClubMemberDto {
    private Long id;
    private Long clubId;
    private String clubName;
    private UserDto user;
    private ClubRole role;
    private MembershipStatus status;
    private LocalDateTime joinedAt;
    private UserDto reviewedBy;
    private LocalDateTime reviewedAt;

    public ClubMemberDto() {}

    public ClubMemberDto(ClubMembership membership) {
        if (membership != null) {
            this.id = membership.getId();
            if (membership.getClub() != null) {
                this.clubId = membership.getClub().getId();
                this.clubName = membership.getClub().getName();
            }
            if (membership.getUser() != null) {
                this.user = new UserDto(membership.getUser());
            }
            this.role = membership.getRole();
            this.status = membership.getStatus();
            this.joinedAt = membership.getJoinedAt();
            if (membership.getReviewedBy() != null) this.reviewedBy = new UserDto(membership.getReviewedBy());
            this.reviewedAt = membership.getReviewedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public ClubRole getRole() { return role; }
    public void setRole(ClubRole role) { this.role = role; }

    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    public UserDto getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(UserDto reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
