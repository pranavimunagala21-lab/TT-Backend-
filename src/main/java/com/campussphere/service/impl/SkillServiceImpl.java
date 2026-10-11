package com.campussphere.service.impl;

import com.campussphere.dto.SkillDto;
import com.campussphere.entity.Skill;
import com.campussphere.exception.ConflictException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.SkillRepository;
import com.campussphere.service.SkillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public List<SkillDto> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(SkillDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public SkillDto getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        return new SkillDto(skill);
    }

    @Override
    @Transactional
    public SkillDto createSkill(SkillDto skillDto) {
        if (skillRepository.existsByNameIgnoreCase(skillDto.getName())) {
            throw new ConflictException("Skill with name '" + skillDto.getName() + "' already exists");
        }
        Skill skill = new Skill();
        skill.setName(skillDto.getName());
        skill.setCategory(skillDto.getCategory());
        skill.setDescription(skillDto.getDescription());
        return new SkillDto(skillRepository.save(skill));
    }

    @Override
    @Transactional
    public SkillDto updateSkill(Long id, SkillDto skillDto) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

        if (!skill.getName().equalsIgnoreCase(skillDto.getName()) && skillRepository.existsByNameIgnoreCase(skillDto.getName())) {
            throw new ConflictException("Skill with name '" + skillDto.getName() + "' already exists");
        }

        if (skillDto.getName() != null) skill.setName(skillDto.getName());
        if (skillDto.getCategory() != null) skill.setCategory(skillDto.getCategory());
        if (skillDto.getDescription() != null) skill.setDescription(skillDto.getDescription());

        return new SkillDto(skillRepository.save(skill));
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        skillRepository.delete(skill);
    }
}
