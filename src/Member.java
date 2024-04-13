import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;

public class Member {

    public static void main(int memberId, Connection conn, Scanner scanner) throws SQLException, ParseException {
        while (true) {
            System.out.println("""
                    Please select an option:
                       1. View Profile
                       2. Display Dashboard
                       3. Schedule Management
                       4. Leave System""");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewProfile(memberId, conn, scanner);
                    break;
                case "2":
                    displayDashboard(memberId, conn, scanner);
                    break;
                case "3":
                    viewSchedule(memberId, conn, scanner);
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

    public static void viewSchedule(Integer memberId, Connection conn, Scanner scanner) throws SQLException, ParseException {
        String query = ("""
                SELECT ts.session_name, ts.session_date, ts.start_time, ts.end_time, t.first_name AS trainer_first_name, t.last_name AS trainer_last_name
                FROM TrainingSession ts
                INNER JOIN Trainers t ON ts.trainer_id = t.trainer_id
                WHERE ts.member_id = ?""");
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, memberId);
        ResultSet rs = stmt.executeQuery();

        System.out.println("\nHere is your current Schedule: ");
        System.out.println("One-on-One Sessions:");

        if (!rs.next()) {
            System.out.println("You are not enrolled in any upcoming One-on-One Sessions.");
        }
        while (rs.next()) {
            System.out.println(rs.getString("session_name") + " session on " +
                    rs.getDate("session_date") + " from " + rs.getTime("start_time") +
                    " to " + rs.getTime("end_time") + " with Trainer " +
                    rs.getString("trainer_first_name") + " " + rs.getString("trainer_last_name") + "\n");
        }

        query = ("""
                SELECT gs.session_name, gs.session_date, gs.start_time, gs.end_time, t.first_name AS trainer_first_name, t.last_name AS trainer_last_name
                FROM GroupSession gs
                INNER JOIN GroupSessionEnrollment ge ON gs.session_id = ge.session_id
                INNER JOIN Trainers t ON gs.trainer_id = t.trainer_id
                WHERE ge.member_id = ?""");
        stmt = conn.prepareStatement(query);
        stmt.setInt(1, memberId);
        rs = stmt.executeQuery();

        System.out.println("\nGroup Sessions:");
        if (!rs.next()) {
            System.out.println("You are not enrolled in any upcoming Group Sessions.\n");
        }
        while (rs.next()) {
            System.out.println(rs.getString("session_name") + " session on " +
                    rs.getDate("session_date") + " from " + rs.getTime("start_time") +
                    " to " + rs.getTime("end_time") + " with Trainer " +
                    rs.getString("trainer_first_name") + " " + rs.getString("trainer_last_name") + "\n");
        }

        rs.close();
        stmt.close();

//        System.out.print("Would you like to add a session (y/n): ");
//        String choice = scanner.nextLine().trim().toLowerCase();
//
//        if (choice.equals("y") || choice.equals("yes")) {
//            return;
//        }

        System.out.println("Returning back to main menu.\n");
    }

    public static void viewProfile(Integer memberId, Connection conn, Scanner scanner) throws SQLException, ParseException {
        String[] profile = new String[9];
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Members WHERE member_id = ?");
        stmt.setInt(1, memberId);

        ResultSet rs = stmt.executeQuery();
        rs.next();

        System.out.println("Here is your information: ");

        for (int i = 0; i < 8; i++) {
            System.out.println(rs.getString(i + 2));
            if (i != 5) {
                profile[i] = rs.getString(i + 2);
                continue;
            }
            profile[i] = rs.getDate(i + 2).toString();
        }

        rs.close();
        stmt.close();

        System.out.print("Would you like to update your information (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            updateProfile(memberId, conn, scanner, profile);
            return;
        }

        System.out.println("Returning back to main menu.\n");
    }

    public static void updateProfile(Integer memberId, Connection conn, Scanner scanner, String[] profile) throws SQLException, ParseException {
        System.out.println("""
                What would you like to update today?
                   1. First Name
                   2. Last Name
                   3. Email
                   4. Password
                   5. Phone
                   6. Date of Birth
                   7. Height
                   8. Weight
                   9. Fitness Goal""");
        System.out.print("Enter your choice or multiple choices separated by commas (e.g., 6, 7 for height and weight): ");

        String choices = scanner.nextLine().trim();
        String[] updates = choices.split(",");
        java.sql.Date dobDate = null;

        for (String update : updates) {
            int val = Integer.parseInt(update.trim());
            switch (val) {
                case 1:
                    System.out.print("Enter new first name:");
                    profile[val - 1] = scanner.nextLine().trim();
                    break;
                case 2:
                    System.out.print("Enter new last name:");
                    profile[val - 1] = scanner.nextLine().trim();
                    break;
                case 3:
                    System.out.print("Enter new email:");
                    profile[val - 1] = scanner.nextLine().trim();
                    break;
                case 4:
                    System.out.print("Enter new password:");
                    profile[val - 1] = scanner.nextLine().trim();
                    break;
                case 5:
                    System.out.println("Enter new phone:");
                    profile[val - 1] = scanner.nextLine().trim();
                    break;
                case 6:
                    System.out.println("Enter new date of birth (yyyy-mm-dd):");
                    profile[val - 1] = scanner.nextLine().trim();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        dobDate = new Date(sdf.parse(profile[val - 1].trim()).getTime());
                    } catch (ParseException e) {
                        System.out.println("Invalid Date of Birth format. Please try again later.");
                        return;
                    }
                    break;
                case 7:
                    System.out.println("Enter new height:");
                    profile[val - 1] = scanner.nextLine().trim();
                    break;
                case 8:
                    System.out.println("Enter new weight:");
                    profile[val - 1] = scanner.nextLine().trim();
                    break;
                case 9:
                    System.out.println("Enter new fitness goal:");
                    profile[val - 1] = scanner.nextLine().trim();
                    break;
                default:
                    System.out.println(val + " is an invalid choice.");
                    break;
            }
        }

        if (dobDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dobDate = new Date(sdf.parse(profile[5].trim()).getTime());
        }

        String SQL = "UPDATE members SET first_name = ?, last_name = ?, email = ?, password = ?, phone = ?, date_of_birth = ?, height = ?, weight = ?, fitness_goal = ? WHERE member_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, profile[0]);
        pstmt.setString(2, profile[1]);
        pstmt.setString(3, profile[2]);
        pstmt.setString(4, profile[3]);
        pstmt.setString(5, profile[4]);
        pstmt.setDate(6, dobDate);

        try {
            pstmt.setDouble(7, Double.parseDouble(profile[6]));
            pstmt.setDouble(8, Double.parseDouble(profile[7]));
        } catch (NumberFormatException e) {
            System.out.print("Enter a valid number for height and weight, please try again. ");
            viewProfile(memberId, conn, scanner);
            return;
        }

        pstmt.setString(9, profile[8]);
        pstmt.setInt(10, memberId);

        pstmt.executeUpdate();
        System.out.println("Profile updated successfully!\n");

        viewProfile(memberId, conn, scanner);
    }

    public static void displayDashboard(Integer memberId, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Dashboard:");
        String[] info = new String[3];

        String sql = "SELECT exercise_routines, fitness_achievements, health_stats FROM members WHERE member_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, memberId);

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            info[0] = rs.getString("exercise_routines");
            info[1] = rs.getString("fitness_achievements");
            info[2] = rs.getString("health_stats");
        }

        if (info[0] == null) {
            System.out.println("No exercise routines found.");
        } else {
            System.out.println("Exercise Routines: " + info[0]);
        }

        if (info[1] == null) {
            System.out.println("No fitness achievements found.");
        } else {
            System.out.println("Fitness Achievements: " + info[1]);
        }

        if (info[2] == null) {
            System.out.println("No health statistics found.");
        } else {
            System.out.println("Health Statistics: " + info[2]);
        }

        System.out.print("Would you like to update any of the information (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            updateDashboard(memberId, conn, scanner, info);
            return;
        }

        System.out.println("Returning back to main menu.\n");
    }

    public static void updateDashboard(Integer memberId, Connection conn, Scanner scanner, String[] info) throws SQLException {
        System.out.println("""
                What would you like to update today?
                   1. Exercise Routines
                   2. Fitness Achievements
                   3. Health Statistics""");
        System.out.print("Enter your choice or multiple choices separated by commas (e.g., 1, 2 for two selections): ");

        String choices = scanner.nextLine().trim();
        String[] updates = choices.split(",");

        for (String update : updates) {
            int val = Integer.parseInt(update.trim());
            switch (val) {
                case 1:
                    System.out.print("Enter Exercise Routines: ");
                    info[val-1] = scanner.nextLine().trim();
                    break;
                case 2:
                    System.out.print("Enter Fitness Achievements: ");
                    info[val-1] = scanner.nextLine().trim();
                    break;
                case 3:
                    System.out.print("Enter Health Statistics: ");
                    info[val-1] = scanner.nextLine().trim();
                    break;
                default:
                    System.out.println(val + " is an invalid choice.");
                    break;
            }
        }

        String SQL = "UPDATE Members SET exercise_routines = ?, fitness_achievements = ?, health_stats = ? WHERE member_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(SQL);
        pstmt.setString(1, info[0]);
        pstmt.setString(2, info[1]);
        pstmt.setString(3, info[2]);
        pstmt.setInt(4, memberId);

        pstmt.executeUpdate();
        System.out.println("Dashboard updated successfully!\n");

        displayDashboard(memberId, conn, scanner);
    }
}