package com.techpulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techpulse.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer empId;

    @Column(length = 10)
    private String empName;

    @Column(length = 10)
    private Integer salary;

    @Column(unique = true)
    private String email;

    private String dept;

    private String city;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    // an employee can work on many projects
    @ManyToMany
    @JoinTable(name = "employee_project",
            joinColumns = @JoinColumn(name = "emp_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id"))
    @JsonIgnore
    private Set<Project> projects;
}
