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
    @SerializedName("tax_rate")
    private Double taxRate;
    @SerializedName("tax_amount")
    private Double taxAmount;
    @SerializedName("total_amount")
    private Double totalAmount;
    @SerializedName("valid_until")
    private Date validUntil;
    @SerializedName("issue_date")
    private Date issueDate;
    @SerializedName("quote_date")
    private Date quoteDate;
    @SerializedName("expiry_date")
    private Date expiryDate;
    private String terms;
    @SerializedName("discount_rate")
    private Double discountRate;
    @SerializedName("discount_amount")
    private Double discountAmount;
    private String currency;
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
    @SerializedName("employee_in_charge_id")
    private String employeeInChargeId;

    private List<Product> products;

    public String getEmployeeInChargeId() { return employeeInChargeId; }
    public void setEmployeeInChargeId(String employeeInChargeId) { this.employeeInChargeId = employeeInChargeId; }

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
    
    public Double getTaxRate() { return taxRate; }
    public void setTaxRate(Double taxRate) { this.taxRate = taxRate; }
    
    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    
    public Date getQuoteDate() { return quoteDate; }
    public void setQuoteDate(Date quoteDate) { this.quoteDate = quoteDate; }
    
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    
    public String getTerms() { return terms; }
    public void setTerms(String terms) { this.terms = terms; }
    
    public Double getDiscountRate() { return discountRate; }
    public void setDiscountRate(Double discountRate) { this.discountRate = discountRate; }
    
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
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
               ", totalAmount=" + totalAmount +
               '}';
    }

    /**
     * Quote Item Model - Mô hình dữ liệu chi tiết báo giá
     */
    public static class QuoteItem {
        private String id;
        private String description;
        @SerializedName("name_product")
        private String nameProduct;
        private Double quantity;
        @SerializedName("unit_price")
        private Double unitPrice;
        @SerializedName("total_price")
        private Double totalPrice;
        private String unit;
        private Double area;
        private Double volume;
        private Double height;
        private Double length;
        private Double depth;
        @SerializedName("discount_rate")
        private Double discountRate;
        @SerializedName("quote_id")
        private String quoteId;
        @SerializedName("product_service_id")
        private String productServiceId;

        public QuoteItem() {}

        public QuoteItem(String description, Double quantity, Double unitPrice) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getNameProduct() { return nameProduct; }
        public void setNameProduct(String nameProduct) { this.nameProduct = nameProduct; }
        
        public Double getQuantity() { return quantity; }
        public void setQuantity(Double quantity) { this.quantity = quantity; }
        
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
        
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        
        public Double getArea() { return area; }
        public void setArea(Double area) { this.area = area; }
        
        public Double getVolume() { return volume; }
        public void setVolume(Double volume) { this.volume = volume; }
        
        public Double getHeight() { return height; }
        public void setHeight(Double height) { this.height = height; }
        
        public Double getLength() { return length; }
        public void setLength(Double length) { this.length = length; }
        
        public Double getDepth() { return depth; }
        public void setDepth(Double depth) { this.depth = depth; }
        
        public Double getDiscountRate() { return discountRate; }
        public void setDiscountRate(Double discountRate) { this.discountRate = discountRate; }
        
        public String getQuoteId() { return quoteId; }
        public void setQuoteId(String quoteId) { this.quoteId = quoteId; }
        
        public String getProductServiceId() { return productServiceId; }
        public void setProductServiceId(String productServiceId) { this.productServiceId = productServiceId; }
    }
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
