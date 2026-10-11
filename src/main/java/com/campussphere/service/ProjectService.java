package com.campussphere.service;

import com.campussphere.dto.AddProjectMemberRequest;
import com.campussphere.dto.DocumentDto;
import com.campussphere.dto.ProjectDto;
import com.campussphere.dto.ProjectMemberDto;
import com.campussphere.entity.enums.ProjectStatus;

import java.util.List;

public interface ProjectService {
    List<ProjectDto> getAllProjects();
    ProjectDto getProjectById(Long id);
    ProjectDto createProject(ProjectDto projectDto, String currentUserEmail);
    ProjectDto updateProject(Long id, ProjectDto projectDto, String currentUserEmail);
    ProjectDto reviewProject(Long id, boolean approved, String reason, String reviewerEmail);
    void deleteProject(Long id, String currentUserEmail);
    List<ProjectDto> searchProjects(String query, String category, ProjectStatus status);
    List<ProjectMemberDto> getProjectMembers(Long projectId);
    ProjectMemberDto addProjectMember(Long projectId, AddProjectMemberRequest request);
    ProjectMemberDto requestProjectMembership(Long projectId, String requesterEmail);
    ProjectMemberDto reviewProjectMembership(Long membershipId, boolean approved, String reviewerEmail);
    void removeProjectMember(Long projectId, Long userId);
    List<DocumentDto> getProjectDocuments(Long projectId, String actorEmail);
    List<ProjectDto> getSimilarProjects(Long projectId);
}
