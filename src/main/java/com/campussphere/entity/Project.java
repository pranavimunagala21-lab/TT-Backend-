package com.campussphere.entity;

import com.campussphere.entity.enums.ProjectStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 3000)
    private String description;

    @Column(length = 2000)
    private String problemStatement;

    private String category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_technologies", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "technology")
    private Set<String> technologies = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_required_skills", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "skill")
    private Set<String> requiredSkills = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_required_equipment", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "equipment_item")
    private Set<String> requiredEquipment = new HashSet<>();

    private String requiredVenue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.IDEA;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id")
    private Club club;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(length = 2000)
    private String achievements;

    @Column(length = 2000)
    private String lessonsLearned;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;
    private LocalDateTime reviewedAt;
    @Column(length = 1000)
    private String reviewReason;

    public Project() {}

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProblemStatement() { return problemStatement; }
    public void setProblemStatement(String problemStatement) { this.problemStatement = problemStatement; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Set<String> getTechnologies() { return technologies; }
    public void setTechnologies(Set<String> technologies) { this.technologies = technologies; }

    public Set<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(Set<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public Set<String> getRequiredEquipment() { return requiredEquipment; }
    public void setRequiredEquipment(Set<String> requiredEquipment) { this.requiredEquipment = requiredEquipment; }

    public String getRequiredVenue() { return requiredVenue; }
    public void setRequiredVenue(String requiredVenue) { this.requiredVenue = requiredVenue; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }

    public String getLessonsLearned() { return lessonsLearned; }
    public void setLessonsLearned(String lessonsLearned) { this.lessonsLearned = lessonsLearned; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public String getReviewReason() { return reviewReason; }
    public void setReviewReason(String reviewReason) { this.reviewReason = reviewReason; }
}
