import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;

public class Member {

    public static void main(int memberId, Connection conn, Scanner scanner) throws SQLException {
        while(true) {
            System.out.println("""
                        Signed in Successfully!
                        Please select an option:
                           1. Update Profile
                           2. Display Dashboard
                           3. Leave System""");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    updateProfile(memberId, conn, scanner);
                    break;
                case "2":
                    displayDashboard(memberId, conn, scanner);
                    break;
                case "3":
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

    public static void updateProfile(Integer memberId, Connection conn, Scanner scanner) throws SQLException {
        System.out.println("""
                        What would you like to update today?
                           1. First Name
                           2. Last Name
                           3. Email
                           4. Phone
                           5. Date of Birth
                           6. Height
                           7. Weight
                           8. Fitness Goal""");
        System.out.print("Enter your choice or multiple choices separated by commas (e.g., 6, 7 for height and weight): ");

        String choices = scanner.nextLine();
        String[] updates = choices.split(",");
        String[] newProfile = new String[8];

        for (int i = 0; i < 9; i++) {
            if (!choices.contains(i + "")) {
//                newProfile[i] = get original value
            }
        }

        java.sql.Date dobDate = null;

        for (String update : updates) {
            int val = Integer.parseInt(update.trim());
            switch (val) {
                case 1:
                    System.out.println("Enter new first name:");
                    newProfile[val - 1] = scanner.nextLine();
                    break;
                case 2:
                    System.out.println("Enter new last name:");
                    newProfile[val - 1] = scanner.nextLine();
                    break;
                case 3:
                    System.out.println("Enter new email:");
                    newProfile[val - 1] = scanner.nextLine();
                    break;
                case 4:
                    System.out.println("Enter new phone:");
                    newProfile[val - 1] = scanner.nextLine();
                    break;
                case 5:
                    System.out.println("Enter new date of birth (yyyy-mm-dd):");
                    newProfile[val - 1] = scanner.nextLine();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        dobDate = new Date(sdf.parse(newProfile[val - 1].trim()).getTime());
                    } catch (ParseException e) {
                        System.out.println("Invalid Date of Birth format. Please try again later.");
                        return;
                    }

                    break;
                case 6:
                    System.out.println("Enter new height:");
                    newProfile[val - 1] = scanner.nextLine();
                    break;
                case 7:
                    System.out.println("Enter new weight:");
                    newProfile[val - 1] = scanner.nextLine();
                    break;
                case 8:
                    System.out.println("Enter new fitness goal:");
                    newProfile[val - 1] = scanner.nextLine();
                    break;
                default:
                    System.out.println(val + " is an invalid choice.");
                    break;
            }
        }

        if (dobDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                // this is from database so cannot be bad format
                // ** need to change when getting values **
                dobDate = new Date(sdf.parse(newProfile[6].trim()).getTime());
            } catch (ParseException e) {
            }
        }

        String SQL = "UPDATE members SET first_name = ?, last_name = ?, email = ?, phone = ?, date_of_birth = ?, height = ?, weight = ?, fitness_goal = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, newProfile[0]);
            pstmt.setString(2, newProfile[1]);
            pstmt.setString(3, newProfile[2]);
            pstmt.setString(4, newProfile[3]);
            pstmt.setDate(5, dobDate);
            pstmt.setDouble(6, Double.parseDouble(newProfile[5]));
            pstmt.setDouble(7, Double.parseDouble(newProfile[6]));
            pstmt.setString(8, newProfile[7]);
            pstmt.setInt(9, memberId);

            pstmt.executeUpdate();
            System.out.println("Profile updated successfully!");
        }
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
