package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MaterialAdjustmentRule {
    @SerializedName("id")
    private String id;

    @SerializedName("expense_object_id")
    private String expenseObjectId;

    @SerializedName("dimension_type")
    private String dimensionType; // area, volume, height, length, depth, quantity

    @SerializedName("change_type")
    private String changeType; // percentage, absolute

    @SerializedName("change_value")
    private Double changeValue;

    @SerializedName("change_direction")
    private String changeDirection; // increase, decrease, both

    @SerializedName("adjustment_type")
    private String adjustmentType; // percentage, absolute

    @SerializedName("adjustment_value")
    private Double adjustmentValue;

    @SerializedName("priority")
    private Integer priority;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("is_active")
    private Boolean isActive;

    @SerializedName("max_adjustment_percentage")
    private Double maxAdjustmentPercentage;

    @SerializedName("max_adjustment_value")
    private Double maxAdjustmentValue;

    @SerializedName("allowed_category_ids")
    private List<String> allowedCategoryIds;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("created_by")
    private String createdBy;

    public MaterialAdjustmentRule() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpenseObjectId() {
        return expenseObjectId;
    }

    public void setExpenseObjectId(String expenseObjectId) {
        this.expenseObjectId = expenseObjectId;
    }

    public String getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(String dimensionType) {
        this.dimensionType = dimensionType;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public Double getChangeValue() {
        return changeValue;
    }

    public void setChangeValue(Double changeValue) {
        this.changeValue = changeValue;
    }

    public String getChangeDirection() {
        return changeDirection;
    }

    public void setChangeDirection(String changeDirection) {
        this.changeDirection = changeDirection;
    }

    public String getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public Double getAdjustmentValue() {
        return adjustmentValue;
    }

    public void setAdjustmentValue(Double adjustmentValue) {
        this.adjustmentValue = adjustmentValue;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Double getMaxAdjustmentPercentage() {
        return maxAdjustmentPercentage;
    }

    public void setMaxAdjustmentPercentage(Double maxAdjustmentPercentage) {
        this.maxAdjustmentPercentage = maxAdjustmentPercentage;
    }

    public Double getMaxAdjustmentValue() {
        return maxAdjustmentValue;
    }

    public void setMaxAdjustmentValue(Double maxAdjustmentValue) {
        this.maxAdjustmentValue = maxAdjustmentValue;
    }

    public List<String> getAllowedCategoryIds() {
        return allowedCategoryIds;
    }

    public void setAllowedCategoryIds(List<String> allowedCategoryIds) {
        this.allowedCategoryIds = allowedCategoryIds;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
