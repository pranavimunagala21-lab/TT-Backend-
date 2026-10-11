package com.campussphere.dto;

import com.campussphere.entity.Club;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClubDto {
    private Long id;
    @NotBlank(message = "Club name is required")
    @Size(max = 255, message = "Club name must be at most 255 characters")
    private String name;
    private String description;
    private String department;
    private String category;
    private String facultyMentor;
    private String achievements;
    private String activities;
    private String history;
    private String logoUrl;
    private LocalDateTime createdAt;

    public ClubDto() {}

    public ClubDto(Club club) {
        if (club != null) {
            this.id = club.getId();
            this.name = club.getName();
            this.description = club.getDescription();
            this.department = club.getDepartment();
            this.category = club.getCategory();
            this.facultyMentor = club.getFacultyMentor() != null ? club.getFacultyMentor().getName() : null;
            this.achievements = club.getAchievements();
            this.activities = club.getActivities();
            this.history = club.getHistory();
            this.logoUrl = club.getLogoUrl();
            this.createdAt = club.getCreatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFacultyMentor() { return facultyMentor; }
    public void setFacultyMentor(String facultyMentor) { this.facultyMentor = facultyMentor; }

    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }

    public String getActivities() { return activities; }
    public void setActivities(String activities) { this.activities = activities; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
