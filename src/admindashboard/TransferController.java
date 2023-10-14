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

public class TransferController implements Initializable {

    @FXML
    private TextField transferAccount;
    @FXML
    private TextField receivingAccount;
    @FXML
    private TextField transferAmount;
    @FXML
    private Label numericCheck;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }



    @FXML
    private void transfer(ActionEvent event) {

        //checks whether all fields are filled
        if(fieldsNotEmpty()) {

            //checks whether transfer account  transfer amount and  receiving account provided consist of digits only
            if(isNumeric(transferAccount.getText(), transferAmount.getText(), receivingAccount.getText())) {

                long transferAmt = Long.parseLong(transferAmount.getText());
                long account = Long.parseLong(transferAccount.getText());
                long receivingAcc  = Long.parseLong(receivingAccount.getText());

                //checks whether transfer account and receiving account numbers provided exist in the database
                if(numberExists(account) && numberExists(receivingAcc)) {

                    //checks transfer account to see if there is enough money to transfer
                    if(enoughMoney(account, transferAmt)) {

                        deductTransferAccount(transferAmt, account);
                        depositReceivingAccount(receivingAcc, transferAmt);


                        //shows a successful withdrawal
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(null);
                        alert.setHeaderText(null);
                        alert.setContentText("Successfully transferred");
                        alert.showAndWait();


                    }
                    else{
                        //alerts user that not enough money in the specified account
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle(null);
                        alert.setHeaderText(null);
                        alert.setContentText("Not enough money in the account!");
                        alert.showAndWait();

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

            }

            //clear form
            this.transferAccount.clear();
            this.receivingAccount.clear();
            this.transferAmount.clear();


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


    private void deductTransferAccount(long transferAmt, long account) {

        String sqlUpdate = "UPDATE ClientData  SET Balance = Balance - ? WHERE Acc_Num = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlUpdate);

            statement.setLong(1, transferAmt);
            statement.setLong(2, account);


            statement.executeUpdate();

            //close
            connection.close();

        }
        catch(SQLException e) {
            e.printStackTrace();
        }

    }

    private void depositReceivingAccount(long receivingAccount, long receivingAmount) {

        String sqlUpdate = "UPDATE ClientData  SET Balance = Balance + ? WHERE Acc_Num = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlUpdate);

            statement.setLong(1, receivingAmount);
            statement.setLong(2, receivingAccount);


            statement.executeUpdate();

            //close db connection
            connection.close();
            statement.close();

        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    //verifies that fields are filled
    private boolean fieldsNotEmpty() {

        if(transferAccount.getText().isEmpty() || transferAmount.getText().isEmpty() || receivingAccount.getText().isEmpty())
        {
            return false;
        }
        else {
            return true;
        }

    }

    //checks whether user input are numerals only
    private boolean isNumeric(String transferAccount, String transferAmount, String receivingAccount) {

        if(transferAccount.contains("-") || transferAccount.contains("+") || transferAmount.contains("-") || transferAmount.contains("+")
        || receivingAccount.contains("-") || receivingAccount.contains("+"))
        {
            return false;
        }
        else{

            try {
                Long.parseLong(transferAccount);
                Long.parseLong(transferAmount);
                Long.parseLong(receivingAccount);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    //check if there is enough money to withdraw
    private boolean enoughMoney(long transferAccountNum, long transferAmt) {

        long balance;
        String query = "SELECT Balance FROM ClientData WHERE Acc_Num = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement= connection.prepareStatement(query);

            statement.setLong(1, transferAccountNum);

            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            balance = resultSet.getLong(1);

            if(transferAmt > balance){
                return false;
            }

            //close
            statement.close();
            resultSet.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }


    //checks whether account exists in the database
    private boolean numberExists(long accNum) {

        String sqlCount = "SELECT COUNT(Acc_Num) FROM ClientData WHERE Acc_Num = ?";
        int recordCount = 0;

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlCount);

            statement.setLong(1, accNum);


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
        }


        if(recordCount > 0) {
            System.out.println(recordCount + " match");
            return true;
        }
        else {
            return false;
        }

    }


}
