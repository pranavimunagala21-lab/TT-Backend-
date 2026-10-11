package com.campussphere.repository;

import com.campussphere.entity.Equipment;
import com.campussphere.entity.enums.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findBySerialNumber(String serialNumber);
    List<Equipment> findByCategory(String category);
    List<Equipment> findByStatus(EquipmentStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Equipment e where e.id = :id")
    Optional<Equipment> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT e FROM Equipment e WHERE " +
           "(:query IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:status IS NULL OR e.status = :status)")
    List<Equipment> searchEquipment(@Param("query") String query,
                                    @Param("category") String category,
                                    @Param("status") EquipmentStatus status);
}
