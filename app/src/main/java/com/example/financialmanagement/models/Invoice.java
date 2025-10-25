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
    private String title;
    private String description;
    private Double subtotal;
    private Double tax;
    private Double total;
    private Double paid;
    private Double balance;
    @SerializedName("due_date")
    private Date dueDate;
    @SerializedName("paid_date")
    private Date paidDate;
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
    
    public Double getPaid() { return paid; }
    public void setPaid(Double paid) { this.paid = paid; }
    
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    
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
               ", total=" + total +
               ", balance=" + balance +
               '}';
    }

    /**
     * Invoice Item Model - Mô hình dữ liệu chi tiết hóa đơn
     */
    public static class InvoiceItem {
        private String id;
        private String description;
        private Integer quantity;
        private Double unitPrice;
        private Double total;
        @SerializedName("invoice_id")
        private String invoiceId;

        public InvoiceItem() {}

        public InvoiceItem(String description, Integer quantity, Double unitPrice) {
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
        
        public String getInvoiceId() { return invoiceId; }
        public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
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
