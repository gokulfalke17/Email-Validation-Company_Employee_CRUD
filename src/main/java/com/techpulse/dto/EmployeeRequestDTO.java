package com.techpulse.dto;

import com.techpulse.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmployeeRequestDTO {

    @NotBlank
    @Size(max = 10)
    private String empName;

    @NotNull
    private Integer salary;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank
    private String dept;

    @NotBlank
    private String city;

    @NotNull
    private Status status;

    @NotNull
    private Integer companyId;
}
