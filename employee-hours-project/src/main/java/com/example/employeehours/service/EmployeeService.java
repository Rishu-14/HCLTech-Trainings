package com.example.employeehours.service;

import com.example.employeehours.model.Employee;
import com.example.employeehours.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) { this.repo = repo; }

    public List<Employee> topEmployeesWithAvgGreaterThan(double threshold, int limit) {
        return repo.findAll().stream()
            .filter(e -> e.averageWeeklyHours() > threshold)
            .sorted() // uses Comparable implemented in Employee (descending avg)
            .limit(limit)
            .collect(Collectors.toList());
    }
}