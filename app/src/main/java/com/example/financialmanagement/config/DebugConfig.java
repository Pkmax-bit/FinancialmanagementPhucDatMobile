package com.example.financialmanagement.config;

import android.util.Log;

/**
 * Debug Configuration - Cấu hình debug
 * Quản lý các cài đặt debug cho ứng dụng
 */
public class DebugConfig {
    
    // Debug flags
    public static final boolean API_DEBUG_ENABLED = true;
    public static final boolean NETWORK_DEBUG_ENABLED = true;
    public static final boolean AUTH_DEBUG_ENABLED = true;
    public static final boolean QUERY_DEBUG_ENABLED = true;
    public static final boolean RESPONSE_DEBUG_ENABLED = true;
    
    // Log tags
    public static final String TAG_API = "API_DEBUG";
    public static final String TAG_NETWORK = "NETWORK_DEBUG";
    public static final String TAG_AUTH = "AUTH_DEBUG";
    public static final String TAG_QUERY = "QUERY_DEBUG";
    public static final String TAG_RESPONSE = "RESPONSE_DEBUG";
    
    /**
     * Log API debug information
     */
    public static void logApi(String message) {
        if (API_DEBUG_ENABLED) {
            Log.d(TAG_API, message);
        }
    }
    
    /**
     * Log network debug information
     */
    public static void logNetwork(String message) {
        if (NETWORK_DEBUG_ENABLED) {
            Log.d(TAG_NETWORK, message);
        }
    }
    
    /**
     * Log authentication debug information
     */
    public static void logAuth(String message) {
        if (AUTH_DEBUG_ENABLED) {
            Log.d(TAG_AUTH, message);
        }
    }
    
    /**
     * Log query debug information
     */
    public static void logQuery(String message) {
        if (QUERY_DEBUG_ENABLED) {
            Log.d(TAG_QUERY, message);
        }
    }
    
    /**
     * Log response debug information
     */
    public static void logResponse(String message) {
        if (RESPONSE_DEBUG_ENABLED) {
            Log.d(TAG_RESPONSE, message);
        }
    }
    
    /**
     * Log error with appropriate tag
     */
    public static void logError(String tag, String message, Throwable error) {
        Log.e(tag, message, error);
    }
    
    /**
     * Log warning with appropriate tag
     */
    public static void logWarning(String tag, String message) {
        Log.w(tag, message);
    }
    
    /**
     * Log info with appropriate tag
     */
    public static void logInfo(String tag, String message) {
        Log.i(tag, message);
    }
    
    /**
     * Check if any debug is enabled
     */
    public static boolean isAnyDebugEnabled() {
        return API_DEBUG_ENABLED || NETWORK_DEBUG_ENABLED || AUTH_DEBUG_ENABLED || 
               QUERY_DEBUG_ENABLED || RESPONSE_DEBUG_ENABLED;
    }
    
    /**
     * Get debug status summary
     */
    public static String getDebugStatus() {
        return "API: " + API_DEBUG_ENABLED + 
               ", Network: " + NETWORK_DEBUG_ENABLED + 
               ", Auth: " + AUTH_DEBUG_ENABLED + 
               ", Query: " + QUERY_DEBUG_ENABLED + 
               ", Response: " + RESPONSE_DEBUG_ENABLED;
    }
}
