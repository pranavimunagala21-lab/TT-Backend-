package com.campussphere.dto;

import com.campussphere.entity.HistoricalRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HistoricalRecordDto {
    private Long id;
    @NotBlank(message = "Project or activity title is required")
    @Size(max = 255)
    private String projectTitle;
    private String department;
    private Integer year;
    private String category;
    private String technologies;
    private String summary;
    private String outcomes;
    private String lessonsLearned;
    private String mentorName;
    private String clubName;
    private Long projectId;
    private Long clubId;
    private UserDto recordedBy;
    private LocalDate eventDate;
    private LocalDateTime createdAt;

    public HistoricalRecordDto() {}
    public HistoricalRecordDto(HistoricalRecord record) {
        id = record.getId(); projectTitle = record.getProjectTitle(); department = record.getDepartment(); year = record.getYear();
        category = record.getCategory(); technologies = record.getTechnologies(); summary = record.getSummary(); outcomes = record.getOutcomes();
        lessonsLearned = record.getLessonsLearned(); mentorName = record.getMentorName(); clubName = record.getClubName();
        if (record.getProject() != null) projectId = record.getProject().getId();
        if (record.getClub() != null) clubId = record.getClub().getId();
        if (record.getRecordedBy() != null) recordedBy = new UserDto(record.getRecordedBy());
        eventDate = record.getEventDate(); createdAt = record.getCreatedAt();
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getOutcomes() { return outcomes; }
    public void setOutcomes(String outcomes) { this.outcomes = outcomes; }
    public String getLessonsLearned() { return lessonsLearned; }
    public void setLessonsLearned(String lessonsLearned) { this.lessonsLearned = lessonsLearned; }
    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }
    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }
    public UserDto getRecordedBy() { return recordedBy; }
    public void setRecordedBy(UserDto recordedBy) { this.recordedBy = recordedBy; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
