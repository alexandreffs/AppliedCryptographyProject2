package pa2.client;

import pa2.crypto.ClientKeys;
import pa2.crypto.HMACIndex;
import pa2.crypto.PaillierCrypto;
import pa2.model.EmployeeDecrypted;
import pa2.model.EmployeeEncrypted;
import pa2.server.QueryService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class QueryClient {
    private final QueryService server;
    private final ClientKeys keys;

    public QueryClient(QueryService server, ClientKeys keys) {
        this.server = server;
        this.keys = keys;
    }

    public EmployeeDecrypted searchByEmployeeID(String employeeID) throws Exception {
        String token = HMACIndex.token(employeeID, keys.indexKey);
        EmployeeEncrypted encrypted = server.searchByEmployeeID(token);
        return encrypted == null ? null : EmployeeEncryptor.decryptAndVerify(encrypted, keys);
    }

    public EmployeeDecrypted searchByFullName(String fullName) throws Exception {
        String token = HMACIndex.token(fullName, keys.indexKey);
        EmployeeEncrypted encrypted = server.searchByFullName(token);
        return encrypted == null ? null : EmployeeEncryptor.decryptAndVerify(encrypted, keys);
    }

    public List<EmployeeDecrypted> searchByDepartmentID(String departmentID) throws Exception {
        String token = HMACIndex.token(departmentID, keys.indexKey);
        return decryptList(server.searchByDepartmentID(token));
    }

    public List<EmployeeDecrypted> employeesOrderedBySalary() throws Exception {
        return decryptList(server.employeesOrderedBySalary());
    }

    public EmployeeDecrypted employeeWithHighestSalary() throws Exception {
        EmployeeEncrypted encrypted = server.employeeWithHighestSalary();
        return encrypted == null ? null : EmployeeEncryptor.decryptAndVerify(encrypted, keys);
    }

    public boolean hasHigherSalary(String fullNameA, String fullNameB) throws Exception {
        String tokenA = HMACIndex.token(fullNameA, keys.indexKey);
        String tokenB = HMACIndex.token(fullNameB, keys.indexKey);
        return server.hasHigherSalary(tokenA, tokenB);
    }

    public List<EmployeeDecrypted> employeesOrderedByAge() throws Exception {
        return decryptList(server.employeesOrderedByAge());
    }

    public EmployeeDecrypted oldestEmployee() throws Exception {
        EmployeeEncrypted encrypted = server.oldestEmployee();
        return encrypted == null ? null : EmployeeEncryptor.decryptAndVerify(encrypted, keys);
    }

    public BigInteger departmentPayrollSum(String departmentID) throws Exception {
        String token = HMACIndex.token(departmentID, keys.indexKey);
        String encryptedSum = server.departmentPayrollSum(token, keys.paillierPublicKey);
        if (encryptedSum == null) return BigInteger.ZERO;

        PaillierCrypto paillier = new PaillierCrypto();
        return paillier.decrypt(new BigInteger(encryptedSum), keys.paillierPublicKey, keys.paillierPrivateKey);
    }

    public List<BigInteger> bonusesForEligibleEmployees() throws Exception {
        String yesToken = HMACIndex.token("Yes", keys.indexKey);
        List<String> encryptedBonuses = server.bonusesForEligibleEmployees(yesToken, keys.paillierPublicKey);

        PaillierCrypto paillier = new PaillierCrypto();
        List<BigInteger> bonuses = new ArrayList<>();

        for (String encrypted : encryptedBonuses) {
            /*
             * Server returns Enc(salary * 25).
             * Client divides by 100 after decryption.
             */
            BigInteger scaled = paillier.decrypt(new BigInteger(encrypted), keys.paillierPublicKey, keys.paillierPrivateKey);
            bonuses.add(scaled.divide(BigInteger.valueOf(100)));
        }

        return bonuses;
    }

    public BigInteger salaryInUsd(String fullName, int scaledExchangeRate) throws Exception {
        String token = HMACIndex.token(fullName, keys.indexKey);
        String encrypted = server.salaryConvertedWithScaledRate(token, scaledExchangeRate, keys.paillierPublicKey);

        if (encrypted == null) return null;

        PaillierCrypto paillier = new PaillierCrypto();
        BigInteger scaled = paillier.decrypt(new BigInteger(encrypted), keys.paillierPublicKey, keys.paillierPrivateKey);

        /*
         * Example:
         * scaledExchangeRate = 110 means 1.10 USD.
         * Decrypted value is salary * 110, so divide by 100.
         */
        return scaled.divide(BigInteger.valueOf(100));
    }

    private List<EmployeeDecrypted> decryptList(List<EmployeeEncrypted> encryptedList) throws Exception {
        List<EmployeeDecrypted> out = new ArrayList<>();
        for (EmployeeEncrypted encrypted : encryptedList) {
            out.add(EmployeeEncryptor.decryptAndVerify(encrypted, keys));
        }
        return out;
    }
}
