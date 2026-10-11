package com.campussphere.repository;

import com.campussphere.entity.ResourceRequest;
import com.campussphere.entity.enums.ResourceRequestStatus;
import com.campussphere.entity.enums.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long> {
    List<ResourceRequest> findByRequesterId(Long requesterId);
    List<ResourceRequest> findByProjectId(Long projectId);
    List<ResourceRequest> findByStatus(ResourceRequestStatus status);
    List<ResourceRequest> findByResourceType(ResourceType resourceType);
}
