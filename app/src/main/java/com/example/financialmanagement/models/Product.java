package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("quantity")
    private Double quantity;
    
    @SerializedName("price")
    private Double unitPrice;
    
    @SerializedName("total_price")
    private Double totalPrice;
    
    @SerializedName("unit")
    private String unit;
    
    @SerializedName("name_product")
    private String nameProduct;
    
    @SerializedName("area")
    private Double area;
    
    @SerializedName("volume")
    private Double volume;
    
    @SerializedName("height")
    private Double height;
    
    @SerializedName("length")
    private Double length;
    
    @SerializedName("depth")
    private Double depth;
    
    @SerializedName("discount_rate")
    private Double discountRate;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("type")
    private String type;
    
    @SerializedName("code")
    private String sku;
    
    @SerializedName("weight")
    private double weight;
    
    @SerializedName("dimensions")
    private String dimensions;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructors
    public Product() {}
    
    public Product(String id, String name, String description, Double quantity, Double unitPrice, String unit) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.totalPrice = quantity * unitPrice;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null && quantity != null) {
            this.totalPrice = quantity * this.unitPrice;
        }
    }
    
    public Double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
        if (this.quantity != null && unitPrice != null) {
            this.totalPrice = this.quantity * unitPrice;
        }
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getNameProduct() {
        return nameProduct;
    }
    
    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }
    
    public Double getArea() {
        return area;
    }
    
    public void setArea(Double area) {
        this.area = area;
    }
    
    public Double getVolume() {
        return volume;
    }
    
    public void setVolume(Double volume) {
        this.volume = volume;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Double getLength() {
        return length;
    }
    
    public void setLength(Double length) {
        this.length = length;
    }
    
    public Double getDepth() {
        return depth;
    }
    
    public void setDepth(Double depth) {
        this.depth = depth;
    }
    
    public Double getDiscountRate() {
        return discountRate;
    }
    
    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public String getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
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
}
