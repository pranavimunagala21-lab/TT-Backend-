package com.campussphere.dto.user;

import jakarta.validation.constraints.NotBlank;

public class UserSkillDto {

    @NotBlank(message = "Skill name is required")
    private String skillName;

    public UserSkillDto() {}

    public UserSkillDto(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
}
