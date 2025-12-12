package com.techpulse.service.impl;

import com.techpulse.dto.ProjectRequestDTO;
import com.techpulse.dto.ProjectResponseDTO;
import com.techpulse.entity.Company;
import com.techpulse.entity.Employee;
import com.techpulse.entity.Project;
import com.techpulse.mapper.ProjectMapper;
import com.techpulse.repository.ICompanyRepository;
import com.techpulse.repository.IEmployeeRepository;
import com.techpulse.repository.IProjectRepository;
import com.techpulse.service.IProjectService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository projectRepository;
    private final ICompanyRepository companyRepository;
    private final IEmployeeRepository employeeRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(IProjectRepository projectRepository,
                              ICompanyRepository companyRepository,
                              IEmployeeRepository employeeRepository,
                              ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectResponseDTO createProject(ProjectRequestDTO request) {
        Project project = projectMapper.toEntity(request);
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found"));
            project.setCompany(company);
        }
        if (request.getEmployeeIds() != null && !request.getEmployeeIds().isEmpty()) {
            Set<Employee> employees = request.getEmployeeIds().stream()
                    .map(id -> employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id)))
                    .collect(Collectors.toSet());
            project.setEmployees(employees);
        }

        Project saved = projectRepository.save(project);
        ProjectResponseDTO dto = projectMapper.toDTO(saved);
        if (saved.getEmployees() != null) {
            dto.setEmployeeIds(saved.getEmployees().stream().map(Employee::getEmpId).collect(Collectors.toSet()));
        }
        return dto;
    }

    @Override
    public ProjectResponseDTO updateProject(Integer projectId, ProjectRequestDTO request) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        // simple patch-like update
        if (request.getProjectName() != null) project.setProjectName(request.getProjectName());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getStatus() != null) project.setStatus(request.getStatus());
        if (request.getStartDate() != null) project.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) project.setEndDate(request.getEndDate());
        if (request.getBudget() != null) project.setBudget(request.getBudget());
        if (request.getClient() != null) project.setClient(request.getClient());
        if (request.getManager() != null) project.setManager(request.getManager());

        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found"));
            project.setCompany(company);
        }

        if (request.getEmployeeIds() != null) {
            Set<Employee> employees = request.getEmployeeIds().stream()
                    .map(id -> employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id)))
                    .collect(Collectors.toSet());
            project.setEmployees(employees);
        }

        Project saved = projectRepository.save(project);
        ProjectResponseDTO dto = projectMapper.toDTO(saved);
        if (saved.getEmployees() != null) {
            dto.setEmployeeIds(saved.getEmployees().stream().map(Employee::getEmpId).collect(Collectors.toSet()));
        }
        return dto;
    }

    @Override
    public void deleteProject(Integer projectId) {
        if (!projectRepository.existsById(projectId)) throw new IllegalArgumentException("Project not found");
        projectRepository.deleteById(projectId);
    }

    @Override
    public ProjectResponseDTO getProject(Integer projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        ProjectResponseDTO dto = projectMapper.toDTO(project);
        if (project.getEmployees() != null) dto.setEmployeeIds(project.getEmployees().stream().map(Employee::getEmpId).collect(Collectors.toSet()));
        return dto;
    }

    @Override
    public List<ProjectResponseDTO> listProjects() {
        return projectRepository.findAll().stream().map(p -> {
            ProjectResponseDTO dto = projectMapper.toDTO(p);
            if (p.getEmployees() != null) dto.setEmployeeIds(p.getEmployees().stream().map(Employee::getEmpId).collect(Collectors.toSet()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectResponseDTO> searchProjects(String q) {
        if (q == null || q.isBlank()) return listProjects();
        String lower = q.toLowerCase();
        return projectRepository.findAll().stream()
                .filter(p -> (p.getProjectName() != null && p.getProjectName().toLowerCase().contains(lower))
                        || (p.getDescription() != null && p.getDescription().toLowerCase().contains(lower))
                        || (p.getStatus() != null && p.getStatus().toLowerCase().contains(lower))
                        || (p.getClient() != null && p.getClient().toLowerCase().contains(lower))
                        || (p.getManager() != null && p.getManager().toLowerCase().contains(lower)))
                .map(p -> {
                    ProjectResponseDTO dto = projectMapper.toDTO(p);
                    if (p.getEmployees() != null) dto.setEmployeeIds(p.getEmployees().stream().map(Employee::getEmpId).collect(Collectors.toSet()));
                    return dto;
                }).collect(Collectors.toList());
    }
}

