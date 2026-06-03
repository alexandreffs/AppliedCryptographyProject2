package pa2.client;

import pa2.crypto.ClientKeys;
import pa2.model.EmployeeDecrypted;
import pa2.server.EmployeeRepository;
import pa2.server.QueryService;

import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive terminal menu for testing the 11 required project operations.
 *
 * Run after Bootstrap:
 *
 * mvn exec:java -Dexec.mainClass="pa2.client.InteractiveClientApp"
 */
public class InteractiveClientApp {

    public static void main(String[] args) throws Exception {
        ClientKeys keys = ClientKeys.loadOrCreate(Bootstrap.DEFAULT_KEYS);

        try (
                Scanner scanner = new Scanner(System.in);
                EmployeeRepository repository = new EmployeeRepository()) {
            repository.connect();

            QueryService server = new QueryService(repository);
            QueryClient client = new QueryClient(server, keys);

            boolean running = true;

            while (running) {
                printMenu();
                System.out.print("Choose an option: ");

                String option = scanner.nextLine().trim();

                try {
                    switch (option) {
                        case "1" -> searchByEmployeeID(scanner, client);
                        case "2" -> searchByFullName(scanner, client);
                        case "3" -> employeesOrderedBySalary(client);
                        case "4" -> searchByDepartment(scanner, client);
                        case "5" -> highestSalary(client);
                        case "6" -> compareSalaries(scanner, client);
                        case "7" -> employeesOrderedByAge(client);
                        case "8" -> salaryToUsd(scanner, client);
                        case "9" -> departmentPayroll(scanner, client);
                        case "10" -> oldestEmployee(client);
                        case "11" -> bonusesForEligibleEmployees(client);
                        case "0", "q", "Q", "exit", "EXIT" -> {
                            running = false;
                            System.out.println("Exiting client.");
                        }
                        default -> System.out.println("Invalid option. Choose a number from 1 to 11, or 0 to exit.");
                    }
                } catch (Exception e) {
                    System.out.println();
                    System.out.println("Operation failed:");
                    System.out.println(e.getMessage());
                    System.out.println();
                }

                if (running) {
                    System.out.println();
                    System.out.print("Press ENTER to return to the menu...");
                    scanner.nextLine();
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("====================================================");
        System.out.println(" Privacy-Preserving Employee Database - MySQL Client");
        System.out.println("====================================================");
        System.out.println("1  - Search and retrieve by Employee Identification");
        System.out.println("2  - Search and retrieve by Employee Full Name");
        System.out.println("3  - Search and retrieve employees ordered by Salary");
        System.out.println("4  - Search and retrieve employees by DepartmentID");
        System.out.println("5  - Search and retrieve employee with highest Salary");
        System.out.println("6  - Compare salaries between two employees by Full Name");
        System.out.println("7  - Search and retrieve employees ordered by Age");
        System.out.println("8  - Convert salary of an employee to USD");
        System.out.println("9  - Department employees + encrypted payroll sum");
        System.out.println("10 - Find oldest employee");
        System.out.println("11 - Compute 25% bonus for Bonus Eligibility employees");
        System.out.println("0  - Exit");
        System.out.println("====================================================");
    }

    private static void searchByEmployeeID(Scanner scanner, QueryClient client) throws Exception {
        System.out.print("Employee ID, example EMP1001: ");
        String employeeID = scanner.nextLine().trim();

        EmployeeDecrypted result = client.searchByEmployeeID(employeeID);

        System.out.println();
        System.out.println("Result:");
        printEmployee(result);
    }

    private static void searchByFullName(Scanner scanner, QueryClient client) throws Exception {
        System.out.print("Full Name, example Emma Johnson: ");
        String fullName = scanner.nextLine().trim();

        EmployeeDecrypted result = client.searchByFullName(fullName);

        System.out.println();
        System.out.println("Result:");
        printEmployee(result);
    }

    private static void employeesOrderedBySalary(QueryClient client) throws Exception {
        List<EmployeeDecrypted> employees = client.employeesOrderedBySalary();

        System.out.println();
        System.out.println("Employees ordered by salary:");
        printEmployeeList(employees);
    }

    private static void searchByDepartment(Scanner scanner, QueryClient client) throws Exception {
        System.out.print("DepartmentID, example DPT109: ");
        String departmentID = scanner.nextLine().trim();

        List<EmployeeDecrypted> employees = client.searchByDepartmentID(departmentID);

        System.out.println();
        System.out.println("Employees in department " + departmentID + ":");
        printEmployeeList(employees);
    }

    private static void highestSalary(QueryClient client) throws Exception {
        EmployeeDecrypted result = client.employeeWithHighestSalary();

        System.out.println();
        System.out.println("Employee with highest salary:");
        printEmployee(result);
    }

    private static void compareSalaries(Scanner scanner, QueryClient client) throws Exception {
        System.out.print("First employee Full Name: ");
        String fullNameA = scanner.nextLine().trim();

        System.out.print("Second employee Full Name: ");
        String fullNameB = scanner.nextLine().trim();

        boolean firstHasHigherSalary = client.hasHigherSalary(fullNameA, fullNameB);

        System.out.println();
        if (firstHasHigherSalary) {
            System.out.println(fullNameA + " has a higher salary than " + fullNameB + ".");
        } else {
            System.out.println(fullNameA + " does not have a higher salary than " + fullNameB + ".");
        }
    }

    private static void employeesOrderedByAge(QueryClient client) throws Exception {
        List<EmployeeDecrypted> employees = client.employeesOrderedByAge();

        System.out.println();
        System.out.println("Employees ordered by age:");
        printEmployeeList(employees);
    }

    private static void salaryToUsd(Scanner scanner, QueryClient client) throws Exception {
        System.out.print("Employee Full Name, example Emma Johnson: ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Exchange rate scaled by 100, example 110 for 1.10 USD/EUR: ");
        int scaledRate = Integer.parseInt(scanner.nextLine().trim());

        EmployeeDecrypted employee = client.searchByFullName(fullName);
        BigInteger salaryUsd = client.salaryInUsd(fullName, scaledRate);

        System.out.println();

        if (employee == null || salaryUsd == null) {
            System.out.println("Employee not found.");
            return;
        }

        System.out.println("Employee: " + employee.fullName);
        System.out.println("Original salary in EUR: " + employee.salary);
        System.out.println("Exchange rate: " + (scaledRate / 100.0));
        System.out.println("Converted salary in USD: " + salaryUsd);
    }

    private static void departmentPayroll(Scanner scanner, QueryClient client) throws Exception {
        System.out.print("DepartmentID, example DPT109: ");
        String departmentID = scanner.nextLine().trim();

        BigInteger payroll = client.departmentPayrollSum(departmentID);

        System.out.println();
        System.out.println("Total payroll for department " + departmentID + ": " + payroll);
    }

    private static void oldestEmployee(QueryClient client) throws Exception {
        EmployeeDecrypted result = client.oldestEmployee();

        System.out.println();
        System.out.println("Oldest employee:");
        printEmployee(result);
    }

    private static void bonusesForEligibleEmployees(QueryClient client) throws Exception {
        List<BigInteger> bonuses = client.bonusesForEligibleEmployees();

        System.out.println();
        System.out.println("25% bonuses for eligible employees:");

        if (bonuses.isEmpty()) {
            System.out.println("No eligible employees found.");
            return;
        }

        for (int i = 0; i < bonuses.size(); i++) {
            System.out.println((i + 1) + ". Bonus = " + bonuses.get(i));
        }
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