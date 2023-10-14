package clientdashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import savvytnoum.DbConnection;
import savvytnoum.LoginController;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ChangeUsernameController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private Label emptyField;

    private final int clientID = LoginController.setClientID();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadUsername(clientID);

    }

    @FXML
    private void save(ActionEvent event) {

        //verifies that the txt field is not empty
        if(fieldsNotEmpty()) {

            //checks if username already exists in the database
            if(noUsernameExists(username.getText())) {

                changeUsername(username.getText(), clientID);

                //shows a successful update of info
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Username saved");
                alert.showAndWait();

            }
            else{
                //informs user that the username already exists
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Username already exists!");
                alert.show();
            }

            //clear text field
            username.clear();
        }
        else{
            emptyField.setText("Enter an username!");
        }

    }

    //verifies that fields are filled
    private boolean fieldsNotEmpty() {

        if(username.getText().isEmpty())
        {
            return false;
        }
        else {
            return true;
        }

    }

    //checks if the  the provided username exists in the database
    private boolean noUsernameExists(String username) {


        String sqlCount = "SELECT COUNT(Username) FROM ClientLogin WHERE Username = ?";
        int recordCount;

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlCount);

            statement.setString(1, username);


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


    //load username from database and display it on the text field
    private void loadUsername(int id) {

        String ExistingUsername = null;

        String query = "SELECT Username FROM ClientLogin WHERE ID = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            ExistingUsername = resultSet.getString(1);

            statement.close();
            resultSet.close();
            connection.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        username.setText(ExistingUsername);
    }

    //update username in the database
    private void changeUsername( String username, int id) {

        String SqlUpdate = "UPDATE ClientLogin SET Username = ? WHERE ID = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(SqlUpdate);

            statement.setString(1, username);
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
