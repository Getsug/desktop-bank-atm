package admindashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class ResetClientPassController implements Initializable {

    @FXML
    private TextField clientEmail;
    @FXML
    private Label emptyMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void resetPass(ActionEvent event) {

        if(fieldsNotEmpty()) {
            updatePassword(clientEmail.getText());
        }
        else{
            emptyMessage.setText("Type an email!");
        }

    }

    private void updatePassword(String email) {
        int newPassword;
        int max = 50000;
        int min  = 1000;

        Random random = new Random();

        //Generate a random integer between 1000 ~ 50000
        newPassword = random.nextInt(max - min) + min;

        //clientID
        int id = getClientID(email);

        //System.out.println(id);

        String sqlUpdate = "UPDATE ClientLogin SET Password = ? WHERE ID = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlUpdate);

            statement.setString(1, Integer.toString(newPassword));
            statement.setInt(2, id);


            statement.executeUpdate();

            connection.close();
            statement.close();


            //shows a successful password reset
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Password rest");
            alert.showAndWait();

        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        //send email to client
        sendEmail(email, Integer.toString(newPassword));
    }

    //returns the client id
    private int getClientID(String email) {

        int ID = -1; // -1 means not in database.

        String sqlQuery = "SELECT ID FROM ClientData WHERE Email = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            statement.setString(1, email);


            ResultSet resultSet =  statement.executeQuery();

            resultSet.next();

            ID = resultSet.getInt(1);

            connection.close();
            statement.close();

        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return ID;
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
            message.setSubject("Password Reset");
            message.setText("\nTemporary password: " +
                    temporaryPassword + "\nFor strong security please change your password as soon as possible!");

            Transport.send(message);
            System.out.println("Message sent");
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //verifies that fields are filled
    private boolean fieldsNotEmpty() {

        if(clientEmail.getText().isEmpty())
        {
            return false;
        }
        else {
            return true;
        }

    }
}
