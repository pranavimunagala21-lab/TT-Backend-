package com.campussphere.controller;

import com.campussphere.dto.*;
import com.campussphere.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController @RequestMapping("/api/documents") @Tag(name="Campus Documents")
public class DocumentController {
    private final DocumentService service;
    public DocumentController(DocumentService service){this.service=service;}
    @GetMapping @Operation(summary="List accessible documents with optional relationship filters") public List<DocumentDto> list(@RequestParam(required=false) Long projectId,@RequestParam(required=false) Long clubId,@RequestParam(required=false) Long knowledgeRecordId,Authentication auth){return service.list(projectId,clubId,knowledgeRecordId,auth.getName());}
    @GetMapping("/{id}") @Operation(summary="Get authorized document metadata") public DocumentDto get(@PathVariable Long id,Authentication auth){return service.get(id,auth.getName());}
    @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE) @Operation(summary="Upload a document and associate it with a campus record") public ResponseEntity<DocumentDto> upload(@Valid @ModelAttribute DocumentUploadRequest request,Authentication auth){return ResponseEntity.status(HttpStatus.CREATED).body(service.upload(request,auth.getName()));}
    @GetMapping("/{id}/download") @Operation(summary="Download an authorized document") public ResponseEntity<Resource> download(@PathVariable Long id,Authentication auth){DocumentDownload d=service.download(id,auth.getName()); MediaType type; try{type=MediaType.parseMediaType(d.contentType());}catch(Exception ex){type=MediaType.APPLICATION_OCTET_STREAM;} ContentDisposition disposition=ContentDisposition.attachment().filename(d.fileName(),StandardCharsets.UTF_8).build(); return ResponseEntity.ok().contentType(type).header(HttpHeaders.CONTENT_DISPOSITION,disposition.toString()).body(d.resource());}
    @DeleteMapping("/{id}") @Operation(summary="Delete a document when authorized") public ResponseEntity<Void> delete(@PathVariable Long id,Authentication auth){service.delete(id,auth.getName());return ResponseEntity.noContent().build();}
}
