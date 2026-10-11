package com.campussphere.service;

import com.campussphere.dto.HistoricalRecordDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface HistoricalRecordService {
    Page<HistoricalRecordDto> search(String query, String category, Long projectId, Long clubId, LocalDate fromDate, LocalDate toDate, Pageable pageable);
    HistoricalRecordDto get(Long id);
    HistoricalRecordDto create(HistoricalRecordDto request, String actorEmail);
}
