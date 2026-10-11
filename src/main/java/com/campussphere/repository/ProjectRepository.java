package com.campussphere.repository;

import com.campussphere.entity.Project;
import com.campussphere.entity.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);
    List<Project> findByClubId(Long clubId);
    List<Project> findByStatus(ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE " +
           "(:query IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.problemStatement) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:status IS NULL OR p.status = :status)")
    List<Project> searchProjects(@Param("query") String query,
                                 @Param("category") String category,
                                 @Param("status") ProjectStatus status);
}
