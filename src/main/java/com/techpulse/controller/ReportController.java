package com.techpulse.controller;

import com.techpulse.dto.ReportDTO;
import com.techpulse.service.IReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final IReportService reportService;

    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    // Only OWNER can generate the report (enforced by Spring Security)
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('OWNER')")
    public ReportDTO getCompanyReport(@PathVariable Integer companyId) {
        return reportService.generateReportForCompany(companyId);
    }
}
