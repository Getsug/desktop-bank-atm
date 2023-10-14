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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DepositController implements Initializable {

    @FXML
    private TextField depositAmount;
    @FXML
    private Label numericCheck;

    private final long accountNumber = LoginController.setAccountNumber();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML
    public void depositButton(ActionEvent event) {

        //checks whether all fields are filled
        if(fieldsNotEmpty()) {

            //checks whether amount provided consist of digits only
            if(isNumeric(depositAmount.getText())) {

                long depositAmt = Long.parseLong(depositAmount.getText());

                String sqlUpdate = "UPDATE ClientData  SET Balance = Balance + ? WHERE Acc_Num = ?";

                try {
                    Connection connection = DbConnection.getDatabase();

                    PreparedStatement statement = connection.prepareStatement(sqlUpdate);

                    statement.setLong(1, depositAmt);
                    statement.setLong(2, accountNumber);


                    statement.executeUpdate();

                    connection.close();
                    statement.close();

                    //clear form
                    this.depositAmount.clear();

                    //shows a successful deposit
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully deposited");
                    alert.showAndWait();

                }
                catch(SQLException e) {
                    e.printStackTrace();
                }

            }
            else {

                //prompts user to enter positive digits only
                numericCheck.setText("Enter positive digits only!");
                depositAmount.clear();

            }

        }
        else {
            //create an alert box when the text field is not filled
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Please type the amount");
            alert.showAndWait();
        }

    }

    //verifies that fields are filled
    private boolean fieldsNotEmpty() {

        if(depositAmount.getText().isEmpty())
        {
            return false;
        }
        else {
            return true;
        }

    }

    //checks whether user input positive numerals only
    private boolean isNumeric(String depositAmount) {

        if(depositAmount.contains("-") || depositAmount.contains("+")) {
            return  false;
        }
        else{

            try {

                Long.parseLong(depositAmount);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }

        }

        return true;
    }


}
