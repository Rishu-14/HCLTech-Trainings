package com.example.employeehours.util;

import com.example.employeehours.model.Employee;
import java.util.Comparator;

public class Comparators {
    public static final Comparator<Employee> BY_NAME_ASC = Comparator.comparing(Employee::getName);
    public static final Comparator<Employee> BY_AVG_HOURS_ASC = Comparator.comparingDouble(Employee::averageWeeklyHours);
}