package com.techpulse.service;

import com.techpulse.dto.EmployeeRequestDTO;
import com.techpulse.dto.EmployeeResponseDTO;
import com.techpulse.entity.Employee;
import com.techpulse.entity.enums.Status;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IEmployeeService {


    public EmployeeResponseDTO addEmployee(EmployeeRequestDTO dto);
    public List<EmployeeResponseDTO> getEmployees();
    public Page<EmployeeResponseDTO> getEmployees(int page, int size);
    public EmployeeResponseDTO getEmployees(Integer empId);
    public EmployeeResponseDTO updateEmployee(Integer empId, EmployeeRequestDTO dto);
    public void deleteEmployee(Integer empId);
    public Page<EmployeeResponseDTO> filterEmployees(
            String empName,
            String dept,
            String email,
            String city,
            Status status,
            int page,
            int size
    ) ;


    /*
    public Employee addEmployee(Employee employee);
    public List<Employee> getEmployees();
    public Page<Employee> getEmployeePageByPage(int page, int size);
    public Employee getEmployees(Integer empId);
    public Employee updateEmployee(Integer empId, Employee newEmployee);
    public void deleteEmployee(Integer empId);
    */
}
