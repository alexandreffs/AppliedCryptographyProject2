package pa2.client;

import pa2.model.EmployeePlain;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DatasetLoader {
    private DatasetLoader() {}

    /*
     * Correct mapping for the uploaded CSV:
     *
     * employeeID,First Name,Last Name,Full Name,DateofBirth,Age,Email,
     * Contact Phone Number,Personal Phone Number,JobTitle,DepartmentID,
     * HireDate,EmploiementType,Salary,SalaryBand,BonusEligibiity
     */
    public static List<EmployeePlain> loadCsv(Path path) throws Exception {
        List<EmployeePlain> employees = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return employees;
            }

            String[] headers = splitCsvLine(headerLine);
            Map<String, Integer> index = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                index.put(headers[i].trim(), i);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] c = splitCsvLine(line);

                EmployeePlain e = new EmployeePlain();
                e.employeeID = get(c, index, "employeeID");
                e.firstName = get(c, index, "First Name");
                e.lastName = get(c, index, "Last Name");
                e.fullName = get(c, index, "Full Name");
                e.dateOfBirth = get(c, index, "DateofBirth");
                e.age = Integer.parseInt(get(c, index, "Age"));
                e.email = get(c, index, "Email");
                e.contactPhoneNumber = get(c, index, "Contact Phone Number");
                e.personalPhoneNumber = get(c, index, "Personal Phone Number");
                e.jobTitle = get(c, index, "JobTitle");
                e.departmentID = get(c, index, "DepartmentID");
                e.hireDate = get(c, index, "HireDate");
                e.employmentType = get(c, index, "EmploiementType");
                e.salary = Long.parseLong(get(c, index, "Salary"));
                e.salaryBand = get(c, index, "SalaryBand");
                e.bonusEligibility = get(c, index, "BonusEligibiity");

                employees.add(e);
            }
        }

        return employees;
    }

    private static String get(String[] columns, Map<String, Integer> index, String name) {
        Integer i = index.get(name);
        if (i == null || i >= columns.length) {
            throw new IllegalArgumentException("Missing column in CSV: " + name);
        }
        return columns[i].trim();
    }

    /*
     * Small CSV splitter that supports quoted commas.
     * Your current dataset is simple, but this is safer than String.split(",").
     */
    private static String[] splitCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        values.add(current.toString());
        return values.toArray(new String[0]);
    }
}
