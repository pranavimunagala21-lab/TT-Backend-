package com.campussphere.service.impl;

import com.campussphere.dto.EquipmentRequestCreateRequest;
import com.campussphere.dto.EquipmentRequestDto;
import com.campussphere.entity.Equipment;
import com.campussphere.entity.EquipmentRequest;
import com.campussphere.entity.Project;
import com.campussphere.entity.User;
import com.campussphere.entity.enums.EquipmentCondition;
import com.campussphere.entity.enums.EquipmentRequestStatus;
import com.campussphere.entity.enums.EquipmentStatus;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ConflictException;
import com.campussphere.exception.ForbiddenException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.EquipmentRepository;
import com.campussphere.repository.EquipmentRequestRepository;
import com.campussphere.repository.ProjectRepository;
import com.campussphere.repository.UserRepository;
import com.campussphere.service.EquipmentRequestService;
import com.campussphere.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentRequestServiceImpl implements EquipmentRequestService {
    private final EquipmentRequestRepository requestRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notifications;

    public EquipmentRequestServiceImpl(EquipmentRequestRepository requestRepository, EquipmentRepository equipmentRepository,
                                       UserRepository userRepository, ProjectRepository projectRepository, NotificationService notifications) {
        this.requestRepository = requestRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.notifications = notifications;
    }

    @Override
    public List<EquipmentRequestDto> getRequests(String requesterEmail, boolean includeAll, EquipmentRequestStatus status) {
        User user = userByEmail(requesterEmail);
        List<EquipmentRequest> requests = status == null
                ? (includeAll ? requestRepository.findAll() : requestRepository.findByRequesterId(user.getId()))
                : requestRepository.findByStatus(status).stream().filter(r -> includeAll || r.getRequester().getId().equals(user.getId())).collect(Collectors.toList());
        return requests.stream().map(EquipmentRequestDto::new).collect(Collectors.toList());
    }

    @Override
    public EquipmentRequestDto getRequest(Long id) { return new EquipmentRequestDto(requestById(id)); }

    @Override
    @Transactional
    public EquipmentRequestDto createRequest(EquipmentRequestCreateRequest request, String requesterEmail) {
        if (request.getExpectedReturnDate() != null && request.getExpectedReturnDate().isBefore(LocalDate.now())) throw new BadRequestException("Expected return date cannot be in the past");
        Equipment equipment = equipmentRepository.findByIdForUpdate(request.getEquipmentId()).orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + request.getEquipmentId()));
        validateCheckoutAvailability(equipment, request.getQuantity());
        EquipmentRequest entity = new EquipmentRequest();
        entity.setEquipment(equipment);
        entity.setRequester(userByEmail(requesterEmail));
        entity.setQuantity(request.getQuantity());
        entity.setExpectedReturnDate(request.getExpectedReturnDate());
        entity.setNotes(request.getNotes());
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
            entity.setProject(project);
        }
        entity.setStatus(EquipmentRequestStatus.PENDING);
        return new EquipmentRequestDto(requestRepository.save(entity));
    }

    @Override
    @Transactional
    public EquipmentRequestDto approve(Long id, String approverEmail) {
        EquipmentRequest request = requestRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Equipment request not found with id: " + id));
        Equipment equipment = equipmentRepository.findByIdForUpdate(request.getEquipment().getId()).orElseThrow();
        if (request.getStatus() != EquipmentRequestStatus.PENDING) throw new ConflictException("Only pending requests can be approved");
        validateCheckoutAvailability(equipment, request.getQuantity());
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() - request.getQuantity());
        equipment.setStatus(equipment.getAvailableQuantity() > 0 ? EquipmentStatus.AVAILABLE : EquipmentStatus.IN_USE);
        request.setStatus(EquipmentRequestStatus.APPROVED);
        request.setApprovedBy(userByEmail(approverEmail));
        request.setApprovedAt(LocalDateTime.now());
        request.setReviewedBy(request.getApprovedBy());
        request.setReviewedAt(request.getApprovedAt());
        equipmentRepository.save(equipment);
        notifications.notifyOnce(request.getRequester(), "Equipment request approved", "Your request for " + request.getQuantity() + " × " + equipment.getName() + " was approved.", "EQUIPMENT", "/api/equipment-requests/" + id, "equipment-request:" + id + ":approved");
        return new EquipmentRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EquipmentRequestDto reject(Long id, String approverEmail, String reason) {
        EquipmentRequest request = requestRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Equipment request not found with id: " + id));
        if (request.getStatus() != EquipmentRequestStatus.PENDING) throw new ConflictException("Only pending requests can be rejected");
        if (reason == null || reason.isBlank()) throw new BadRequestException("Rejection reason is required");
        request.setStatus(EquipmentRequestStatus.REJECTED);
        request.setReviewedBy(userByEmail(approverEmail));
        request.setReviewedAt(LocalDateTime.now());
        request.setNotes(joinNotes(request.getNotes(), "Rejected: " + reason.trim()));
        notifications.notifyOnce(request.getRequester(), "Equipment request rejected", "Your request for " + request.getEquipment().getName() + " was rejected: " + reason.trim(), "EQUIPMENT", "/api/equipment-requests/" + id, "equipment-request:" + id + ":rejected");
        return new EquipmentRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EquipmentRequestDto checkout(Long id, String operatorEmail) {
        EquipmentRequest request = requestRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Equipment request not found with id: " + id));
        User operator = userByEmail(operatorEmail);
        requireOwnerOrResourceManager(request, operator);
        Equipment equipment = equipmentRepository.findByIdForUpdate(request.getEquipment().getId()).orElseThrow();
        if (request.getStatus() != EquipmentRequestStatus.APPROVED) throw new ConflictException("Only approved requests can be checked out");
        if (equipment.getStatus() == EquipmentStatus.MAINTENANCE || equipment.getConditionStatus() == EquipmentCondition.NEEDS_REPAIR) throw new ConflictException("Equipment is under maintenance and cannot be checked out");
        request.setStatus(EquipmentRequestStatus.CHECKED_OUT);
        request.setCheckedOutAt(LocalDateTime.now());
        request.setCheckedOutBy(operator);
        notifications.notifyOnce(request.getRequester(), "Equipment checked out", request.getEquipment().getName() + " has been checked out for your request.", "EQUIPMENT", "/api/equipment-requests/" + id, "equipment-request:" + id + ":checked-out");
        return new EquipmentRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EquipmentRequestDto returnEquipment(Long id, String operatorEmail, String condition) {
        EquipmentRequest request = requestRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Equipment request not found with id: " + id));
        User operator = userByEmail(operatorEmail);
        requireOwnerOrResourceManager(request, operator);
        Equipment equipment = equipmentRepository.findByIdForUpdate(request.getEquipment().getId()).orElseThrow();
        if (request.getStatus() != EquipmentRequestStatus.CHECKED_OUT) throw new ConflictException("Only checked-out equipment can be returned");
        EquipmentCondition returnedCondition = parseCondition(condition);
        request.setStatus(EquipmentRequestStatus.RETURNED);
        request.setActualReturnDate(LocalDate.now());
        request.setReturnedTo(operator);
        request.setConditionOnReturn(returnedCondition.name());
        equipment.setAvailableQuantity(Math.min(equipment.getQuantity(), equipment.getAvailableQuantity() + request.getQuantity()));
        equipment.setConditionStatus(returnedCondition);
        equipment.setStatus(returnedCondition == EquipmentCondition.NEEDS_REPAIR ? EquipmentStatus.MAINTENANCE : (equipment.getAvailableQuantity() > 0 ? EquipmentStatus.AVAILABLE : EquipmentStatus.IN_USE));
        equipmentRepository.save(equipment);
        notifications.notifyOnce(request.getRequester(), "Equipment returned", request.getEquipment().getName() + " was returned and recorded as " + returnedCondition.name() + ".", "EQUIPMENT", "/api/equipment-requests/" + id, "equipment-request:" + id + ":returned");
        return new EquipmentRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EquipmentRequestDto cancel(Long id, String requesterEmail, boolean manager) {
        EquipmentRequest request = requestRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Equipment request not found with id: " + id));
        User requester = userByEmail(requesterEmail);
        if (!manager && !request.getRequester().getId().equals(requester.getId())) throw new ForbiddenException("Only the requester or a resource manager can cancel this request");
        if (request.getStatus() != EquipmentRequestStatus.PENDING && request.getStatus() != EquipmentRequestStatus.APPROVED) throw new ConflictException("Only pending or approved requests can be cancelled");
        if (request.getStatus() == EquipmentRequestStatus.APPROVED) {
            Equipment equipment = equipmentRepository.findByIdForUpdate(request.getEquipment().getId()).orElseThrow();
            equipment.setAvailableQuantity(equipment.getAvailableQuantity() + request.getQuantity());
            equipment.setStatus(equipment.getConditionStatus() == EquipmentCondition.NEEDS_REPAIR
                    ? EquipmentStatus.MAINTENANCE
                    : (equipment.getAvailableQuantity() > 0 ? EquipmentStatus.AVAILABLE : EquipmentStatus.IN_USE));
            equipmentRepository.save(equipment);
        }
        request.setStatus(EquipmentRequestStatus.CANCELLED);
        return new EquipmentRequestDto(requestRepository.save(request));
    }

    private void validateCheckoutAvailability(Equipment equipment, Integer quantity) {
        if (quantity == null || quantity < 1) throw new BadRequestException("Quantity must be at least 1");
        if (equipment.getStatus() == EquipmentStatus.MAINTENANCE || equipment.getConditionStatus() == EquipmentCondition.NEEDS_REPAIR) throw new ConflictException("Equipment is under maintenance and unavailable");
        if (quantity > equipment.getAvailableQuantity()) throw new ConflictException("Requested quantity exceeds the available inventory");
    }
    private void requireOwnerOrResourceManager(EquipmentRequest request, User operator) {
        if (!request.getRequester().getId().equals(operator.getId())
                && operator.getRole() != com.campussphere.entity.enums.Role.ADMIN
                && operator.getRole() != com.campussphere.entity.enums.Role.RESOURCE_MANAGER) {
            throw new ForbiddenException("Only the requester or a resource manager can manage this equipment allocation");
        }
    }
    private EquipmentCondition parseCondition(String condition) {
        try { return EquipmentCondition.valueOf(condition == null ? "" : condition.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new BadRequestException("Return condition must be one of EXCELLENT, GOOD, FAIR, NEEDS_REPAIR"); }
    }
    private EquipmentRequest requestById(Long id) { return requestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipment request not found with id: " + id)); }
    private User userByEmail(String email) { return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email)); }
    private String joinNotes(String current, String extra) {
        if (current == null || current.isBlank()) return extra.substring(0, Math.min(1000, extra.length()));
        String suffix = "\n" + extra;
        if (suffix.length() >= 1000) return suffix.substring(suffix.length() - 1000);
        return current.substring(0, Math.min(current.length(), 1000 - suffix.length())) + suffix;
    }
}
