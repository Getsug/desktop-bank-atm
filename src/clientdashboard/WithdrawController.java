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

public class WithdrawController implements Initializable {

    @FXML
    private TextField withdrawAmount;
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
    private void withdraw(ActionEvent event) {

        //checks whether all fields are filled
        if(fieldsNotEmpty()) {

            //checks whether amount provided consist of digits only
            if(isNumeric(withdrawAmount.getText())) {

                //checks whether the provided password is correct
                if(correctPassword(password.getText(), clientID)) {

                    long withdrawAmt = Long.parseLong(withdrawAmount.getText());
                    long accountNum = LoginController.setAccountNumber();

                    //checks account balance to see if there is enough money to withdraw
                    if(enoughMoney(accountNum, withdrawAmt)) {

                        String sqlUpdate = "UPDATE ClientData  SET Balance = Balance - ? WHERE Acc_Num = ?";

                        try {
                            Connection connection = DbConnection.getDatabase();

                            PreparedStatement statement = connection.prepareStatement(sqlUpdate);

                            statement.setLong(1, withdrawAmt);
                            statement.setLong(2, accountNum);


                            statement.executeUpdate();
                            connection.close();

                            //clear form
                            this.withdrawAmount.clear();
                            this.password.clear();

                            //shows a successful withdrawal
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle(null);
                            alert.setHeaderText(null);
                            alert.setContentText("Successfully withdrawn");
                            alert.showAndWait();

                        }
                        catch(SQLException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        //alerts user that not enough money in the specified account
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle(null);
                        alert.setHeaderText(null);
                        alert.setContentText("Not enough money in the account!");
                        alert.show();

                        //clear
                        withdrawAmount.clear();
                        password.clear();
                    }



                }
                else {
                    wrongPassword.setText("Incorrect Password!");

                    //clear
                    withdrawAmount.clear();
                    password.clear();

                }


            }
            else {
                numericCheck.setText("Enter positive digits only!");

                //clear
                withdrawAmount.clear();
                password.clear();
            }



        }
        else {
            //create an alert box when any text field is not filled
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Please fill all the fields");
            alert.showAndWait();
        }


    }

    //methods

    //verifies that fields are filled
    private boolean fieldsNotEmpty() {

        if(withdrawAmount.getText().isEmpty() || password.getText().isEmpty())
        {
            return false;
        }
        else {
            return true;
        }

    }

    //checks whether user input in the amount field numerals only
    private boolean isNumeric(String amount) {

        if(amount.contains("-") || amount.contains("+")) {
            return false;
        }
        else{

            try {

                Long.parseLong(amount);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    //check if there is enough money to withdraw
    private boolean enoughMoney(long accountNum, long withdrawAmt) {

        long balance;
        String query = "SELECT Balance FROM ClientData WHERE Acc_Num = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(query);

            statement.setLong(1, accountNum);

            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            balance = resultSet.getLong(1);

            if(withdrawAmt > balance){
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

            if(userProvidedPassword.equals(accountPassword)) {
                //close
                connection.close();
                statement.close();
                resultSet.close();

                return true;
            }
            else {
                //close
                connection.close();
                statement.close();
                resultSet.close();

                return false;
            }


        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

}
