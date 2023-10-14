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

public class WithdrawController implements Initializable {

    @FXML
    private TextField withdrawAccount;
    @FXML
    private TextField withdrawAmount;
    @FXML
    private Label numericCheck;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void withdraw(ActionEvent event) {

        //checks whether all fields are filled
        if(fieldsNotEmpty()) {

            //checks whether account number  and amount provided consist of digits only
            if(isNumeric(withdrawAccount.getText(), withdrawAmount.getText())) {

                //checks whether account number provided exists in the database
                if(numberExists()) {

                    //Ensures only integers are input
                    long withdrawAmt = Long.parseLong(withdrawAmount.getText());
                    long account = Long.parseLong(withdrawAccount.getText());

                    //checks account balance to see if there is enough money to withdraw
                    if(enoughMoney(account, withdrawAmt)) {

                        String sqlUpdate = "UPDATE ClientData  SET Balance = Balance - ? WHERE Acc_Num = ?";

                        try {
                            Connection connection = DbConnection.getDatabase();

                            PreparedStatement statement = connection.prepareStatement(sqlUpdate);

                            statement.setLong(1, withdrawAmt);
                            statement.setLong(2, account);


                            statement.executeUpdate();
                            connection.close();

                            //clear form
                            this.withdrawAccount.clear();
                            this.withdrawAmount.clear();

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
                withdrawAccount.clear();
                withdrawAmount.clear();
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

        if(withdrawAccount.getText().isEmpty() || withdrawAmount.getText().isEmpty())
        {
            return false;
        }
        else {
            return true;
        }

    }

    //checks whether account exists in the database
    private boolean numberExists() {

        long accNum = Long.parseLong(withdrawAccount.getText());

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

    //checks whether user input are numerals only
    private boolean isNumeric(String accountNumber, String amount) {

        if(accountNumber.contains("-") || accountNumber.contains("+") || amount.contains("-") || amount.contains("+")) {
            return false;
        }
        else{

            try {
                Long.parseLong(accountNumber);
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
}
