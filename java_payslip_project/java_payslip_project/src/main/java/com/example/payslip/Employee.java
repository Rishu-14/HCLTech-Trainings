package com.example.payslip;

import java.util.List;

public class Employee {
    public String id;
    public String name;
    public int basicSalary;
    public String joiningDate;
    public List<Boolean> attendance;

    // Jackson requires a default constructor
    public Employee() {}

    @Override
    public String toString() {
        return id + " - " + name + " - " + basicSalary;
    }
}
