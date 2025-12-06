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
    private static final String KEY_USER_ROLE = "user_role";
    
    private Context context;
    private SharedPreferences prefs;
    private AuthApi authApi;
    // AuthManager.java
public static final String SUPABASE_URL = "https://mfmijckzlhevduwfigkl.supabase.co";
public static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1mbWlqY2t6bGhldmR1d2ZpZ2tsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTY1MzkxMTIsImV4cCI6MjA3MjExNTExMn0.VPFmvLghhO32JybxDzq-CGVQedgI-LN7Q07rwDhxU4E";
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
                    
                    // Parse JWT token to extract user information
                    try {
                        parseJWTToken(authResponse);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JWT token", e);
                    }
                    
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
    
    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, null);
    }
    
    /**
     * Parse JWT token to extract user information
     */
    private void parseJWTToken(AuthResponse authResponse) {
        try {
            String token = authResponse.getAccessToken();
            if (token != null && !token.isEmpty()) {
                // JWT token has 3 parts separated by dots
                String[] parts = token.split("\\.");
                if (parts.length == 3) {
                    // Decode the payload (second part)
                    String payload = parts[1];
                    
                    // Add padding if needed
                    while (payload.length() % 4 != 0) {
                        payload += "=";
                    }
                    
                    // Decode base64
                    byte[] decodedBytes = android.util.Base64.decode(payload, android.util.Base64.DEFAULT);
                    String decodedPayload = new String(decodedBytes);
                    
                    // Parse JSON payload
                    JSONObject payloadJson = new JSONObject(decodedPayload);
                    
                    // Extract user information
                    authResponse.setUserId(payloadJson.optString("sub", null));
                    authResponse.setUserEmail(payloadJson.optString("email", null));
                    
                    // Extract user metadata
                    JSONObject userMetadata = payloadJson.optJSONObject("user_metadata");
                    if (userMetadata != null) {
                        authResponse.setUserName(userMetadata.optString("full_name", null));
                        authResponse.setUserRole(userMetadata.optString("role", null));
                    }
                    
                    Log.d(TAG, "JWT Token parsed successfully:");
                    Log.d(TAG, "User ID: " + authResponse.getUserId());
                    Log.d(TAG, "User Email: " + authResponse.getUserEmail());
                    Log.d(TAG, "User Name: " + authResponse.getUserName());
                    Log.d(TAG, "User Role: " + authResponse.getUserRole());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JWT token", e);
        }
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
        editor.putString(KEY_USER_ROLE, authResponse.getUserRole());
        editor.apply();
        
        // Debug log saved data
        Log.d(TAG, "Auth data saved:");
        Log.d(TAG, "Access Token: " + (authResponse.getAccessToken() != null ? "Present" : "Null"));
        Log.d(TAG, "User ID: " + authResponse.getUserId());
        Log.d(TAG, "User Email: " + authResponse.getUserEmail());
        Log.d(TAG, "User Name: " + authResponse.getUserName());
        Log.d(TAG, "User Role: " + authResponse.getUserRole());
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
        editor.remove(KEY_USER_ROLE);
        editor.apply();
        
        Log.d(TAG, "Auth data cleared");
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
     * Đăng ký
     */
    public void register(String email, String password, String fullName, AuthCallback callback) {
        Map<String, Object> registerData = new HashMap<>();
        registerData.put("email", email);
        registerData.put("password", password);
        
        Map<String, String> data = new HashMap<>();
        data.put("full_name", fullName);
        registerData.put("data", data);
        
        Call<AuthResponse> call = authApi.register(registerData);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    // Auto login after register if token is present
                    if (authResponse.getAccessToken() != null) {
                        saveAuthData(authResponse);
                    }
                    callback.onAuthSuccess();
                } else {
                    String error = "Lỗi đăng ký: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBody);
                            error = errorJson.optString("msg", errorJson.optString("message", error));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    callback.onAuthError(error);
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e(TAG, "Register failed", t);
                callback.onAuthError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Quên mật khẩu (Gửi email reset)
     */
    public void resetPassword(String email, AuthCallback callback) {
        Map<String, String> resetData = new HashMap<>();
        resetData.put("email", email);
        
        Call<Void> call = authApi.resetPassword(resetData);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onAuthSuccess();
                } else {
                    String error = "Lỗi gửi email: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject errorJson = new JSONObject(errorBody);
                            error = errorJson.optString("msg", errorJson.optString("message", error));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error response", e);
                    }
                    callback.onAuthError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Reset password failed", t);
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
        
        @POST(NetworkConfig.Endpoints.REGISTER)
        Call<AuthResponse> register(@Body Map<String, Object> registerData);

        @POST(NetworkConfig.Endpoints.LOGOUT)
        Call<Void> logout();
        
        @POST(NetworkConfig.Endpoints.REFRESH_TOKEN)
        Call<AuthResponse> refreshToken(@Body Map<String, String> refreshData);

        @POST(NetworkConfig.Endpoints.RESET_PASSWORD)
        Call<Void> resetPassword(@Body Map<String, String> resetData);
    }
    
    /**
     * Auth Response Model
     */
    public static class AuthResponse {
        private String access_token;
        private String refresh_token;
        private String token_type;
        private int expires_in;
        private String userId;
        private String userEmail;
        private String userName;
        private String userRole;
        
        // Getters and Setters
        public String getAccessToken() {
            return access_token;
        }
        
        public void setAccessToken(String accessToken) {
            this.access_token = accessToken;
        }
        
        public String getRefreshToken() {
            return refresh_token;
        }
        
        public void setRefreshToken(String refreshToken) {
            this.refresh_token = refreshToken;
        }
        
        public String getTokenType() {
            return token_type;
        }
        
        public void setTokenType(String tokenType) {
            this.token_type = tokenType;
        }
        
        public int getExpiresIn() {
            return expires_in;
        }
        
        public void setExpiresIn(int expiresIn) {
            this.expires_in = expiresIn;
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
        
        public String getUserRole() {
            return userRole;
        }
        
        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }
    }
    
}
