package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/internet_cafe";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Loaded");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load JDBC driver");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to database...");
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}