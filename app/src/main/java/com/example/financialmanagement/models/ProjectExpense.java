package com.example.financialmanagement.models;

import java.util.Date;

/**
 * ProjectExpense Model - Mô hình dữ liệu chi phí dự án
 * Tương ứng với API endpoint /api/project-expenses
 */
public class ProjectExpense {
    private String id;
    private String projectId;
    private String expenseObjectId;
    private String description;
    private Double amount;
    private String currency;
    private String status;
    private String role;
    private Date expenseDate;
    private String notes;
    private String createdBy;
    private Date createdAt;
    private Date updatedAt;
    
    // Related objects
    private Project project;
    private ExpenseObject expenseObject;

    // Constructors
    public ProjectExpense() {}

    public ProjectExpense(String projectId, String expenseObjectId, String description, Double amount) {
        this.projectId = projectId;
        this.expenseObjectId = expenseObjectId;
        this.description = description;
        this.amount = amount;
        this.currency = "VND";
        this.status = "pending";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getExpenseObjectId() {
        return expenseObjectId;
    }

    public void setExpenseObjectId(String expenseObjectId) {
        this.expenseObjectId = expenseObjectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ExpenseObject getExpenseObject() {
        return expenseObject;
    }

    public void setExpenseObject(ExpenseObject expenseObject) {
        this.expenseObject = expenseObject;
    }

    // Utility methods
    public boolean isApproved() {
        return "approved".equals(status);
    }

    public boolean isPending() {
        return "pending".equals(status);
    }

    public boolean isRejected() {
        return "rejected".equals(status);
    }

    public String getStatusDisplayName() {
        switch (status) {
            case "pending": return "Chờ duyệt";
            case "approved": return "Đã duyệt";
            case "rejected": return "Từ chối";
            case "paid": return "Đã thanh toán";
            default: return status;
        }
    }

    public String getRoleDisplayName() {
        if (role == null) {
            return "Không xác định";
        }
        
        switch (role) {
            case "workshop": return "Xưởng sản xuất";
            case "office": return "Văn phòng";
            case "management": return "Quản lý";
            case "sales": return "Bán hàng";
            default: return role;
        }
    }
}
