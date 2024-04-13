import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Admin {

    public static void main(int adminId, Connection conn, Scanner scanner) throws SQLException {
        while(true) {
            System.out.println("""
                    Please select an option:
                       1. Room Booking Management
                       2. Equipment Maintenance Monitoring
                       3. Class Schedule Updating
                       4. Billing and Payment Processing
                       5. Leave System""");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

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
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        }
    }

    private static void roomManagement(int adminId, Connection conn, Scanner scanner) {
    }

    private static void monitorEquipment(int adminId, Connection conn, Scanner scanner) {

    }

    private static void updateSchedule(int adminId, Connection conn, Scanner scanner) {
    }

    private static void billing(int adminId, Connection conn, Scanner scanner) {
    }

}
