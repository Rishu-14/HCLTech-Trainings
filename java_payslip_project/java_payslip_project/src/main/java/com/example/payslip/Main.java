package com.example.payslip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.nio.file.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Path res = Paths.get("src/main/resources/employees.json");
        if (!Files.exists(res)) {
            System.err.println("employees.json not found at " + res.toAbsolutePath());
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        List<Employee> employees = mapper.readValue(res.toFile(), new TypeReference<List<Employee>>() {});
        System.out.println("Loaded employees: " + employees.size());
        Path outputDir = Paths.get("output");
        PayslipGenerator.processEmployees(employees, outputDir);
    }
}
