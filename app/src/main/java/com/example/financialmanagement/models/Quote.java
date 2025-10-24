package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

/**
 * Quote Model - Mô hình dữ liệu báo giá
 * Tương ứng với API endpoint /api/quotes
 */
public class Quote {
    private String id;
    @SerializedName("quote_number")
    private String quoteNumber;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("customer_id")
    private String customerId;
    private String status; // draft, sent, approved, rejected, converted
    private String title;
    private String description;
    private Double subtotal;
    private Double tax;
    private Double total;
    @SerializedName("valid_until")
    private Date validUntil;
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
    private Customer customer;
    private List<QuoteItem> items;

    // Constructors
    public Quote() {}

    public Quote(String projectId, String customerId, String title) {
        this.projectId = projectId;
        this.customerId = customerId;
        this.title = title;
        this.status = "draft";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getQuoteNumber() { return quoteNumber; }
    public void setQuoteNumber(String quoteNumber) { this.quoteNumber = quoteNumber; }
    
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
    
    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }
    
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    
    public Date getValidUntil() { return validUntil; }
    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }
    
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
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public List<QuoteItem> getItems() { return items; }
    public void setItems(List<QuoteItem> items) { this.items = items; }

    @Override
    public String toString() {
        return "Quote{" +
               "id='" + id + '\'' +
               ", quoteNumber='" + quoteNumber + '\'' +
               ", title='" + title + '\'' +
               ", status='" + status + '\'' +
               ", total=" + total +
               '}';
    }

    /**
     * Quote Item Model - Mô hình dữ liệu chi tiết báo giá
     */
    public static class QuoteItem {
        private String id;
        private String description;
        private Integer quantity;
        private Double unitPrice;
        private Double total;
        @SerializedName("quote_id")
        private String quoteId;

        public QuoteItem() {}

        public QuoteItem(String description, Integer quantity, Double unitPrice) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.total = quantity * unitPrice;
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
        
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
        
        public String getQuoteId() { return quoteId; }
        public void setQuoteId(String quoteId) { this.quoteId = quoteId; }
    }
}
