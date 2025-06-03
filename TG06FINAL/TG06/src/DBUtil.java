
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://140.119.19.73:3315/TG06" + "?useSSL=false";
    private static final String USER = "TG06";
    private static final String PASSWORD = "bMIEqf";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

