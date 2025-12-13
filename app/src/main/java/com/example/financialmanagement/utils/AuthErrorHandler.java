package com.example.financialmanagement.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.example.financialmanagement.activities.LoginActivity;
import com.example.financialmanagement.auth.AuthManager;

/**
 * Auth Error Handler - Xử lý lỗi xác thực
 * Tự động logout và redirect về LoginActivity khi token hết hạn
 */
public class AuthErrorHandler {
    
    private static final String TAG = "AuthErrorHandler";
    
    /**
     * Xử lý lỗi xác thực (401/403)
     * Clear auth data và redirect về LoginActivity
     */
    public static void handleAuthError(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null, cannot handle auth error");
            return;
        }
        
        Log.d(TAG, "Handling authentication error - logging out and redirecting to login");
        
        // Clear authentication data
        AuthManager authManager = new AuthManager(context);
        authManager.logout();
        
        // Create intent to LoginActivity with flags to clear activity stack
        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.putExtra("auth_expired", true); // Flag to indicate auth expired
        
        // Start LoginActivity
        try {
            context.startActivity(loginIntent);
            Log.d(TAG, "Redirected to LoginActivity with auth_expired flag");
        } catch (Exception e) {
            Log.e(TAG, "Error starting LoginActivity: " + e.getMessage(), e);
        }
    }
    
    /**
     * Kiểm tra xem response code có phải là lỗi xác thực không
     */
    public static boolean isAuthError(int responseCode) {
        return responseCode == 401 || responseCode == 403;
    }
    
    /**
     * Xử lý lỗi xác thực với message tùy chỉnh
     */
    public static void handleAuthError(Context context, String message) {
        Log.d(TAG, "Handling authentication error: " + message);
        handleAuthError(context);
    }
}

