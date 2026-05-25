package pa2.client;

import pa2.crypto.ClientKeys;
import pa2.model.EmployeeDecrypted;
import pa2.server.EmployeeRepository;
import pa2.server.QueryService;

import java.math.BigInteger;
import java.util.List;

public class ClientApp {
    public static void main(String[] args) throws Exception {
        ClientKeys keys = ClientKeys.loadOrCreate(Bootstrap.DEFAULT_KEYS);

        try (EmployeeRepository repository = new EmployeeRepository()) {
            repository.connect();

            QueryService server = new QueryService(repository);
            QueryClient client = new QueryClient(server, keys);

            System.out.println("---- Demo queries over encrypted MySQL table ----");

            System.out.println("\n1) Search by employeeID EMP1001");
            System.out.println(client.searchByEmployeeID("EMP1001"));

            System.out.println("\n2) Search by full name Emma Johnson");
            System.out.println(client.searchByFullName("Emma Johnson"));

            System.out.println("\n3) Employees from department DPT109");
            print(client.searchByDepartmentID("DPT109"));

            System.out.println("\n4) Employees ordered by salary");
            print(client.employeesOrderedBySalary());

            System.out.println("\n5) Highest salary");
            System.out.println(client.employeeWithHighestSalary());

            System.out.println("\n6) Is Emma Johnson salary higher than John Smith?");
            System.out.println(client.hasHigherSalary("Emma Johnson", "John Smith"));

            System.out.println("\n7) Employees ordered by age");
            print(client.employeesOrderedByAge());

            System.out.println("\n8) Emma Johnson salary in USD using 1.10 exchange rate");
            System.out.println(client.salaryInUsd("Emma Johnson", 110));

            System.out.println("\n9) Payroll sum for DPT109");
            BigInteger payroll = client.departmentPayrollSum("DPT109");
            System.out.println(payroll);

            System.out.println("\n10) Oldest employee");
            System.out.println(client.oldestEmployee());

            System.out.println("\n11) Bonuses for eligible employees");
            System.out.println(client.bonusesForEligibleEmployees());
        }
    }

    private static void print(List<EmployeeDecrypted> list) {
        for (EmployeeDecrypted e : list) {
            System.out.println(e);
        }
    }
}
