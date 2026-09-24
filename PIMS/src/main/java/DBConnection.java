import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/healthfirst_pims?useSSL=false&serverTimezone=Africa/Johannesburg";

    private static final String USER = "root";

    private static final String PASSWORD = "tkays";

    public static Connection getConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );

            System.out.println("Database connected successfully!");

            return con;

        } catch (ClassNotFoundException e) {

            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Database connection failed.");
            e.printStackTrace();
        }

        return null;
    }
}