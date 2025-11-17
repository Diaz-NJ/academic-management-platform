package com.ptc.amp.controllers;

import com.ptc.amp.dao.TaskDAO;
import com.ptc.amp.models.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/api/tasks/*")
public class TaskServlet extends HttpServlet {
    private TaskDAO taskDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        taskDAO = new TaskDAO();
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
    }
    
    // GET - Retrieve tasks
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all tasks for a user
                int userId = Integer.parseInt(request.getParameter("userId"));
                List<Task> tasks = taskDAO.getTasksByUserId(userId);
                
                String json = gson.toJson(tasks);
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(json);
                
            } else {
                // Get specific task by ID
                String[] splits = pathInfo.split("/");
                int taskId = Integer.parseInt(splits[1]);
                
                Task task = taskDAO.getTaskById(taskId);
                
                if (task != null) {
                    String json = gson.toJson(task);
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(json);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Task not found\"}");
                }
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // POST - Create new task
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            // Read JSON from request body
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            Task task = gson.fromJson(sb.toString(), Task.class);
            
            boolean success = taskDAO.createTask(task);
            
            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"message\": \"Task created successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to create task\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // PUT - Update task
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            // Read JSON from request body
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            
            Task task = gson.fromJson(sb.toString(), Task.class);
            
            boolean success = taskDAO.updateTask(task);
            
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"message\": \"Task updated successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to update task\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // DELETE - Delete task
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            String pathInfo = request.getPathInfo();
            String[] splits = pathInfo.split("/");
            int taskId = Integer.parseInt(splits[1]);
            
            boolean success = taskDAO.deleteTask(taskId);
            
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"message\": \"Task deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"error\": \"Failed to delete task\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
}