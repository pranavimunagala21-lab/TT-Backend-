package com.campussphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_embeddings")
public class AIEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType; // PROJECT, KNOWLEDGE, USER, VENUE, EQUIPMENT

    @Column(nullable = false)
    private Long entityId;

    @Column(length = 4000)
    private String textContent;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String embeddingJson;

    private LocalDateTime createdAt = LocalDateTime.now();

    public AIEmbedding() {}

    public AIEmbedding(String entityType, Long entityId, String textContent, String embeddingJson) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.textContent = textContent;
        this.embeddingJson = embeddingJson;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public String getEmbeddingJson() { return embeddingJson; }
    public void setEmbeddingJson(String embeddingJson) { this.embeddingJson = embeddingJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
