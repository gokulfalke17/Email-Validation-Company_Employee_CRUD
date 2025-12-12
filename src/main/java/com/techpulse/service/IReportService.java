package com.techpulse.service;

import com.techpulse.dto.ReportDTO;

public interface IReportService {
    ReportDTO generateReportForCompany(Integer companyId);
}

