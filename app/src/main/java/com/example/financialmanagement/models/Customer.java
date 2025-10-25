package com.example.financialmanagement.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Customer Model - Mô hình dữ liệu khách hàng
 * Tương ứng với API endpoint /api/customers
 */
public class Customer implements Serializable {
    private String id;
    private String customerCode;
    private String name;
    private String type;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String taxId;
    private String status;
    private Double creditLimit;
    private Integer paymentTerms;
    private String notes;
    private String assignedTo;
    private Integer projectCount;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Customer() {}

    public Customer(String customerCode, String name, String type) {
        this.customerCode = customerCode;
        this.name = name;
        this.type = type;
        this.status = "active";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Integer getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(Integer paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
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

    public String getCustomerType() {
        return type;
    }

    public void setCustomerType(String customerType) {
        this.type = customerType;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    // Utility methods
    public boolean isActive() {
        return "active".equals(status);
    }

    public boolean isIndividual() {
        return "individual".equals(type);
    }

    public boolean isCompany() {
        return "company".equals(type);
    }

    public boolean isGovernment() {
        return "government".equals(type);
    }

    public String getTypeDisplayName() {
        switch (type) {
            case "individual": return "Cá nhân";
            case "company": return "Công ty";
            case "government": return "Cơ quan nhà nước";
            default: return type;
        }
    }

    public String getStatusDisplayName() {
        switch (status) {
            case "active": return "Hoạt động";
            case "inactive": return "Không hoạt động";
            case "prospect": return "Tiềm năng";
            default: return status;
        }
    }

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        if (address != null && !address.isEmpty()) {
            fullAddress.append(address);
        }
        if (city != null && !city.isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(city);
        }
        if (country != null && !country.isEmpty()) {
            if (fullAddress.length() > 0) fullAddress.append(", ");
            fullAddress.append(country);
        }
        return fullAddress.toString();
    }
}
