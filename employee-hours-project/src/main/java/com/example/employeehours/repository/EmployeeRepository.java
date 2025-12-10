package com.example.employeehours.repository;

import com.example.employeehours.model.Employee;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeRepository {
    private List<Employee> employees = new ArrayList<>();

    @PostConstruct
    public void load() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/employees.json");
            if (is != null) {
                employees = mapper.readValue(is, new TypeReference<List<Employee>>(){});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Employee> findAll() { return employees; }
}