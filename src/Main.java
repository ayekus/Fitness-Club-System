import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        String configFilePath = "config.properties";
        FileInputStream propsInput = new FileInputStream(configFilePath);

        Properties prop = new Properties();
        prop.load(propsInput);

        Connection conn = DriverManager.getConnection(prop.getProperty("DB_URL"), prop.getProperty("DB_USER"), prop.getProperty("DB_PASSWORD"));
        System.out.println("Connected\n");

        prop.clear();

        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("""
                    Welcome to the Fitness Club
                    Please select an option:
                       1. Existing Member Sign-In
                       2. Trainer Sign-in
                       3. Admin Sign-in
                       4. Register new member
                       5. Exit""");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    memberSignIn(conn, scanner);
                    break;
                case "2":
                    trainerSignIn(conn, scanner);
                    break;
                case "3":
                    adminSignIn(conn, scanner);
                    break;
                case "4":
                    register(conn, scanner);
                    break;
                case "5":
                    conn.close();
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.print("Invalid choice, ");
                    break;
            }
            System.out.println("please try again.\n");
        }
    }

    public static void memberSignIn(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        PreparedStatement stmt = conn.prepareStatement("SELECT member_id, first_name FROM Members WHERE email = ? AND password = ?");
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("Logging in.\nWelcome " + rs.getString("first_name"));
            // Call something in member using rs.getInt("member_id") as init member id.
        } else {
            System.out.print("Invalid email and password combination, ");
            return;
        }

        rs.close();
        stmt.close();
    }

    public static void trainerSignIn(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        PreparedStatement stmt = conn.prepareStatement("SELECT trainer_id, first_name FROM Trainers WHERE email = ? AND password = ?");
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("Logging in.\nWelcome Trainer " + rs.getString("first_name"));
            Trainer.main(rs.getInt("trainer_id"), conn, scanner);
        } else {
            System.out.print("Invalid email and password combination, ");
            return;
        }

        rs.close();
        stmt.close();
    }

    public static void adminSignIn(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        PreparedStatement stmt = conn.prepareStatement("SELECT admin_id, first_name FROM Admin WHERE email = ? AND password = ?");
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("Logging in.\nWelcome Admin " + rs.getString("first_name"));
            Admin.main(rs.getInt("admin_id"), conn, scanner);
        } else {
            System.out.print("Invalid email and password combination, ");
            return;
        }

        rs.close();
        stmt.close();
    }

    public static void register(Connection conn, Scanner scanner) {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter First Name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("Enter Date of Birth: ");
        String dob = scanner.nextLine();

        System.out.print("Enter Height: ");
        String height = scanner.nextLine();

        System.out.print("Enter Weight: ");
        String weight = scanner.nextLine();

        System.out.print("Enter Fitness Goals: ");
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
            try {
                stmt.setDouble(7, Double.parseDouble(height));
                stmt.setDouble(8, Double.parseDouble(weight));
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number for height and weight, ");
                return;
            }
            stmt.setString(9, fitnessGoals);
            stmt.executeUpdate();
            System.out.println("You have registered successfully!");

            // Login to user
        } catch (SQLException e) {
            System.out.println("Error registering new member: " + e.getMessage());
        }
    }

}
