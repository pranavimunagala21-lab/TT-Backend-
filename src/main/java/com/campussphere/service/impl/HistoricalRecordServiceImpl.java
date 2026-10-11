package com.campussphere.service.impl;

import com.campussphere.dto.HistoricalRecordDto;
import com.campussphere.entity.Club;
import com.campussphere.entity.HistoricalRecord;
import com.campussphere.entity.Project;
import com.campussphere.entity.User;
import com.campussphere.entity.enums.Role;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ForbiddenException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.ClubRepository;
import com.campussphere.repository.HistoricalRecordRepository;
import com.campussphere.repository.ProjectRepository;
import com.campussphere.repository.UserRepository;
import com.campussphere.service.HistoricalRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class HistoricalRecordServiceImpl implements HistoricalRecordService {
    private final HistoricalRecordRepository historicalRepository;
    private final ProjectRepository projectRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;

    public HistoricalRecordServiceImpl(HistoricalRecordRepository historicalRepository, ProjectRepository projectRepository,
                                       ClubRepository clubRepository, UserRepository userRepository) {
        this.historicalRepository = historicalRepository;
        this.projectRepository = projectRepository;
        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<HistoricalRecordDto> search(String query, String category, Long projectId, Long clubId,
                                             LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        validateDates(fromDate, toDate);
        return historicalRepository.searchRecords(blankToNull(query), blankToNull(category), projectId, clubId, fromDate, toDate, pageable)
                .map(HistoricalRecordDto::new);
    }

    @Override
    public HistoricalRecordDto get(Long id) {
        return new HistoricalRecordDto(historicalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Historical record not found with id: " + id)));
    }

    @Override
    @Transactional
    public HistoricalRecordDto create(HistoricalRecordDto request, String actorEmail) {
        if (request.getProjectTitle() == null || request.getProjectTitle().isBlank()) throw new BadRequestException("Activity or project title is required");
        User actor = userRepository.findByEmail(actorEmail).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + actorEmail));
        if (actor.getRole() != Role.ADMIN && actor.getRole() != Role.FACULTY && actor.getRole() != Role.CLUB_ADMIN) {
            throw new ForbiddenException("Only administrators, faculty, or club administrators can add historical records");
        }
        HistoricalRecord record = new HistoricalRecord();
        record.setProjectTitle(request.getProjectTitle().trim());
        record.setDepartment(request.getDepartment());
        record.setYear(request.getYear());
        record.setCategory(request.getCategory());
        record.setTechnologies(request.getTechnologies());
        record.setSummary(request.getSummary());
        record.setOutcomes(request.getOutcomes());
        record.setLessonsLearned(request.getLessonsLearned());
        record.setMentorName(request.getMentorName());
        record.setEventDate(request.getEventDate() == null ? LocalDate.now() : request.getEventDate());
        if (record.getYear() == null) record.setYear(record.getEventDate().getYear());
        record.setRecordedBy(actor);

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId()).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
            record.setProject(project);
            record.setProjectTitle(project.getTitle());
            if (record.getCategory() == null) record.setCategory(project.getCategory());
            if (record.getTechnologies() == null) record.setTechnologies(String.join(", ", project.getTechnologies()));
            if (record.getSummary() == null) record.setSummary(project.getDescription());
            if (record.getOutcomes() == null) record.setOutcomes(project.getAchievements());
            if (record.getLessonsLearned() == null) record.setLessonsLearned(project.getLessonsLearned());
        }
        if (request.getClubId() != null) {
            Club club = clubRepository.findById(request.getClubId()).orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + request.getClubId()));
            record.setClub(club);
            record.setClubName(club.getName());
        } else {
            record.setClubName(request.getClubName());
        }
        return new HistoricalRecordDto(historicalRepository.save(record));
    }

    private void validateDates(LocalDate from, LocalDate to) { if (from != null && to != null && to.isBefore(from)) throw new BadRequestException("toDate must be on or after fromDate"); }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
