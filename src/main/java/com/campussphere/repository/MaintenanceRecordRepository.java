package com.campussphere.repository;

import com.campussphere.entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    List<MaintenanceRecord> findByEquipmentId(Long equipmentId);
    List<MaintenanceRecord> findByStatus(String status);
    boolean existsByEquipmentIdAndStatus(Long equipmentId, String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MaintenanceRecord m where m.id = :id")
    java.util.Optional<MaintenanceRecord> findByIdForUpdate(@Param("id") Long id);
}
