package com.techpulse.service.impl;

import com.techpulse.dto.ReportDTO;
import com.techpulse.mapper.CompanyMapper;
import com.techpulse.repository.ICompanyRepository;
import com.techpulse.service.IReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements IReportService {

    private final ICompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public ReportServiceImpl(ICompanyRepository companyRepository,
                             CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Override
    public ReportDTO generateReportForCompany(Integer companyId) {
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        var companyDto = companyMapper.toDTO(company);
        var employees = company.getEmployees() == null ? java.util.List.of() : company.getEmployees();
        var projects = company.getProjects() == null ? java.util.List.of() : company.getProjects();

        return new ReportDTO(companyDto, employees, projects);
    }
}
