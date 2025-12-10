package com.techpulse.service;

import com.techpulse.dto.EmployeeRequestDTO;
import com.techpulse.dto.EmployeeResponseDTO;
import com.techpulse.entity.Employee;
import com.techpulse.entity.enums.Status;
import com.techpulse.exception.EmployeeNotFoundException;
import com.techpulse.mapper.EmployeeMapper;
import com.techpulse.repository.IEmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private IEmployeeRepository repository;

// @Autowired
// private ModelMapper mapper;
//
//
// // Convert DTO <-> Entity
// private Employee toEntity(EmployeeRequestDTO dto) {
//     return mapper.map(dto, Employee.class);
// }
//
// //Convert Entity <-> DTO
// private EmployeeResponseDTO toDTO(Employee employee) {
//     return mapper.map(employee, EmployeeResponseDTO.class);
// }

    @Autowired
    private EmployeeMapper mapper;



   /* private Employee toEntity(EmployeeRequestDTO dto) {
        Employee emp = new Employee();
        emp.setEmpName(dto.getEmpName());
        emp.setSalary(dto.getSalary());
        emp.setEmail(dto.getEmail());
        emp.setDept(dto.getDept());
        return emp;
    }


    private EmployeeResponseDTO toDTO(Employee emp) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmpId(emp.getEmpId());
        dto.setEmpName(emp.getEmpName());
        dto.setSalary(emp.getSalary());
        dto.setEmail(emp.getEmail());
        dto.setDept(emp.getDept());
        return dto;
    }*/

    @Override
    public Page<EmployeeResponseDTO> filterEmployees(
            String empName,
            String dept,
            String email,
            String city,
            Status status,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = repository.filterEmployees(empName, dept, email, city, status, pageable);
        return employeePage.map(emp -> mapper.toDTO((emp)));
    }




    @Override
    public EmployeeResponseDTO addEmployee(EmployeeRequestDTO dto) {
//        Employee employee = toEntity(dto);
        Employee employee = mapper.toEntity(dto);
        Employee savedEmployee = repository.save(employee);
//        return toDTO(savedEmployee);
        return mapper.toDTO(savedEmployee);
    }


    @Override
    public List<EmployeeResponseDTO> getEmployees() {
        List<EmployeeResponseDTO> list =
                repository.findAll()
                .stream()
//                .map(this::toDTO)
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public Page<EmployeeResponseDTO> getEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage = repository.findAll(pageable);
//        return employeePage.map(this::toDTO);
        return  employeePage.map(mapper::toDTO);
    }

    @Override
    public EmployeeResponseDTO getEmployees(Integer empId) {
        Employee employee = repository.findById(empId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found :: "+empId));
//        return toDTO(employee);
        return mapper.toDTO(employee);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Integer empId, EmployeeRequestDTO dto) {
        Employee oldEmployee = repository.findById(empId)
                        .orElseThrow(() -> new EmployeeNotFoundException("Cannot Update! Employee Not Found :: "+empId));
        oldEmployee.setEmpName(dto.getEmpName());
        oldEmployee.setEmail(dto.getEmail());
        oldEmployee.setSalary(dto.getSalary());
        oldEmployee.setDept(dto.getDept());

        Employee updatedEmployee = repository.save(oldEmployee);
//        return toDTO(updatedEmployee);
        return mapper.toDTO(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Integer empId) {
        if(!repository.existsById(empId)) {
            throw new EmployeeNotFoundException("Cannot Delete.! Employee Not Found :: "+ empId);
        }
        repository.deleteById(empId);
    }




    /*
    @Override
    public Employee addEmployee(Employee employee) {
        return repository.save(employee);
    }

    @Override
    public List<Employee> getEmployees() {
        return repository.findAll();
    }

    @Override
    public Page<Employee> getEmployeePageByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    @Override
    public Employee getEmployees(Integer empId) {
        return repository.findById(empId).orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with id :: "+ empId));
    }

    @Override
    public Employee updateEmployee(Integer empId, Employee newEmployee) {
        Employee oldEmployee = repository.findById(empId).orElseThrow(() -> new EmployeeNotFoundException("Cannot, update! Employee Not Found :: " + empId));
        newEmployee.setEmpId(empId);
        return repository.save(newEmployee);
    }

    @Override
    public void deleteEmployee(Integer empId) {
        if(!repository.existsById(empId)) {
            throw new EmployeeNotFoundException("Cannot Delete! Employee Not Found :: "+ empId);
        }
        repository.deleteById(empId);
    }

    */
}
