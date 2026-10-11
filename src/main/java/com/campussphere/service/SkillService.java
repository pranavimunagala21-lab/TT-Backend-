package com.campussphere.service;

import com.campussphere.dto.SkillDto;

import java.util.List;

public interface SkillService {
    List<SkillDto> getAllSkills();
    SkillDto getSkillById(Long id);
    SkillDto createSkill(SkillDto skillDto);
    SkillDto updateSkill(Long id, SkillDto skillDto);
    void deleteSkill(Long id);
}
