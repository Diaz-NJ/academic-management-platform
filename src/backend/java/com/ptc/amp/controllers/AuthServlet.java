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
        userDAO = new UserDAO();
        gson = new Gson();
    }
    
    // POST /api/auth/login - Login
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Enable CORS if needed
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/login".equals(pathInfo)) {
                handleLogin(request, response, out);
            } else if ("/register".equals(pathInfo)) {
                handleRegister(request, response, out);
            } else if ("/logout".equals(pathInfo)) {
                handleLogout(request, response, out);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Endpoint not found\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
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
            
            if (sb.length() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Request body is empty\"}");
                return;
            }
            
            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
            
            if (!json.has("email") || !json.has("password")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Email and password are required\"}");
                return;
            }
            
            String email = json.get("email").getAsString();
            String password = json.get("password").getAsString();
            
            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Email and password cannot be empty\"}");
                return;
            }
            
            User user = userDAO.authenticateUser(email, password);
            
            if (user != null) {
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("userName", user.getFullName());
                session.setAttribute("userEmail", user.getEmail());
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
                
                // Return user data (without password)
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
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\": \"Invalid email or password\"}");
            }
            
        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid JSON format\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Login failed: " + e.getMessage() + "\"}");
            e.printStackTrace();
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
            
            if (sb.length() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Request body is empty\"}");
                return;
            }
            
            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
            
            // Validate required fields
            String[] requiredFields = {"email", "password", "studentId", "firstName", "lastName", "section"};
            for (String field : requiredFields) {
                if (!json.has(field) || json.get(field).getAsString().trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"" + field + " is required\"}");
                    return;
                }
            }
            
            // Check if user already exists
            String email = json.get("email").getAsString();
            if (userDAO.getUserByEmail(email) != null) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print("{\"error\": \"Email already registered\"}");
                return;
            }
            
            String studentId = json.get("studentId").getAsString();
            if (userDAO.getUserByStudentId(studentId) != null) {
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
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"success\": true, \"message\": \"Registration successful\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Registration failed\"}");
            }
            
        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Invalid JSON format\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Registration failed: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"success\": true, \"message\": \"Logged out successfully\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Logout failed: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
    
    // GET /api/auth/session - Check session
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Enable CORS if needed
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        PrintWriter out = response.getWriter();
        
        try {
            HttpSession session = request.getSession(false);
            
            if (session != null && session.getAttribute("userId") != null) {
                JsonObject json = new JsonObject();
                json.addProperty("authenticated", true);
                json.addProperty("userId", (Integer) session.getAttribute("userId"));
                json.addProperty("userName", (String) session.getAttribute("userName"));
                json.addProperty("userEmail", (String) session.getAttribute("userEmail"));
                
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson(json));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"authenticated\": false}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Session check failed: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // Handle OPTIONS request for CORS
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}