import java.sql.*;
import java.util.Scanner;

public class Admin {

    public static void main(int adminId, Connection conn, Scanner scanner) throws SQLException {
        while(true) {
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
                    roomManagement(adminId, conn, scanner);
                    break;
                case "2":
                    monitorEquipment(adminId, conn, scanner);
                    break;
                case "3":
                    updateSchedule(adminId, conn, scanner);
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

    private static void roomManagement(int adminId, Connection conn, Scanner scanner) {
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


    private static void updateSchedule(int adminId, Connection conn, Scanner scanner) {
    }

    private static void billing(int adminId, Connection conn, Scanner scanner) throws SQLException {
        String query = "SELECT * FROM Payments";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        System.out.println("Payment ID | Member ID | Amount |      Description      | Payment Date");

        while (rs.next()) {
            System.out.printf("%-10d | %-9d | %-6.2f | %-21s | %s\n",
                    rs.getInt("payment_id"), rs.getInt("member_id"), rs.getDouble("amount"),
                    rs.getString("payment_desc"), rs.getDate("payment_date"));
        }

        rs.close();
        stmt.close();

    }

}
