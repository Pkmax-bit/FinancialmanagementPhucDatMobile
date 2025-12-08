package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Project Service - Xử lý API calls cho dự án
 */
public class ProjectService {
    private ApiService apiService;
    private Context context;

    public ProjectService(Context context) {
        this.context = context;
        this.apiService = ApiClient.getRetrofit(context).create(ApiService.class);
    }

    /**
     * Callback interface for project operations
     */
    public interface ProjectCallback {
        void onSuccess(List<Project> projects);
        void onSuccess(Project project);
        void onSuccess();
        void onError(String error);
    }

    /**
     * Get all projects
     */
    public void getProjects(ProjectCallback callback) {
        getProjects(new HashMap<>(), callback);
    }

    /**
     * Get projects with filters
     */
    /**
     * Get projects with filters
     */
    public void getProjects(Map<String, Object> params, ProjectCallback callback) {
        Call<List<Project>> call = apiService.getProjects(params);
        call.enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải dự án: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Get project by ID
     */
    public void getProject(String projectId, ProjectCallback callback) {
        Call<Project> call = apiService.getProject(projectId);
        call.enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải chi tiết dự án: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Get project by ID (alias for compatibility)
     */
    public void getProjectById(String projectId, ProjectCallback callback) {
        getProject(projectId, callback);
    }

    /**
     * Get all projects (alias for compatibility)
     */
    public void getAllProjects(Map<String, Object> params, ProjectCallback callback) {
        getProjects(params, callback);
    }

    /**
     * Get public projects (fallback)
     */
    public void getProjectsPublic(ProjectCallback callback) {
        getProjects(callback);
    }

    /**
     * Create new project
     */
    public void createProject(Project project, ProjectCallback callback) {
        Call<Project> call = apiService.createProject(project);
        call.enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tạo dự án: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Update project
     */
    public void updateProject(String projectId, Project project, ProjectCallback callback) {
        Call<Project> call = apiService.updateProject(projectId, project);
        call.enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi cập nhật dự án: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Delete project
     */
    public void deleteProject(String projectId, ProjectCallback callback) {
        Call<Void> call = apiService.deleteProject(projectId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Lỗi xóa dự án: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}