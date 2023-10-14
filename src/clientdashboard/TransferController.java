package clientdashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import savvytnoum.DbConnection;
import savvytnoum.LoginController;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TransferController implements Initializable {

    @FXML
    private TextField receivingAccount;
    @FXML
    private TextField transferAmount;
    @FXML
    private PasswordField password;
    @FXML
    private Label numericCheck;
    @FXML
    private Label wrongPassword;

    private final int clientID = LoginController.setClientID();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void transfer(ActionEvent event) {

        //checks whether all fields are filled
        if(fieldsNotEmpty()) {

            //checks whether receiving account  and transfer amount provided consist of digits only
            if(isNumeric(receivingAccount.getText(), transferAmount.getText())) {

                long receivingAccountNum = Long.parseLong(receivingAccount.getText());
                long transferAmt = Long.parseLong(transferAmount.getText());
                long transferAccountNum = LoginController.setAccountNumber();

                //System.out.println(receivingAccountNum);
                //System.out.println(transferAccountNum);

                //checks whether the  receiving account number exist in the database
                if(numberExists(receivingAccountNum)) {

                    //checks whether the provided password is correct
                    if(correctPassword(password.getText(),clientID)) {

                        //checks transfer account balance to see if there is enough money to transfer
                        if(enoughMoney(transferAccountNum, transferAmt)) {

                            deductTransferAccount(transferAmt, transferAccountNum);
                            depositReceivingAccount(receivingAccountNum, transferAmt);

                            //shows a successful withdrawal
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(null);
                            alert.setHeaderText(null);
                            alert.setContentText("Successfully transferred");
                            alert.showAndWait();

                        }
                        else {
                            //alerts user that not enough money in the account
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle(null);
                            alert.setHeaderText(null);
                            alert.setContentText("Not enough money in the account!");
                            alert.show();
                        }

                    }
                    else {
                        wrongPassword.setText("Incorrect Password!");
                    }

                }
                else {
                    //alerts user to enter an  account that exists in the database
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText("Account number not in the database!");
                    alert.show();
                }

            }
            else {
                //prompts user to enter positive digits only
                numericCheck.setText("Enter digits only!");
            }

            //clear form
            this.receivingAccount.clear();
            this.transferAmount.clear();
            this.password.clear();




        }
        else {
            //create an alert box when any text field is not filled
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the fields");
            alert.show();
        }

    }

    private void deductTransferAccount(long transferAmt, long transferAccount) {

        String sqlUpdate = "UPDATE ClientData  SET Balance = Balance - ? WHERE Acc_Num = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlUpdate);

            statement.setLong(1, transferAmt);
            statement.setLong(2, transferAccount);


            statement.executeUpdate();

            //close db connection
            statement.close();
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
            statement.close();
            connection.close();


        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }


    //verifies that fields are filled
    private boolean fieldsNotEmpty() {

        if(receivingAccount.getText().isEmpty() || transferAmount.getText().isEmpty() || password.getText().isEmpty() )
        {
            return false;
        }
        else {
            return true;
        }

    }

    //checks whether user input are numerals only
    private boolean isNumeric(String receivingAccount, String transferAmount) {

        if(transferAmount.contains("-") || transferAmount.contains("+")
                || receivingAccount.contains("-") || receivingAccount.contains("+"))
        {
            return false;
        }
        else{

            try {
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
            return false;
        }

        if(recordCount > 0) {
            System.out.println(recordCount + " match");
            return true;
        }
        else {
            return false;
        }


    }

    //checks whether the password provided matches the one in the database
    private boolean correctPassword(String userProvidedPassword, int id) {

        String accountPassword = null;
        String sqlQuery = "SELECT Password FROM ClientLogin WHERE ID = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {

                accountPassword = resultSet.getString(1);
            }

            //close
            //close
            connection.close();
            statement.close();
            resultSet.close();



        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if(userProvidedPassword.equals(accountPassword)) {

            return true;
        }
        else {

            return false;
        }

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


}
