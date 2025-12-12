package com.techpulse.service;

import com.techpulse.dto.ProjectRequestDTO;
import com.techpulse.dto.ProjectResponseDTO;

import java.util.List;

public interface IProjectService {
    ProjectResponseDTO createProject(ProjectRequestDTO request);
    ProjectResponseDTO updateProject(Integer projectId, ProjectRequestDTO request);
    void deleteProject(Integer projectId);
    ProjectResponseDTO getProject(Integer projectId);
    List<ProjectResponseDTO> listProjects();
    List<ProjectResponseDTO> searchProjects(String q); // search across multiple fields
}

