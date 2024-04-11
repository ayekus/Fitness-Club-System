import java.util.Date;

public class Admin {
    private int adminId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dateOfBirth;

    public Admin(int adminId, String firstName, String lastName, String email, String phone, Date dateOfBirth) {
        this.adminId = adminId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
    }

}
