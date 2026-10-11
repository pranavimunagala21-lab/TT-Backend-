package com.campussphere.dto;

import com.campussphere.entity.DocumentEntity;
import java.time.LocalDateTime;

public class DocumentDto {
    private Long id;
    private String title;
    private String description;
    private String documentType;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String storageLocation;
    private String tags;
    private Long projectId;
    private String projectTitle;
    private UserDto uploader;
    private LocalDateTime uploadedAt;
    private String originalFileName;
    private Long clubId;
    private String clubName;
    private Long knowledgeRecordId;
    private String knowledgeRecordTitle;

    public DocumentDto() {}

    public DocumentDto(DocumentEntity doc) {
        if (doc != null) {
            this.id = doc.getId();
            this.title = doc.getTitle();
            this.description = doc.getDescription();
            this.documentType = doc.getDocumentType();
            this.fileUrl = doc.getId() == null ? null : "/api/documents/" + doc.getId() + "/download";
            this.fileType = doc.getFileType();
            this.fileSize = doc.getFileSize();
            // The private on-disk key is never exposed through API metadata.
            this.storageLocation = null;
            this.originalFileName = doc.getOriginalFileName();
            this.tags = doc.getTags();
            if (doc.getProject() != null) {
                this.projectId = doc.getProject().getId();
                this.projectTitle = doc.getProject().getTitle();
            }
            if (doc.getClub() != null) {
                this.clubId = doc.getClub().getId();
                this.clubName = doc.getClub().getName();
            }
            if (doc.getKnowledgeRecord() != null) {
                this.knowledgeRecordId = doc.getKnowledgeRecord().getId();
                this.knowledgeRecordTitle = doc.getKnowledgeRecord().getTitle();
            }
            if (doc.getUploader() != null) {
                this.uploader = new UserDto(doc.getUploader());
            }
            this.uploadedAt = doc.getUploadedAt();
        }
    }

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

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public UserDto getUploader() { return uploader; }
    public void setUploader(UserDto uploader) { this.uploader = uploader; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }
    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }
    public Long getKnowledgeRecordId() { return knowledgeRecordId; }
    public void setKnowledgeRecordId(Long knowledgeRecordId) { this.knowledgeRecordId = knowledgeRecordId; }
    public String getKnowledgeRecordTitle() { return knowledgeRecordTitle; }
    public void setKnowledgeRecordTitle(String knowledgeRecordTitle) { this.knowledgeRecordTitle = knowledgeRecordTitle; }
}
