package com.example.financialmanagement.models;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    private String id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String due_date;
    private String assignee_id;
    private String creator_id;
    private String project_id;
    private String created_at;

    // Related objects
    private Employee assignee;
    private Employee creator;
    private Project project;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDueDate() { return due_date; }
    public void setDueDate(String due_date) { this.due_date = due_date; }

    public String getAssigneeId() { return assignee_id; }
    public void setAssigneeId(String assignee_id) { this.assignee_id = assignee_id; }

    public String getCreatorId() { return creator_id; }
    public void setCreatorId(String creator_id) { this.creator_id = creator_id; }

    public String getProjectId() { return project_id; }
    public void setProjectId(String project_id) { this.project_id = project_id; }

    public String getCreatedAt() { return created_at; }
    public void setCreatedAt(String created_at) { this.created_at = created_at; }

    public Employee getAssignee() { return assignee; }
    public void setAssignee(Employee assignee) { this.assignee = assignee; }

    public Employee getCreator() { return creator; }
    public void setCreator(Employee creator) { this.creator = creator; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public String getStatusDisplayName() {
        switch (status) {
            case "todo": return "Cần làm";
            case "in_progress": return "Đang làm";
            case "review": return "Đang duyệt";
            case "done": return "Hoàn thành";
            default: return status;
        }
    }
    
    public String getPriorityDisplayName() {
        switch (priority) {
            case "low": return "Thấp";
            case "medium": return "Trung bình";
            case "high": return "Cao";
            case "urgent": return "Khẩn cấp";
            default: return priority;
        }
    }
}
