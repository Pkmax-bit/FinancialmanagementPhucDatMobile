package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TaskParticipant implements Serializable {
    @SerializedName("id")
    private String id;
    
    @SerializedName("task_id")
    private String taskId;
    
    @SerializedName("employee_id")
    private String employeeId;
    
    @SerializedName("role")
    private String role; // responsible, participant, observer
    
    @SerializedName("employee_name")
    private String employeeName;

    public TaskParticipant() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getRoleDisplayName() {
        if (role == null) return "Thành viên";
        switch (role) {
            case "responsible": return "Chịu trách nhiệm";
            case "participant": return "Tham gia";
            case "observer": return "Quan sát";
            default: return role;
        }
    }
}
