package admindashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import savvytnoum.DbConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DepositController implements Initializable {

    @FXML
    private TextField depositAccount;
    @FXML
    private TextField depositAmount;
    @FXML
    private Label numericCheck;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void deposit(ActionEvent event) {

        //checks whether all fields are filled
        if(fieldsNotEmpty()) {

            //checks whether account number provided consists of digits only
            if(isNumeric(depositAccount.getText(), depositAmount.getText())) {

                //checks whether account number provided exists in the database
                if(numberExists()) {

                    //Ensures only integers are input
                    long depositAmt = Long.parseLong(depositAmount.getText());
                    long account = Long.parseLong(depositAccount.getText());


                    String sqlUpdate = "UPDATE ClientData  SET Balance = Balance + ? WHERE Acc_Num = ?";

                    try {
                        Connection connection = DbConnection.getDatabase();

                        PreparedStatement statement = connection.prepareStatement(sqlUpdate);

                        statement.setLong(1, depositAmt);
                        statement.setLong(2, account);


                        statement.executeUpdate();

                        connection.close();
                        statement.close();

                        //clear form
                        this.depositAccount.clear();
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
                    //alerts user to enter an  account that exists in the database
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Account number not in the database!");
                    alert.showAndWait();

                }

            }
            else {
                //prompts user to enter positive digits only
                numericCheck.setText("Enter digits only!");
                depositAccount.clear();
                depositAmount.clear();
            }



        }
        else{
            //create an alert box when any text field is not filled
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
        }


    }

    //verifies that fields are filled
    private boolean fieldsNotEmpty() {

        if(depositAccount.getText().isEmpty() || depositAmount.getText().isEmpty())
        {
            return false;
        }
        else {
            return true;
        }

    }

    //checks whether account exists in the database
    private boolean numberExists() {

        long accNum = Long.parseLong(depositAccount.getText());

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
            System.out.println(recordCount + " match");
            return true;
        }
        else {
            return false;
        }

    }

    //checks whether user input are numerals only
    private boolean isNumeric(String depositAccount, String depositAmount) {

        if(depositAccount.contains("-") || depositAccount.contains("+") || depositAmount.contains("-") || depositAmount.contains("+")) {
            return  false;
        }
        else{

            try {
                Long.parseLong(depositAccount);
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
