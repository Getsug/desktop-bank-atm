package clientdashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import savvytnoum.LoginController;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private Label clientName;
    @FXML
    private BorderPane borderPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    @FXML
    public void deposit(ActionEvent event) {
        loadUserInterface("deposit");
    }
    @FXML
    private void withdraw(ActionEvent event) {
        loadUserInterface("withdraw");
    }
    @FXML
    private void transfer(ActionEvent event) {
        loadUserInterface("transfer");
    }
    @FXML
    private void balance(ActionEvent event) {
        loadUserInterface("balance");
    }
    @FXML
    private void settings(ActionEvent event) {


        loadUserInterface("settings");
    }

    @FXML
    private void logout(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit?");

        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            //close the admin window
            stage.close();

            //reveal the loginPage
            LoginController.clientStage.show();

        }
    }




    private void loadUserInterface(String name){

        try {

            Stage stage = new Stage();

            Pane root = FXMLLoader.load(getClass().getResource(name + ".fxml"));

            Scene scene = new Scene(root);

            stage.setTitle(name);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }



    //display client name on login
    //name is set on the LoginController
    public void setClientName(String name) {
        clientName.setText(name);
    }




}
