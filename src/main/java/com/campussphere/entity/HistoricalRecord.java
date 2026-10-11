package com.campussphere.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "historical_records")
public class HistoricalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectTitle;

    private String department;
    private Integer year;
    private String category;
    private String technologies; // comma-separated or text

    @Column(length = 3000)
    private String summary;

    @Column(length = 2000)
    private String outcomes;

    @Column(length = 2000)
    private String lessonsLearned;

    private String mentorName;
    private String clubName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recorded_by_id")
    private User recordedBy;

    private LocalDate eventDate;
    private LocalDateTime createdAt = LocalDateTime.now();

    public HistoricalRecord() {}

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

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }
    public User getRecordedBy() { return recordedBy; }
    public void setRecordedBy(User recordedBy) { this.recordedBy = recordedBy; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
