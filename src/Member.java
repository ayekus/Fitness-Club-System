import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;

public class Member {

    public static void main(int memberId, Connection conn, Scanner scanner) throws SQLException, ParseException {
        while(true) {
            System.out.println("""
                        Please select an option:
                           1. View Profile
                           2. Display Dashboard
                           3. Schedule Management
                           4. Leave System""");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewProfile(memberId, conn, scanner);
                    break;
                case "2":
                    displayDashboard(memberId, conn, scanner);
                    break;
                case "3":
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

        System.out.println("Would you like to update your information (y/n): ");
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

        String choices = scanner.nextLine();
        String[] updates = choices.split(",");
        java.sql.Date dobDate = null;

        for (String update : updates) {
            int val = Integer.parseInt(update.trim());
            switch (val) {
                case 1:
                    System.out.print("Enter new first name:");
                    profile[val - 1] = scanner.nextLine();
                    break;
                case 2:
                    System.out.print("Enter new last name:");
                    profile[val - 1] = scanner.nextLine();
                    break;
                case 3:
                    System.out.print("Enter new email:");
                    profile[val - 1] = scanner.nextLine();
                    break;
                case 4:
                    System.out.print("Enter new password:");
                    profile[val - 1] = scanner.nextLine();
                    break;
                case 5:
                    System.out.println("Enter new phone:");
                    profile[val - 1] = scanner.nextLine();
                    break;
                case 6:
                    System.out.println("Enter new date of birth (yyyy-mm-dd):");
                    profile[val - 1] = scanner.nextLine();

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
                    profile[val - 1] = scanner.nextLine();
                    break;
                case 8:
                    System.out.println("Enter new weight:");
                    profile[val - 1] = scanner.nextLine();
                    break;
                case 9:
                    System.out.println("Enter new fitness goal:");
                    profile[val - 1] = scanner.nextLine();
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

    private Connection conn;

    public static String getExerciseRoutines(Integer memberId, Connection conn) {
        String sql = "SELECT exercise_routines FROM members WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("exercise_routines");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching exercise routines: " + e.getMessage());
        }
        return null;
    }

    public static String getFitnessAchievements(Integer memberId, Connection conn) {
        String sql = "SELECT fitness_achievements FROM members WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("fitness_achievements");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching fitness achievements: " + e.getMessage());
        }
        return null;
    }

    public static String getHealthStats(Integer memberId, Connection conn) {
        String sql = "SELECT health_stats FROM members WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("health_stats");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching health statistics: " + e.getMessage());
        }
        return null;
    }

    public static void updateExerciseRoutines(String data, Integer memberId, Connection conn) {
        String sql = "UPDATE members SET exercise_routines = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data);
            pstmt.setInt(2, memberId);
            pstmt.executeUpdate();
            System.out.println("Exercise routines updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating exercise routines: " + e.getMessage());
        }
    }

    public static void updateFitnessAchievements(String data, Integer memberId, Connection conn) {
        String sql = "UPDATE members SET fitness_achievements = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data);
            pstmt.setInt(2, memberId);
            pstmt.executeUpdate();
            System.out.println("Fitness achievements updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating fitness achievements: " + e.getMessage());
        }
    }

    public static void updateHealthStats(String data, Integer memberId, Connection conn) {
        String sql = "UPDATE members SET health_stats = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data);
            pstmt.setInt(2, memberId);
            pstmt.executeUpdate();
            System.out.println("Health statistics updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating health statistics: " + e.getMessage());
        }
    }

    public static void displayDashboard(Integer memberId, Connection conn, Scanner scanner) {

        System.out.println("Dashboard:");

        String exerciseRoutines = getExerciseRoutines(memberId, conn);
        String fitnessAchievements = getFitnessAchievements(memberId, conn);
        String healthStats = getHealthStats(memberId, conn);

        boolean dataMissing = false;

        if (exerciseRoutines == null) {
            System.out.println("No exercise routines found.");
            dataMissing = true;
        } else {
            System.out.println("Exercise Routines: " + exerciseRoutines);
        }

        if (fitnessAchievements == null) {
            System.out.println("No fitness achievements found.");
            dataMissing = true;
        } else {
            System.out.println("Fitness Achievements: " + fitnessAchievements);
        }

        if (healthStats == null) {
            System.out.println("No health statistics found.");
            dataMissing = true;
        } else {
            System.out.println("Health Statistics: " + healthStats);
        }

        if (dataMissing) {
            System.out.println("Please enter the missing information:");
            while (true) {
                System.out.println("1. Enter Exercise Routines");
                System.out.println("2. Enter Fitness Achievements");
                System.out.println("3. Enter Health Statistics");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        System.out.print("Enter Exercise Routines: ");
                        updateExerciseRoutines(scanner.nextLine(), memberId, conn);
                        break;
                    case "2":
                        System.out.print("Enter Fitness Achievements: ");
                        updateFitnessAchievements(scanner.nextLine(), memberId, conn);
                        break;
                    case "3":
                        System.out.print("Enter Health Statistics: ");
                        updateHealthStats(scanner.nextLine(), memberId, conn);
                        break;
                    case "4":
                        return; // Exit the input loop
                    default:
                        System.out.println("Invalid choice. Please enter a valid number.");
                        break;
                }
            }
        }
    }
}
