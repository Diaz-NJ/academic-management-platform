package com.ptc.amp.controllers;

import com.ptc.amp.dao.UserDAO;
import com.ptc.amp.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        System.out.println("========================================");
        System.out.println("AuthServlet initializing...");
        System.out.println("========================================");
        
        try {
            userDAO = new UserDAO();
            gson = new Gson();
            System.out.println("✓ AuthServlet initialized successfully");
        } catch (Exception e) {
            System.err.println("✗ AuthServlet initialization failed!");
            e.printStackTrace();
            throw new ServletException("Failed to initialize AuthServlet", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Log the request
        String pathInfo = request.getPathInfo();
        System.out.println("\n=== POST Request Received ===");
        System.out.println("Path: " + request.getRequestURI());
        System.out.println("PathInfo: " + pathInfo);
        System.out.println("Method: " + request.getMethod());
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Enable CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        PrintWriter out = response.getWriter();
        
        try {
            if ("/login".equals(pathInfo)) {
                System.out.println("→ Handling login");
                handleLogin(request, response, out);
            } else if ("/register".equals(pathInfo)) {
                System.out.println("→ Handling registration");
                handleRegister(request, response, out);
            } else if ("/logout".equals(pathInfo)) {
                System.out.println("→ Handling logout");
                handleLogout(request, response, out);
            } else {
                System.out.println("✗ Unknown endpoint: " + pathInfo);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Endpoint not found: " + pathInfo + "\"}");
            }
        } catch (Exception e) {
            System.err.println("✗ Error processing request:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }
        
        out.flush();
        System.out.println("=== Request Complete ===\n");
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        
        try {
            // Read JSON from request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            String requestBody = sb.toString();
            System.out.println("Request body: " + requestBody);
            
            if (requestBody.isEmpty()) {
                System.out.println("✗ Empty request body");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Request body is empty\"}");
                return;
            }
            
            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();
            
            if (!json.has("email") || !json.has("password")) {
                System.out.println("✗ Missing email or password");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Email and password are required\"}");
                return;
            }
            
            String email = json.get("email").getAsString();
            String password = json.get("password").getAsString();
            
            System.out.println("Login attempt for: " + email);
            
            if (email.trim().isEmpty() || password.trim().isEmpty()) {
                System.out.println("✗ Empty email or password");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Email and password cannot be empty\"}");
                return;
            }
            
            User user = userDAO.authenticateUser(email, password);
            
            if (user != null) {
                System.out.println("✓ Login successful for: " + user.getEmail());
                
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("userName", user.getFullName());
                session.setAttribute("userEmail", user.getEmail());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                
                // Return user data
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("success", true);
                responseJson.addProperty("userId", user.getUserId());
                responseJson.addProperty("name", user.getFullName());
                responseJson.addProperty("email", user.getEmail());
                responseJson.addProperty("studentId", user.getStudentId());
                responseJson.addProperty("section", user.getSection());
                
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(responseJson));
            } else {
                System.out.println("✗ Invalid credentials for: " + email);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\": \"Invalid email or password\"}");
            }
            
        } catch (JsonSyntaxException e) {
            System.err.println("✗ Invalid JSON format");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid JSON format\"}");
        } catch (Exception e) {
            System.err.println("✗ Login error:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Login failed: " + e.getMessage() + "\"}");
        }
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        
        try {
            // Read JSON from request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            String requestBody = sb.toString();
            System.out.println("Registration request body: " + requestBody);
            
            if (requestBody.isEmpty()) {
                System.out.println("✗ Empty request body");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Request body is empty\"}");
                return;
            }
            
            JsonObject json = JsonParser.parseString(requestBody).getAsJsonObject();
            
            // Validate required fields
            String[] requiredFields = {"email", "password", "studentId", "firstName", "lastName", "section"};
            for (String field : requiredFields) {
                if (!json.has(field) || json.get(field).getAsString().trim().isEmpty()) {
                    System.out.println("✗ Missing field: " + field);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"" + field + " is required\"}");
                    return;
                }
            }
            
            String email = json.get("email").getAsString();
            String studentId = json.get("studentId").getAsString();
            
            System.out.println("Registration attempt for: " + email + " (" + studentId + ")");
            
            // Check if user already exists
            if (userDAO.getUserByEmail(email) != null) {
                System.out.println("✗ Email already exists: " + email);
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print("{\"error\": \"Email already registered\"}");
                return;
            }
            
            if (userDAO.getUserByStudentId(studentId) != null) {
                System.out.println("✗ Student ID already exists: " + studentId);
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print("{\"error\": \"Student ID already registered\"}");
                return;
            }
            
            // Create new user
            User user = new User();
            user.setStudentId(studentId);
            user.setFirstName(json.get("firstName").getAsString());
            user.setLastName(json.get("lastName").getAsString());
            user.setEmail(email);
            user.setSection(json.get("section").getAsString());
            
            String password = json.get("password").getAsString();
            
            boolean success = userDAO.createUser(user, password);
            
            if (success) {
                System.out.println("✓ Registration successful for: " + email);
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"success\": true, \"message\": \"Registration successful\"}");
            } else {
                System.out.println("✗ Registration failed for: " + email);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Registration failed\"}");
            }
            
        } catch (JsonSyntaxException e) {
            System.err.println("✗ Invalid JSON format");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid JSON format\"}");
        } catch (Exception e) {
            System.err.println("✗ Registration error:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Registration failed: " + e.getMessage() + "\"}");
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String userEmail = (String) session.getAttribute("userEmail");
                System.out.println("Logout for: " + userEmail);
                session.invalidate();
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"success\": true, \"message\": \"Logged out successfully\"}");
        } catch (Exception e) {
            System.err.println("✗ Logout error:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Logout failed: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        System.out.println("\n=== GET Request: " + pathInfo + " ===");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        PrintWriter out = response.getWriter();
        
        try {
            if ("/session".equals(pathInfo) || pathInfo == null) {
                HttpSession session = request.getSession(false);
                
                if (session != null && session.getAttribute("userId") != null) {
                    JsonObject json = new JsonObject();
                    json.addProperty("authenticated", true);
                    json.addProperty("userId", (Integer) session.getAttribute("userId"));
                    json.addProperty("userName", (String) session.getAttribute("userName"));
                    json.addProperty("userEmail", (String) session.getAttribute("userEmail"));
                    
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(gson.toJson(json));
                    System.out.println("✓ Session valid");
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"authenticated\": false}");
                    System.out.println("✗ No valid session");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Endpoint not found\"}");
            }
        } catch (Exception e) {
            System.err.println("✗ Session check error:");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Session check failed: " + e.getMessage() + "\"}");
        }
        
        out.flush();
        System.out.println("=== Request Complete ===\n");
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}