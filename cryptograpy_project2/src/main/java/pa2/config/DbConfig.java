package pa2.config;

public final class DbConfig {
    private DbConfig() {}

    public static final String HOST = "localhost";
    public static final int PORT = 3306;
    public static final String DATABASE = "company_db";
    public static final String USER = "root";

    /*
     * Change this to your MySQL password.
     * This matches the teacher's script idea:
     * host=localhost, user=root, password=your_password, database=company_db.
     */
    public static final String PASSWORD = "your_password";

    public static final String JDBC_URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
}
