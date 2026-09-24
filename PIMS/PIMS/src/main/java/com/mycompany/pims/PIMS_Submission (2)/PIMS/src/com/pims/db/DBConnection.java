package com.pims.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place for obtaining a JDBC connection to the pims_db MySQL database.
 * Update URL / USER / PASS to match your local MySQL setup.
 */
public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/pims_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root"; // <-- change to your MySQL root password

    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    /** Returns a live connection, creating one if necessary or if the old one closed. */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASS);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
