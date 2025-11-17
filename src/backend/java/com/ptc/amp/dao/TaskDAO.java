package com.ptc.amp.dao;

import com.ptc.amp.config.DatabaseConfig;
import com.ptc.amp.models.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    
    // Create Task
    public boolean createTask(Task task) {
        String sql = "INSERT INTO tasks (user_id, title, description, subject, due_date, priority, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, task.getUserId());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setString(4, task.getSubject());
            pstmt.setTimestamp(5, Timestamp.valueOf(task.getDueDate()));
            pstmt.setString(6, task.getPriority());
            pstmt.setString(7, task.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating task: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all tasks for a user
    public List<Task> getTasksByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY due_date ASC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Task task = extractTaskFromResultSet(rs);
                tasks.add(task);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching tasks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tasks;
    }
    
    // Get task by ID
    public Task getTaskById(int taskId) {
        String sql = "SELECT * FROM tasks WHERE task_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTaskFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching task: " + e.getMessage());
e.printStackTrace();
}
return null;
}

// Update Task
public boolean updateTask(Task task) {
    String sql = "UPDATE tasks SET title = ?, description = ?, subject = ?, " +
                 "due_date = ?, priority = ?, status = ? WHERE task_id = ?";
    
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, task.getTitle());
        pstmt.setString(2, task.getDescription());
        pstmt.setString(3, task.getSubject());
        pstmt.setTimestamp(4, Timestamp.valueOf(task.getDueDate()));
        pstmt.setString(5, task.getPriority());
        pstmt.setString(6, task.getStatus());
        pstmt.setInt(7, task.getTaskId());
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.err.println("Error updating task: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Update Task Status
public boolean updateTaskStatus(int taskId, String status) {
    String sql = "UPDATE tasks SET status = ? WHERE task_id = ?";
    
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, status);
        pstmt.setInt(2, taskId);
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.err.println("Error updating task status: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Delete Task
public boolean deleteTask(int taskId) {
    String sql = "DELETE FROM tasks WHERE task_id = ?";
    
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, taskId);
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.err.println("Error deleting task: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

// Get tasks by status
public List<Task> getTasksByStatus(int userId, String status) {
    List<Task> tasks = new ArrayList<>();
    String sql = "SELECT * FROM tasks WHERE user_id = ? AND status = ? ORDER BY due_date ASC";
    
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, userId);
        pstmt.setString(2, status);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Task task = extractTaskFromResultSet(rs);
            tasks.add(task);
        }
        
    } catch (SQLException e) {
        System.err.println("Error fetching tasks by status: " + e.getMessage());
        e.printStackTrace();
    }
    
    return tasks;
}

// Helper method to extract Task from ResultSet
private Task extractTaskFromResultSet(ResultSet rs) throws SQLException {
    Task task = new Task();
    task.setTaskId(rs.getInt("task_id"));
    task.setUserId(rs.getInt("user_id"));
    task.setTitle(rs.getString("title"));
    task.setDescription(rs.getString("description"));
    task.setSubject(rs.getString("subject"));
    task.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
    task.setPriority(rs.getString("priority"));
    task.setStatus(rs.getString("status"));
    task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    task.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
    return task;
}
}