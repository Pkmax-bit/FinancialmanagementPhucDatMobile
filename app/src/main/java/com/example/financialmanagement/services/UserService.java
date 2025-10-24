package com.example.financialmanagement.services;

import android.content.Context;
import com.example.financialmanagement.models.User;
import com.example.financialmanagement.models.Employee;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import com.example.financialmanagement.utils.ApiDebugger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;
import java.util.Map;
import java.util.HashMap;

/**
 * User Service - Service quản lý người dùng
 * Xử lý các API calls liên quan đến user và employee
 */
public class UserService {
    
    private Context context;
    private UserApi userApi;
    
    public UserService(Context context) {
        this.context = context;
        this.userApi = ApiClient.getRetrofit(context).create(UserApi.class);
    }
    
    /**
     * Lấy thông tin user hiện tại
     */
    public void getCurrentUser(UserCallback callback) {
        ApiDebugger.logRequest("GET", "Current User", null, null);
        
        Call<User> call = userApi.getCurrentUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                ApiDebugger.logResponse(response.code(), response.message(), 
                    response.body() != null ? response.body().toString() : "null");
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải thông tin user: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ApiDebugger.logError("getCurrentUser", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Lấy thông tin user với employee details
     */
    public void getUserWithEmployee(String userId, UserCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("include_employee", true);
        
        ApiDebugger.logRequest("GET", "User with Employee", null, params);
        
        Call<User> call = userApi.getUser(userId, params);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                ApiDebugger.logResponse(response.code(), response.message(), 
                    response.body() != null ? response.body().toString() : "null");
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải thông tin user: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ApiDebugger.logError("getUserWithEmployee", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Cập nhật thông tin user
     */
    public void updateUser(String userId, User user, UserCallback callback) {
        ApiDebugger.logRequest("PUT", "Update User", null, user);
        
        Call<User> call = userApi.updateUser(userId, user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                ApiDebugger.logResponse(response.code(), response.message(), 
                    response.body() != null ? response.body().toString() : "null");
                
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi cập nhật user: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ApiDebugger.logError("updateUser", t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * User API Interface
     */
    public interface UserApi {
        @GET(NetworkConfig.Endpoints.USERS + "/me")
        Call<User> getCurrentUser();
        
        @GET(NetworkConfig.Endpoints.USERS + "/{id}")
        Call<User> getUser(@Path("id") String userId, @QueryMap Map<String, Object> params);
        
        @PUT(NetworkConfig.Endpoints.USERS + "/{id}")
        Call<User> updateUser(@Path("id") String userId, @Body User user);
    }
    
    /**
     * User Callback Interface
     */
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }
}
