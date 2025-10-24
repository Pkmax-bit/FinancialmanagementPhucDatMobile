package com.example.financialmanagement.models;

import java.util.List;

/**
 * Project Response Model - Mô hình response từ API projects
 * Tương ứng với cấu trúc response từ server
 */
public class ProjectResponse {
    private boolean success;
    private List<Project> projects;
    private int total;
    private String message;
    
    // Constructors
    public ProjectResponse() {}
    
    public ProjectResponse(boolean success, List<Project> projects, int total) {
        this.success = success;
        this.projects = projects;
        this.total = total;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public List<Project> getProjects() {
        return projects;
    }
    
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "ProjectResponse{" +
                "success=" + success +
                ", projects=" + (projects != null ? projects.size() : 0) +
                ", total=" + total +
                ", message='" + message + '\'' +
                '}';
    }
}
