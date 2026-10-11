package com.campussphere.service;

import com.campussphere.dto.KnowledgeRecordDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface KnowledgeService {
    Page<KnowledgeRecordDto> search(String query, String category, Long projectId, Long clubId, LocalDate fromDate, LocalDate toDate, Pageable pageable);
    KnowledgeRecordDto get(Long id);
    KnowledgeRecordDto create(KnowledgeRecordDto request, String authorEmail);
    KnowledgeRecordDto update(Long id, KnowledgeRecordDto request, String actorEmail);
    void delete(Long id, String actorEmail);
}
