package com.campussphere.repository;

import com.campussphere.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCase(String name);
    List<Skill> findByNameContainingIgnoreCase(String name);
    List<Skill> findByCategoryIgnoreCase(String category);
}
