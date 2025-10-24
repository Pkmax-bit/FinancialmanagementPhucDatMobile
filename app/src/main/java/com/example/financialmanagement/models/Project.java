package com.example.financialmanagement.models;

import java.util.Date;

/**
 * Project Model - Mô hình dữ liệu dự án
 * Tương ứng với API endpoint /api/projects
 */
public class Project {
    private String id;
    private String name;
    private String projectCode;
    private String description;
    private String status;
    private String priority;
    private Double budget;
    private Date startDate;
    private Date endDate;
    private String customerId;
    private String managerId;
    private Double hourlyRate;
    private Integer progress;
    private Double actualCost;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Project() {}

    public Project(String name, String projectCode, String description, String status, String priority) {
        this.name = name;
        this.projectCode = projectCode;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Double getActualCost() {
        return actualCost;
    }

    public void setActualCost(Double actualCost) {
        this.actualCost = actualCost;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility methods
    public boolean isActive() {
        return "active".equals(status);
    }

    public boolean isCompleted() {
        return "completed".equals(status);
    }

    public String getStatusDisplayName() {
        switch (status) {
            case "active": return "Đang hoạt động";
            case "completed": return "Hoàn thành";
            case "on_hold": return "Tạm dừng";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }

    public String getPriorityDisplayName() {
        switch (priority) {
            case "high": return "Cao";
            case "medium": return "Trung bình";
            case "low": return "Thấp";
            default: return priority;
        }
    }
}
