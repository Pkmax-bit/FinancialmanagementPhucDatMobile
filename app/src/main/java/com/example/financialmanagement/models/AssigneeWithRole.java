package com.example.financialmanagement.models;

public class AssigneeWithRole {
    private String employeeId;
    private String employeeName;
    private String employeeRole;
    private String responsibilityType; // "accountable", "responsible", "consulted", "informed"

    public AssigneeWithRole() {
    }

    public AssigneeWithRole(String employeeId, String employeeName, String employeeRole) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeRole = employeeRole;
        this.responsibilityType = "responsible"; // default
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }

    public String getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(String responsibilityType) {
        this.responsibilityType = responsibilityType;
    }

    public String getResponsibilityTypeDisplayName() {
        switch (responsibilityType) {
            case "accountable":
                return "Chịu trách nhiệm chính";
            case "responsible":
                return "Thực hiện";
            case "consulted":
                return "Được tham khảo";
            case "informed":
                return "Được thông báo";
            default:
                return "Thực hiện";
        }
    }

    public int getResponsibilityColor() {
        switch (responsibilityType) {
            case "accountable":
                return android.graphics.Color.parseColor("#FF3B30");
            case "responsible":
                return android.graphics.Color.parseColor("#0075FF");
            case "consulted":
                return android.graphics.Color.parseColor("#FF9500");
            case "informed":
                return android.graphics.Color.parseColor("#34C759");
            default:
                return android.graphics.Color.parseColor("#0075FF");
        }
    }
}
