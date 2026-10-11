package com.campussphere.repository;

import com.campussphere.entity.HistoricalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface HistoricalRecordRepository extends JpaRepository<HistoricalRecord, Long> {
    List<HistoricalRecord> findByDepartment(String department);
    List<HistoricalRecord> findByYear(Integer year);
    List<HistoricalRecord> findByCategory(String category);

    @Query("SELECT h FROM HistoricalRecord h LEFT JOIN h.project p LEFT JOIN h.club c WHERE " +
           "(:query IS NULL OR LOWER(h.projectTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(h.summary) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(h.outcomes) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(h.lessonsLearned) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(h.technologies) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR LOWER(h.category) = LOWER(:category)) AND " +
           "(:projectId IS NULL OR p.id = :projectId) AND " +
           "(:clubId IS NULL OR c.id = :clubId) AND " +
           "(:fromDate IS NULL OR h.eventDate >= :fromDate) AND " +
           "(:toDate IS NULL OR h.eventDate <= :toDate)")
    Page<HistoricalRecord> searchRecords(@Param("query") String query, @Param("category") String category,
                                         @Param("projectId") Long projectId, @Param("clubId") Long clubId,
                                         @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
                                         Pageable pageable);
}
