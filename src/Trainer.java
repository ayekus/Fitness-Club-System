import java.sql.*;
import java.util.Scanner;

public class Trainer {

    public static void main(int trainerId, Connection conn, Scanner scanner) throws SQLException {
        while(true) {
                System.out.println("""
                        Please select an option:
                           1. Change Availability
                           2. View Schedule
                           3. View Member Profile
                           4. Leave System""");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    changeAvailability(trainerId, conn, scanner);
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

    public static void changeAvailability(int trainerId, Connection conn, Scanner scanner) throws SQLException {
        String query = "SELECT * FROM TrainerAvailability WHERE trainer_id = ? ORDER BY start_time";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, trainerId);
        ResultSet rs = stmt.executeQuery();


        if (!rs.isBeforeFirst()) {
            System.out.println("\nYou currently have no availability. ");
        } else {
            System.out.println("\nCurrent Availability:");
        }
        int count = 0;
        while (rs.next()) {
            System.out.println("Availability " + ++count + ": ");
            System.out.print(rs.getBoolean("is_group_availability") ? (rs.getString("session_name") + "\n") : "");
            System.out.println(rs.getTime("start_time") + " - " + rs.getTime("end_time") +
                    " (Type: " + (rs.getBoolean("is_group_availability") ? "Group" : "One-on-One") + ")\n");
        }

        System.out.print("Would you like to change your availability (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            while (true) {
                System.out.println("""
                    Please select an option:
                       1. Add a time slot
                       2. Remove a time slot""");
                System.out.print("Enter your choice: ");

                choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        addAvailability(trainerId, conn, scanner);
                        return;
                    case "2":
                        removeAvailability(trainerId, conn, scanner);
                        return;
                    default:
                        System.out.println("Invalid choice, please try again.\n");
                        break;
                }
            }
        }

        System.out.println("Returning back to main menu.\n");
    }

    public static void addAvailability(int trainerId, Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter start time of availability to add (HH:mm:ss): ");
        String startTime = scanner.nextLine().trim();
        System.out.print("Enter end time of availability to add (HH:mm:ss): ");
        String endTime = scanner.nextLine().trim();

        System.out.print("Is this availability for a group session? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        boolean isGroupAvailability = input.equals("y") || input.equals("yes");

        String sessionName = null;
        if (isGroupAvailability) {
            System.out.print("What do you want to name the session: ");
            sessionName = scanner.nextLine().trim();
        }

        String query = "INSERT INTO TrainerAvailability (trainer_id, start_time, end_time, is_group_availability, session_name) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, trainerId);
        stmt.setTime(2, Time.valueOf(startTime));
        stmt.setTime(3, Time.valueOf(endTime));
        stmt.setBoolean(4, isGroupAvailability);
        stmt.setString(5, sessionName);

        stmt.executeUpdate();
        stmt.close();

        System.out.println("Availability added successfully.");

        changeAvailability(trainerId, conn, scanner);
    }

    public static void removeAvailability(int trainerId, Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the start time of availability to remove (HH:mm:ss): ");
        String startTime = scanner.nextLine().trim();
        System.out.print("Enter the end time of availability to remove (HH:mm:ss): ");
        String endTime = scanner.nextLine().trim();

        String query = "DELETE FROM TrainerAvailability WHERE trainer_id = ? AND start_time = ? AND end_time = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, trainerId);
        stmt.setTime(2, Time.valueOf(startTime));
        stmt.setTime(3, Time.valueOf(endTime));

        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Availability removed successfully.");
            System.out.println("Please note, pre-booked sessions in that time slot will still be kept.");
        } else {
            System.out.println("No matching availability found to remove.");
        }

        stmt.close();

        changeAvailability(trainerId, conn, scanner);
    }

    public static void displaySchedule(int trainerId, Connection conn) throws SQLException {
        String trainingQuery = "SELECT * FROM TrainingSession WHERE trainer_id = ?";
        PreparedStatement trainingStmt = conn.prepareStatement(trainingQuery);
        trainingStmt.setInt(1, trainerId);
        ResultSet trainingRs = trainingStmt.executeQuery();

        System.out.println("One-on-one Session Schedule:");
        while (trainingRs.next()) {
            System.out.println("Session Name: " + trainingRs.getString("session_name"));
            System.out.println("Session Date: " + trainingRs.getDate("session_date"));
            System.out.println("Start Time: " + trainingRs.getTime("start_time"));
            System.out.println("End Time: " + trainingRs.getTime("end_time"));
            System.out.println("Room ID: " + trainingRs.getInt("room_id"));
            System.out.println();
        }

        String groupQuery = "SELECT * FROM GroupSession WHERE trainer_id = ?";
        PreparedStatement groupStmt = conn.prepareStatement(groupQuery);
        groupStmt.setInt(1, trainerId);
        ResultSet groupRs = groupStmt.executeQuery();

        System.out.println("Group Session Schedule:");
        while (groupRs.next()) {
            System.out.println("Session ID: " + groupRs.getInt("group_session_id"));
            System.out.println("Session Name: " + groupRs.getString("session_name"));
            System.out.println("Session Date: " + groupRs.getDate("session_date"));
            System.out.println("Start Time: " + groupRs.getTime("start_time"));
            System.out.println("End Time: " + groupRs.getTime("end_time"));
            System.out.println("Room ID: " + groupRs.getInt("room_id"));
            System.out.println();
        }

        trainingRs.close();
        trainingStmt.close();
        groupRs.close();
        groupStmt.close();
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
