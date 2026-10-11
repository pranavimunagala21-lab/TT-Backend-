package com.campussphere.service;

import com.campussphere.dto.DocumentDownload;
import com.campussphere.dto.DocumentDto;
import com.campussphere.dto.DocumentUploadRequest;
import java.util.List;

public interface DocumentService {
    List<DocumentDto> list(Long projectId, Long clubId, Long knowledgeRecordId, String actorEmail);
    DocumentDto get(Long id, String actorEmail);
    DocumentDto upload(DocumentUploadRequest request, String actorEmail);
    DocumentDownload download(Long id, String actorEmail);
    void delete(Long id, String actorEmail);
    List<DocumentDto> getProjectDocuments(Long projectId, String actorEmail);
}
