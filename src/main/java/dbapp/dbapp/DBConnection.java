package dbapp.dbapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static String url;
    private static String username;
    private static String password;

    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    public DBConnection(String url, String user, String password) {
        DBConnection.url = url;
        DBConnection.username = user;
        DBConnection.password = password;
    }


    public Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(url, username, password);
    }
}