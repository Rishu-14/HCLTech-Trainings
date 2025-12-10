package com.example.employeehours.model;

import java.util.List;
import java.util.OptionalDouble;

public class Employee implements Comparable<Employee> {
    private long id;
    private String name;
    private List<Double> dailyHours; // 7 numbers for a week

    public Employee() {}

    public Employee(long id, String name, List<Double> dailyHours) {
        this.id = id;
        this.name = name;
        this.dailyHours = dailyHours;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Double> getDailyHours() { return dailyHours; }
    public void setDailyHours(List<Double> dailyHours) { this.dailyHours = dailyHours; }

    public double averageWeeklyHours() {
        if (dailyHours == null || dailyHours.isEmpty()) return 0.0;
        OptionalDouble od = dailyHours.stream().mapToDouble(Double::doubleValue).average();
        return od.isPresent() ? od.getAsDouble() : 0.0;
    }

    @Override
    public int compareTo(Employee other) {
        // Descending order by average weekly hours
        return Double.compare(other.averageWeeklyHours(), this.averageWeeklyHours());
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + id + ", name='" + name + ''' + ", avg=" + averageWeeklyHours() + '}';
    }
}