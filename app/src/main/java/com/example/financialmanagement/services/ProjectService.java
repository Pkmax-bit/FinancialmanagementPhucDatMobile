package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.Project;
import com.example.financialmanagement.models.ProjectResponse;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import com.example.financialmanagement.utils.ApiDebugger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

/**
 * Project Service - Service quản lý dự án
 * Xử lý các API calls liên quan đến dự án
 */
public class ProjectService {
    
    private Context context;
    private ProjectApi projectApi;
    
    public ProjectService(Context context) {
        this.context = context;
        this.projectApi = ApiClient.getRetrofit(context).create(ProjectApi.class);
    }
    
    /**
     * Lấy tất cả dự án
     */
    public void getAllProjects(ProjectCallback callback) {
        getAllProjects(null, callback);
    }
    
    /**
     * Lấy dự án với parameters
     */
    public void getAllProjects(Map<String, Object> params, ProjectCallback callback) {
        // Debug logging
        ApiDebugger.logRequest("GET", NetworkConfig.BASE_URL + NetworkConfig.Endpoints.PROJECTS, null, null);
        ApiDebugger.logQueryParams(params);
        
        Call<List<Project>> call = projectApi.getProjects(params);
        call.enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                ApiDebugger.logResponse(response.code(), response.message(), 
                    response.body() != null ? response.body().toString() : "null");
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Lỗi tải danh sách dự án: " + response.code();
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                ApiDebugger.logError("getAllProjects", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Lấy dự án theo ID
     */
    public void getProject(String projectId, ProjectCallback callback) {
        // Debug logging
        String url = NetworkConfig.BASE_URL + NetworkConfig.Endpoints.PROJECTS + "/" + projectId;
        ApiDebugger.logRequest("GET", url, null, null);
        ApiDebugger.logQueryParams(Map.of("projectId", projectId));
        
        Call<Project> call = projectApi.getProject(projectId);
        call.enqueue(new Callback<Project>() {
            @Override
            public void onResponse(Call<Project> call, Response<Project> response) {
                ApiDebugger.logResponse(response.code(), response.message(), 
                    response.body() != null ? response.body().toString() : "null");
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải dự án: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Project> call, Throwable t) {
                ApiDebugger.logError("getProject", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Tạo dự án mới
     */
    public void createProject(Project project, ProjectCallback callback) {
        Call<Project> call = projectApi.createProject(project);
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
     * Cập nhật dự án
     */
    public void updateProject(String projectId, Project project, ProjectCallback callback) {
        Call<Project> call = projectApi.updateProject(projectId, project);
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
     * Xóa dự án
     */
    public void deleteProject(String projectId, ProjectCallback callback) {
        Call<Void> call = projectApi.deleteProject(projectId);
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
    
    /**
     * Tìm kiếm dự án
     */
    public void searchProjects(String query, Integer limit, ProjectCallback callback) {
        Call<List<Project>> call = projectApi.searchProjects(query, limit);
        call.enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Lỗi tìm kiếm dự án: " + response.code();
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Lấy dự án gần đây
     */
    public void getRecentProjects(int limit, ProjectCallback callback) {
        // TODO: Implement getRecentProjects API call
        // For now, return empty list
        callback.onSuccess(new java.util.ArrayList<>());
    }
    
    /**
     * Project API Interface
     */
    public interface ProjectApi {
        @GET(NetworkConfig.Endpoints.PROJECTS)
        Call<List<Project>> getProjects(@QueryMap Map<String, Object> params);
        
        @GET(NetworkConfig.Endpoints.PROJECT_DETAIL)
        Call<Project> getProject(@Path("id") String projectId);
        
        @POST(NetworkConfig.Endpoints.PROJECTS)
        Call<Project> createProject(@Body Project project);
        
        @PUT(NetworkConfig.Endpoints.PROJECT_DETAIL)
        Call<Project> updateProject(@Path("id") String projectId, @Body Project project);
        
        @DELETE(NetworkConfig.Endpoints.PROJECT_DETAIL)
        Call<Void> deleteProject(@Path("id") String projectId);
        
        @GET(NetworkConfig.Endpoints.PROJECT_SEARCH)
        Call<List<Project>> searchProjects(@Query("q") String query, @Query("limit") Integer limit);
    }
    
    /**
     * Project Callback Interface
     */
    public interface ProjectCallback {
        void onSuccess(List<Project> projects);
        void onSuccess(Project project);
        void onError(String error);
        
        // Default implementations to avoid ambiguous calls
        default void onSuccess() {
            onSuccess((List<Project>) null);
        }
    }
}
