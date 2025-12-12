package com.techpulse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techpulse.entity.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

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

    // Project feature removed — no project relationship here
}
