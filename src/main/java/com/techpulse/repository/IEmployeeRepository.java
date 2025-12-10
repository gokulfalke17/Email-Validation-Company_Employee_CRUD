package com.techpulse.repository;

import com.techpulse.dto.EmployeeResponseDTO;
import com.techpulse.entity.Employee;
import com.techpulse.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IEmployeeRepository extends JpaRepository<Employee, Integer> {

    /*@Query(value = """
       SELECT * FROM employee
       WHERE (:empName IS NULL OR empName LIKE :empName)
       AND (:dept IS NULL OR dept LIKE :dept)
       AND (:email IS NULL OR email LIKE :email)
       AND (:city IS NULL OR city LIKE :city)
       AND (:status IS NULL OR status = :status)
       """, nativeQuery = true)*/

    @Query("""
       SELECT e FROM Employee e
       WHERE (:empName IS NULL OR e.empName LIKE :empName)
       AND (:dept IS NULL OR e.dept LIKE :dept)
       AND (:email IS NULL OR e.email LIKE :email)
       AND (:city IS NULL OR e.city LIKE :city)
       AND (:status IS NULL OR e.status = :status)
       """)
    Page<Employee> filterEmployees(
            @Param("empName") String empName,
            @Param("dept") String dept,
            @Param("email") String email,
            @Param("city") String city,
            @Param("status") Status status,
            Pageable pageable
    );

}
