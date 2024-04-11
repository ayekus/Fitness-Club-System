import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    // Database connection details
    private final String url = "jdbc:postgresql://localhost:5432/database_name";
    private final String user = "username";
    private final String password = "password";

    public static void main(String[] args) {
        Main database = new Main();

        try (Connection conn = DriverManager.getConnection(database.url, database.user, database.password)) {
             System.out.println("Connected");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }}
}
