package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ChecklistItemAssignment implements Serializable {
    @SerializedName("employee_id")
    private String employeeId;

    @SerializedName("employee_name")
    private String employeeName;

    @SerializedName("responsibility_type")
    private String responsibilityType; // accountable, responsible, consulted, informed

    public ChecklistItemAssignment() {}

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

    public String getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(String responsibilityType) {
        this.responsibilityType = responsibilityType;
    }

    public String getRoleDisplayName() {
        if (responsibilityType == null) return "Thực hiện";
        switch (responsibilityType) {
            case "accountable": return "Chịu trách nhiệm chính";
            case "responsible": return "Thực hiện";
            case "consulted": return "Tham vấn";
            case "informed": return "Thông báo";
            default: return responsibilityType;
        }
    }
}
