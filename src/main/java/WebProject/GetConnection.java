package WebProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GetConnection {

    private static final String URL = "jdbc:mysql://localhost:4306/hospital?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root@123";

    // ✅ Always load driver and return new connection
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");  // load MySQL driver
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
