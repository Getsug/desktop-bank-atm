package savvytnoum;

import java.sql.*;

public class LoginModel {

    //Admin info
    private static int adminID;


    Connection connection;

    public LoginModel () {

        try {
            this.connection = DbConnection.getDatabase();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        if(this.connection == null) {
            System.exit(1);
        }

    }

    public boolean isDbConnected() {
        return connection != null;
    }



    public boolean isAdminLogin(String username, String pass) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String sqlQuery = "SELECT * FROM AdminLogin where Username = ? and Password = ?";

        try {
            statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, username);
            statement.setString(2, pass);

            resultSet = statement.executeQuery();

            if(resultSet.next()) {

                adminID = resultSet.getInt(1);
                return  true;
            }

            return false;

        }
        catch (Exception e) {
            return  false;
        }
        finally {
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }


    public boolean isClientLogin(String username, String pass) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sqlQuery = "SELECT * FROM ClientLogin where Username = ? and Password = ?";

        try {
            ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, username);
            ps.setString(2, pass);

            rs = ps.executeQuery();

            if(rs.next()) {
                return  true;
            }

            return false;

        }
        catch (Exception e) {
            return  false;
        }
        finally {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    //returns admin ID
    public static int setAdminID() {
        return adminID;
    }

}
