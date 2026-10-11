package com.campussphere.entity;

import com.campussphere.entity.enums.ClubRole;
import com.campussphere.entity.enums.MembershipStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "club_memberships")
public class ClubMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    private ClubRole role = ClubRole.MEMBER;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status = MembershipStatus.PENDING;

    private LocalDateTime joinedAt = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;
    private LocalDateTime reviewedAt;

    public ClubMembership() {}

    public ClubMembership(User user, Club club, ClubRole role, MembershipStatus status) {
        this.user = user;
        this.club = club;
        this.role = role;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }

    public ClubRole getRole() { return role; }
    public void setRole(ClubRole role) { this.role = role; }

    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = status; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
