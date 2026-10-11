package com.campussphere.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentStorageService {
    String store(MultipartFile file);
    Resource load(String storageKey);
    void delete(String storageKey);
}
