package com.ptc.amp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    // Try different connection strings
    private static final String URL = "jdbc:mysql://localhost:3306/academic_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Explicitly load MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                System.out.println("Attempting to connect to database...");
                System.out.println("URL: " + URL);
                System.out.println("User: " + USER);
                
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                
                System.out.println("✓ Database connected successfully!");
                return connection;
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL Driver not found!");
            System.err.println("Make sure mysql-connector-java-8.0.33.jar is in lib/ folder");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed!");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nTroubleshooting:");
            System.err.println("1. Is MySQL running? Check with: sc query MySQL80");
            System.err.println("2. Is the password correct? Try: mysql -u root -p");
            System.err.println("3. Does the database exist? Run: CREATE DATABASE academic_management_db;");
            e.printStackTrace();
        }
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    // Test method
    public static void main(String[] args) {
        System.out.println("Testing database connection...\n");
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("\n✓ SUCCESS! Connection test passed.");
            closeConnection();
        } else {
            System.out.println("\n✗ FAILED! Could not connect to database.");
        }
    }
}