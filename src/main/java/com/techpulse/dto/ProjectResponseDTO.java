package com.techpulse.dto;

import lombok.Data;
import java.util.Set;

@Data
public class ProjectResponseDTO {
    private Integer projectId;
    private String projectName;
    private String description;
    private String status;
    private String startDate;
    private String endDate;
    private Double budget;
    private String client;
    private String manager;
    private Integer companyId;
    private Set<Integer> employeeIds;
}

