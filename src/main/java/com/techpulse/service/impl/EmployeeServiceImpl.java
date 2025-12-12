package com.techpulse.service.impl;

import com.techpulse.dto.EmailValidationResponse;
import com.techpulse.dto.EmployeeRequestDTO;
import com.techpulse.dto.EmployeeResponseDTO;
import com.techpulse.entity.Company;
import com.techpulse.entity.Employee;
import com.techpulse.entity.enums.Status;
import com.techpulse.exception.BadRequestException;
import com.techpulse.exception.EmployeeNotFoundException;
import com.techpulse.mapper.CompanyMapper;
import com.techpulse.mapper.EmployeeMapper;
import com.techpulse.repository.ICompanyRepository;
import com.techpulse.repository.IEmployeeRepository;
import com.techpulse.service.IEmailValidationService;
import com.techpulse.service.IEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private IEmployeeRepository repository;

    @Autowired
    private ICompanyRepository companyRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private IEmailValidationService emailValidationService;

    @Override
    public Page<EmployeeResponseDTO> filterEmployees(
            String empName, String dept, String email,
            String city, Status status, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> empPage = repository.filterEmployees(empName, dept, email, city, status, pageable);

        return empPage.map(employee -> {
            EmployeeResponseDTO dto = employeeMapper.toDTO(employee);

            if (employee.getCompany() != null) {
                dto.setCompany(companyMapper.toDTO(employee.getCompany()));
            }

            return dto;
        });
    }

    @Override
    public EmployeeResponseDTO addEmployee(EmployeeRequestDTO dto) throws NamingException {

        log.info("EmployeeServiceImpl.addEmployee called for email={}", dto.getEmail());
        EmailValidationResponse emailValidationResponse = null;
        try {
            emailValidationResponse = emailValidationService.validateEmail(dto.getEmail());
            log.info("Email validation completed: {} (valid={})", emailValidationResponse, emailValidationResponse == null ? null : emailValidationResponse.isValid());
        } catch (Exception e) {
            // log and rethrow as BadRequest to ensure the controller sends a 400
            log.error("Error during email validation for {}: {}", dto.getEmail(), e.getMessage(), e);
            throw new BadRequestException("Email validation failed: " + e.getMessage());
        }

        if (emailValidationResponse == null || !emailValidationResponse.isValid()) {
            throw new BadRequestException("Invalid email address: " + dto.getEmail());
        }

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found :: " + dto.getCompanyId()));

        Employee employee = employeeMapper.toEntity(dto);
        employee.setCompany(company);

        Employee savedEmployee = repository.save(employee);

        EmployeeResponseDTO response = employeeMapper.toDTO(savedEmployee);
        response.setCompany(companyMapper.toDTO(company));

        return response;
    }

    @Override
    public List<EmployeeResponseDTO> getEmployees() {
        return repository.findAll()
                .stream()
                .map(emp -> {
                    EmployeeResponseDTO dto = employeeMapper.toDTO(emp);

                    if (emp.getCompany() != null) {
                        dto.setCompany(companyMapper.toDTO(emp.getCompany()));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeResponseDTO> getEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> pageDb = repository.findAll(pageable);

        return pageDb.map(emp -> {
            EmployeeResponseDTO dto = employeeMapper.toDTO(emp);
            if (emp.getCompany() != null) {
                dto.setCompany(companyMapper.toDTO(emp.getCompany()));
            }
            return dto;
        });
    }

    @Override
    public EmployeeResponseDTO getEmployees(Integer empId) {
        Employee employee = repository.findById(empId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found :: " + empId));

        EmployeeResponseDTO dto = employeeMapper.toDTO(employee);

        if (employee.getCompany() != null) {
            dto.setCompany(companyMapper.toDTO(employee.getCompany()));
        }

        return dto;
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Integer empId, EmployeeRequestDTO dto) {
        Employee emp = repository.findById(empId)
                .orElseThrow(() -> new EmployeeNotFoundException("Cannot Update! Employee Not Found :: " + empId));

        emp.setEmpName(dto.getEmpName());
        emp.setEmail(dto.getEmail());
        emp.setSalary(dto.getSalary());
        emp.setDept(dto.getDept());
        emp.setCity(dto.getCity());
        emp.setStatus(dto.getStatus());

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        emp.setCompany(company);

        Employee updated = repository.save(emp);

        EmployeeResponseDTO response = employeeMapper.toDTO(updated);
        response.setCompany(companyMapper.toDTO(company));

        return response;
    }

    @Override
    public void deleteEmployee(Integer empId) {
        if (!repository.existsById(empId)) {
            throw new EmployeeNotFoundException("Cannot Delete! Employee Not Found :: " + empId);
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
