package com.example.financialmanagement.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Project Model - Mô hình dữ liệu dự án
 * Tương ứng với API endpoint /api/projects
 */
public class Project implements Serializable {
    private String id;
    private String projectCode;
    private String name;
    private String description;
    private String customerId;
    private String customerName;
    private String status;
    private String priority;
    private Double budget;
    private Double actualCost;
    private Date startDate;
    private Date endDate;
    private Date createdAt;
    private Date updatedAt;
    private String assignedTo;
    private String notes;
    private Integer progress; // Progress percentage (0-100)

    // Constructors
    public Project() {}

    public Project(String projectCode, String name, String customerId) {
        this.projectCode = projectCode;
        this.name = name;
        this.customerId = customerId;
        this.status = "active";
        this.priority = "medium";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public Double getActualCost() {
        return actualCost;
    }

    public void setActualCost(Double actualCost) {
        this.actualCost = actualCost;
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

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    // Utility methods
    public boolean isActive() {
        return "active".equals(status);
    }

    public boolean isCompleted() {
        return "completed".equals(status);
    }

    public boolean isCancelled() {
        return "cancelled".equals(status);
    }

    public boolean isHighPriority() {
        return "high".equals(priority);
    }

    public boolean isLowPriority() {
        return "low".equals(priority);
    }

    public String getStatusDisplayName() {
        switch (status) {
            case "active": return "Đang thực hiện";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            case "on_hold": return "Tạm dừng";
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

    public Double getRemainingBudget() {
        if (budget == null || actualCost == null) {
            return budget;
        }
        return budget - actualCost;
    }

    public boolean isOverBudget() {
        if (budget == null || actualCost == null) {
            return false;
        }
        return actualCost > budget;
    }
}