package com.techpulse.service.impl;

import com.techpulse.dto.CompanyRequestDTO;
import com.techpulse.dto.CompanyResponseDTO;
import com.techpulse.dto.EmployeeResponseDTO;
import com.techpulse.entity.Company;
import com.techpulse.entity.Employee;
import com.techpulse.exception.CompanyNotAvailableException;
import com.techpulse.mapper.CompanyMapper;
import com.techpulse.mapper.EmployeeMapper;
import com.techpulse.repository.ICompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompanyServiceImplTest {

    @Mock
    private ICompanyRepository repository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private CompanyServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testAddCompany() {
        CompanyRequestDTO request = new CompanyRequestDTO();
        request.setCompanyName("TechPulse");

        Company company = new Company();
        company.setCompanyName("TechPulse");

        Company saved = new Company();
        saved.setCompanyId(1);
        saved.setCompanyName("TechPulse");

        CompanyResponseDTO responseDTO = new CompanyResponseDTO();
        responseDTO.setCompanyId(1);
        responseDTO.setCompanyName("TechPulse");

        when(companyMapper.toEntity(request)).thenReturn(company);
        when(repository.save(company)).thenReturn(saved);
        when(companyMapper.toDTO(saved)).thenReturn(responseDTO);

        CompanyResponseDTO result = service.addCompany(request);

        assertEquals(1, result.getCompanyId());
        verify(repository, times(1)).save(company);
    }


    @Test
    void testGetCompaniesList() {
        Company c1 = new Company();
        c1.setCompanyId(1);
        c1.setCompanyName("ABC");
        c1.setEmployees(List.of(new Employee()));

        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setCompanyId(1);
        dto.setEmployees(new ArrayList<>());

        when(repository.findAll()).thenReturn(List.of(c1));
        when(companyMapper.toDTO(c1)).thenReturn(dto);
        when(employeeMapper.toDTO(any(Employee.class))).thenReturn(new EmployeeResponseDTO());

        List<CompanyResponseDTO> companies = service.getCompanies();

        assertEquals(1, companies.size());
        verify(repository, times(1)).findAll();
    }



    @Test
    void testGetCompanyById() {
        Company company = new Company();
        company.setCompanyId(1);
        company.setEmployees(List.of(new Employee()));

        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setCompanyId(1);

        when(repository.findById(1)).thenReturn(Optional.of(company));
        when(companyMapper.toDTO(company)).thenReturn(dto);
        when(employeeMapper.toDTO(any(Employee.class))).thenReturn(new EmployeeResponseDTO());

        CompanyResponseDTO result = service.getCompanies(1);

        assertEquals(1, result.getCompanyId());
        verify(repository).findById(1);
    }

    @Test
    void testGetCompanyByIdNotFound() {
        when(repository.findById(100)).thenReturn(Optional.empty());

        assertThrows(CompanyNotAvailableException.class,
                () -> service.getCompanies(100));
    }


    @Test
    void testUpdateCompany() {
        CompanyRequestDTO request = new CompanyRequestDTO();
        request.setCompanyName("NewName");
        request.setNoOfEmployees(50);

        Company oldCompany = new Company();
        oldCompany.setCompanyId(1);

        Company updated = new Company();
        updated.setCompanyName("NewName");

        CompanyResponseDTO responseDTO = new CompanyResponseDTO();
        responseDTO.setCompanyName("NewName");

        when(repository.findById(1)).thenReturn(Optional.of(oldCompany));
        when(repository.save(oldCompany)).thenReturn(updated);
        when(companyMapper.toDTO(updated)).thenReturn(responseDTO);

        CompanyResponseDTO result = service.updateCompanyDetails(1, request);

        assertEquals("NewName", result.getCompanyName());
        verify(repository).save(oldCompany);
    }


    @Test
    void testDeleteCompany() {
        when(repository.existsById(1)).thenReturn(true);

        service.deleteCompany(1);

        verify(repository).deleteById(1);
    }

    @Test
    void testDeleteCompanyNotFound() {
        when(repository.existsById(99)).thenReturn(false);

        assertThrows(CompanyNotAvailableException.class,
                () -> service.deleteCompany(99));
    }
}
