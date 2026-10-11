package com.campussphere.service;

import com.campussphere.dto.EquipmentRequestCreateRequest;
import com.campussphere.dto.EquipmentRequestDto;
import com.campussphere.entity.enums.EquipmentRequestStatus;
import java.util.List;

public interface EquipmentRequestService {
    List<EquipmentRequestDto> getRequests(String requesterEmail, boolean includeAll, EquipmentRequestStatus status);
    EquipmentRequestDto getRequest(Long id);
    EquipmentRequestDto createRequest(EquipmentRequestCreateRequest request, String requesterEmail);
    EquipmentRequestDto approve(Long id, String approverEmail);
    EquipmentRequestDto reject(Long id, String approverEmail, String reason);
    EquipmentRequestDto checkout(Long id, String operatorEmail);
    EquipmentRequestDto returnEquipment(Long id, String operatorEmail, String condition);
    EquipmentRequestDto cancel(Long id, String requesterEmail, boolean manager);
}
