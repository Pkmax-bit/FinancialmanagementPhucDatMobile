package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TaskAttachment implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("task_id")
    private String taskId;
    
    @SerializedName("file_name")
    private String fileName;
    
    @SerializedName("original_file_name")
    private String originalFileName;
    
    @SerializedName("file_url")
    private String fileUrl;
    
    @SerializedName("file_type")
    private String fileType;
    
    @SerializedName("file_size")
    private Long fileSize;
    
    @SerializedName("uploaded_by")
    private String uploadedBy;
    
    @SerializedName("uploaded_by_name")
    private String uploadedByName;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("checklist_item_id")
    private String checklistItemId;

    public TaskAttachment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getFileUrl() { 
        // Loại bỏ dấu ? ở cuối URL nếu có
        if (fileUrl != null && fileUrl.endsWith("?")) {
            return fileUrl.substring(0, fileUrl.length() - 1);
        }
        return fileUrl; 
    }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }

    public String getUploadedByName() { return uploadedByName; }
    public void setUploadedByName(String uploadedByName) { this.uploadedByName = uploadedByName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getChecklistItemId() { return checklistItemId; }
    public void setChecklistItemId(String checklistItemId) { this.checklistItemId = checklistItemId; }
}

