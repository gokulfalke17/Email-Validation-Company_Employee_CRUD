package com.techpulse.service.impl;

import com.techpulse.dto.EmployeeRequestDTO;
import com.techpulse.dto.EmployeeResponseDTO;
import com.techpulse.entity.Company;
import com.techpulse.entity.Employee;
import com.techpulse.entity.enums.Status;
import com.techpulse.exception.EmployeeNotFoundException;
import com.techpulse.mapper.CompanyMapper;
import com.techpulse.mapper.EmployeeMapper;
import com.techpulse.repository.ICompanyRepository;
import com.techpulse.repository.IEmployeeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import javax.naming.NamingException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @Mock
    private IEmployeeRepository repository;

    @Mock
    private ICompanyRepository companyRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private EmployeeServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testFilterEmployees() {

        Employee employee = new Employee();
        employee.setEmpId(1);
        employee.setCompany(new Company());

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmpId(1);

        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(repository.filterEmployees(any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        when(employeeMapper.toDTO(employee)).thenReturn(dto);

        Page<EmployeeResponseDTO> result =
                service.filterEmployees(null, null, null, null, Status.ACTIVE, 0, 5);

        assertEquals(1, result.getContent().size());
    }


    @Test
    void testAddEmployee() throws NamingException {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setCompanyId(1);
        request.setEmpName("John");

        Company company = new Company();
        company.setCompanyId(1);

        Employee employee = new Employee();
        employee.setEmpName("John");

        Employee saved = new Employee();
        saved.setEmpId(101);

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmpId(101);

        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(employeeMapper.toEntity(request)).thenReturn(employee);
        when(repository.save(employee)).thenReturn(saved);
        when(employeeMapper.toDTO(saved)).thenReturn(dto);
        when(companyMapper.toDTO(company)).thenReturn(new com.techpulse.dto.CompanyResponseDTO());

        EmployeeResponseDTO result = service.addEmployee(request);

        assertEquals(101, result.getEmpId());
        verify(repository).save(employee);
    }


    @Test
    void testGetEmployeesList() {
        Employee emp = new Employee();
        emp.setEmpId(1);
        emp.setCompany(new Company());

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmpId(1);

        when(repository.findAll()).thenReturn(List.of(emp));
        when(employeeMapper.toDTO(emp)).thenReturn(dto);

        List<EmployeeResponseDTO> result = service.getEmployees();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }



    @Test
    void testGetEmployeesByPage() {
        Employee emp = new Employee();
        emp.setEmpId(1);

        Page<Employee> page = new PageImpl<>(List.of(emp));

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmpId(1);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(employeeMapper.toDTO(emp)).thenReturn(dto);

        Page<EmployeeResponseDTO> result = service.getEmployees(0, 5);

        assertEquals(1, result.getContent().size());
    }


    @Test
    void testGetEmployeeById() {
        Employee emp = new Employee();
        emp.setEmpId(1);

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmpId(1);

        when(repository.findById(1)).thenReturn(Optional.of(emp));
        when(employeeMapper.toDTO(emp)).thenReturn(dto);

        EmployeeResponseDTO result = service.getEmployees(1);

        assertEquals(1, result.getEmpId());
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        when(repository.findById(5)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> service.getEmployees(5));
    }


    @Test
    void testUpdateEmployee() {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setEmpName("Updated");
        request.setCompanyId(1);

        Employee oldEmp = new Employee();
        oldEmp.setEmpId(1);

        Company company = new Company();
        company.setCompanyId(1);

        Employee updated = new Employee();
        updated.setEmpName("Updated");

        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmpName("Updated");

        when(repository.findById(1)).thenReturn(Optional.of(oldEmp));
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));
        when(repository.save(oldEmp)).thenReturn(updated);
        when(employeeMapper.toDTO(updated)).thenReturn(dto);

        EmployeeResponseDTO result = service.updateEmployee(1, request);

        assertEquals("Updated", result.getEmpName());
    }



    @Test
    void testDeleteEmployee() {
        when(repository.existsById(1)).thenReturn(true);

        service.deleteEmployee(1);

        verify(repository).deleteById(1);
    }

    @Test
    void testDeleteEmployeeNotFound() {
        when(repository.existsById(99)).thenReturn(false);

        assertThrows(EmployeeNotFoundException.class,
                () -> service.deleteEmployee(99));
    }
}
