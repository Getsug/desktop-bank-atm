package admindashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private BorderPane borderPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void resetAdminPassword(ActionEvent event) {

        loadUserInterface("resetAdminPass.fxml");
    }
    @FXML
    private void resetClientPassword(ActionEvent event) {

        loadUserInterface("resetClientPass.fxml");
    }


    private void loadUserInterface(String name) {

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
