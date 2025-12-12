package com.techpulse.dto;

import com.techpulse.dto.CompanyResponseDTO;
import java.util.List;

public class ReportDTO {
    private CompanyResponseDTO company;
    private List<?> employees;
    private List<?> projects;

    public ReportDTO(CompanyResponseDTO company, List<?> employees, List<?> projects) {
        this.company = company;
        this.employees = employees;
        this.projects = projects;
    }

    public CompanyResponseDTO getCompany() { return company; }
    public List<?> getEmployees() { return employees; }
    public List<?> getProjects() { return projects; }
}

