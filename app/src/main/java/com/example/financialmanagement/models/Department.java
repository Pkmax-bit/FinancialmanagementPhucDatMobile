package com.example.financialmanagement.models;

import java.io.Serializable;

/**
 * Department Model - Mô hình dữ liệu phòng ban
 */
public class Department implements Serializable {
    private String id;
    private String name;
    private String description;

    public Department() {}

    public Department(String name) {
        this.name = name;
    }

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
}
