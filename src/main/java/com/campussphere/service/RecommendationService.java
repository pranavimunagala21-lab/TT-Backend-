package com.campussphere.service;

import com.campussphere.dto.*;
import com.campussphere.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RecommendationService {
    Page<RankedPersonMatchDto> peopleForProject(Long projectId, Role role, String query, Pageable pageable);
    List<RecommendationDto> venuesForProject(Long projectId, LocalDateTime start, LocalDateTime end, Integer attendees, Set<String> facilities);
    List<RecommendationDto> equipmentForProject(Long projectId);
    Page<DiscoveryRecommendationDto> relatedProjects(Long projectId, Pageable pageable);
    Page<DiscoveryRecommendationDto> relatedKnowledge(Long projectId, Pageable pageable);
}
