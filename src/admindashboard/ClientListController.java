package admindashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import savvytnoum.DbConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ClientListController implements Initializable {

    @FXML
    private TableView<ClientData> clientTable;
    @FXML
    private TableColumn<ClientData, Integer> idColumn;
    @FXML
    private TableColumn<ClientData, String> firstnameColumn;
    @FXML
    private TableColumn<ClientData, String> lastnameColumn;
    @FXML
    private TableColumn<ClientData, String> dobColumn;
    @FXML
    private TableColumn<ClientData, String> genderColumn;
    @FXML
    private TableColumn<ClientData, String> emailColumn;
    @FXML
    private TableColumn<ClientData, String> accTypeColumn;
    @FXML
    private TableColumn<ClientData, Long> accNumColumn;
    @FXML
    private TableColumn<ClientData, Long> balanceColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadData();

    }

    //TODO add search functionality

    //load data from database
    public void loadData() {

        String query = "SELECT ID, Firstname, Lastname, DOB, Gender, Email, Acc_Type, Acc_Num, Balance FROM ClientData";

        try {
            Connection connection = DbConnection.getDatabase();
            ObservableList<ClientData> data = FXCollections.observableArrayList();

            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while(resultSet.next()) {
                data.add(new ClientData(resultSet.getInt("ID"),
                        resultSet.getString("Firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getString("DOB"),
                        resultSet.getString("Gender"),
                        resultSet.getString("Email"),
                        resultSet.getString("Acc_Type"),
                        resultSet.getLong("Acc_Num"),
                        resultSet.getLong("Balance")
                ));

            }



            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
            genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            accTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accType"));
            accNumColumn.setCellValueFactory(new PropertyValueFactory<>("accNum"));
            balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));

            clientTable.setItems(data);

            connection.close();


        }
        catch (SQLException e) {
            System.err.println("Error " + e);
            e.printStackTrace();
        }

    }

}
