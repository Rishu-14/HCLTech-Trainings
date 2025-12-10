Java Payslip Generator Project
==============================

What's included
- A Maven Java project (Java 17) that reads a large employees.json (contains 5200 employees).
- Uses Jackson (jackson-databind) to parse JSON.
- Generates payslips in parallel using Java parallel streams and writes per-employee payslip text files.
- Calculates PF (employee 12% of basic), a simple HRA (20%), a special allowance (10%), professional tax,
  prorates salary by attendance for the month, and outputs a salary distribution CSV and a summary.

How to build & run
1. Ensure you have Java 17+ and Maven installed.
2. From the project root:
   mvn package
3. Run:
   mvn exec:java -Dexec.mainClass="com.example.payslip.Main"

Output
- output/payslips/ : contains per-employee payslip text files
- output/salary_distribution.csv : distribution by basic salary bracket
- output/summary.txt : totals and counts

Notes
- The employees.json has an "attendance" array for a 30-day month. The program counts present days and prorates salary.
- The project uses parallel streams to process employees concurrently - suitable for large datasets.
