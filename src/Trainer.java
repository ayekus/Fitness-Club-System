import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

public class Trainer {

    public static void main(int trainerId, Connection conn, Scanner scanner) throws SQLException {
        while(true) {
                System.out.println("""
                        Signed in Successfully!
                        Please select an option:
                           1. Set Availability
                           2. Show Schedule
                           3. View Member Profile
                           4. Billing and Payment Processing
                           5. Leave System""");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine();

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
        String dateString = scanner.nextLine();
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

    public static void viewMemberProfile(Connection conn, Scanner scanner) {
        System.out.println("Enter member name to view profile:");
        String memberName = scanner.nextLine();

        System.out.println("Looking up profile for member: " + memberName);
        // get memeber from database

        //if found
        System.out.println("Member Name: " + memberName);
        System.out.println("Member Details: [Placeholder details]");

        //else
        System.out.println("Member " + memberName + " not found.");

    }


}
