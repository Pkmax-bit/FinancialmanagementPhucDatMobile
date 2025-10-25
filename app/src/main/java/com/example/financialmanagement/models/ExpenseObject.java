package com.example.financialmanagement.models;

import java.util.Date;

/**
 * ExpenseObject Model - Mô hình dữ liệu đối tượng chi phí
 * Tương ứng với API endpoint /api/expense-objects
 */
public class ExpenseObject {
    private String id;
    private String name;
    private String code;
    private String description;
    private String role;
    private Boolean isActive;
    private Boolean isParent;
    private String parentId;
    private Integer hierarchyLevel;
    private Date createdAt;
    private Date updatedAt;

    // Related objects
    private ExpenseObject parent;
    private java.util.List<ExpenseObject> children;

    // Constructors
    public ExpenseObject() {}

    public ExpenseObject(String name, String code, String role) {
        this.name = name;
        this.code = code;
        this.role = role;
        this.isActive = true;
        this.isParent = false;
        this.hierarchyLevel = 0;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean isParent) {
        this.isParent = isParent;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
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

    public ExpenseObject getParent() {
        return parent;
    }

    public void setParent(ExpenseObject parent) {
        this.parent = parent;
    }

    public java.util.List<ExpenseObject> getChildren() {
        return children;
    }

    public void setChildren(java.util.List<ExpenseObject> children) {
        this.children = children;
    }

    // Utility methods
    public boolean isActive() {
        return isActive != null && isActive;
    }

    public boolean isParent() {
        return isParent != null && isParent;
    }

    public boolean hasParent() {
        return parentId != null && !parentId.isEmpty();
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
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

    public String getHierarchyDisplayName() {
        if (hierarchyLevel == null) return name;
        
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < hierarchyLevel; i++) {
            indent.append("  ");
        }
        return indent.toString() + name;
    }
}
