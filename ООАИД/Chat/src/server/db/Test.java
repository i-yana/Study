package server.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Yana on 03.12.15.
 */
public class Test {

    private static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/test", "", "");
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private static void createDbUserTable() throws SQLException {
        Connection dbConnection = null;
        Statement statement = null;

        String createTableSQL = "CREATE TABLE DBUSER("
                + "USER_ID INT(5) NOT NULL, "
                + "USERNAME CHAR(20) NOT NULL, "
                + "CREATED_BY CHAR(20) NOT NULL, "
                + "PRIMARY KEY (USER_ID) "
                + ")";
        String insertTableSQL = "INSERT INTO DBUSER"
                + "(USER_ID, USERNAME, CREATED_BY) " + "VALUES"
                + "(1,'mkyong','system')";

        String dropTableSQL = "DROP TABLE DBUSER";
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.createStatement();

            // выполнить SQL запрос
            statement.execute(dropTableSQL);
            statement.execute(createTableSQL);
            statement.execute(insertTableSQL);
            System.out.println("Table \"dbuser\" is created!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    private static String getCurrentTimeStamp() {
        Date today = new Date();
        return DateFormat.getInstance().format(today.getTime());
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        try {
            createDbUserTable();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
