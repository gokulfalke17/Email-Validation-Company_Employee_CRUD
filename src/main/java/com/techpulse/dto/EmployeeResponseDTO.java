package com.techpulse.dto;

import com.techpulse.entity.enums.Status;
import lombok.Data;

@Data
public class EmployeeResponseDTO {

    private Integer empId;
    private String empName;
    private Integer salary;
    private String email;
    private String dept;
    private String city;

    private Status status;
}
