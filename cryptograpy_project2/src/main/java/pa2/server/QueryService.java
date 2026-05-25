package pa2.server;

import pa2.crypto.PaillierCrypto;
import pa2.model.EmployeeEncrypted;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class QueryService {
    private final EmployeeRepository repository;

    public QueryService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public EmployeeEncrypted searchByEmployeeID(String employeeIDIndex) throws Exception {
        return repository.findOneByToken("employee_id_index", employeeIDIndex);
    }

    public EmployeeEncrypted searchByFullName(String fullNameIndex) throws Exception {
        return repository.findOneByToken("full_name_index", fullNameIndex);
    }

    public List<EmployeeEncrypted> searchByDepartmentID(String departmentIDIndex) throws Exception {
        return repository.findManyByToken("department_id_index", departmentIDIndex);
    }

    public List<EmployeeEncrypted> employeesOrderedBySalary() throws Exception {
        return repository.findAllOrderedBy("salary_ope", false);
    }

    public EmployeeEncrypted employeeWithHighestSalary() throws Exception {
        List<EmployeeEncrypted> list = repository.findAllOrderedBy("salary_ope", true);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean hasHigherSalary(String fullNameIndexA, String fullNameIndexB) throws Exception {
        EmployeeEncrypted a = searchByFullName(fullNameIndexA);
        EmployeeEncrypted b = searchByFullName(fullNameIndexB);

        if (a == null || b == null) {
            throw new IllegalArgumentException("One or both employees were not found.");
        }

        return a.salaryOpe > b.salaryOpe;
    }

    public List<EmployeeEncrypted> employeesOrderedByAge() throws Exception {
        return repository.findAllOrderedBy("age_ope", false);
    }

    public EmployeeEncrypted oldestEmployee() throws Exception {
        List<EmployeeEncrypted> list = repository.findAllOrderedBy("age_ope", true);
        return list.isEmpty() ? null : list.get(0);
    }

    public String departmentPayrollSum(
            String departmentIDIndex,
            PaillierCrypto.PublicKey publicKey
    ) throws Exception {
        List<EmployeeEncrypted> employees = searchByDepartmentID(departmentIDIndex);

        if (employees.isEmpty()) {
            return null;
        }

        BigInteger encryptedSum = BigInteger.ONE;

        for (EmployeeEncrypted employee : employees) {
            encryptedSum = encryptedSum
                    .multiply(new BigInteger(employee.salaryPaillier))
                    .mod(publicKey.nSquared);
        }

        return encryptedSum.toString();
    }

    public List<String> bonusesForEligibleEmployees(
            String bonusEligibilityIndex,
            PaillierCrypto.PublicKey publicKey
    ) throws Exception {
        List<EmployeeEncrypted> employees =
                repository.findManyByToken("bonus_eligibility_index", bonusEligibilityIndex);

        List<String> encryptedBonuses = new ArrayList<>();

        for (EmployeeEncrypted employee : employees) {
            BigInteger encryptedSalary = new BigInteger(employee.salaryPaillier);

            /*
             * Enc(salary)^25 = Enc(salary * 25).
             * Client decrypts and divides by 100.
             */
            BigInteger encryptedBonus = encryptedSalary
                    .modPow(BigInteger.valueOf(25), publicKey.nSquared);

            encryptedBonuses.add(encryptedBonus.toString());
        }

        return encryptedBonuses;
    }

    public String salaryConvertedWithScaledRate(
            String fullNameIndex,
            int scaledExchangeRate,
            PaillierCrypto.PublicKey publicKey
    ) throws Exception {
        EmployeeEncrypted employee = searchByFullName(fullNameIndex);

        if (employee == null) {
            return null;
        }

        BigInteger encryptedSalary = new BigInteger(employee.salaryPaillier);

        /*
         * Example:
         * scaledExchangeRate = 110 means multiplying by 1.10.
         * Server computes Enc(salary * 110).
         * Client decrypts and divides by 100.
         */
        return encryptedSalary
                .modPow(BigInteger.valueOf(scaledExchangeRate), publicKey.nSquared)
                .toString();
    }
}
