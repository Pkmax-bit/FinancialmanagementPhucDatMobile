package com.example.financialmanagement.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.network.NetworkConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.*;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Auth Manager - Quản lý xác thực
 * Xử lý login, logout, token management với Supabase
 */
public class AuthManager {
    
    private static final String TAG = "AuthManager";
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    
    private Context context;
    private SharedPreferences prefs;
    private AuthApi authApi;
    // AuthManager.java
public static final String SUPABASE_URL = "https://ncqfxuhyfqumrdclcpkr.supabase.co";
public static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5jcWZ4dWl5ZnF1bXJkY2xjcGtyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MjQ1NTM0OTEsImV4cCI6MjA0MDEyOTQ5MX0.B_qWnYl79v5NzDjD0fYgA_qVqAaQ1x63_6WpYyXcJ4";
    public AuthManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.authApi = ApiClient.getRetrofit(context).create(AuthApi.class);
    }
    
    /**
     * Đăng nhập
     */
    public void login(String email, String password, AuthCallback callback) {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", password);
        
        Call<AuthResponse> call = authApi.login(loginData);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    saveAuthData(authResponse);
                    callback.onAuthSuccess();
                } else {
                    String error = "Lỗi đăng nhập: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBody);
                            error = errorJson.optString("message", error);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    callback.onAuthError(error);
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, "Login failed", t);
                callback.onAuthError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Đăng xuất
     */
    public void logout() {
        clearAuthData();
    }
    
    /**
     * Kiểm tra trạng thái đăng nhập
     */
    public boolean isLoggedIn() {
        String accessToken = getAccessToken();
        return accessToken != null && !accessToken.isEmpty();
    }
    
    /**
     * Lấy access token
     */
    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }
    
    /**
     * Lấy refresh token
     */
    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }
    
    /**
     * Lấy thông tin người dùng
     */
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }
    
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }
    
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }
    
    /**
     * Lưu dữ liệu xác thực
     */
    private void saveAuthData(AuthResponse authResponse) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, authResponse.getAccessToken());
        editor.putString(KEY_REFRESH_TOKEN, authResponse.getRefreshToken());
        editor.putString(KEY_USER_ID, authResponse.getUserId());
        editor.putString(KEY_USER_EMAIL, authResponse.getUserEmail());
        editor.putString(KEY_USER_NAME, authResponse.getUserName());
        editor.apply();
    }
    
    /**
     * Xóa dữ liệu xác thực
     */
    private void clearAuthData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_NAME);
        editor.apply();
    }
    
    /**
     * Refresh token
     */
    public void refreshToken(AuthCallback callback) {
        String refreshToken = getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            callback.onAuthError("Không có refresh token");
            return;
        }
        
        Map<String, String> refreshData = new HashMap<>();
        refreshData.put("refresh_token", refreshToken);
        
        Call<AuthResponse> call = authApi.refreshToken(refreshData);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    saveAuthData(authResponse);
                    callback.onAuthSuccess();
                } else {
                    callback.onAuthError("Lỗi refresh token: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, "Refresh token failed", t);
                callback.onAuthError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    
    /**
     * Auth API Interface
     */
    public interface AuthApi {
        @POST(NetworkConfig.Endpoints.LOGIN)
        Call<AuthResponse> login(@Body Map<String, String> loginData);
        
        @POST(NetworkConfig.Endpoints.LOGOUT)
        Call<Void> logout();
        
        @POST(NetworkConfig.Endpoints.REFRESH_TOKEN)
        Call<AuthResponse> refreshToken(@Body Map<String, String> refreshData);
    }
    
    /**
     * Auth Response Model
     */
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String userId;
        private String userEmail;
        private String userName;
        
        // Getters and Setters
        public String getAccessToken() {
            return accessToken;
        }
        
        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
        
        public String getRefreshToken() {
            return refreshToken;
        }
        
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getUserEmail() {
            return userEmail;
        }
        
        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }
        
        public String getUserName() {
            return userName;
        }
        
        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
