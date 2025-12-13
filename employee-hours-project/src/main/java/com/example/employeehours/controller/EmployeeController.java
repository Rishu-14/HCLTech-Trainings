package com.example.employeehours.controller;

import com.example.employeehours.model.Employee;
import com.example.employeehours.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EmployeeController {
    private final EmployeeService service;
    public EmployeeController(EmployeeService service) { this.service = service; }

    @GetMapping("/top-employees")
    public List<Employee> topEmployees(@RequestParam(defaultValue = "8.0") double minAvg,
                                       @RequestParam(defaultValue = "20") int limit) {
        return service.topEmployeesWithAvgGreaterThan(minAvg, limit);
    }
}