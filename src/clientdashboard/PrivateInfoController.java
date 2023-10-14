package clientdashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import savvytnoum.DbConnection;
import savvytnoum.LoginController;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PrivateInfoController implements Initializable {


    @FXML
    private ComboBox<String> gender;
    @FXML
    private TextField phone;
    @FXML
    private TextField email;
    @FXML
    private TextArea address;
    @FXML
    private Label emailExist;


    String genderValue;

    private final long clientAccount = LoginController.setAccountNumber();

    ObservableList<String> genderList = FXCollections.observableArrayList("Male", "Female", "Other");



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gender.setItems(genderList);
        loadUserInfo(clientAccount);

    }

    @FXML
    private  void save(ActionEvent event) {

        //verifies that the txt field is not empty
        if(fieldsNotEmpty()) {

            //checks for email modifications
            if(noEmailChange(email.getText(), clientAccount)) {

                //modify user data except the email
                modifyInfo(gender.getValue(), phone.getText(), address.getText());

                //shows a successful update of info
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Information saved");
                alert.showAndWait();

            }
            else {

                //checks if email already exists in the database
                if(noEmailExists(email.getText())) {

                    changeUserInfo(gender.getValue(), phone.getText(), email.getText(), address.getText());

                    //shows a successful update of info
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Information updated");
                    alert.showAndWait();

                }
                else {
                    emailExist.setText("Email already used!");
                }
            }
        }
        else {
            //informs user that the username already exists
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Fill all the fields");
            alert.show();
        }

    }

    //Listener to get values from the checkbox when any is selected
    @FXML
    private void getGenderValue() {
        genderValue = gender.getValue();
    }


    //load user info from database and display it on the text fields
    private void loadUserInfo(long accountNum) {

        String gen;
        String mobile;
        String mail;
        String adrs;


        String query = "SELECT Gender, Mobile_Num, Email, Address FROM ClientData WHERE Acc_Num = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setLong(1, accountNum);

            ResultSet resultSet = statement.executeQuery();

            gen = resultSet.getString(1);
            mobile = resultSet.getString(2);
            mail = resultSet.getString(3);
            adrs = resultSet.getString(4);

            //set the values to display
            gender.setValue(gen);
            phone.setText(mobile);
            email.setText(mail);
            address.setText(adrs);

            statement.close();
            resultSet.close();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //update user Info in the database
    private void changeUserInfo( String gender, String mobile, String email, String address) {

        String SqlUpdate = "UPDATE ClientData SET Gender = ?, Mobile_Num = ?, Email =?, Address = ? WHERE Acc_Num = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(SqlUpdate);

            statement.setString(1, gender);
            statement.setString(2, mobile);
            statement.setString(3, email);
            statement.setString(4, address);
            statement.setLong(5, clientAccount);

            statement.executeUpdate();

            statement.close();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //modifies user data except the email
    private void modifyInfo(String gender, String mobile,String address) {
        String SqlUpdate = "UPDATE ClientData SET Gender = ?, Mobile_Num = ?, Address = ? WHERE Acc_Num = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(SqlUpdate);

            statement.setString(1, gender);
            statement.setString(2, mobile);
            statement.setString(3, address);
            statement.setLong(4, clientAccount);

            statement.executeUpdate();

            statement.close();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //verifies that fields are filled
    private boolean fieldsNotEmpty() {

        if(gender.getValue().isEmpty() || phone.getText().isEmpty() || email.getText().isEmpty() || address.getText().isEmpty())
        {
            return false;
        }
        else {
            return true;
        }

    }

    //checks if the  the provided email exists in the database
    private boolean noEmailExists(String email) {


        String sqlCount = "SELECT COUNT(Email) FROM ClientData WHERE Email = ?";
        int recordCount;

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlCount);

            statement.setString(1, email);


            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            recordCount = resultSet.getInt(1);

            //close
            statement.close();
            resultSet.close();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if(recordCount > 0) {
            System.out.println(recordCount + " match");
            return false;
        }

        return true;
    }

    //checks if user didn't modify the email
    private boolean noEmailChange(String email, long clientAccount) {

        String sqlCount = "SELECT Email FROM ClientData WHERE Acc_Num = ?";
        String dbEmail;

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlCount);

            statement.setLong(1, clientAccount);


            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            dbEmail = resultSet.getString(1);

            //close
            statement.close();
            resultSet.close();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return email.equals(dbEmail);
    }


}
