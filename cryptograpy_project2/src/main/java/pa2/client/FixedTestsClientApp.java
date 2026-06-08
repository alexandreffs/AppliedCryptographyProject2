package pa2.client;

import pa2.crypto.ClientKeys;
import pa2.model.EmployeeDecrypted;
import pa2.server.EmployeeRepository;
import pa2.server.QueryService;

import java.math.BigInteger;
import java.util.List;

// mvn exec:java -Dexec.mainClass="pa2.client.FixedTestsClientApp"
public class FixedTestsClientApp {

    private static final String TEST_EMPLOYEE_ID = "EMP1001";
    private static final String TEST_FULL_NAME_A = "Emma Johnson";
    private static final String TEST_FULL_NAME_B = "John Smith";
    private static final String TEST_DEPARTMENT_ID = "DPT109";

    // 110 means 1.10 USD/EUR
    private static final int TEST_USD_EXCHANGE_RATE_SCALED = 110;

    public static void main(String[] args) throws Exception {
        ClientKeys keys = ClientKeys.loadOrCreate(Bootstrap.DEFAULT_KEYS);

        try (EmployeeRepository repository = new EmployeeRepository()) {
            repository.connect();

            QueryService server = new QueryService(repository);
            QueryClient client = new QueryClient(server, keys);

            System.out.println("====================================================");
            System.out.println(" Fixed 11 Tests - Privacy-Preserving Employee DB");
            System.out.println("====================================================");

            test1SearchByEmployeeID(client);
            test2SearchByFullName(client);
            test3EmployeesOrderedBySalary(client);
            test4SearchByDepartment(client);
            test5HighestSalary(client);
            test6CompareSalaries(client);
            test7EmployeesOrderedByAge(client);
            test8SalaryToUsd(client);
            test9DepartmentPayroll(client);
            test10OldestEmployee(client);
            test11BonusesForEligibleEmployees(client);

            System.out.println();
            System.out.println("====================================================");
            System.out.println(" All fixed tests completed.");
            System.out.println("====================================================");
        }
    }

    private static void test1SearchByEmployeeID(QueryClient client) throws Exception {
        printTestHeader("1 - Search and retrieve by Employee Identification");

        long start = PerformanceUtils.now();

        EmployeeDecrypted result = client.searchByEmployeeID(TEST_EMPLOYEE_ID);

        long latency = PerformanceUtils.elapsedMs(start);

        printEmployee(result);
        System.out.println("Latency: " + latency + " ms");
    }

    private static void test2SearchByFullName(QueryClient client) throws Exception {
        printTestHeader("2 - Search and retrieve by Employee Full Name");

        long start = PerformanceUtils.now();

        EmployeeDecrypted result = client.searchByFullName(TEST_FULL_NAME_A);

        long latency = PerformanceUtils.elapsedMs(start);

        printEmployee(result);
        System.out.println("Latency: " + latency + " ms");
    }

    private static void test3EmployeesOrderedBySalary(QueryClient client) throws Exception {
        printTestHeader("3 - Search and retrieve employees ordered by Salary");

        long start = PerformanceUtils.now();

        List<EmployeeDecrypted> employees = client.employeesOrderedBySalary();

        long latency = PerformanceUtils.elapsedMs(start);

        printEmployeeList(employees);
        System.out.println("Latency: " + latency + " ms");
    }

    private static void test4SearchByDepartment(QueryClient client) throws Exception {
        printTestHeader("4 - Search and retrieve employees by DepartmentID");

        long start = PerformanceUtils.now();

        List<EmployeeDecrypted> employees = client.searchByDepartmentID(TEST_DEPARTMENT_ID);

        long latency = PerformanceUtils.elapsedMs(start);

        printEmployeeList(employees);
        System.out.println("Latency: " + latency + " ms");
    }

    private static void test5HighestSalary(QueryClient client) throws Exception {
        printTestHeader("5 - Search and retrieve employee with highest Salary");

        long start = PerformanceUtils.now();

        EmployeeDecrypted result = client.employeeWithHighestSalary();

        long latency = PerformanceUtils.elapsedMs(start);

        printEmployee(result);
        System.out.println("Latency: " + latency + " ms");
    }

    private static void test6CompareSalaries(QueryClient client) throws Exception {
        printTestHeader("6 - Compare salaries between two employees by Full Name");

        long start = PerformanceUtils.now();

        boolean result = client.hasHigherSalary(TEST_FULL_NAME_A, TEST_FULL_NAME_B);

        long latency = PerformanceUtils.elapsedMs(start);

        if (result) {
            System.out.println(TEST_FULL_NAME_A + " has a higher salary than " + TEST_FULL_NAME_B + ".");
        } else {
            System.out.println(TEST_FULL_NAME_A + " does not have a higher salary than " + TEST_FULL_NAME_B + ".");
        }

        System.out.println("Latency: " + latency + " ms");
    }

    private static void test7EmployeesOrderedByAge(QueryClient client) throws Exception {
        printTestHeader("7 - Search and retrieve employees ordered by Age");

        long start = PerformanceUtils.now();

        List<EmployeeDecrypted> employees = client.employeesOrderedByAge();

        long latency = PerformanceUtils.elapsedMs(start);

        printEmployeeList(employees);
        System.out.println("Latency: " + latency + " ms");
    }

    private static void test8SalaryToUsd(QueryClient client) throws Exception {
        printTestHeader("8 - Obtain salary converted to USD");

        long start = PerformanceUtils.now();

        EmployeeDecrypted employee = client.searchByFullName(TEST_FULL_NAME_A);
        BigInteger salaryUsd = client.salaryInUsd(
                TEST_FULL_NAME_A,
                TEST_USD_EXCHANGE_RATE_SCALED);

        long latency = PerformanceUtils.elapsedMs(start);

        if (employee == null || salaryUsd == null) {
            System.out.println("Employee not found.");
        } else {
            System.out.println("Employee: " + employee.fullName);
            System.out.println("Original salary in EUR: " + employee.salary);
            System.out.println("Scaled exchange rate: " + TEST_USD_EXCHANGE_RATE_SCALED);
            System.out.println("Exchange rate: " + (TEST_USD_EXCHANGE_RATE_SCALED / 100.0));
            System.out.println("Converted salary in USD: " + salaryUsd);
        }

        System.out.println("Latency: " + latency + " ms");
    }

    private static void test9DepartmentPayroll(QueryClient client) throws Exception {
        printTestHeader("9 - Department employees and encrypted payroll sum");

        long start = PerformanceUtils.now();

        List<EmployeeDecrypted> employees = client.searchByDepartmentID(TEST_DEPARTMENT_ID);
        BigInteger payroll = client.departmentPayrollSum(TEST_DEPARTMENT_ID);

        long latency = PerformanceUtils.elapsedMs(start);

        System.out.println("Department: " + TEST_DEPARTMENT_ID);
        System.out.println();
        System.out.println("Employees in department:");
        printEmployeeList(employees);
        System.out.println();
        System.out.println("Decrypted total payroll after homomorphic server sum: " + payroll);
        System.out.println("Latency: " + latency + " ms");
    }

    private static void test10OldestEmployee(QueryClient client) throws Exception {
        printTestHeader("10 - Find oldest employee");

        long start = PerformanceUtils.now();

        EmployeeDecrypted result = client.oldestEmployee();

        long latency = PerformanceUtils.elapsedMs(start);

        printEmployee(result);
        System.out.println("Latency: " + latency + " ms");
    }

    private static void test11BonusesForEligibleEmployees(QueryClient client) throws Exception {
        printTestHeader("11 - Compute 25% bonus for Bonus Eligibility employees");

        long start = PerformanceUtils.now();

        List<BigInteger> bonuses = client.bonusesForEligibleEmployees();

        long latency = PerformanceUtils.elapsedMs(start);

        if (bonuses.isEmpty()) {
            System.out.println("No eligible employees found.");
        } else {
            for (int i = 0; i < bonuses.size(); i++) {
                System.out.println((i + 1) + ". Bonus = " + bonuses.get(i));
            }
        }

        System.out.println("Latency: " + latency + " ms");
    }

    private static void printTestHeader(String title) {
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println(title);
        System.out.println("----------------------------------------------------");
    }

    private static void printEmployee(EmployeeDecrypted employee) {
        if (employee == null) {
            System.out.println("No employee found.");
            return;
        }

        System.out.println("----------------------------------------");
        System.out.println("Employee ID:        " + employee.employeeID);
        System.out.println("Full Name:          " + employee.fullName);
        System.out.println("First Name:         " + employee.firstName);
        System.out.println("Last Name:          " + employee.lastName);
        System.out.println("Date of Birth:      " + employee.dateOfBirth);
        System.out.println("Age:                " + employee.age);
        System.out.println("Email:              " + employee.email);
        System.out.println("Contact Phone:      " + employee.contactPhoneNumber);
        System.out.println("Personal Phone:     " + employee.personalPhoneNumber);
        System.out.println("Job Title:          " + employee.jobTitle);
        System.out.println("Department ID:      " + employee.departmentID);
        System.out.println("Hire Date:          " + employee.hireDate);
        System.out.println("Employment Type:    " + employee.employmentType);
        System.out.println("Salary:             " + employee.salary);
        System.out.println("Salary Band:        " + employee.salaryBand);
        System.out.println("Bonus Eligibility:  " + employee.bonusEligibility);
        System.out.println("----------------------------------------");
    }

    private static void printEmployeeList(List<EmployeeDecrypted> employees) {
        if (employees == null || employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        for (int i = 0; i < employees.size(); i++) {
            EmployeeDecrypted e = employees.get(i);

            System.out.printf(
                    "%3d | %-8s | %-25s | Dept: %-8s | Age: %3d | Salary: %8d | Bonus: %s%n",
                    i + 1,
                    e.employeeID,
                    e.fullName,
                    e.departmentID,
                    e.age,
                    e.salary,
                    e.bonusEligibility);
        }
    }
}