package savvytnoum;

import admindashboard.*;
import clientdashboard.*;
import clientdashboard.DepositController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public LoginModel loginModel = new LoginModel();

    @FXML
    private Label dbStatus;
    @FXML
    private Label loginStatus;
    @FXML
    private RadioButton admin;
    @FXML
    private RadioButton client;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button loginButton;
    @FXML
    private ToggleGroup user;

    //client info
    private static int clientID;
    private static String clientFirstname;
    private static long clientAccountNumber;

    //admin and client stage
    public static Stage adminStage;
    public static Stage clientStage;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(this.loginModel.isDbConnected()) {
            dbStatus.setText("Connected");
        }
        else {
            dbStatus.setText("Not Connected");
        }

    }

    public void Login(ActionEvent event){

        String option = getUserType();

        try {


            switch (option) {
                case "Admin":

                    if (loginModel.isAdminLogin(username.getText(), password.getText())) {

                        adminStage = (Stage) loginButton.getScene().getWindow();
                        //hide the stage
                        adminStage.hide();
                        username.clear();
                        password.clear();

                        //close the login window
                        //stage.close();

                        //open admin dashboard
                        adminLogin();
                    } else {
                        loginStatus.setText("Wrong username and password");
                        username.clear();
                        password.clear();
                    }

                    break;
                case "Client":

                    if (loginModel.isClientLogin(username.getText(), password.getText())) {


                        clientStage = (Stage) loginButton.getScene().getWindow();

                        //hide the stage
                        clientStage.hide();
                        username.clear();
                        password.clear();

                        //close the login window
                        //stage.close();

                        //open client dashboard
                        clientLogin();

                    } else {
                        loginStatus.setText("Wrong username and password");
                        username.clear();
                        password.clear();
                    }
                    break;
                case "none selected":
                    loginStatus.setText("Select user and fill all fields");
                    username.clear();
                    password.clear();
                    break;
            }


        } catch (Exception localException) {
            localException.printStackTrace();
        }


    }

    //method to open admin dashboard
    public void adminLogin() {

        Stage adminStage = new Stage();

        try {

            FXMLLoader adminLoader = new FXMLLoader();
            Pane adminRoot = (Pane)adminLoader.load(getClass().getResource("/admindashboard/admin.fxml").openStream());

            AdminController adminController = (AdminController)adminLoader.getController();

            Scene scene = new Scene(adminRoot);
            adminStage.setScene(scene);
            adminStage.setTitle("Admin Dashboard");
            adminStage.setResizable(true);
            adminStage.show();

            //prompts logout when the exit is pressed(x)
            adminStage.setOnCloseRequest(event -> {
                event.consume();
                logout(adminStage);
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    // method to open client dashboard
    public void clientLogin() {

        clientID = getClientID(username.getText(), password.getText());

        // loads Firstname and Account Number
        loadClientData(clientID);


        try {
            Stage clientStage = new Stage();
            FXMLLoader clientLoader = new FXMLLoader();
            Pane clientRoot = (Pane) clientLoader.load(getClass().getResource("/clientdashboard/client.fxml").openStream());

            ClientController clientController = (ClientController) clientLoader.getController();
            //sets the username on the client dashboard
            clientController.setClientName(clientFirstname);


            Scene scene = new Scene(clientRoot);
            clientStage.setScene(scene);
            clientStage.setTitle("Client Dashboard");
            clientStage.setResizable(true);
            clientStage.show();

            //prompts logout when the exit is pressed(x)
            clientStage.setOnCloseRequest(event -> {
                event.consume();
                logout(clientStage);
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Get data based on the radio button selected
    public String getUserType() {

        if(admin.isSelected()) {
            return "Admin";
        }
        else if(client.isSelected()) {
            return "Client";
        }
        else {
            return "none selected";
        }

    }

    private int  getClientID(String user, String pass) {


        int ID  = 0;  // Zero means no record in the database
        String  sqlQuery= "SELECT ID FROM ClientLogin WHERE Username = ? and Password = ?";

        try {

            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            statement.setString(1, user);
            statement.setString(2, pass);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                ID = resultSet.getInt(1);

            }

            //close
            connection.close();
            statement.close();
            resultSet.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return ID;
    }

    private void loadClientData(int id) {

        String sqlQuery = "SELECT Firstname, Acc_Num FROM ClientData WHERE ID = ?";

        try {
            Connection connection = DbConnection.getDatabase();

            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                clientFirstname = resultSet.getString(1);
                clientAccountNumber = resultSet.getLong(2);
            }

            //close
            connection.close();
            statement.close();
            resultSet.close();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //returns client account number
    public static long setAccountNumber() {
        return clientAccountNumber;
    }

    //returns client ID
    public static int setClientID() {
        return clientID;
    }


    //logout method
    private void logout(Stage stage) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {

            //close the admin window
            stage.close();
        }
    }



}
