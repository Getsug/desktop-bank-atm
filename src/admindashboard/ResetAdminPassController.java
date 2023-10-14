package admindashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import savvytnoum.DbConnection;
import savvytnoum.LoginModel;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ResetAdminPassController implements Initializable {

    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField retypePassword;
    @FXML
    private Label noMatch;

    int adminId = LoginModel.setAdminID();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void save(ActionEvent event) {

        //System.out.println(adminId);


        //checks whether all fields are filled
        if(fieldsNotEmpty()) {

            //checks whether both passwords match
            if(passwordsMatch(newPassword.getText(), retypePassword.getText())) {

                //updates the password
                changePassword(newPassword.getText(), adminId);

                //displays password change was successful
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Password changed");
                alert.showAndWait();

            }
            else {
                noMatch.setText("Passwords don't match!");
            }

        }
        else {
            //create an alert box when the text field is not filled
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Please type the password twice");
            alert.showAndWait();
        }


    }

    //verifies that fields are filled
    private boolean fieldsNotEmpty() {

        if(newPassword.getText().isEmpty() || retypePassword.getText().isEmpty())
        {
            return false;
        }
        else {
            return true;
        }

    }

    //checks whether the two passwords typed in match
    private boolean passwordsMatch(String newPassword, String retypePassword) {

        return retypePassword.equals(newPassword);
    }

    //update password in the database
    private void changePassword( String password, int id) {

        String SqlUpdate = "UPDATE AdminLogin SET Password = ? WHERE ID = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(SqlUpdate);

            statement.setString(1, password);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement.close();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
