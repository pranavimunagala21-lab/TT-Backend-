package com.campussphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class DocumentUploadRequest {
    @NotNull(message = "A file is required")
    private MultipartFile file;
    @NotBlank(message = "Document title is required")
    @Size(max = 255)
    private String title;
    @Size(max = 2000)
    private String description;
    private String documentType;
    @Size(max = 1000)
    private String tags;
    private Long projectId;
    private Long clubId;
    private Long knowledgeRecordId;
    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getClubId() { return clubId; }
    public void setClubId(Long clubId) { this.clubId = clubId; }
    public Long getKnowledgeRecordId() { return knowledgeRecordId; }
    public void setKnowledgeRecordId(Long knowledgeRecordId) { this.knowledgeRecordId = knowledgeRecordId; }
}
