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
    
    @SerializedName("parent_id")
    private String parentId;
    
    @SerializedName("is_pinned")
    private Boolean isPinned;
    
    @SerializedName("replies")
    private java.util.List<TaskComment> replies;
    
    @SerializedName("read_by")
    private java.util.List<String> readBy; // List of user IDs who read this message
    
    @SerializedName("read_count")
    private Integer readCount; // Number of users who read this message
    
    @SerializedName("reactions")
    private java.util.List<MessageReaction> reactions; // Reactions summary
    
    // Local-only fields for UI state (not from API)
    private transient String sendStatus; // "sending", "sent", "failed"
    private transient String tempId; // Temporary ID for pending messages

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
    
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    
    public Boolean getIsPinned() { return isPinned != null && isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }
    
    public java.util.List<TaskComment> getReplies() { return replies; }
    public void setReplies(java.util.List<TaskComment> replies) { this.replies = replies; }
    
    public String getSendStatus() { return sendStatus; }
    public void setSendStatus(String sendStatus) { this.sendStatus = sendStatus; }
    
    public String getTempId() { return tempId; }
    public void setTempId(String tempId) { this.tempId = tempId; }
    
    public java.util.List<String> getReadBy() { return readBy; }
    public void setReadBy(java.util.List<String> readBy) { this.readBy = readBy; }
    
    public Integer getReadCount() { return readCount != null ? readCount : 0; }
    public void setReadCount(Integer readCount) { this.readCount = readCount; }
    
    public java.util.List<MessageReaction> getReactions() { return reactions; }
    public void setReactions(java.util.List<MessageReaction> reactions) { this.reactions = reactions; }
    
    public String getDisplayName() {
        if (employeeName != null && !employeeName.isEmpty()) return employeeName;
        if (userName != null && !userName.isEmpty()) return userName;
        return "Người dùng";
    }
}
