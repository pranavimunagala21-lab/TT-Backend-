package com.campussphere.service.impl;

import com.campussphere.dto.KnowledgeRecordDto;
import com.campussphere.entity.Club;
import com.campussphere.entity.KnowledgeRecord;
import com.campussphere.entity.Project;
import com.campussphere.entity.User;
import com.campussphere.entity.enums.ClubRole;
import com.campussphere.entity.enums.MembershipStatus;
import com.campussphere.entity.enums.Role;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ConflictException;
import com.campussphere.exception.ForbiddenException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.ClubMembershipRepository;
import com.campussphere.repository.ClubRepository;
import com.campussphere.repository.DocumentRepository;
import com.campussphere.repository.KnowledgeRecordRepository;
import com.campussphere.repository.ProjectMembershipRepository;
import com.campussphere.repository.ProjectRepository;
import com.campussphere.repository.UserRepository;
import com.campussphere.service.KnowledgeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {
    private final KnowledgeRecordRepository knowledgeRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ClubRepository clubRepository;
    private final ProjectMembershipRepository projectMemberships;
    private final ClubMembershipRepository clubMemberships;
    private final DocumentRepository documentRepository;

    public KnowledgeServiceImpl(KnowledgeRecordRepository knowledgeRepository, UserRepository userRepository,
                                ProjectRepository projectRepository, ClubRepository clubRepository,
                                ProjectMembershipRepository projectMemberships, ClubMembershipRepository clubMemberships,
                                DocumentRepository documentRepository) {
        this.knowledgeRepository = knowledgeRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.clubRepository = clubRepository;
        this.projectMemberships = projectMemberships;
        this.clubMemberships = clubMemberships;
        this.documentRepository = documentRepository;
    }

    @Override
    public Page<KnowledgeRecordDto> search(String query, String category, Long projectId, Long clubId,
                                            LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        validateDates(fromDate, toDate);
        return knowledgeRepository.searchRecords(blankToNull(query), blankToNull(category), projectId, clubId, fromDate, toDate, pageable)
                .map(KnowledgeRecordDto::new);
    }

    @Override
    @Transactional
    public KnowledgeRecordDto get(Long id) {
        KnowledgeRecord record = findKnowledge(id);
        record.setViews(record.getViews() == null ? 1 : record.getViews() + 1);
        return new KnowledgeRecordDto(record);
    }

    @Override
    @Transactional
    public KnowledgeRecordDto create(KnowledgeRecordDto request, String authorEmail) {
        validateRequest(request);
        User author = userByEmail(authorEmail);
        KnowledgeRecord record = new KnowledgeRecord();
        record.setAuthor(author);
        apply(request, record, author);
        record.setViews(0);
        return new KnowledgeRecordDto(knowledgeRepository.save(record));
    }

    @Override
    @Transactional
    public KnowledgeRecordDto update(Long id, KnowledgeRecordDto request, String actorEmail) {
        validateRequest(request);
        KnowledgeRecord record = findKnowledge(id);
        User actor = userByEmail(actorEmail);
        requireCanManage(record, actor);
        apply(request, record, actor);
        return new KnowledgeRecordDto(knowledgeRepository.save(record));
    }

    @Override
    @Transactional
    public void delete(Long id, String actorEmail) {
        KnowledgeRecord record = findKnowledge(id);
        requireCanManage(record, userByEmail(actorEmail));
        if (documentRepository.existsByKnowledgeRecordId(id)) throw new ConflictException("Knowledge record has associated documents and cannot be deleted");
        knowledgeRepository.delete(record);
    }

    private void apply(KnowledgeRecordDto dto, KnowledgeRecord record, User actor) {
        if (dto.getTitle() != null) record.setTitle(dto.getTitle().trim());
        record.setDescription(dto.getDescription());
        record.setContent(dto.getContent());
        record.setCategory(dto.getCategory());
        record.setType(dto.getType());
        record.setReferenceId(dto.getReferenceId());
        record.setDepartment(dto.getDepartment());
        record.setTechnology(dto.getTechnology());
        record.setRecordDate(dto.getRecordDate() == null && record.getRecordDate() == null ? LocalDate.now() : dto.getRecordDate() == null ? record.getRecordDate() : dto.getRecordDate());
        record.setYear(dto.getYear() == null ? record.getRecordDate().getYear() : dto.getYear());
        record.setMentor(dto.getMentor());
        record.setTags(dto.getTags());

        if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + dto.getProjectId()));
            requireCanContribute(project, actor);
            record.setProject(project);
        }
        if (dto.getClubId() != null) {
            Club club = clubRepository.findById(dto.getClubId()).orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + dto.getClubId()));
            requireClubContributor(club, actor);
            record.setClubEntity(club);
            record.setClub(club.getName());
        } else if (dto.getClub() != null) {
            record.setClub(dto.getClub());
        }
    }

    private void requireCanManage(KnowledgeRecord record, User actor) {
        if (isPrivileged(actor)) return;
        if (record.getAuthor() != null && record.getAuthor().getId().equals(actor.getId())) return;
        if (record.getProject() != null && record.getProject().getOwner().getId().equals(actor.getId())) return;
        if (record.getClubEntity() != null) {
            var membership = clubMemberships.findByClubIdAndUserId(record.getClubEntity().getId(), actor.getId());
            if (membership.isPresent() && membership.get().getStatus() == MembershipStatus.APPROVED
                    && (membership.get().getRole() == ClubRole.ADMIN || membership.get().getRole() == ClubRole.OFFICER)) return;
        }
        throw new ForbiddenException("Only the author, linked project owner, club officer, or an administrator can modify this record");
    }

    private void requireCanContribute(Project project, User actor) {
        if (isPrivileged(actor) || project.getOwner().getId().equals(actor.getId())) return;
        projectMemberships.findByProjectIdAndUserId(project.getId(), actor.getId()).filter(m -> m.getStatus() == MembershipStatus.APPROVED)
                .orElseThrow(() -> new ForbiddenException("You must be a project member to link knowledge to this project"));
    }

    private void requireClubContributor(Club club, User actor) {
        if (isPrivileged(actor)) return;
        clubMemberships.findByClubIdAndUserId(club.getId(), actor.getId()).filter(m -> m.getStatus() == MembershipStatus.APPROVED)
                .orElseThrow(() -> new ForbiddenException("You must be an approved club member to link knowledge to this club"));
    }

    private boolean isPrivileged(User user) { return user.getRole() == Role.ADMIN || user.getRole() == Role.FACULTY; }
    private void validateRequest(KnowledgeRecordDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) throw new BadRequestException("Knowledge title is required");
        if (dto.getContent() != null && dto.getContent().length() > 10000) throw new BadRequestException("Knowledge content exceeds 10000 characters");
        if (dto.getDescription() != null && dto.getDescription().length() > 2000) throw new BadRequestException("Description exceeds 2000 characters");
    }
    private void validateDates(LocalDate from, LocalDate to) { if (from != null && to != null && to.isBefore(from)) throw new BadRequestException("toDate must be on or after fromDate"); }
    private KnowledgeRecord findKnowledge(Long id) { return knowledgeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Knowledge record not found with id: " + id)); }
    private User userByEmail(String email) { return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email)); }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
