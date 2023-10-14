package admindashboard;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import savvytnoum.LoginController;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class AdminController implements Initializable {

    //Buttons

    //fxml id
    @FXML
    private BorderPane borderPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    @FXML
    public void createAccount(ActionEvent event) {
        loadUi("createNewAccount.fxml");
    }

    @FXML
    public void clientList(ActionEvent event) {
        loadUi("clientList.fxml");
    }

    @FXML
    public void deposit(ActionEvent event) {
        loadUi("Deposit.fxml");
    }
    @FXML
    public void withdraw(ActionEvent event){
        loadUi("withdraw.fxml");
    }
    @FXML
    public void transfer(ActionEvent event){
        loadUi("transfer.fxml");
    }
    @FXML
    public void settings(ActionEvent event) throws IOException {

        Stage stage = new Stage();

        Parent root = FXMLLoader.load(getClass().getResource("settings.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Settings");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();

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

            //show loginPage
            LoginController.adminStage.show();

        }
    }



    //load fxml
    public void loadUi(String name) {

        Pane root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(name));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        borderPane.setCenter(root);

    }


}
