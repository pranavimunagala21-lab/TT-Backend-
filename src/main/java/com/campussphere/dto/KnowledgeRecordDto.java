package com.campussphere.dto;

import com.campussphere.entity.KnowledgeRecord;
import java.time.LocalDateTime;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class KnowledgeRecordDto {
    private Long id;
    @NotBlank(message = "Knowledge title is required")
    @Size(max = 255, message = "Knowledge title must be at most 255 characters")
    private String title;
    private String description;
    private String content;
    private String category;
    private String type;
    private Long referenceId;
    private String department;
    private String technology;
    private Integer year;
    private LocalDate recordDate;
    private String club;
    private String mentor;
    private String tags;
    private UserDto author;
    private Long projectId;
    private String projectTitle;
    private Long clubId;
    private String clubName;
    private Integer views;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KnowledgeRecordDto() {}

    public KnowledgeRecordDto(KnowledgeRecord record) {
        if (record != null) {
            this.id = record.getId();
            this.title = record.getTitle();
            this.description = record.getDescription();
            this.content = record.getContent();
            this.category = record.getCategory();
            this.type = record.getType();
            this.referenceId = record.getReferenceId();
            this.department = record.getDepartment();
            this.technology = record.getTechnology();
            this.year = record.getYear();
            this.recordDate = record.getRecordDate();
            this.club = record.getClub();
            this.mentor = record.getMentor();
            this.tags = record.getTags();
            if (record.getAuthor() != null) {
                this.author = new UserDto(record.getAuthor());
            }
            if (record.getProject() != null) {
                this.projectId = record.getProject().getId();
                this.projectTitle = record.getProject().getTitle();
            }
            if (record.getClubEntity() != null) {
                this.clubId = record.getClubEntity().getId();
                this.clubName = record.getClubEntity().getName();
            }
            this.views = record.getViews();
            this.createdAt = record.getCreatedAt();
            this.updatedAt = record.getUpdatedAt();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public String getClub() { return club; }
    public void setClub(String club) { this.club = club; }

    public String getMentor() { return mentor; }
    public void setMentor(String mentor) { this.mentor = mentor; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public UserDto getAuthor() { return author; }
    public void setAuthor(UserDto author) { this.author = author; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }
    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
