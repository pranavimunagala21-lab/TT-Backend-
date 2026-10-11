package com.campussphere.repository;

import com.campussphere.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findByProjectId(Long projectId);
    List<DocumentEntity> findByUploaderId(Long uploaderId);
    List<DocumentEntity> findByDocumentType(String documentType);
    List<DocumentEntity> findByClubId(Long clubId);
    List<DocumentEntity> findByKnowledgeRecordId(Long knowledgeRecordId);
    boolean existsByProjectId(Long projectId);
    boolean existsByKnowledgeRecordId(Long knowledgeRecordId);
    Optional<DocumentEntity> findByStorageLocation(String storageLocation);
}
