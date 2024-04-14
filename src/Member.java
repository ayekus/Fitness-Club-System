import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
            System.out.println();

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

    public static void viewSchedule(Integer memberId, Connection conn, Scanner scanner) throws SQLException {
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
        boolean enrolled = false;

        while (rs.next()) {
            enrolled = true;
            System.out.println(rs.getString("session_name") + " session on " +
                    rs.getDate("session_date") + " from " + rs.getTime("start_time") + " to "
                    + rs.getTime("end_time") + " with Trainer " +
                    rs.getString("trainer_first_name") + " "
                    + rs.getString("trainer_last_name"));
        }

        if (!enrolled) {
            System.out.println("You are not enrolled in any upcoming One-on-One Sessions.");
        }

        stmt.close();
        rs.close();

        query = ("""
            SELECT gs.*, t.first_name as trainer_first_name, t.last_name as trainer_last_name
            FROM GroupSession gs
            JOIN GroupSessionEnrollment gse ON gs.group_session_id = gse.group_session_id
            JOIN Trainers t ON gs.trainer_id = t.trainer_id
            WHERE gse.member_id = ?""");

        PreparedStatement stmt2 = conn.prepareStatement(query);
        stmt2.setInt(1, memberId);
        ResultSet rs2 = stmt2.executeQuery();

        System.out.println("\nGroup Sessions:");
        enrolled = false;
        while (rs2.next()) {
            enrolled = true;
            System.out.println(rs2.getString("session_name") + " session on " +
                    rs2.getDate("session_date") + " from " + rs2.getTime("start_time") + " to "
                    + rs2.getTime("end_time") + " with Trainer " +
                    rs2.getString("trainer_first_name") + " "
                    + rs2.getString("trainer_last_name"));
        }
        if (!enrolled) {
            System.out.println("You are not enrolled in any upcoming Group Sessions.\n");
        }

        rs2.close();
        stmt2.close();


        System.out.print("\nWould you like to add a session (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            addSession(memberId, conn, scanner);
        }

        System.out.println("Returning back to main menu.\n");
    }

    public static void addSession(Integer memberId, Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the date you want to book a session for (yyyy-mm-dd): ");
        String bookingDateStr = scanner.nextLine().trim();

        LocalDate bookingDate;
        try {
            bookingDate = LocalDate.parse(bookingDateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format entered. ");
            viewSchedule(memberId, conn, scanner);
            return;
        }

        String query = """
                SELECT ap.*, t.first_name AS trainer_first_name, t.last_name AS trainer_last_name, r.room_desc
                FROM ApprovedSessions AS ap
                INNER JOIN Trainers AS t ON ap.trainer_id = t.trainer_id
                LEFT JOIN Rooms AS r ON ap.room_id = r.room_id""";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        System.out.println("\nAvailable Sessions:\n");
        boolean hasSessions = false;
        while (rs.next()) {
            hasSessions = true;
            System.out.println("Session ID: " + rs.getInt("session_id"));
            System.out.println("Trainer: " + rs.getString("trainer_first_name") + " " + rs.getString("trainer_last_name"));
            System.out.println("Start Time: " + rs.getTime("start_time") + " - End Time: " + rs.getTime("end_time") +
                    " (Type: " + (rs.getBoolean("is_group_session") ? "Group)" : "One-on-One)"));
            System.out.println("Room: " + rs.getString("room_desc") + " Room");
        }

        if (!hasSessions) {
            System.out.println("There are no available sessions.\n");
            viewSchedule(memberId, conn, scanner);
            return;
        }

        System.out.print("Enter the Session ID of the session you want to schedule: ");
        int sessionId = Integer.parseInt(scanner.nextLine().trim());

        String selectedQuery = "SELECT * FROM ApprovedSessions WHERE session_id = ?";

        PreparedStatement selectedStmt = conn.prepareStatement(selectedQuery);
        selectedStmt.setInt(1, sessionId);
        ResultSet selectedRs = selectedStmt.executeQuery();

        if (!selectedRs.next()) {
            System.out.println("Invalid session ID.");
            viewSchedule(memberId, conn, scanner);
            return;
        }

        LocalTime startTime = selectedRs.getTime("start_time").toLocalTime();
        LocalTime endTime = selectedRs.getTime("end_time").toLocalTime();
        boolean isGroupSession = selectedRs.getBoolean("is_group_session");
        int room_id = selectedRs.getInt("room_id");

        // Check if user is already enrolled for a session at the same time
        String trainingSessionQuery = """
                            SELECT * FROM TrainingSession
                            WHERE member_id = ? AND session_date = ? AND
                                  ((start_time >= ? AND start_time < ?) OR
                                  (end_time > ? AND end_time <= ?))""";

        String groupSessionEnrollmentQuery = """
                            SELECT gs.* FROM GroupSession gs
                            INNER JOIN GroupSessionEnrollment ge ON gs.group_session_id = ge.group_session_id
                            WHERE ge.member_id = ? AND gs.session_date = ? AND
                                  ((gs.start_time >= ? AND gs.start_time < ?) OR
                                  (gs.end_time > ? AND gs.end_time <= ?))""";

        PreparedStatement trainingStmt = conn.prepareStatement(trainingSessionQuery);
        trainingStmt.setInt(1, memberId);
        trainingStmt.setDate(2, Date.valueOf(bookingDate));
        trainingStmt.setTime(3, Time.valueOf(startTime));
        trainingStmt.setTime(4, Time.valueOf(endTime));
        trainingStmt.setTime(5, Time.valueOf(startTime));
        trainingStmt.setTime(6, Time.valueOf(endTime));

        ResultSet trainingRs = trainingStmt.executeQuery();

        if (trainingRs.next()) {
            System.out.println("You are already enrolled in a session at this time.");
            viewSchedule(memberId, conn, scanner);
            return;
        }

        PreparedStatement groupStmt = conn.prepareStatement(groupSessionEnrollmentQuery);
        groupStmt.setInt(1, memberId);
        groupStmt.setDate(2, Date.valueOf(bookingDate));
        groupStmt.setTime(3, Time.valueOf(startTime));
        groupStmt.setTime(4, Time.valueOf(endTime));
        groupStmt.setTime(5, Time.valueOf(startTime));
        groupStmt.setTime(6, Time.valueOf(endTime));

        ResultSet groupRs = groupStmt.executeQuery();

        if (groupRs.next()) {
            System.out.println("You are already enrolled in a group session at this time.");
            viewSchedule(memberId, conn, scanner);
            return;
        }

        // If it is a one-on-one session, check if someone has already booked the session
        if (!isGroupSession) {
            String oneOnOneSessionQuery = """
                    SELECT * FROM TrainingSession
                    WHERE trainer_id = ? AND session_date = ? AND
                          ((start_time >= ? AND start_time < ?) OR
                          (end_time > ? AND end_time <= ?))""";

            PreparedStatement oneOnOneStmt = conn.prepareStatement(oneOnOneSessionQuery);
            oneOnOneStmt.setInt(1, selectedRs.getInt("trainer_id"));
            oneOnOneStmt.setDate(2, Date.valueOf(bookingDate));
            oneOnOneStmt.setTime(3, Time.valueOf(startTime));
            oneOnOneStmt.setTime(4, Time.valueOf(endTime));
            oneOnOneStmt.setTime(5, Time.valueOf(startTime));
            oneOnOneStmt.setTime(6, Time.valueOf(endTime));

            ResultSet oneOnOneRs = oneOnOneStmt.executeQuery();

            if (oneOnOneRs.next()) {
                System.out.println("The selected session slot is already booked. Please try again later.");
                viewSchedule(memberId, conn, scanner);
                return;
            }
        }

        if (!Main.processPayment(conn, scanner, 10, memberId, "Session Sign-up Fee")) {
            System.out.println("Payment failed. Please try again. ");
            viewSchedule(memberId, conn, scanner);
            return;
        }

        if (!isGroupSession) {
            System.out.print("What do you want to do during this session: ");
            String sessionName = scanner.nextLine().trim();

            String insertTrainingSessionQuery = """
                    INSERT INTO TrainingSession (member_id, trainer_id, session_date, start_time, end_time, session_name, room_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)""";

            PreparedStatement insertTrainingSessionStmt = conn.prepareStatement(insertTrainingSessionQuery);
            insertTrainingSessionStmt.setInt(1, memberId);
            insertTrainingSessionStmt.setInt(2, selectedRs.getInt("trainer_id"));
            insertTrainingSessionStmt.setDate(3, Date.valueOf(bookingDate));
            insertTrainingSessionStmt.setTime(4, Time.valueOf(startTime));
            insertTrainingSessionStmt.setTime(5, Time.valueOf(endTime));
            insertTrainingSessionStmt.setString(6, sessionName);
            insertTrainingSessionStmt.setInt(6, room_id);

            insertTrainingSessionStmt.executeUpdate();
            System.out.println("Session successfully booked.");

            viewSchedule(memberId, conn, scanner);
            return;
        }

        String groupSessionQuery = """
                            SELECT * FROM GroupSession
                            WHERE session_date = ? AND start_time = ? AND end_time = ? AND trainer_id = ?""";

        PreparedStatement groupSessionStmt = conn.prepareStatement(groupSessionQuery);
        groupSessionStmt.setDate(1, Date.valueOf(bookingDate));
        groupSessionStmt.setTime(2, Time.valueOf(startTime));
        groupSessionStmt.setTime(3, Time.valueOf(endTime));
        groupSessionStmt.setInt(4, selectedRs.getInt("trainer_id"));

        ResultSet groupSessionRs = groupSessionStmt.executeQuery();

        int groupSessionId;

        if (groupSessionRs.next()) {
            groupSessionId = groupSessionRs.getInt("group_session_id");
        } else {
            // If a group session doesn't exist, add it to the GroupSession table
            String insertGroupSessionQuery = """
                                INSERT INTO GroupSession (session_name, session_date, start_time, end_time, trainer_id, room_id)
                                VALUES (?, ?, ?, ?, ?, ?)""";

            PreparedStatement insertGroupSessionStmt = conn.prepareStatement(insertGroupSessionQuery, Statement.RETURN_GENERATED_KEYS);
            insertGroupSessionStmt.setString(1, selectedRs.getString("session_name"));
            insertGroupSessionStmt.setDate(2, Date.valueOf(bookingDate));
            insertGroupSessionStmt.setTime(3, Time.valueOf(startTime));
            insertGroupSessionStmt.setTime(4, Time.valueOf(endTime));
            insertGroupSessionStmt.setInt(5, selectedRs.getInt("trainer_id"));
            insertGroupSessionStmt.setInt(6, room_id);

            insertGroupSessionStmt.executeUpdate();
            ResultSet generatedKeys = insertGroupSessionStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                groupSessionId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to get the generated session ID."); // Should not get here
            }
        }

        // After obtaining the group session id, enroll the member into the session
        String enrollGroupSessionQuery = """
                                INSERT INTO GroupSessionEnrollment (group_session_id, member_id)
                                VALUES (?, ?)""";

        PreparedStatement enrollGroupSessionStmt = conn.prepareStatement(enrollGroupSessionQuery);
        enrollGroupSessionStmt.setInt(1, groupSessionId);
        enrollGroupSessionStmt.setInt(2, memberId);

        enrollGroupSessionStmt.executeUpdate();
        System.out.println("Session successfully booked.");
        viewSchedule(memberId, conn, scanner);
    }

    public static void viewProfile(Integer memberId, Connection conn, Scanner scanner) throws SQLException, ParseException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Members WHERE member_id = ?");
        stmt.setInt(1, memberId);

        ResultSet rs = stmt.executeQuery();
        rs.next();

        System.out.println("Here is your information: \n");

        System.out.println("First Name: " + rs.getString("first_name"));
        System.out.println("Last Name: " + rs.getString("last_name"));
        System.out.println("Email: " + rs.getString("email"));
        System.out.println("Phone: " + rs.getString("phone"));
        System.out.println("Date of Birth: " + rs.getDate("date_of_birth"));
        System.out.println("Height: " + rs.getBigDecimal("height"));
        System.out.println("Weight: " + rs.getBigDecimal("weight"));
        System.out.println("Fitness Goal: " + rs.getString("fitness_goal"));

        String[] profile = new String[9];
        for (int i = 0; i < 8; i++) {
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