// DBConnection.java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // !!! 安全警告：不應在生產環境中硬編碼這些信息 !!!
    private static final String DB_URL = "jdbc:mysql://140.119.19.73:3315/TG06?useSSL=false";
    private static final String USER = "TG06";
    private static final String PASS = "bMIEqf";

    public static Connection getConnection() throws SQLException {
        try {
            // 確保 MySQL JDBC 驅動已加載 (對於較新的 JDBC 版本，這步可能不是必需的)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void main(String[] args) {
        // 測試連接
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Successfully connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}