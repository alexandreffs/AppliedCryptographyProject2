package pa2.server;

import pa2.config.DbConfig;
import pa2.model.EmployeeEncrypted;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository implements AutoCloseable {
    private Connection connection;

    public void connect() throws Exception {
        connection = DriverManager.getConnection(DbConfig.JDBC_URL, DbConfig.USER, DbConfig.PASSWORD);
    }

    public void createTableIfNotExists() throws Exception {
        String sql = """
                CREATE TABLE IF NOT EXISTS employees_encrypted (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,

                    employee_id_enc TEXT NOT NULL,
                    employee_id_index VARCHAR(512) NOT NULL,

                    first_name_enc TEXT NOT NULL,
                    last_name_enc TEXT NOT NULL,

                    full_name_enc TEXT NOT NULL,
                    full_name_index VARCHAR(512) NOT NULL,

                    date_of_birth_enc TEXT NOT NULL,

                    age_enc TEXT NOT NULL,
                    age_ope BIGINT NOT NULL,

                    email_enc TEXT NOT NULL,
                    contact_phone_number_enc TEXT NOT NULL,
                    personal_phone_number_enc TEXT NOT NULL,

                    job_title_enc TEXT NOT NULL,

                    department_id_enc TEXT NOT NULL,
                    department_id_index VARCHAR(512) NOT NULL,

                    hire_date_enc TEXT NOT NULL,
                    employment_type_enc TEXT NOT NULL,

                    salary_enc TEXT NOT NULL,
                    salary_paillier TEXT NOT NULL,
                    salary_ope BIGINT NOT NULL,

                    salary_band_enc TEXT NOT NULL,

                    bonus_eligibility_enc TEXT NOT NULL,
                    bonus_eligibility_index VARCHAR(512) NOT NULL,

                    record_hmac TEXT NOT NULL,
                    record_signature TEXT NOT NULL,

                    INDEX idx_employee_id (employee_id_index),
                    INDEX idx_full_name (full_name_index),
                    INDEX idx_department_id (department_id_index),
                    INDEX idx_bonus_eligibility (bonus_eligibility_index),
                    INDEX idx_salary_ope (salary_ope),
                    INDEX idx_age_ope (age_ope)
                )
                """;

        try (Statement st = connection.createStatement()) {
            st.execute(sql);
        }
    }

    public void clearTable() throws Exception {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("DELETE FROM employees_encrypted");
        }
    }

    public void insertAll(List<EmployeeEncrypted> employees) throws Exception {
        for (EmployeeEncrypted e : employees) {
            insert(e);
        }
    }

    public void insert(EmployeeEncrypted e) throws Exception {
        String sql = """
                INSERT INTO employees_encrypted (
                    employee_id_enc, employee_id_index,
                    first_name_enc, last_name_enc,
                    full_name_enc, full_name_index,
                    date_of_birth_enc,
                    age_enc, age_ope,
                    email_enc, contact_phone_number_enc, personal_phone_number_enc,
                    job_title_enc,
                    department_id_enc, department_id_index,
                    hire_date_enc, employment_type_enc,
                    salary_enc, salary_paillier, salary_ope,
                    salary_band_enc,
                    bonus_eligibility_enc, bonus_eligibility_index,
                    record_hmac, record_signature
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, e.employeeIDEnc);
            ps.setString(i++, e.employeeIDIndex);
            ps.setString(i++, e.firstNameEnc);
            ps.setString(i++, e.lastNameEnc);
            ps.setString(i++, e.fullNameEnc);
            ps.setString(i++, e.fullNameIndex);
            ps.setString(i++, e.dateOfBirthEnc);
            ps.setString(i++, e.ageEnc);
            ps.setLong(i++, e.ageOpe);
            ps.setString(i++, e.emailEnc);
            ps.setString(i++, e.contactPhoneNumberEnc);
            ps.setString(i++, e.personalPhoneNumberEnc);
            ps.setString(i++, e.jobTitleEnc);
            ps.setString(i++, e.departmentIDEnc);
            ps.setString(i++, e.departmentIDIndex);
            ps.setString(i++, e.hireDateEnc);
            ps.setString(i++, e.employmentTypeEnc);
            ps.setString(i++, e.salaryEnc);
            ps.setString(i++, e.salaryPaillier);
            ps.setLong(i++, e.salaryOpe);
            ps.setString(i++, e.salaryBandEnc);
            ps.setString(i++, e.bonusEligibilityEnc);
            ps.setString(i++, e.bonusEligibilityIndex);
            ps.setString(i++, e.recordHmac);
            ps.setString(i++, e.recordSignature);
            ps.executeUpdate();
        }
    }

    public EmployeeEncrypted findOneByToken(String column, String token) throws Exception {
        String sql = "SELECT * FROM employees_encrypted WHERE " + checkedColumn(column) + " = ? LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<EmployeeEncrypted> findManyByToken(String column, String token) throws Exception {
        String sql = "SELECT * FROM employees_encrypted WHERE " + checkedColumn(column) + " = ?";
        List<EmployeeEncrypted> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        }

        return result;
    }

    public List<EmployeeEncrypted> findAllOrderedBy(String column, boolean descending) throws Exception {
        String sql = "SELECT * FROM employees_encrypted ORDER BY " + checkedColumn(column)
                + (descending ? " DESC" : " ASC");

        List<EmployeeEncrypted> result = new ArrayList<>();

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) result.add(map(rs));
        }

        return result;
    }

    private String checkedColumn(String column) {
        return switch (column) {
            case "employee_id_index", "full_name_index", "department_id_index",
                 "bonus_eligibility_index", "salary_ope", "age_ope" -> column;
            default -> throw new IllegalArgumentException("Unsafe or unsupported column: " + column);
        };
    }

    private EmployeeEncrypted map(ResultSet rs) throws Exception {
        EmployeeEncrypted e = new EmployeeEncrypted();

        e.dbId = rs.getLong("id");
        e.employeeIDEnc = rs.getString("employee_id_enc");
        e.employeeIDIndex = rs.getString("employee_id_index");
        e.firstNameEnc = rs.getString("first_name_enc");
        e.lastNameEnc = rs.getString("last_name_enc");
        e.fullNameEnc = rs.getString("full_name_enc");
        e.fullNameIndex = rs.getString("full_name_index");
        e.dateOfBirthEnc = rs.getString("date_of_birth_enc");
        e.ageEnc = rs.getString("age_enc");
        e.ageOpe = rs.getLong("age_ope");
        e.emailEnc = rs.getString("email_enc");
        e.contactPhoneNumberEnc = rs.getString("contact_phone_number_enc");
        e.personalPhoneNumberEnc = rs.getString("personal_phone_number_enc");
        e.jobTitleEnc = rs.getString("job_title_enc");
        e.departmentIDEnc = rs.getString("department_id_enc");
        e.departmentIDIndex = rs.getString("department_id_index");
        e.hireDateEnc = rs.getString("hire_date_enc");
        e.employmentTypeEnc = rs.getString("employment_type_enc");
        e.salaryEnc = rs.getString("salary_enc");
        e.salaryPaillier = rs.getString("salary_paillier");
        e.salaryOpe = rs.getLong("salary_ope");
        e.salaryBandEnc = rs.getString("salary_band_enc");
        e.bonusEligibilityEnc = rs.getString("bonus_eligibility_enc");
        e.bonusEligibilityIndex = rs.getString("bonus_eligibility_index");
        e.recordHmac = rs.getString("record_hmac");
        e.recordSignature = rs.getString("record_signature");

        return e;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) connection.close();
    }
}
