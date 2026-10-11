package com.campussphere.repository;

import com.campussphere.entity.AIEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

@Repository
public interface AIEmbeddingRepository extends JpaRepository<AIEmbedding, Long> {
    Optional<AIEmbedding> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<AIEmbedding> findByEntityType(String entityType);
    List<AIEmbedding> findByEntityTypeIn(Collection<String> entityTypes);
    List<AIEmbedding> findByEntityTypeInAndEntityIdIn(Collection<String> entityTypes, Collection<Long> entityIds);
    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);
}
