package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TaskComment implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("task_id")
    private String taskId;
    
    @SerializedName("user_id")
    private String userId;
    
    @SerializedName("employee_id")
    private String employeeId;
    
    @SerializedName("comment")
    private String comment;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("user_name")
    private String userName;
    
    @SerializedName("employee_name")
    private String employeeName;
    
    @SerializedName("type")
    private String type; // text, file, image
    
    @SerializedName("file_url")
    private String fileUrl;

    public TaskComment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public String getDisplayName() {
        if (employeeName != null && !employeeName.isEmpty()) return employeeName;
        if (userName != null && !userName.isEmpty()) return userName;
        return "Người dùng";
    }
}
