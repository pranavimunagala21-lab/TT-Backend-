package com.campussphere.service.impl;

import com.campussphere.dto.AddClubMemberRequest;
import com.campussphere.dto.ClubDto;
import com.campussphere.dto.ClubMemberDto;
import com.campussphere.dto.ProjectDto;
import com.campussphere.entity.Club;
import com.campussphere.entity.ClubMembership;
import com.campussphere.entity.User;
import com.campussphere.entity.enums.MembershipStatus;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ConflictException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.ClubMembershipRepository;
import com.campussphere.repository.ClubRepository;
import com.campussphere.repository.ProjectRepository;
import com.campussphere.repository.UserRepository;
import com.campussphere.service.ClubService;
import com.campussphere.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final ClubMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notifications;

    public ClubServiceImpl(ClubRepository clubRepository,
                           ClubMembershipRepository membershipRepository,
                           UserRepository userRepository,
                           ProjectRepository projectRepository, NotificationService notifications) {
        this.clubRepository = clubRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.notifications = notifications;
    }

    @Override
    public List<ClubDto> getAllClubs() {
        return clubRepository.findAll().stream()
                .map(ClubDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public ClubDto getClubById(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        return new ClubDto(club);
    }

    private User resolveFacultyMentor(String mentorIdentifier) {
        if (mentorIdentifier == null || mentorIdentifier.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(mentorIdentifier)
                .orElseGet(() -> userRepository.findByName(mentorIdentifier).orElse(null));
    }

    @Override
    @Transactional
    public ClubDto createClub(ClubDto clubDto) {
        if (clubRepository.existsByNameIgnoreCase(clubDto.getName())) {
            throw new ConflictException("Club with name '" + clubDto.getName() + "' already exists");
        }
        Club club = new Club();
        club.setName(clubDto.getName());
        club.setDescription(clubDto.getDescription());
        club.setDepartment(clubDto.getDepartment());
        club.setCategory(clubDto.getCategory());
        club.setFacultyMentor(resolveFacultyMentor(clubDto.getFacultyMentor()));
        club.setAchievements(clubDto.getAchievements());
        club.setActivities(clubDto.getActivities());
        club.setHistory(clubDto.getHistory());
        club.setLogoUrl(clubDto.getLogoUrl());

        return new ClubDto(clubRepository.save(club));
    }

    @Override
    @Transactional
    public ClubDto updateClub(Long id, ClubDto clubDto) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));

        if (!club.getName().equalsIgnoreCase(clubDto.getName()) && clubRepository.existsByNameIgnoreCase(clubDto.getName())) {
            throw new ConflictException("Club with name '" + clubDto.getName() + "' already exists");
        }

        if (clubDto.getName() != null) club.setName(clubDto.getName());
        if (clubDto.getDescription() != null) club.setDescription(clubDto.getDescription());
        if (clubDto.getDepartment() != null) club.setDepartment(clubDto.getDepartment());
        if (clubDto.getCategory() != null) club.setCategory(clubDto.getCategory());
        if (clubDto.getFacultyMentor() != null) club.setFacultyMentor(resolveFacultyMentor(clubDto.getFacultyMentor()));
        if (clubDto.getAchievements() != null) club.setAchievements(clubDto.getAchievements());
        if (clubDto.getActivities() != null) club.setActivities(clubDto.getActivities());
        if (clubDto.getHistory() != null) club.setHistory(clubDto.getHistory());
        if (clubDto.getLogoUrl() != null) club.setLogoUrl(clubDto.getLogoUrl());

        return new ClubDto(clubRepository.save(club));
    }

    @Override
    @Transactional
    public void deleteClub(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + id));
        clubRepository.delete(club);
    }

    @Override
    public List<ClubMemberDto> getClubMembers(Long clubId) {
        if (!clubRepository.existsById(clubId)) {
            throw new ResourceNotFoundException("Club not found with id: " + clubId);
        }
        return membershipRepository.findByClubId(clubId).stream()
                .map(ClubMemberDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClubMemberDto addClubMember(Long clubId, AddClubMemberRequest request) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + clubId));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        if (membershipRepository.existsByClubIdAndUserId(clubId, request.getUserId())) {
            throw new BadRequestException("User is already a member of this club");
        }

        ClubMembership membership = new ClubMembership();
        membership.setClub(club);
        membership.setUser(user);
        membership.setRole(request.getRole());
        membership.setStatus(MembershipStatus.APPROVED);

        return new ClubMemberDto(membershipRepository.save(membership));
    }

    @Override
    @Transactional
    public ClubMemberDto requestMembership(Long clubId, String userEmail) {
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (membershipRepository.existsByClubIdAndUserId(clubId, user.getId())) throw new BadRequestException("You already have a membership or pending request");
        ClubMembership membership = new ClubMembership(user, club, com.campussphere.entity.enums.ClubRole.MEMBER, MembershipStatus.PENDING);
        return new ClubMemberDto(membershipRepository.save(membership));
    }

    @Override
    public List<ClubMemberDto> getMyMemberships(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        return membershipRepository.findByUserId(user.getId()).stream().map(ClubMemberDto::new).toList();
    }

    @Override
    @Transactional
    public ClubMemberDto reviewMembership(Long membershipId, boolean approved, String reviewerEmail) {
        ClubMembership membership = membershipRepository.findById(membershipId).orElseThrow(() -> new ResourceNotFoundException("Membership request not found"));
        if (membership.getStatus() != MembershipStatus.PENDING) throw new ConflictException("Only pending membership requests can be reviewed");
        membership.setStatus(approved ? MembershipStatus.APPROVED : MembershipStatus.REJECTED);
        membership.setReviewedBy(userRepository.findByEmail(reviewerEmail).orElseThrow(() -> new ResourceNotFoundException("Reviewer not found")));
        membership.setReviewedAt(java.time.LocalDateTime.now());
        ClubMemberDto result = new ClubMemberDto(membershipRepository.save(membership));
        notifications.notifyOnce(membership.getUser(), approved ? "Club membership approved" : "Club membership declined",
                "Your request to join " + membership.getClub().getName() + (approved ? " was approved." : " was declined."),
                "CLUB", "/api/clubs/" + membership.getClub().getId(), "club-membership:" + membershipId + ":" + (approved ? "approved" : "rejected"));
        return result;
    }

    @Override
    @Transactional
    public void removeClubMember(Long clubId, Long userId) {
        ClubMembership membership = membershipRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found for clubId " + clubId + " and userId " + userId));
        membershipRepository.delete(membership);
    }

    @Override
    public List<ProjectDto> getClubProjects(Long clubId) {
        if (!clubRepository.existsById(clubId)) {
            throw new ResourceNotFoundException("Club not found with id: " + clubId);
        }
        return projectRepository.findByClubId(clubId).stream()
                .map(ProjectDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClubDto> searchClubs(String query, String department, String category) {
        return clubRepository.searchClubs(query, department, category).stream()
                .map(ClubDto::new)
                .collect(Collectors.toList());
    }
}
