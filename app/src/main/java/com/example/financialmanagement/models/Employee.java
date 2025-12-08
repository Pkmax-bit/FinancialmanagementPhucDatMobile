package com.example.financialmanagement.models;

import java.util.Date;
import com.example.financialmanagement.models.Department;
import com.example.financialmanagement.models.Position;

/**
 * Employee Model - Mô hình dữ liệu nhân viên
 * Tương ứng với API endpoint /api/employees
 */
public class Employee {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    
    @com.google.gson.annotations.SerializedName("first_name")
    private String firstName;
    
    @com.google.gson.annotations.SerializedName("last_name")
    private String lastName;
    
    @com.google.gson.annotations.SerializedName("email")
    private String email;
    
    @com.google.gson.annotations.SerializedName("phone")
    private String phone;
    
    @com.google.gson.annotations.SerializedName("department_id")
    private String departmentId;
    
    @com.google.gson.annotations.SerializedName("position_id")
    private String positionId;
    
    @com.google.gson.annotations.SerializedName("hire_date")
    private Date hireDate;
    
    @com.google.gson.annotations.SerializedName("salary")
    private Double salary;
    
    @com.google.gson.annotations.SerializedName("manager_id")
    private String managerId;
    
    @com.google.gson.annotations.SerializedName("employee_code")
    private String employeeCode;
    
    @com.google.gson.annotations.SerializedName("status")
    private String status;
    
    @com.google.gson.annotations.SerializedName("created_at")
    private Date createdAt;
    
    @com.google.gson.annotations.SerializedName("updated_at")
    private Date updatedAt;
    
    // Related objects
    @com.google.gson.annotations.SerializedName("departments")
    private Department department;
    
    @com.google.gson.annotations.SerializedName("positions")
    private Position position;
    
    @com.google.gson.annotations.SerializedName("managers")
    private Employee manager;

    // Constructors
    public Employee() {}

    public Employee(String firstName, String lastName, String email, Date hireDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hireDate = hireDate;
        this.status = "active";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    // Utility methods
    @com.google.gson.annotations.SerializedName("full_name")
    private String fullName;

    // ... existing fields ...

    public String getFullName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        return firstName + " " + lastName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isActive() {
        return "active".equals(status);
    }

    public String getStatusDisplayName() {
        switch (status) {
            case "active": return "Hoạt động";
            case "inactive": return "Không hoạt động";
            case "terminated": return "Đã nghỉ việc";
            default: return status;
        }
    }
}


