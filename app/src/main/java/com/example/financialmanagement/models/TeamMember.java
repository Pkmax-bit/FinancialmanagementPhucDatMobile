package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class TeamMember implements Serializable {
    private String id;
    @SerializedName("project_id")
    private String projectId;
    private String name;
    private String role;
    @SerializedName("responsibility_type")
    private String responsibilityType;
    private String email;
    private String phone;
    @SerializedName("employee_id")
    private String employeeId;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("hourly_rate")
    private Double hourlyRate;
    private String status;
    private List<String> skills;
    private String avatar;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getResponsibilityType() { return responsibilityType; }
    public void setResponsibilityType(String responsibilityType) { this.responsibilityType = responsibilityType; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
