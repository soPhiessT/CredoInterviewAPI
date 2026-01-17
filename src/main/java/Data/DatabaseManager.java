package Data;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:test_results.db";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static volatile DatabaseManager instance;

    // SQL statements
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS test_results (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                test_name TEXT UNIQUE NOT NULL,
                status TEXT NOT NULL,
                execution_time DATETIME NOT NULL
            )
            """;

    private static final String SELECT_ALL_RESULTS = """
            SELECT * FROM test_results
            """;

    private static final String UPSERT_TEST_RESULT_SQL = """
            INSERT OR REPLACE INTO test_results (test_name, status, execution_time)
            VALUES (?, ?, ?)
            """;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(CREATE_TABLE_SQL);
            System.out.println("Database initialized successfully. Table 'test_results' is ready.");

        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    //უბრალოდ შესამოწმებლად, რომ ნამდვილად ჩაიწერა
    public void printResults() {
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()) {

            ResultSet result = statement.executeQuery(SELECT_ALL_RESULTS);

            while (result.next()) {
                System.out.println(
                        result.getInt("id") + " " +
                        result.getString("test_name") + " " +
                        result.getString("status") + " " +
                        result.getString("execution_time"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void saveTestResult(String testName, String status) {
        if (testName == null || testName.trim().isEmpty()) {
            throw new IllegalArgumentException("Test name cannot be null or empty");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        String currentTimestamp = LocalDateTime.now().format(DATETIME_FORMATTER);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPSERT_TEST_RESULT_SQL)) {

            pstmt.setString(1, testName);
            pstmt.setString(2, status);
            pstmt.setString(3, currentTimestamp);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.format("Test result saved: %s - %s at %s\n", testName, status, currentTimestamp);
            } else {
                System.out.format("No rows affected when saving test result: %s\n", testName);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save test result", e);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found. Ensure sqlite-jdbc dependency is included.", e);
        }

        Connection conn = DriverManager.getConnection(DATABASE_URL);
        System.out.println("Database connection established");
        return conn;
    }
}
