import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        String configFilePath = "config.properties";
        FileInputStream propsInput = new FileInputStream(configFilePath);

        Properties prop = new Properties();
        prop.load(propsInput);

        Connection conn = DriverManager.getConnection(prop.getProperty("DB_URL"), prop.getProperty("DB_USER"), prop.getProperty("DB_PASSWORD"));
        System.out.println("Connected");

        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("Welcome to the Fitness Club");
            System.out.println("1. Existing Member Sign-In");
            System.out.println("2. Trainer Sign-in");
            System.out.println("3. Admin Sign-in");
            System.out.println("4. Register new member");
            System.out.println("Enter your choice: ");

            String choice = scanner.nextLine();

            System.out.println("Enter email: ");
            String email = scanner.nextLine();
            System.out.println("Enter password: ");
            String password = scanner.nextLine();

            switch (choice) {
                case "1":
                    memberSignIn(conn, scanner, email, password);
                    break;
                case "2":
                    trainerSignIn(conn, scanner, email, password);
                    break;
                case "3":
                    adminSignIn(conn, scanner, email, password);
                    break;
                case "4":
                    register(conn, scanner, email, password);
                    break;
                default:
                    System.out.println("Invalid choice\n");
                    break;
            }
        }
    }

    public static void memberSignIn(Connection conn, Scanner scanner, String email, String password) {
        // Implement member sign-in logic
    }

    public static void trainerSignIn(Connection conn, Scanner scanner, String email, String password) {
        // Implement trainer sign-in logic
    }

    public static void adminSignIn(Connection conn, Scanner scanner, String email, String password) {
        // Implement admin sign-in logic
    }

    public static void register(Connection conn, Scanner scanner, String email, String password) {
        System.out.println("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.println("Enter First Name: ");
        String lastName = scanner.nextLine();

        System.out.println("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();

        System.out.println("Enter Date of Birth: ");
        String dob = scanner.nextLine();

        System.out.println("Enter Height: ");
        String height = scanner.nextLine();

        System.out.println("Enter Weight: ");
        String weight = scanner.nextLine();

        System.out.println("Enter Fitness Goals: ");
        String fitnessGoals = scanner.nextLine();

        try {
            String sql = "INSERT INTO Members (first_name, lastName, email, password, phone, date_of_birth, height, weight, fitness_goal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setString(5, phoneNumber);
            stmt.setString(6, dob);
            stmt.setDouble(7, Double.parseDouble(height));
            stmt.setDouble(8, Double.parseDouble(weight));
            stmt.setString(9, fitnessGoals);
            stmt.executeUpdate();
            System.out.println("New member registered successfully!");
        } catch (SQLException e) {
            System.out.println("Error registering new member: " + e.getMessage());
        }
    }

}
