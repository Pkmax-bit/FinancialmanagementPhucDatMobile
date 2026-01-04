package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class TaskChecklist implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("task_id")
    private String taskId;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("progress")
    private float progress;
    
    @SerializedName("items")
    private List<TaskChecklistItem> items;

    public TaskChecklist() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public float getProgress() { return progress; }
    public void setProgress(float progress) { this.progress = progress; }

    public List<TaskChecklistItem> getItems() { return items; }
    public void setItems(List<TaskChecklistItem> items) { this.items = items; }

    public static class TaskChecklistItem implements Serializable {
        @SerializedName("id")
        private String id;
        
        @SerializedName("content")
        private String title;
        
        @SerializedName("is_completed")
        private boolean isCompleted;
        
        @SerializedName("assignee_id")
        private String assigneeId;
        
        @SerializedName("assignee_name")
        private String assigneeName;

        @SerializedName("assignments")
        private List<ChecklistItemAssignment> assignments;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public boolean isCompleted() { return isCompleted; }
        public void setCompleted(boolean completed) { isCompleted = completed; }

        public String getAssigneeId() { return assigneeId; }
        public void setAssigneeId(String assigneeId) { this.assigneeId = assigneeId; }

        public String getAssigneeName() { return assigneeName; }
        public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }

        public List<ChecklistItemAssignment> getAssignments() { return assignments; }
        public void setAssignments(List<ChecklistItemAssignment> assignments) { this.assignments = assignments; }
    }
}
