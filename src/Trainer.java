import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Trainer {
    private int trainerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private List<Date> availableTimes; //maybe for the sake of checking avail times may be unnecessary

    public Trainer(int trainerId, String firstName, String lastName, String email, String phone, Date dateOfBirth) {
        this.trainerId = trainerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.availableTimes = new ArrayList<>();
    }

    public void addAvailability(Date time) {
        availableTimes.add(time);
        System.out.println("Added new availability at: " + time);
    }

    public void displaySchedule() {
        System.out.println("Current schedule for " + firstName + " " + lastName + ":");
        for (Date time : availableTimes) {
            System.out.println(time);
        }
    }

    public void viewMemberProfile(String memberName) {
        System.out.println("Looking up profile for member: " + memberName);
        // fix this*
        System.out.println("Member Name: " + memberName);
        System.out.println("Member Details: [Placeholder details]");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // figure out proper querying
        Trainer trainer = new Trainer(1, "Jane", "Doe", "jane.doe@example.com", "555-1234", new Date());

        while (true) {
            System.out.println("Trainer Management System:");
            System.out.println("1. Set Availability");
            System.out.println("2. Show Schedule");
            System.out.println("3. View Member Profile");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("Enter date and time for availability (format: yyyy-MM-dd):");
                    String dateString = scanner.nextLine();
                    try {
                        Date date = new Date(dateString);
                        trainer.addAvailability(date);
                    } catch (Exception e) {
                        System.out.println("Invalid date format.");
                    }
                    break;
                case 2:
                    trainer.displaySchedule();
                    break;
                case 3:
                    System.out.println("Enter member name to view profile:");
                    String name = scanner.nextLine();
                    trainer.viewMemberProfile(name);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        }
    }

}
