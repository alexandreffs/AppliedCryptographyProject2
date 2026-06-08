package pa2.client;

import pa2.crypto.ClientKeys;
import pa2.crypto.OPECrypto;
import pa2.model.EmployeeEncrypted;
import pa2.model.EmployeePlain;
import pa2.server.EmployeeRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Bootstrap {
    public static final Path DEFAULT_DATASET = Path.of("Dataset-Emp-Database.csv");
    public static final Path DEFAULT_KEYS = Path.of("client_keys/client.properties");

    public static void main(String[] args) throws Exception {
        Path datasetPath = args.length >= 1 ? Path.of(args[0]) : DEFAULT_DATASET;

        long t0 = System.nanoTime();

        ClientKeys keys = ClientKeys.loadOrCreate(DEFAULT_KEYS);
        List<EmployeePlain> plainEmployees = DatasetLoader.loadCsv(datasetPath);

        /*
         * One OPE instance for each ordered attribute.
         *
         * Salary and age must not share the same OPE index because they are
         * different plaintext domains.
         */
        OPECrypto salaryOpe = new OPECrypto();
        OPECrypto ageOpe = new OPECrypto();

        // insert values in sorted order to have correct ordering
        List<EmployeePlain> employeesSortedForOpe = new ArrayList<>(plainEmployees);
        employeesSortedForOpe.sort(
                Comparator.comparingLong((EmployeePlain e) -> e.salary)
                        .thenComparingInt(e -> e.age));

        for (EmployeePlain employee : employeesSortedForOpe) {
            salaryOpe.encrypt(Math.toIntExact(employee.salary));
            ageOpe.encrypt(employee.age);
        }

        long encryptStart = System.nanoTime();

        List<EmployeeEncrypted> encrypted = new ArrayList<>();

        for (EmployeePlain plain : plainEmployees) {
            encrypted.add(EmployeeEncryptor.encrypt(
                    plain,
                    keys,
                    salaryOpe,
                    ageOpe));
        }

        long encryptEnd = System.nanoTime();

        long encryptedTableSizeBytes;

        try (EmployeeRepository repository = new EmployeeRepository()) {
            repository.connect();
            repository.createTableIfNotExists();
            repository.clearTable();
            repository.insertAll(encrypted);
            encryptedTableSizeBytes = repository.getEncryptedTableSizeBytes();
        }

        long t1 = System.nanoTime();

        System.out.println("Bootstrap finished.");
        System.out.println("Plain records read: " + plainEmployees.size());
        System.out.println("Encrypted records uploaded to MySQL: " + encrypted.size());
        System.out.println("Encryption time ms: " + ((encryptEnd - encryptStart) / 1_000_000));
        System.out.println("Total bootstrap time ms: " + ((t1 - t0) / 1_000_000));
        System.out.println("Client keys saved/loaded at: " + DEFAULT_KEYS.toAbsolutePath());

        long plaintextSizeBytes = java.nio.file.Files.size(datasetPath);

        System.out.println("Plaintext CSV size bytes: " + plaintextSizeBytes);
        System.out.println("Encrypted MySQL table size bytes: " + encryptedTableSizeBytes);

        if (encryptedTableSizeBytes > 0) {
            double overhead = (double) encryptedTableSizeBytes / plaintextSizeBytes;
            System.out.printf("Storage overhead: %.2fx%n", overhead);
        }

        CryptoMetrics.print();
    }
}
