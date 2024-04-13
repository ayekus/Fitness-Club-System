import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

public class Trainer {

    public static void main(int trainerId, Connection conn, Scanner scanner) throws SQLException {
        while(true) {
                System.out.println("""
                        Please select an option:
                           1. Set Availability
                           2. Show Schedule
                           3. View Member Profile
                           4. Leave System""");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addAvailability(trainerId, conn, scanner);
                    break;
                case "2":
                    displaySchedule(trainerId, conn);
                    break;
                case "3":
                    viewMemberProfile(conn, scanner);
                    break;
                case "4":
                    System.out.println("Exiting");
                    conn.close();
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        }
    }

    public static void addAvailability(int trainerId, Connection conn, Scanner scanner) {
        System.out.println("Enter date and time for availability (format: yyyy-mm-dd):");
        String dateString = scanner.nextLine().trim();
        try {
            // add to database
            System.out.println("Added new availability at: " + dateString);
        } catch (Exception e) {
            System.out.println("Invalid date format.");
        }
    }

    public static void displaySchedule(int trainerId, Connection conn) {
        // get values from database

//        System.out.println("Current schedule for " + firstName + " " + lastName + ":");
//        for (Date time : availableTimes) {
//            System.out.println(time);
//        }
    }

    public static void viewMemberProfile(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter member first name (case sensitive):");
        String firstName = scanner.nextLine().trim();

        System.out.println("Enter member last name (case sensitive):");
        String lastName = scanner.nextLine().trim();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Members WHERE first_name = ? AND last_name = ?");
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);

        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            System.out.println("Member " + firstName + " " + lastName + " not found.");
            System.out.println("Returning to menu.\n");
            return;
        }

        System.out.println("Member " + lastName + ", " + firstName + "'s profile:\n");

        System.out.println("Member ID: " + rs.getInt("member_id"));
        System.out.println("Email: " + rs.getString("email"));
        System.out.println("Phone: " + rs.getString("phone"));
        System.out.println("Date of Birth: " + rs.getDate("date_of_birth"));
        System.out.println("Height: " + rs.getDouble("height"));
        System.out.println("Weight: " + rs.getDouble("weight"));
        System.out.println("Fitness Goal: " + rs.getString("fitness_goal"));
        System.out.println("Join Date: " + rs.getDate("join_date"));

        System.out.println("Returning to menu.\n");
    }

}
