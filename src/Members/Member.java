package Members;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class Member {

    // Member properties
    private int memberId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private double height;
    private double weight;
    private String fitnessGoal;

    // Constructor
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

    // Simplified for example purposes
    public void updateProfile(String firstName, String lastName, String email, String phone, Date dateOfBirth, double height, double weight, String fitnessGoal) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;
        this.fitnessGoal = fitnessGoal;
    }

    // display dashboard information
    public void displayDashboard() {
        System.out.println("Exercise Routines:");
        System.out.println("Fitness Achievements:");
        System.out.println("Health Statistics:");
    }

    public static void main(String[] args) {
        try {
            String configFilePath = "config.properties";
            FileInputStream propsInput = new FileInputStream(configFilePath);

            Properties prop = new Properties();
            prop.load(propsInput);

            Connection conn = DriverManager.getConnection(prop.getProperty("DB_URL"), prop.getProperty("DB_USER"), prop.getProperty("DB_PASSWORD"));
            Scanner scanner = new Scanner(System.in);

            // line below would be used for db querying just put placeholder for now
            Member member = new Member(0, "Placeholder", "Name", "email@example.com", "000-000-0000", new Date(), 0.0, 0.0, "Fitness Goal");

            while (true) {
                System.out.println("Hello Member");
                System.out.println("1. Update Profile");
                System.out.println("2. Display Dashboard");
                System.out.println("Enter your choice: ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        member.updateProfile(firstName, lastName, email, phone, dateOfBirth, height, weight, fitnessGoal);
                        break;
                    case "2":
                        member.displayDashboard();
                        break;
                    default:
                        System.out.println("Invalid choice\n");
                        break;
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

}
