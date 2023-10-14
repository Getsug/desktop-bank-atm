package savvytnoum;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    public static Connection getDatabase() throws SQLException {

        Connection conn = null;

        try {
            Class.forName("org.sqlite.JDBC");

            conn = DriverManager.getConnection("jdbc:sqlite:BankDb.db");
        }
        catch (ClassNotFoundException e)  {
            e.printStackTrace();
        }

        return conn;
    }
}

