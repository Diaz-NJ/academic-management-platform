package com.ptc.amp.controllers;

import com.ptc.amp.config.DatabaseConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

@WebServlet("/api/test")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><body>");
        out.println("<h1>Test Results</h1>");
        
        // Test database connection
        try {
            out.println("<p>Testing database connection...</p>");
            Connection conn = DatabaseConfig.getConnection();
            
            if (conn != null) {
                out.println("<p style='color:green;'>✓ Database connected successfully!</p>");
                out.println("<p>Connection: " + conn + "</p>");
                DatabaseConfig.closeConnection();
            } else {
                out.println("<p style='color:red;'>✗ Database connection failed!</p>");
            }
        } catch (Exception e) {
            out.println("<p style='color:red;'>✗ Error: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.println("</body></html>");
    }
}