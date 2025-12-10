package com.techpulse.dto;

import com.techpulse.entity.enums.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jdk.jshell.Snippet;
import lombok.Data;

@Data
public class EmployeeRequestDTO {

    @NotBlank
    @Size(max = 10)
    private String empName;

    @NotNull
    private Integer salary;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String dept;

    @NotBlank
    private String city;

    @NotNull
    private Status status;
}
