import java.util.Date;

public class Trainer {
    private int trainerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dateOfBirth;

    public Trainer(int trainerId, String firstName, String lastName, String email, String phone, Date dateOfBirth) {
        this.trainerId = trainerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
    }

}
