import java.sql.*;
import java.util.Scanner;

public class Admin {

    public static void main(int adminId, Connection conn, Scanner scanner) throws SQLException {
        while (true) {
            System.out.println("""
                    Please select an option:
                       1. Room Booking Management
                       2. Equipment Maintenance Monitoring
                       3. Manage Group Session Schedule
                       4. Billing and Payment Processing
                       5. Leave System""");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    roomManagement(conn, scanner);
                    break;
                case "2":
                    monitorEquipment(adminId, conn, scanner);
                    break;
                case "3":
                    updateSchedule(conn, scanner);
                    break;
                case "4":
                    billing(adminId, conn, scanner);
                    break;
                case "5":
                    System.out.println("Exiting");
                    conn.close();
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice, please try again.\n");
                    break;
            }
        }
    }

    private static void roomManagement(Connection conn, Scanner scanner) throws SQLException {
        String query = "SELECT * FROM ApprovedSessions WHERE room_id IS NULL";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        System.out.println("\nSessions that need a room:\n");
        boolean hasSessions = false;
        while (rs.next()) {
            hasSessions = true;
            int sessionId = rs.getInt("session_id");
            int trainerId = rs.getInt("trainer_id");
            Time startTime = rs.getTime("start_time");
            Time endTime = rs.getTime("end_time");
            boolean isGroupSession = rs.getBoolean("is_group_session");
            String sessionName = rs.getString("session_name");

            System.out.println("Session ID: " + sessionId);
            System.out.println("Trainer ID: " + trainerId);
            System.out.println("Start Time: " + startTime);
            System.out.println("End Time: " + endTime);
            System.out.println("Is Group Session: " + isGroupSession);
            System.out.println("Session Name: " + sessionName + "\n");
        }

        if (!hasSessions) {
            System.out.println("There are no sessions that need a room.\n");
            return;
        }

        rs.close();
        stmt.close();

        System.out.print("\nWould you like to assign any rooms (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            roomAssignments(conn, scanner);
            return;
        }

        System.out.println("Returning back to main menu.\n");

    }

    private static void roomAssignments(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the Session Id for the session you would like to add a room for: ");
        int sessionId;
        try {
            sessionId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered, please try again.");
            return;
        }

        String query = "SELECT * FROM Rooms";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        System.out.println("\nAvailable Rooms:\n");
        boolean hasRooms = false;
        while (rs.next()) {
            hasRooms = true;
            int roomId = rs.getInt("room_id");
            String roomDesc = rs.getString("room_desc");

            System.out.println("Room ID: " + roomId);
            System.out.println(roomDesc + " Room\n");
        }

        if (!hasRooms) {
            System.out.println("There are no rooms available.\n");
            return;
        }

        rs.close();
        stmt.close();

        System.out.print("Enter the Room Id for the room you would like to assign: ");
        int roomId;
        try {
            roomId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered, please try again.");
            return;
        }

        String updateQuery = "UPDATE ApprovedSessions SET room_id = ? WHERE session_id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
        updateStmt.setInt(1, roomId);
        updateStmt.setInt(2, sessionId);
        int rowsAffected = updateStmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Room assignment successful.");
        } else {
            System.out.println("Failed to assign room. Please check the provided session ID and room ID and try again.");
        }

        updateStmt.close();

    }

    private static void monitorEquipment(int adminId, Connection conn, Scanner scanner) throws SQLException {
        String query = ("""
                SELECT em.*, a.first_name, a.last_name
                FROM EquipmentMaintenance em
                JOIN Admin a ON em.admin_id = a.admin_id""");

        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(query);

        if (!rs.next()) {
            System.out.println("There is no equipment that needs repairs. Returning to menu.\n");
            return;
        }

        System.out.println("Here is the list of equipment that needs repairs: \n");
        while (rs.next()) {
            System.out.println("Equipment Id: " + rs.getInt("maintenance_id"));
            System.out.println("Equipment: " + rs.getString("equipment_name"));
            System.out.println("Added by admin: " + rs.getString("last_name") + ", " + rs.getString("first_name"));
            System.out.println("Date Added: " + rs.getDate("date_added") + "\n");
        }

        rs.close();
        stmt.close();

        System.out.print("Would you like to add to the maintenance list or repair an item on the list (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            while (true) {
                System.out.println("""
                        Please select an option:
                           1. Add Maintenance Equipment
                           2. Repair Equipment""");
                System.out.print("Enter your choice: ");

                choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        addEquipment(adminId, conn, scanner);
                        return;
                    case "2":
                        runMaintenance(adminId, conn, scanner);
                        return;
                    default:
                        System.out.println("Invalid choice, please try again.\n");
                        break;
                }
            }
        }

        System.out.println("Returning back to main menu.\n");

    }

    private static void addEquipment(int adminId, Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the equipment that needs maintenance: ");
        String equipmentName = scanner.nextLine().trim();

        String query = "INSERT INTO EquipmentMaintenance (equipment_name, admin_id) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, equipmentName);
        stmt.setInt(2, adminId);

        stmt.executeUpdate();

        stmt.close();

        System.out.println("Equipment Maintenance added successfully!\n");

        monitorEquipment(adminId, conn, scanner);
    }

    private static void runMaintenance(int adminId, Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the equipment id that is being repaired: ");
        String equipmentId = scanner.nextLine().trim();
        String query = "DELETE FROM EquipmentMaintenance WHERE maintenance_id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, Integer.parseInt(equipmentId));

        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Maintenance record for equipment id " + equipmentId + " was removed successfully.\n");
        } else {
            System.out.println("No maintenance record found for equipment id " + equipmentId + ".\n");
        }

        stmt.close();
        monitorEquipment(adminId, conn, scanner);
    }


    private static void updateSchedule(Connection conn, Scanner scanner) throws SQLException {
        String query = """
            SELECT ta.*, t.first_name AS trainer_first_name, t.last_name AS trainer_last_name
            FROM TrainerAvailability ta
            INNER JOIN Trainers t ON ta.trainer_id = t.trainer_id""";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        System.out.println("\nSessions Requiring Approval:\n");
        boolean hasSessions = false;
        while (rs.next()) {
            String name = rs.getString("session_name");
            hasSessions = true;
            System.out.println("Availability ID: " + rs.getInt("availability_id"));

            if (name != null) {
                System.out.println(name);
            }

            System.out.println("Trainer: " + rs.getString("trainer_first_name") + " " + rs.getString("trainer_last_name"));
            System.out.println("Start Time: " + rs.getTime("start_time") + " - End Time: " + rs.getTime("end_time") +
                    " (Type: " + (rs.getBoolean("is_group_availability") ? "Group" : "One-on-One") + ")\n");
        }

        if (!hasSessions) {
            System.out.println("There are no available sessions.\n");
            return;
        }

        System.out.print("\nWould you like to approve any sessions (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            approveSchedule(conn, scanner);
            return;
        }

        System.out.println("Returning back to main menu.\n");
    }

    private static void approveSchedule(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the Availability ID of the session you want to approve: ");
        int availabilityId;
        try {
            availabilityId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered, please try again.");
            return;
        }

        String selectQuery = "SELECT * FROM TrainerAvailability WHERE availability_id = ?";
        PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
        selectStmt.setInt(1, availabilityId);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            int trainerId = rs.getInt("trainer_id");
            Time startTime = rs.getTime("start_time");
            Time endTime = rs.getTime("end_time");
            boolean isGroupSession = rs.getBoolean("is_group_availability");
            String sessionName = rs.getString("session_name");

            String insertQuery = "INSERT INTO ApprovedSessions (trainer_id, start_time, end_time, is_group_session, session_name) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, trainerId);
            insertStmt.setTime(2, startTime);
            insertStmt.setTime(3, endTime);
            insertStmt.setBoolean(4, isGroupSession);
            insertStmt.setString(5, sessionName);
            insertStmt.executeUpdate();

            System.out.println("Session approved and moved to Approved Sessions.");

            insertStmt.close();

            String deleteQuery = "DELETE FROM TrainerAvailability WHERE availability_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, availabilityId);
            deleteStmt.executeUpdate();
            deleteStmt.close();
        } else {
            System.out.println("No session found with Availability ID " + availabilityId);
            return;
        }

        rs.close();
        selectStmt.close();
    }



        private static void billing(int adminId, Connection conn, Scanner scanner) throws SQLException {
        String query = "SELECT * FROM Payments";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        System.out.println("\nUnapproved payments: ");
        System.out.println("Payment ID | Member ID | Amount |      Description      | Payment Date");

        while (rs.next()) {
            System.out.printf("%-10d | %-9d | %-6.2f | %-21s | %s\n",
                    rs.getInt("payment_id"), rs.getInt("member_id"), rs.getDouble("amount"),
                    rs.getString("payment_desc"), rs.getDate("payment_date"));
        }

        rs.close();
        stmt.close();

        System.out.println("\nApproved payments: ");
        System.out.println("Approved Payment ID | Member ID | Amount |      Description      | Admin ID | Payment Date | Date Approved");

        String approvedQuery = "SELECT * FROM ApprovedPayments";
        PreparedStatement approvedStmt = conn.prepareStatement(approvedQuery);
        ResultSet approvedRs = approvedStmt.executeQuery();

        while (approvedRs.next()) {
            System.out.printf("%-19d | %-9d | %-6.2f | %-21s | %-8d | %2s | %s\n",
                    approvedRs.getInt("approved_payment_id"), approvedRs.getInt("member_id"), approvedRs.getDouble("approved_amount"),
                    approvedRs.getString("payment_desc"), approvedRs.getInt("admin_id"), approvedRs.getDate("payment_date"),
                    approvedRs.getDate("date_approved"));
        }

        approvedRs.close();
        approvedStmt.close();

        System.out.print("\nWould you like to approve any transactions (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("y") || choice.equals("yes")) {
            updateBilling(adminId, conn, scanner);
            return;
        }

        System.out.println("Returning back to main menu.\n");
    }

    private static void updateBilling(int adminId, Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter the Payment ID of the transaction you would like to approve: ");
        int paymentId;
        try {
            paymentId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered, please try again.");
            return;
        }

        String selectQuery = "SELECT * FROM Payments WHERE payment_id = ?";
        PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
        selectStmt.setInt(1, paymentId);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            int memberId = rs.getInt("member_id");
            double amount = rs.getDouble("amount");
            String description = rs.getString("payment_desc");
            Date paymentDate = rs.getDate("payment_date");

            String insertQuery = "INSERT INTO ApprovedPayments (member_id, approved_amount, payment_desc, admin_id, payment_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, memberId);
            insertStmt.setDouble(2, amount);
            insertStmt.setString(3, description);
            insertStmt.setInt(4, adminId);
            insertStmt.setDate(5, paymentDate);
            insertStmt.executeUpdate();

            String deleteQuery = "DELETE FROM Payments WHERE payment_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, paymentId);
            deleteStmt.executeUpdate();

            System.out.println("Payment approved and moved to Approved Payments Records.");
            insertStmt.close();
            deleteStmt.close();
        } else {
            System.out.println("No payment found with Payment ID " + paymentId);
            return;
        }

        rs.close();
        selectStmt.close();
        billing(adminId, conn, scanner);
    }
}