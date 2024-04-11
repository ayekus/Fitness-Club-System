import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class Member {

    private int memberId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private double height;
    private double weight;
    private String fitnessGoal;

    private static final String url = "jdbc:postgresql://localhost:5432/database_name";
    private static final String user = "username";
    private static final String password = "password";

    public Member(int memberId, String firstName, String lastName, String email, String phone, Date dateOfBirth, double height, double weight, String fitnessGoal) {
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;
        this.fitnessGoal = fitnessGoal;
    }

    public void updateProfile(Connection conn) throws SQLException {
        String SQL = "UPDATE members SET first_name = ?, last_name = ?, email = ?, phone = ?, date_of_birth = ?, height = ?, weight = ?, fitness_goal = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, this.firstName);
            pstmt.setString(2, this.lastName);
            pstmt.setString(3, this.email);
            pstmt.setString(4, this.phone);
            pstmt.setDate(5, new java.sql.Date(this.dateOfBirth.getTime()));
            pstmt.setDouble(6, this.height);
            pstmt.setDouble(7, this.weight);
            pstmt.setString(8, this.fitnessGoal);
            pstmt.setInt(9, this.memberId);

            pstmt.executeUpdate();
            System.out.println("Profile updated successfully!");
        }
    }

    public void displayDashboard() {
        System.out.println("Exercise Routines:");
        System.out.println("Fitness Achievements:");
        System.out.println("Health Statistics:");
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Scanner scanner = new Scanner(System.in);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Assume getting member from database
            Member member = new Member(1, "John", "Doe", "john.doe@example.com", "123-456-7890", new Date(), 175.5, 85.5, "Lose weight");

            while (true) {
                System.out.println("Hello, " + member.firstName + "! What would you like to update today?");
                System.out.println("1. First Name");
                System.out.println("2. Last Name");
                System.out.println("3. Email");
                System.out.println("4. Phone");
                System.out.println("5. Date of Birth");
                System.out.println("6. Height");
                System.out.println("7. Weight");
                System.out.println("8. Fitness Goal");
                System.out.println("9. Display Dashboard");
                System.out.println("0. Exit");
                System.out.println("Enter your choice or multiple choices separated by commas (e.g., 6,7 for height and weight):");

                String choice = scanner.nextLine();

                if ("0".equals(choice)) {
                    System.out.println("Exiting...");
                    break;
                } else if ("9".equals(choice)) {
                    member.displayDashboard();
                    continue;
                }

                String[] updates = choice.split(",");
                for (String update : updates) {
                    switch (update.trim()) {
                        case "1":
                            System.out.println("Enter new first name:");
                            member.firstName = scanner.nextLine();
                            break;
                        case "2":
                            System.out.println("Enter new last name:");
                            member.lastName = scanner.nextLine();
                            break;
                        case "3":
                            System.out.println("Enter new email:");
                            member.email = scanner.nextLine();
                            break;
                        case "4":
                            System.out.println("Enter new phone:");
                            member.phone = scanner.nextLine();
                            break;
                        case "5":
                            System.out.println("Enter new date of birth (yyyy-MM-dd):");
                            member.dateOfBirth = dateFormat.parse(scanner.nextLine());
                            break;
                        case "6":
                            System.out.println("Enter new height:");
                            member.height = Double.parseDouble(scanner.nextLine());
                            break;
                        case "7":
                            System.out.println("Enter new weight:");
                            member.weight = Double.parseDouble(scanner.nextLine());
                            break;
                        case "8":
                            System.out.println("Enter new fitness goal:");
                            member.fitnessGoal = scanner.nextLine();
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter a valid number.");
                            break;
                    }
                }

                member.updateProfile(conn);
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
