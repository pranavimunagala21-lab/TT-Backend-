package com.campussphere.dto;

import com.campussphere.entity.User;
import com.campussphere.entity.enums.Role;
import java.util.Set;
import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private String department;
    private String year;
    private String bio;
    private String avatarUrl;
    private Set<String> skills;
    private Set<String> interests;
    private LocalDateTime createdAt;
    private boolean active;

    public UserDto() {}

    public UserDto(User user) {
        if (user != null) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.name = user.getName();
            this.role = user.getRole();
            this.department = user.getDepartment();
            this.year = user.getYear();
            this.bio = user.getBio();
            this.avatarUrl = user.getAvatarUrl();
            this.skills = user.getSkills();
            this.interests = user.getInterests();
            this.createdAt = user.getCreatedAt();
            this.active = user.isActive();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Set<String> getSkills() { return skills; }
    public void setSkills(Set<String> skills) { this.skills = skills; }

    public Set<String> getInterests() { return interests; }
    public void setInterests(Set<String> interests) { this.interests = interests; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
