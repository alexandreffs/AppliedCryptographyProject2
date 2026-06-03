package pa2.config;

public final class DbConfig {
        private DbConfig() {
        }

        public static final String HOST = "localhost";
        public static final int PORT = 3306;
        public static final String DATABASE = "company_db";
        public static final String USER = "root";
        public static final String PASSWORD = "pa2password";

        public static final String JDBC_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
}
