package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

/**
 * Invoice Model - Mô hình dữ liệu hóa đơn
 * Tương ứng với API endpoint /api/invoices
 */
public class Invoice {
    private String id;
    @SerializedName("invoice_number")
    private String invoiceNumber;
    @SerializedName("project_id")
    private String projectId;
    @SerializedName("customer_id")
    private String customerId;
    @SerializedName("quote_id")
    private String quoteId;
    private String status; // draft, sent, paid, overdue, cancelled
    @SerializedName("payment_status")
    private String paymentStatus;
    private String title;
    private String description;
    private Double subtotal;
    @SerializedName("tax_rate")
    private Double taxRate;
    @SerializedName("tax_amount")
    private Double taxAmount;
    @SerializedName("total_amount")
    private Double totalAmount;
    @SerializedName("paid_amount")
    private Double paidAmount;
    private Double balance;
    @SerializedName("due_date")
    private Date dueDate;
    @SerializedName("paid_date")
    private Date paidDate;
    @SerializedName("payment_date")
    private Date paymentDate;
    @SerializedName("issue_date")
    private Date issueDate;
    @SerializedName("discount_rate")
    private Double discountRate;
    @SerializedName("discount_amount")
    private Double discountAmount;
    private String currency;
    @SerializedName("invoice_type")
    private String invoiceType;
    @SerializedName("payment_terms")
    private String paymentTerms;
    @SerializedName("terms_and_conditions")
    private String termsAndConditions;
    @SerializedName("reminder_sent_at")
    private Date reminderSentAt;
    @SerializedName("reminder_count")
    private Integer reminderCount;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    
    // Products list
    private List<Product> products;
    @SerializedName("created_by")
    private String createdBy;
    
    // Related objects
    private Project project;
    private Customer customer;
    private Quote quote;
    private List<InvoiceItem> items;
    private List<Payment> payments;

    // Constructors
    public Invoice() {}

    public Invoice(String projectId, String customerId, String title) {
        this.projectId = projectId;
        this.customerId = customerId;
        this.title = title;
        this.status = "draft";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    
    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getQuoteId() { return quoteId; }
    public void setQuoteId(String quoteId) { this.quoteId = quoteId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
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
    
    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }
    
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    
    public Double getDiscountRate() { return discountRate; }
    public void setDiscountRate(Double discountRate) { this.discountRate = discountRate; }
    
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }
    
    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    
    public String getTermsAndConditions() { return termsAndConditions; }
    public void setTermsAndConditions(String termsAndConditions) { this.termsAndConditions = termsAndConditions; }
    
    public Date getReminderSentAt() { return reminderSentAt; }
    public void setReminderSentAt(Date reminderSentAt) { this.reminderSentAt = reminderSentAt; }
    
    public Integer getReminderCount() { return reminderCount; }
    public void setReminderCount(Integer reminderCount) { this.reminderCount = reminderCount; }
    
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    
    public Date getPaidDate() { return paidDate; }
    public void setPaidDate(Date paidDate) { this.paidDate = paidDate; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    public Quote getQuote() { return quote; }
    public void setQuote(Quote quote) { this.quote = quote; }
    
    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
    
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    @Override
    public String toString() {
        return "Invoice{" +
               "id='" + id + '\'' +
               ", invoiceNumber='" + invoiceNumber + '\'' +
               ", title='" + title + '\'' +
               ", status='" + status + '\'' +
               ", totalAmount=" + totalAmount +
               ", balance=" + balance +
               '}';
    }

    /**
     * Invoice Item Model - Mô hình dữ liệu chi tiết hóa đơn
     */
    public static class InvoiceItem {
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
        @SerializedName("invoice_id")
        private String invoiceId;
        @SerializedName("product_service_id")
        private String productServiceId;

        public InvoiceItem() {}

        public InvoiceItem(String description, Double quantity, Double unitPrice) {
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
        
        public String getInvoiceId() { return invoiceId; }
        public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
        
        public String getProductServiceId() { return productServiceId; }
        public void setProductServiceId(String productServiceId) { this.productServiceId = productServiceId; }
    }

    /**
     * Payment Model - Mô hình dữ liệu thanh toán
     */
    public static class Payment {
        private String id;
        private Double amount;
        private String method; // cash, bank_transfer, credit_card, etc.
        private String reference;
        private String notes;
        @SerializedName("payment_date")
        private Date paymentDate;
        @SerializedName("invoice_id")
        private String invoiceId;
        @SerializedName("created_by")
        private String createdBy;

        public Payment() {}

        public Payment(Double amount, String method, Date paymentDate) {
            this.amount = amount;
            this.method = method;
            this.paymentDate = paymentDate;
        }

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public Date getPaymentDate() { return paymentDate; }
        public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
        
        public String getInvoiceId() { return invoiceId; }
        public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
        
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
