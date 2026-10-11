package com.campussphere.repository;

import com.campussphere.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Boolean existsByNameIgnoreCase(String name);

    @Query("SELECT c FROM Club c WHERE " +
           "(:query IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:department IS NULL OR c.department = :department) AND " +
           "(:category IS NULL OR c.category = :category)")
    List<Club> searchClubs(@Param("query") String query,
                           @Param("department") String department,
                           @Param("category") String category);
}
