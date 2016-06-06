package ru.nsu.fit.g13205.zharkova.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Created by Yana on 03.05.16.
 */
public class Client {

    public static void main(String[] args) {
        try {
            Locale.setDefault(Locale.ENGLISH);
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.65:1521:XE", "system", "zharkova");
            System.out.println("Success");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }
    }



}
