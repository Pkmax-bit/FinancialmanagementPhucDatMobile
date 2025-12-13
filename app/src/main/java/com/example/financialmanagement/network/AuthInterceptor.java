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
        ApiDebugger.logAuth("AuthInterceptor - Token available: " + (token != null && !token.isEmpty()), 
            authManager.isLoggedIn());
        ApiDebugger.logAuth("AuthInterceptor - Request URL: " + originalRequest.url(), 
            authManager.isLoggedIn());
        
        if (token != null && !token.isEmpty()) {
            // Thêm Authorization header
            Request newRequest = originalRequest.newBuilder()
                    .addHeader(NetworkConfig.Headers.AUTHORIZATION, "Bearer " + token)
                    .build();
            
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
