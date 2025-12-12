package com.techpulse.entity;

import com.techpulse.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(length = 100, nullable = false)
    private String username;

    @Column(length = 100, nullable = false)
    private String password; // in real apps store encrypted

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    // link to company if the user belongs to a company (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    // additional fields
    private String phone;
    private String city;
    private String address;
    private Boolean active = true;
}
