package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

/**
 * Budget Model - Mô hình dữ liệu ngân sách dự án
 * Tương ứng với API endpoint /api/budgets
 */
public class Budget {
    private String id;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("project_name")
    private String projectName;
    private String title;
    private String description;
    private Double totalBudget;
    private Double allocatedBudget;
    private Double spentAmount;
    private Double remainingBudget;
    private String status; // draft, approved, active, completed
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("approved_by")
    private String approvedBy;
    @SerializedName("approved_at")
    private Date approvedAt;
    
    // Related objects
    private Project project;
    private List<BudgetCategory> categories;

    // Constructors
    public Budget() {}

    public Budget(String projectId, String title, Double totalBudget) {
        this.projectId = projectId;
        this.title = title;
        this.totalBudget = totalBudget;
        this.status = "draft";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }
    
    public Double getAllocatedBudget() { return allocatedBudget; }
    public void setAllocatedBudget(Double allocatedBudget) { this.allocatedBudget = allocatedBudget; }
    
    public Double getSpentAmount() { return spentAmount; }
    public void setSpentAmount(Double spentAmount) { this.spentAmount = spentAmount; }
    
    public Double getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(Double remainingBudget) { this.remainingBudget = remainingBudget; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    
    public Date getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Date approvedAt) { this.approvedAt = approvedAt; }
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public List<BudgetCategory> getCategories() { return categories; }
    public void setCategories(List<BudgetCategory> categories) { this.categories = categories; }

    @Override
    public String toString() {
        return "Budget{" +
               "id='" + id + '\'' +
               ", projectName='" + projectName + '\'' +
               ", title='" + title + '\'' +
               ", totalBudget=" + totalBudget +
               ", spentAmount=" + spentAmount +
               ", status='" + status + '\'' +
               '}';
    }

    /**
     * Budget Category Model - Mô hình dữ liệu danh mục ngân sách
     */
    public static class BudgetCategory {
        private String id;
        private String name;
        private String description;
        private Double allocatedAmount;
        private Double spentAmount;
        private Double remainingAmount;
        @SerializedName("budget_id")
        private String budgetId;

        public BudgetCategory() {}

        public BudgetCategory(String name, Double allocatedAmount) {
            this.name = name;
            this.allocatedAmount = allocatedAmount;
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Double getAllocatedAmount() { return allocatedAmount; }
        public void setAllocatedAmount(Double allocatedAmount) { this.allocatedAmount = allocatedAmount; }
        
        public Double getSpentAmount() { return spentAmount; }
        public void setSpentAmount(Double spentAmount) { this.spentAmount = spentAmount; }
        
        public Double getRemainingAmount() { return remainingAmount; }
        public void setRemainingAmount(Double remainingAmount) { this.remainingAmount = remainingAmount; }
        
        public String getBudgetId() { return budgetId; }
        public void setBudgetId(String budgetId) { this.budgetId = budgetId; }
    }
}
