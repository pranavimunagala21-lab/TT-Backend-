package com.campussphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 2000)
    private String description;

    private String department;
    private String category;

    @Column(length = 2000)
    private String achievements;

    @Column(length = 2000)
    private String activities;

    @Column(length = 2000)
    private String history;

    private String logoUrl;
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_mentor_id")
    private User facultyMentor;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Club() {}

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

    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }

    public String getActivities() { return activities; }
    public void setActivities(String activities) { this.activities = activities; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public User getFacultyMentor() { return facultyMentor; }
    public void setFacultyMentor(User facultyMentor) { this.facultyMentor = facultyMentor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
