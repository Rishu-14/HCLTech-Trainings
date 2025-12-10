package com.example.payslip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class PayslipGenerator {
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static class SalaryStats {
        public AtomicLong totalEmployees = new AtomicLong();
        public AtomicLong totalGross = new AtomicLong();
        public AtomicLong totalNet = new AtomicLong();
    }

    public static void processEmployees(List<Employee> employees, Path outputDir) throws Exception {
        Files.createDirectories(outputDir);
        Path payslipDir = outputDir.resolve("payslips");
        Files.createDirectories(payslipDir);

        ConcurrentMap<String, AtomicLong> distribution = new ConcurrentHashMap<>();
        SalaryStats stats = new SalaryStats();

        // Using parallel stream for parallel processing
        employees.parallelStream().forEach(emp -> {
            try {
                int daysWorked = (int) emp.attendance.stream().filter(b -> b != null && b).count();
                int monthDays = emp.attendance.size();
                double attendanceFactor = (double) daysWorked / monthDays;

                // Salary components
                double basic = emp.basicSalary;
                double hra = basic * 0.20; // 20% HRA
                double specialAllowance = basic * 0.10; // 10% allowance
                double grossMonthly = basic + hra + specialAllowance;

                // Pro-rate by attendance
                grossMonthly = grossMonthly * attendanceFactor;

                // Deductions
                double pfEmployee = basic * 0.12 * attendanceFactor; // PF employee 12% on basic
                double pfEmployer = basic * 0.12 * attendanceFactor; // employer contribution (for record)
                double professionalTax = (grossMonthly > 15000) ? 200 : 0;

                double totalDeductions = pfEmployee + professionalTax;
                double net = grossMonthly - totalDeductions;

                // Write payslip file
                String filename = emp.id + "_" + emp.name.replaceAll("[^a-zA-Z0-9]", "_") + ".txt";
                Path outFile = payslipDir.resolve(filename);
                List<String> lines = new ArrayList<>();
                lines.add("Payslip for: " + emp.name + " (" + emp.id + ")");
                lines.add("Joining Date: " + emp.joiningDate);
                lines.add("----------------------------------------");
                lines.add("Basic Salary : " + df.format(emp.basicSalary));
                lines.add("Days Present  : " + daysWorked + " / " + monthDays);
                lines.add("Attendance Factor: " + df.format(attendanceFactor));
                lines.add("HRA (20%)     : " + df.format(hra * attendanceFactor));
                lines.add("Special Allow.: " + df.format(specialAllowance * attendanceFactor));
                lines.add("Gross Salary  : " + df.format(grossMonthly));
                lines.add("PF (Employee 12% on basic): " + df.format(pfEmployee));
                lines.add("Professional Tax: " + df.format(professionalTax));
                lines.add("Total Deductions: " + df.format(totalDeductions));
                lines.add("Net Pay       : " + df.format(net));
                lines.add("Employer PF contribution (record): " + df.format(pfEmployer));
                lines.add("----------------------------------------");
                Files.write(outFile, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

                // Update distribution: salary bracket (e.g., 0-25000, 25k-50k, ...)
                String bracket = getBracket((int) emp.basicSalary);
                distribution.computeIfAbsent(bracket, k -> new AtomicLong()).incrementAndGet();

                stats.totalEmployees.incrementAndGet();
                stats.totalGross.addAndGet((long)Math.round(grossMonthly));
                stats.totalNet.addAndGet((long)Math.round(net));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Write distribution CSV
        Path dist = outputDir.resolve("salary_distribution.csv");
        try (BufferedWriter bw = Files.newBufferedWriter(dist, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            bw.write("Bracket,Count\n");
            distribution.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    try {
                        bw.write(entry.getKey() + "," + entry.getValue().get() + "\n");
                    } catch (IOException e) { e.printStackTrace(); }
                });
        }

        // Summary
        Path summary = outputDir.resolve("summary.txt");
        try (BufferedWriter bw = Files.newBufferedWriter(summary, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            bw.write("Total employees processed: " + stats.totalEmployees.get() + "\n");
            bw.write("Total gross (approx): " + stats.totalGross.get() + "\n");
            bw.write("Total net (approx): " + stats.totalNet.get() + "\n");
            bw.write("Payslips directory: " + payslipDir.toAbsolutePath().toString() + "\n");
        }

        System.out.println("Processing complete. Output written to: " + outputDir.toAbsolutePath());
    }

    private static String getBracket(int basic) {
        if (basic < 25000) return "<25k";
        if (basic < 50000) return "25k-50k";
        if (basic < 100000) return "50k-100k";
        if (basic < 200000) return "100k-200k";
        return ">=200k";
    }
}
