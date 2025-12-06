package com.example.financialmanagement.models;

import java.io.Serializable;

/**
 * Position Model - Mô hình dữ liệu chức vụ
 */
public class Position implements Serializable {
    private String id;
    private String title;
    private String description;
    private String departmentId;

    public Position() {}

    public Position(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
}
