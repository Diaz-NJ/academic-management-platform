package com.ptc.amp.controllers;

import com.ptc.amp.dao.UserDAO;
import com.ptc.amp.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

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
        
        // Read JSON from request body
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        
        JsonObject json = gson.fromJson(sb.toString(), JsonObject.class);
        String email = json.get("email").getAsString();
        String password = json.get("password").getAsString();
        
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
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response, PrintWriter out) 
            throws IOException {
        
        // Read JSON from request body
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            sb.append(line);
        }
        
        JsonObject json = gson.fromJson(sb.toString(), JsonObject.class);
        
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
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response, PrintWriter out) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        response.setStatus(HttpServletResponse.SC_OK);
        out.print("{\"success\": true, \"message\": \"Logged out successfully\"}");
    }
    
    // GET /api/auth/session - Check session
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
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
        
        out.flush();
    }
}