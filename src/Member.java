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
import java.sql.ResultSet;

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

    private Connection conn;

    public String getExerciseRoutines() {
        String sql = "SELECT exercise_routines FROM members WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.memberId);
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

    public String getFitnessAchievements() {
        String sql = "SELECT fitness_achievements FROM members WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.memberId);
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

    public String getHealthStats() {
        String sql = "SELECT health_stats FROM members WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, this.memberId);
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

    public void updateExerciseRoutines(String data) {
        String sql = "UPDATE members SET exercise_routines = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data);
            pstmt.setInt(2, this.memberId);
            pstmt.executeUpdate();
            System.out.println("Exercise routines updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating exercise routines: " + e.getMessage());
        }
    }

    public void updateFitnessAchievements(String data) {
        String sql = "UPDATE members SET fitness_achievements = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data);
            pstmt.setInt(2, this.memberId);
            pstmt.executeUpdate();
            System.out.println("Fitness achievements updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating fitness achievements: " + e.getMessage());
        }
    }

    public void updateHealthStats(String data) {
        String sql = "UPDATE members SET health_stats = ? WHERE member_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data);
            pstmt.setInt(2, this.memberId);
            pstmt.executeUpdate();
            System.out.println("Health statistics updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating health statistics: " + e.getMessage());
        }
    }
    public void displayDashboard(Scanner scanner) {
        System.out.println("Dashboard:");

        String exerciseRoutines = getExerciseRoutines();
        String fitnessAchievements = getFitnessAchievements();
        String healthStats = getHealthStats();

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
                        updateExerciseRoutines(scanner.nextLine());
                        break;
                    case "2":
                        System.out.print("Enter Fitness Achievements: ");
                        updateFitnessAchievements(scanner.nextLine());
                        break;
                    case "3":
                        System.out.print("Enter Health Statistics: ");
                        updateHealthStats(scanner.nextLine());
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

    public static void main(String[] args) {
        Scanner scanner = null;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            scanner = new Scanner(System.in);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // random thing from db
            Member member = new Member(1, "John", "Doe", "john.doe@example.com", "123-456-7890", new Date(), 175.5, 85.5, "Lose weight");

            while (true) {
                System.out.println("Hello, " + member.firstName + "!");
                System.out.println("1. Update Information");
                System.out.println("2. Display Dashboard");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                String mainChoice = scanner.nextLine();

                switch (mainChoice) {
                    case "1": // Update information
                        System.out.println("What would you like to update today?");
                        System.out.println("1. First Name");
                        System.out.println("2. Last Name");
                        System.out.println("3. Email");
                        System.out.println("4. Phone");
                        System.out.println("5. Date of Birth");
                        System.out.println("6. Height");
                        System.out.println("7. Weight");
                        System.out.println("8. Fitness Goal");
                        System.out.print("Enter your choice or multiple choices separated by commas (e.g., 6,7 for height and weight): ");

                        String choices = scanner.nextLine();
                        String[] updates = choices.split(",");
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
                        break;

                    case "2": // Display dashboard
                        member.displayDashboard(scanner);
                        break;

                    case "0": // Exit
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid option selected. Please choose 1, 2, or 0.");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
