package com.campussphere.repository;

import com.campussphere.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByBuilding(String building);
    boolean existsByNameIgnoreCaseAndBuildingIgnoreCase(String name, String building);
    boolean existsByNameIgnoreCaseAndBuildingIgnoreCaseAndIdNot(String name, String building, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Venue v where v.id = :id")
    java.util.Optional<Venue> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT v FROM Venue v WHERE " +
           "(:query IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(v.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:building IS NULL OR v.building = :building) AND " +
           "(:minCapacity IS NULL OR v.capacity >= :minCapacity) AND " +
           "(:status IS NULL OR v.status = :status)")
    List<Venue> searchVenues(@Param("query") String query,
                             @Param("building") String building,
                             @Param("minCapacity") Integer minCapacity,
                             @Param("status") String status);
}
