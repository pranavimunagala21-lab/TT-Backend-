package com.campussphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "knowledge_records")
public class KnowledgeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 10000)
    private String content;

    private String category; // REPORT, TECH_DOC, EVENT_RECORD, HISTORICAL, ACHIEVEMENT, LESSON_LEARNED

    private String type;

    private Long referenceId;

    private String department;

    private String technology;

    private Integer year;

    private LocalDate recordDate;

    private String club;

    private String mentor;

    private String tags;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id")
    private Club clubEntity;

    private Integer views = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public KnowledgeRecord() {}

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

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Club getClubEntity() { return clubEntity; }
    public void setClubEntity(Club clubEntity) { this.clubEntity = clubEntity; }

    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
