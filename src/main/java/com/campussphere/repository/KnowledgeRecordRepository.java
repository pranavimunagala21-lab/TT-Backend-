package com.campussphere.repository;

import com.campussphere.entity.KnowledgeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@Repository
public interface KnowledgeRecordRepository extends JpaRepository<KnowledgeRecord, Long> {

    List<KnowledgeRecord> findByCategory(String category);
    List<KnowledgeRecord> findByDepartment(String department);
    List<KnowledgeRecord> findByYear(Integer year);

    @Query("SELECT k FROM KnowledgeRecord k WHERE " +
           "(:query IS NULL OR LOWER(k.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(k.content) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(k.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(k.tags) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:year IS NULL OR k.year = :year) AND " +
           "(:department IS NULL OR LOWER(k.department) = LOWER(:department)) AND " +
           "(:technology IS NULL OR LOWER(k.technology) LIKE LOWER(CONCAT('%', :technology, '%'))) AND " +
           "(:category IS NULL OR LOWER(k.category) = LOWER(:category)) AND " +
           "(:club IS NULL OR LOWER(k.club) LIKE LOWER(CONCAT('%', :club, '%'))) AND " +
           "(:mentor IS NULL OR LOWER(k.mentor) LIKE LOWER(CONCAT('%', :mentor, '%')))")
    List<KnowledgeRecord> searchKnowledge(@Param("query") String query,
                                         @Param("year") Integer year,
                                         @Param("department") String department,
                                         @Param("technology") String technology,
                                         @Param("category") String category,
                                         @Param("club") String club,
                                         @Param("mentor") String mentor);

    @Query("SELECT k FROM KnowledgeRecord k LEFT JOIN k.project p LEFT JOIN k.clubEntity c WHERE " +
           "(:query IS NULL OR LOWER(k.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(k.content) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(k.description) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(k.tags) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR LOWER(k.category) = LOWER(:category)) AND " +
           "(:projectId IS NULL OR p.id = :projectId) AND " +
           "(:clubId IS NULL OR c.id = :clubId) AND " +
           "(:fromDate IS NULL OR k.recordDate >= :fromDate) AND " +
           "(:toDate IS NULL OR k.recordDate <= :toDate)")
    Page<KnowledgeRecord> searchRecords(@Param("query") String query, @Param("category") String category,
                                        @Param("projectId") Long projectId, @Param("clubId") Long clubId,
                                        @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
                                        Pageable pageable);
}
