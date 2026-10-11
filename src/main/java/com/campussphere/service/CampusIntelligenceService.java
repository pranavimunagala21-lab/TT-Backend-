package com.campussphere.service;

import com.campussphere.dto.AIEmbeddingIndexResponse;
import com.campussphere.dto.AIQueryResponse;
import com.campussphere.dto.AIQueryRequest;
import com.campussphere.dto.CampusSearchResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampusIntelligenceService {
    Page<CampusSearchResultDto> search(AIQueryRequest request,String actorEmail,Pageable pageable);
    AIQueryResponse answer(AIQueryRequest request,String actorEmail);
    AIEmbeddingIndexResponse index(String resourceType,String actorEmail);
}
