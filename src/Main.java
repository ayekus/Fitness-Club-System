import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, SQLException, ParseException {
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

            String choice = scanner.nextLine().trim();

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

    public static void memberSignIn(Connection conn, Scanner scanner) throws SQLException, ParseException {
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        PreparedStatement stmt = conn.prepareStatement("SELECT member_id, first_name FROM Members WHERE email = ? AND password = ?");
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("Logging in.\nWelcome " + rs.getString("first_name") + "\n");
            Member.main(rs.getInt("member_id"), conn, scanner);
        } else {
            System.out.print("Invalid email and password combination, ");
            return;
        }

        rs.close();
        stmt.close();
    }

    public static void trainerSignIn(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        PreparedStatement stmt = conn.prepareStatement("SELECT trainer_id, first_name FROM Trainers WHERE email = ? AND password = ?");
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("Logging in.\nWelcome Trainer " + rs.getString("first_name") + "\n");
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
        String email = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        PreparedStatement stmt = conn.prepareStatement("SELECT admin_id, first_name FROM Admin WHERE email = ? AND password = ?");
        stmt.setString(1, email);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            System.out.println("Logging in.\nWelcome Admin " + rs.getString("first_name") + "\n");
            Admin.main(rs.getInt("admin_id"), conn, scanner);
        } else {
            System.out.print("Invalid email and password combination, ");
            return;
        }

        rs.close();
        stmt.close();
    }

    public static void register(Connection conn, Scanner scanner) throws ParseException {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();

        if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
            System.out.print("Invalid Email format, please try again: ");
            email = scanner.nextLine().trim();

            if (!email.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")) {
                System.out.println("Invalid Email format.");
                return;
            }
        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter Phone Number: ");
        String phoneNumber = scanner.nextLine().trim();

        if (!phoneNumber.matches("^\\d{3}-\\d{3}-\\d{4}$")) {
            System.out.print("Invalid Phone Number, please try again: ");
            phoneNumber = scanner.nextLine().trim();

            if (!phoneNumber.matches("^\\d{3}-\\d{3}-\\d{4}$")) {
                System.out.println("Invalid Phone Number.");
                return;
            }
        }

        System.out.print("Enter Date of Birth (yyyy-mm-dd): ");
        String dob = scanner.nextLine().trim();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dobDate;
        try {
            dobDate = new Date(sdf.parse(dob).getTime());
        } catch (ParseException e) {
            System.out.println("Invalid Date of Birth format. Please enter date in yyyy-mm-dd format.");
            return;
        }

        System.out.print("Enter Height: ");
        String height = scanner.nextLine().trim();

        System.out.print("Enter Weight: ");
        String weight = scanner.nextLine().trim();

        System.out.print("Enter Fitness Goals: ");
        String fitnessGoals = scanner.nextLine().trim();

        try {
            String sql = "INSERT INTO Members (first_name, last_name, email, password, phone, date_of_birth, height, weight, fitness_goal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setString(5, phoneNumber);
            stmt.setDate(6, dobDate);
            try {
                stmt.setDouble(7, Double.parseDouble(height));
                stmt.setDouble(8, Double.parseDouble(weight));
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number for height and weight, ");
                return;
            }
            stmt.setString(9, fitnessGoals);
            stmt.executeUpdate();

            int memberId = -1;
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                memberId = generatedKeys.getInt(1);
            }

            generatedKeys.close();
            stmt.close();

            if (memberId == -1) {
                System.out.println("Error registering, please try again.");
                return;
            }

            generatedKeys.close();
            stmt.close();

            System.out.println("You have registered successfully!");

            if (!processPayment(conn, scanner, 5, memberId, "Registration Fee")) {
                System.out.println("Payment failed. Please try again. ");
                return;
            }

            System.out.println("\nLogging in.\nWelcome " + firstName + "\n");
            Member.main(memberId, conn, scanner);

        } catch (SQLException e) {
            System.out.println("Error registering new member: " + e.getMessage());
        }
    }

    public static boolean processPayment(Connection conn, Scanner scanner, Integer amount, Integer memberId, String description) throws SQLException {
        System.out.print("You will be charged $" + amount + ", do you consent? (y/n): ");
        String consent = scanner.nextLine().trim();
        if (consent.equals("y") || consent.equals("yes")) {

            String query = "INSERT INTO Payments (member_id, amount, payment_desc) VALUES (?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, memberId);
            stmt.setInt(2, amount);
            stmt.setString(3, description);
            stmt.executeUpdate();

            System.out.println("Payment successful and sent for Admin processing.");
            return true;
        }

        return false;
    }

}
