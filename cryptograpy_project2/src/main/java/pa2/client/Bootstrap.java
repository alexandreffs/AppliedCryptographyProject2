package pa2.client;

import pa2.crypto.ClientKeys;
import pa2.model.EmployeeEncrypted;
import pa2.model.EmployeePlain;
import pa2.server.EmployeeRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Bootstrap {
    public static final Path DEFAULT_DATASET = Path.of("Dataset-Emp-Database.csv");
    public static final Path DEFAULT_KEYS = Path.of("client_keys/client.properties");

    public static void main(String[] args) throws Exception {
        Path datasetPath = args.length >= 1 ? Path.of(args[0]) : DEFAULT_DATASET;

        long t0 = System.nanoTime();

        ClientKeys keys = ClientKeys.loadOrCreate(DEFAULT_KEYS);
        List<EmployeePlain> plainEmployees = DatasetLoader.loadCsv(datasetPath);

        long encryptStart = System.nanoTime();

        List<EmployeeEncrypted> encrypted = new ArrayList<>();
        for (EmployeePlain plain : plainEmployees) {
            encrypted.add(EmployeeEncryptor.encrypt(plain, keys));
        }

        long encryptEnd = System.nanoTime();

        try (EmployeeRepository repository = new EmployeeRepository()) {
            repository.connect();
            repository.createTableIfNotExists();
            repository.clearTable();
            repository.insertAll(encrypted);
        }

        long t1 = System.nanoTime();

        System.out.println("Bootstrap finished.");
        System.out.println("Plain records read: " + plainEmployees.size());
        System.out.println("Encrypted records uploaded to MySQL: " + encrypted.size());
        System.out.println("Encryption time ms: " + ((encryptEnd - encryptStart) / 1_000_000));
        System.out.println("Total bootstrap time ms: " + ((t1 - t0) / 1_000_000));
        System.out.println("Client keys saved/loaded at: " + DEFAULT_KEYS.toAbsolutePath());
    }
}
