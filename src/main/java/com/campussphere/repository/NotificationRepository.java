package com.campussphere.repository;

import com.campussphere.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);
    Long countByRecipientIdAndIsReadFalse(Long recipientId);
    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);
    boolean existsByEventKey(String eventKey);
}
