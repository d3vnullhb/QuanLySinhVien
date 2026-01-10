package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
        "jdbc:sqlserver://localhost:1433;"
      + "databaseName=QuanLySinhVien;"
      + "encrypt=true;trustServerCertificate=true";

    private static final String USER = "sa";      
    private static final String PASS = "106204";  

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
