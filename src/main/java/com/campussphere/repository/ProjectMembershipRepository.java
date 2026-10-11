package com.campussphere.repository;

import com.campussphere.entity.ProjectMembership;
import com.campussphere.entity.enums.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, Long> {
    List<ProjectMembership> findByProjectId(Long projectId);
    List<ProjectMembership> findByUserId(Long userId);
    Optional<ProjectMembership> findByProjectIdAndUserId(Long projectId, Long userId);
    Boolean existsByProjectIdAndUserId(Long projectId, Long userId);
    boolean existsByProjectIdAndUserIdAndStatus(Long projectId, Long userId, MembershipStatus status);
    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
