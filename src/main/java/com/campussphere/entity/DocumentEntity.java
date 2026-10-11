package com.campussphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String documentType; // TECHNICAL_SPEC, REPORT, MANUAL, DESIGN, OTHER

    @Column(nullable = false)
    private String fileUrl;

    private String fileType;
    private Long fileSize;

    private String storageLocation; // e.g., s3://bucket/key or local/storage/path
    private String originalFileName;
    private String tags;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "knowledge_record_id")
    private KnowledgeRecord knowledgeRecord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    public DocumentEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }

    public KnowledgeRecord getKnowledgeRecord() { return knowledgeRecord; }
    public void setKnowledgeRecord(KnowledgeRecord knowledgeRecord) { this.knowledgeRecord = knowledgeRecord; }

    public User getUploader() { return uploader; }
    public void setUploader(User uploader) { this.uploader = uploader; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
