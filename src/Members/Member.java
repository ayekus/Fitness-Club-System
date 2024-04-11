package Members;

//import java.util.ArrayList;
import java.util.Date;

public class Member {

    public class Members {

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
        public Members(int memberId, String firstName, String lastName, String email, String phone, Date dateOfBirth, double height, double weight, String fitnessGoal) {
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

        // Profile Management
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
    }
}

