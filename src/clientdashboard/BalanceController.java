package clientdashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import savvytnoum.DbConnection;
import savvytnoum.LoginController;

import java.lang.ref.PhantomReference;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class BalanceController implements Initializable {

    @FXML
    private Label balanceDisplay;

    private final long accountNumber = LoginController.setAccountNumber();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        balanceDisplay.setText(Long.toString(getBalance(accountNumber)));

    }


    //methods

    //returns balance
    private long getBalance(long accountNumber) {

        long balance = 0;

        String sqlQuery = "SELECT Balance FROM ClientData WHERE Acc_Num = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            statement.setLong(1, accountNumber);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {

                balance = resultSet.getLong(1);
            }

            //close
            connection.close();
            statement.close();
            resultSet.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return balance;
    }


}
