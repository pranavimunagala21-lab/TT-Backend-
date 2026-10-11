package com.campussphere.controller;

import com.campussphere.dto.*;
import com.campussphere.service.CampusIntelligenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name="Campus AI Intelligence",description="Grounded campus search, assistant, and embedding indexing")
@PreAuthorize("isAuthenticated()")
public class AIController {
    private final CampusIntelligenceService intelligence;
    public AIController(CampusIntelligenceService intelligence){this.intelligence=intelligence;}

    @PostMapping("/search")
    @Operation(summary="Search projects, knowledge, historical records, and accessible document metadata")
    public Page<CampusSearchResultDto> search(@Valid @RequestBody AIQueryRequest request,Authentication authentication,
            @PageableDefault(size=10,sort="relevance",direction=Sort.Direction.DESC) Pageable pageable){
        return intelligence.search(request,authentication.getName(),pageable);
    }

    @PostMapping("/assistant")
    @Operation(summary="Answer a campus question using retrieved and cited CampusSphere records")
    public AIQueryResponse assistant(@Valid @RequestBody AIQueryRequest request,Authentication authentication){
        return intelligence.answer(request,authentication.getName());
    }

    @PostMapping("/embeddings/index")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Generate and persist embeddings for a bounded set of campus records")
    public ResponseEntity<AIEmbeddingIndexResponse> index(@RequestParam(required=false) String resourceType,Authentication authentication){
        return ResponseEntity.ok(intelligence.index(resourceType,authentication.getName()));
    }
}
