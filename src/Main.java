import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        String configFilePath = "config.properties";
        FileInputStream propsInput = new FileInputStream(configFilePath);

        Properties prop = new Properties();
        prop.load(propsInput);

        Connection conn = DriverManager.getConnection(prop.getProperty("DB_URL"), prop.getProperty("DB_USER"), prop.getProperty("DB_PASSWORD"));
        System.out.println("Connected");

        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("Welcome to the Fitness Club");
            System.out.println("1. Sign in as existing user");
            System.out.println("2. Register new user");
            System.out.println("Enter your choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("sign in");
                    break;
                case "2":
                    System.out.println("Register new user");
                    break;
                default:
                    System.out.println("Invalid choice\n");
                    break;
            }
        }
    }
}
