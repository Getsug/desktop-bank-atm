package admindashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import savvytnoum.DbConnection;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

public class CreateNewAccountController implements Initializable {


    //IDs
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private DatePicker dateOfBirth;
    @FXML
    private ComboBox<String> gender;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField email;
    @FXML
    private ComboBox<String> accountType;
    @FXML
    private TextField accountNumber;
    @FXML
    private TextArea address;

    String genderValue;
    String accountTypeValue;

    ObservableList<String> genderList = FXCollections.observableArrayList("Male","Female","Other");
    ObservableList<String> accountTypeList = FXCollections.observableArrayList("Savings","Current","Fixed Deposit");



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gender.setItems(genderList);
        accountType.setItems(accountTypeList);

    }

    //Listener to get values from the checkbox when any is selected
    @FXML
    private void getGenderValue() {
        genderValue = gender.getValue();
    }

    @FXML
    private void getAccountTypeValue() {
        accountTypeValue =  accountType.getValue();
    }


    //TODO verify that only digits are entered in the account number text field
    @FXML
    private void addClient(ActionEvent event) {

        String sqlInsert = "INSERT INTO ClientData(Firstname, Lastname, DOB, Gender, Mobile_Num, Email, Address, Acc_Type, Acc_Num, Balance) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        //checks if all fields are filled
        if(fieldsNotEmpty()){

            //checks whether account number exists in the database
            if(numberDoesntExists()) {

                long accountNum = Long.parseLong(this.accountNumber.getText());

                try {
                    Connection connection = DbConnection.getDatabase();

                    PreparedStatement statement = connection.prepareStatement(sqlInsert);

                    statement.setString(1,this.firstName.getText());
                    statement.setString(2,this.lastName.getText());
                    statement.setString(3,this.dateOfBirth.getEditor().getText());
                    statement.setString(4,this.genderValue);
                    statement.setString(5,this.phoneNumber.getText());
                    statement.setString(6,this.email.getText());
                    statement.setString(7,this.address.getText());
                    statement.setString(8,this.accountTypeValue);
                    statement.setLong(9,accountNum);
                    statement.setLong(10,0);

                    statement.execute();
                    connection.close();

                    //generate login credentials for the new account
                    createLoginCredentials(email.getText());



                    //clear form
                    this.firstName.clear();
                    this.lastName.clear();
                    this.dateOfBirth.getEditor().clear();
                    this.gender.getSelectionModel().clearSelection();
                    this.phoneNumber.clear();
                    this.email.clear();
                    this.accountType.getSelectionModel().clearSelection();
                    this.accountNumber.clear();
                    this.address.clear();



                    //display success message to the user
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Account successfully created");
                    alert.showAndWait();

                }
                catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Account already exists. Choose a different Account Number!");
                alert.showAndWait();
            }


        }
        else{
            //create an alert box
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
        }


    }

    //make autogenerate login credentials for every new account
    private void createLoginCredentials(String email) {
        int newPassword;
        int max = 10000;
        int min  = 1000;

        Random random = new Random();

        //Generate a random integer between 1000 ~ 10000
        newPassword = random.nextInt(max - min) + min;

        String sqlInsert = "INSERT INTO ClientLogin(Username, Password) VALUES(?, ?)";

        try {
            Connection connection  = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlInsert);

            statement.setString(1, email);
            statement.setString(2, Integer.toString(newPassword));

            statement.execute();

            connection.close();
            statement.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        //send login credentials to the user
        sendEmail(email, Integer.toString(newPassword));
    }


    //send login details to user
    private void sendEmail(String recipient, String temporaryPassword) {

        System.out.println("Preparing to send email...");

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        //TODO:  Replace email and Password with relevant email and password
        String username = "...@gmail.com";
        String password = "password";

        Session session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });


        //write a message to the client
        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Login Credentials");
            message.setText("Hello, Welcome to Maze Bank\nUsername: " + recipient + "\nTemporary password: " +
                    temporaryPassword + "\nFor strong security please change your password as soon as possible!");

            Transport.send(message);
            System.out.println("Message sent");
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }



    //validate fields
    private boolean fieldsNotEmpty() {

        if(firstName.getText().isEmpty() || lastName.getText().isEmpty() || dateOfBirth.getEditor().getText().isEmpty()
                || genderValue == null || phoneNumber.getText().isEmpty() || email.getText().isEmpty() || address.getText().isEmpty()
                ||accountTypeValue == null || accountNumber.getText().isEmpty())
        {

            return false;
        }
        else {
            return true;
        }

    }

    //checks if the account number already exists in the database
    private boolean numberDoesntExists() {

        long accNum = Long.parseLong(accountNumber.getText());

        String sqlCount = "SELECT COUNT(Acc_Num) FROM ClientData WHERE Acc_Num = ?";
        int recordCount = 0;

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlCount);

            statement.setLong(1, accNum);


            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            recordCount = resultSet.getInt(1);

            statement.close();
            resultSet.close();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }


        if(recordCount > 0) {
            System.out.println(recordCount + " match(es)");
            return false;
        }
        else {
            return true;
        }

    }

}
