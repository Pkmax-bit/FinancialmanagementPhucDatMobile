package com.example.financialmanagement.network;

import android.content.Context;
import com.example.financialmanagement.auth.AuthManager;
import com.example.financialmanagement.utils.ApiDebugger;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

/**
 * Auth Interceptor - Interceptor xử lý authentication
 * Tự động thêm Authorization header vào các API requests
 */
public class AuthInterceptor implements Interceptor {
    
    private Context context;
    
    public AuthInterceptor(Context context) {
        this.context = context;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Lấy token từ AuthManager
        AuthManager authManager = new AuthManager(context);
        String token = authManager.getAccessToken();
        
        // Debug logging
        ApiDebugger.logAuth(token, authManager.isLoggedIn());
        long startTime = System.currentTimeMillis();
        
        if (token != null && !token.isEmpty()) {
            // Kiểm tra xem header đã tồn tại chưa (có thể đã được thêm bởi @Header annotation)
            String existingAuth = originalRequest.header(NetworkConfig.Headers.AUTHORIZATION);
            Request.Builder requestBuilder = originalRequest.newBuilder();
            
            if (existingAuth == null || existingAuth.isEmpty()) {
                // Chỉ thêm header nếu chưa có
                requestBuilder.addHeader(NetworkConfig.Headers.AUTHORIZATION, "Bearer " + token);
            } else {
                // Header đã tồn tại, sử dụng header hiện có
                ApiDebugger.logAuth("AuthInterceptor - Authorization header already exists, using existing header", true);
            }
            
            Request newRequest = requestBuilder.build();
            
            ApiDebugger.logAuth("AuthInterceptor - Added Bearer token", true);
            Response response = chain.proceed(newRequest);
            
            // Check for authentication errors
            if (response.code() == 401 || response.code() == 403) {
                ApiDebugger.logAuth("AuthInterceptor - Authentication failed: " + response.code(), false);
                ApiDebugger.logAuth("AuthInterceptor - Response body: " + response.body(), false);
                
                // Log the specific error for debugging
                if (response.code() == 403) {
                    ApiDebugger.logAuth("AuthInterceptor - 403 Forbidden: User may not have permission to access this resource", false);
                } else if (response.code() == 401) {
                    ApiDebugger.logAuth("AuthInterceptor - 401 Unauthorized: Token is invalid or expired", false);
                }
                
                // Handle authentication error: logout and redirect to login
                com.example.financialmanagement.utils.AuthErrorHandler.handleAuthError(context);
            }
            
            return response;
        } else {
            ApiDebugger.logAuth("AuthInterceptor - No token available, proceeding without auth", false);
        }
        
        return chain.proceed(originalRequest);
    }
}
