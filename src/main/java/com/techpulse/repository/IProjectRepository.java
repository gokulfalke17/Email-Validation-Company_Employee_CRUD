package com.techpulse.repository;

import com.techpulse.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProjectRepository extends JpaRepository<Project, Integer> {
}

