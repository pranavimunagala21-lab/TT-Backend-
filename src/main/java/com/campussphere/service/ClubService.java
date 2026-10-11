package com.campussphere.service;

import com.campussphere.dto.AddClubMemberRequest;
import com.campussphere.dto.ClubDto;
import com.campussphere.dto.ClubMemberDto;
import com.campussphere.dto.ProjectDto;

import java.util.List;

public interface ClubService {
    List<ClubDto> getAllClubs();
    ClubDto getClubById(Long id);
    ClubDto createClub(ClubDto clubDto);
    ClubDto updateClub(Long id, ClubDto clubDto);
    void deleteClub(Long id);
    List<ClubMemberDto> getClubMembers(Long clubId);
    ClubMemberDto addClubMember(Long clubId, AddClubMemberRequest request);
    ClubMemberDto requestMembership(Long clubId, String userEmail);
    List<ClubMemberDto> getMyMemberships(String userEmail);
    ClubMemberDto reviewMembership(Long membershipId, boolean approved, String reviewerEmail);
    void removeClubMember(Long clubId, Long userId);
    List<ProjectDto> getClubProjects(Long clubId);
    List<ClubDto> searchClubs(String query, String department, String category);
}
