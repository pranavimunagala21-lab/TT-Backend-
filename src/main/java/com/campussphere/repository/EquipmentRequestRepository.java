package com.campussphere.repository;

import com.campussphere.entity.EquipmentRequest;
import com.campussphere.entity.enums.EquipmentRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EquipmentRequestRepository extends JpaRepository<EquipmentRequest, Long> {
    List<EquipmentRequest> findByRequesterId(Long requesterId);
    List<EquipmentRequest> findByEquipmentId(Long equipmentId);
    List<EquipmentRequest> findByProjectId(Long projectId);
    List<EquipmentRequest> findByStatus(EquipmentRequestStatus status);

    boolean existsByEquipmentId(Long equipmentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from EquipmentRequest r where r.id = :id")
    java.util.Optional<EquipmentRequest> findByIdForUpdate(@Param("id") Long id);
}
