package com.ptc.amp.models;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String section;
    private LocalDateTime createdAt;
    
    // Constructors
    public User() {}
    
    public User(String studentId, String firstName, String lastName, 
                String email, String passwordHash, String section) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.section = section;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", studentId='" + studentId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", section='" + section + '\'' +
                '}';
    }
}