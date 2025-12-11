package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProjectListResponse {
    @SerializedName("projects")
    private List<Project> projects;
    
    @SerializedName("count")
    private int count;

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
