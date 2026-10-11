package com.campussphere.dto;

import com.campussphere.entity.Project;
import com.campussphere.entity.enums.ProjectStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class ProjectDto {
    private Long id;
    private String title;
    private String description;
    private String problemStatement;
    private String category;
    private Set<String> technologies;
    private Set<String> requiredSkills;
    private Set<String> requiredEquipment;
    private String requiredVenue;
    private ProjectStatus status;
    private UserDto owner;
    private Long clubId;
    private String clubName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String achievements;
    private String lessonsLearned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDto reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewReason;

    public ProjectDto() {}

    public ProjectDto(Project project) {
        if (project != null) {
            this.id = project.getId();
            this.title = project.getTitle();
            this.description = project.getDescription();
            this.problemStatement = project.getProblemStatement();
            this.category = project.getCategory();
            this.technologies = project.getTechnologies();
            this.requiredSkills = project.getRequiredSkills();
            this.requiredEquipment = project.getRequiredEquipment();
            this.requiredVenue = project.getRequiredVenue();
            this.status = project.getStatus();
            if (project.getOwner() != null) {
                this.owner = new UserDto(project.getOwner());
            }
            if (project.getClub() != null) {
                this.clubId = project.getClub().getId();
                this.clubName = project.getClub().getName();
            }
            this.startDate = project.getStartDate();
            this.endDate = project.getEndDate();
            this.achievements = project.getAchievements();
            this.lessonsLearned = project.getLessonsLearned();
            this.createdAt = project.getCreatedAt();
            this.updatedAt = project.getUpdatedAt();
            if (project.getReviewedBy() != null) this.reviewedBy = new UserDto(project.getReviewedBy());
            this.reviewedAt = project.getReviewedAt();
            this.reviewReason = project.getReviewReason();
        }
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

    public UserDto getOwner() { return owner; }
    public void setOwner(UserDto owner) { this.owner = owner; }

    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

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
    public UserDto getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(UserDto reviewedBy) { this.reviewedBy = reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public String getReviewReason() { return reviewReason; }
    public void setReviewReason(String reviewReason) { this.reviewReason = reviewReason; }
}
