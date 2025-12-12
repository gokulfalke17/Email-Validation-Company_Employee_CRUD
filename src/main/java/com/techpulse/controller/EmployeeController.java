package com.techpulse.controller;

import com.techpulse.dto.EmployeeRequestDTO;
import com.techpulse.dto.EmployeeResponseDTO;
import com.techpulse.entity.enums.Status;
import com.techpulse.response.ApiResponse;
import com.techpulse.service.IEmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NamingException;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private IEmployeeService service;


    @GetMapping("/search")
    public ResponseEntity<ApiResponse> filterEmployees(
            @RequestParam(required = false) String empName,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<EmployeeResponseDTO> dto = service.filterEmployees(empName, dept, email, city, status, page, size);

        return  ResponseEntity.ok(
                new ApiResponse(true, "Employee Filtered Successfully...", dto)
        );
    }


    @PostMapping
    public ResponseEntity<ApiResponse> addEmployee(@Valid @RequestBody EmployeeRequestDTO dto) throws NamingException {
        EmployeeResponseDTO addedEmployee = service.addEmployee(dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "Employee Added Successfully...", addedEmployee)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getEmployees() {
        List<EmployeeResponseDTO> list = service.getEmployees();
        return ResponseEntity.ok(
                new ApiResponse(true, "Employees are Available", list)
        );
    }

    @GetMapping("/pageable")
    public ResponseEntity<ApiResponse> getEmployeesPageByPage(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size) {

        Page<EmployeeResponseDTO> pageByPageEmployees = service.getEmployees(page, size);
        return ResponseEntity.ok(
                new ApiResponse(true, "Employees Found.", pageByPageEmployees)
        );
    }

    @GetMapping("/{empId}")
    public ResponseEntity<ApiResponse> getEmployees(@PathVariable Integer empId) {
        EmployeeResponseDTO employee = service.getEmployees(empId);
        return ResponseEntity.ok(
                new ApiResponse(true, "Employee Found.", employee)
        );
    }

    @PutMapping("/{empId}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable Integer empId, @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO updatedEmployee = service.updateEmployee(empId, dto);
        return ResponseEntity.ok(
                new ApiResponse(true, "Employee Updated Successfully.", updatedEmployee)
        );
    }


    @DeleteMapping("/{empId}")
    public  ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Integer empId) {
        service.deleteEmployee(empId);
        return ResponseEntity.ok(
                new ApiResponse(true, "Employee Deleted Successfully...", null)
        );
    }

}
