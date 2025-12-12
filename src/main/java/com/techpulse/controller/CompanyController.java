package com.techpulse.controller;

import com.techpulse.dto.CompanyRequestDTO;
import com.techpulse.dto.CompanyResponseDTO;
import com.techpulse.response.ApiResponse;
import com.techpulse.service.ICompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    @Autowired
    private ICompanyService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> addCompany(@Valid @RequestBody CompanyRequestDTO dto) {
        CompanyResponseDTO addedCompany = service.addCompany(dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "Company Added Successfully...", addedCompany)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getCompanies() {
        List<CompanyResponseDTO> list = service.getCompanies();
        return ResponseEntity.ok(
                new ApiResponse(true, "Companies Found...", list)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCompanies(@PathVariable Integer id) {
        CompanyResponseDTO company = service.getCompanies(id);
        return ResponseEntity.ok(
                new ApiResponse(true, "Company Found...", company)
        );
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateCompany(@PathVariable Integer id, @Valid @RequestBody CompanyRequestDTO dto) {
        CompanyResponseDTO updateCompanyDetails = service.updateCompanyDetails(id, dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "Company Updated Successfully...", updateCompanyDetails)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCompany(@PathVariable Integer id) {
        service.deleteCompany(id);
        return ResponseEntity.ok(
                new ApiResponse(true, "Company Deleted Successfully...", null)
        );
    }
}
