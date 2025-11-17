package com.ptc.amp;

import com.ptc.amp.config.DatabaseConfig;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        Connection conn = DatabaseConfig.getConnection();
        
        if (conn != null) {
            System.out.println("✓ Database connected successfully!");
            DatabaseConfig.closeConnection();
        } else {
            System.out.println("✗ Failed to connect to database");
        }
    }
}