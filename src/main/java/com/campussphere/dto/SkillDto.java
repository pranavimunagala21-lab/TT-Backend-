package com.campussphere.dto;

import com.campussphere.entity.Skill;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SkillDto {
    private Long id;
    @NotBlank(message = "Skill name is required")
    @Size(max = 255, message = "Skill name must be at most 255 characters")
    private String name;
    private String category;
    private String description;

    public SkillDto() {}

    public SkillDto(Skill skill) {
        if (skill != null) {
            this.id = skill.getId();
            this.name = skill.getName();
            this.category = skill.getCategory();
            this.description = skill.getDescription();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
