package com.example.financialmanagement.utils;

import android.util.Log;
import com.example.financialmanagement.config.DebugConfig;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 * API Debugger - Utility class for debugging API calls
 * Cung cấp các method để debug API requests và responses
 */
public class ApiDebugger {
    
    private static final String TAG = "API_DEBUGGER";
    
    /**
     * Log API request details
     */
    public static void logRequest(String method, String url, Map<String, String> headers, Object body) {
        if (!DebugConfig.API_DEBUG_ENABLED) return;
        
        DebugConfig.logApi("=== API REQUEST ===");
        DebugConfig.logApi("Method: " + method);
        DebugConfig.logApi("URL: " + url);
        
        if (headers != null && !headers.isEmpty()) {
            DebugConfig.logApi("Headers:");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                DebugConfig.logApi("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        if (body != null) {
            DebugConfig.logApi("Body: " + body.toString());
        }
        DebugConfig.logApi("==================");
    }
    
    /**
     * Log API response details
     */
    public static void logResponse(int code, String message, String body) {
        if (!DebugConfig.RESPONSE_DEBUG_ENABLED) return;
        
        DebugConfig.logResponse("=== API RESPONSE ===");
        DebugConfig.logResponse("Code: " + code);
        DebugConfig.logResponse("Message: " + message);
        if (body != null) {
            DebugConfig.logResponse("Body: " + body);
        }
        DebugConfig.logResponse("===================");
    }
    
    /**
     * Log query parameters
     */
    public static void logQueryParams(Map<String, Object> queryParams) {
        if (!DebugConfig.QUERY_DEBUG_ENABLED) return;
        
        if (queryParams != null && !queryParams.isEmpty()) {
            DebugConfig.logQuery("=== QUERY PARAMETERS ===");
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                DebugConfig.logQuery(entry.getKey() + " = " + entry.getValue());
            }
            DebugConfig.logQuery("========================");
        }
    }
    
    /**
     * Log query parameters from URL
     */
    public static void logQueryFromUrl(String url) {
        if (!DebugConfig.QUERY_DEBUG_ENABLED) return;
        
        if (url != null && url.contains("?")) {
            String query = url.substring(url.indexOf("?") + 1);
            DebugConfig.logQuery("=== URL QUERY ===");
            DebugConfig.logQuery("Query String: " + query);
            
            // Parse individual parameters
            String[] params = query.split("&");
            for (String param : params) {
                if (param.contains("=")) {
                    String[] keyValue = param.split("=", 2);
                    DebugConfig.logQuery(keyValue[0] + " = " + keyValue[1]);
                }
            }
            DebugConfig.logQuery("================");
        }
    }
    
    /**
     * Log error details
     */
    public static void logError(String operation, Throwable error) {
        if (!DebugConfig.API_DEBUG_ENABLED) return;
        
        DebugConfig.logError(TAG, "=== API ERROR ===", null);
        DebugConfig.logError(TAG, "Operation: " + operation, null);
        DebugConfig.logError(TAG, "Error: " + error.getMessage(), error);
        DebugConfig.logError(TAG, "================", null);
    }
    
    /**
     * Log network configuration
     */
    public static void logNetworkConfig(String baseUrl, int timeout) {
        if (!DebugConfig.NETWORK_DEBUG_ENABLED) return;
        
        DebugConfig.logNetwork("=== NETWORK CONFIG ===");
        DebugConfig.logNetwork("Base URL: " + baseUrl);
        DebugConfig.logNetwork("Timeout: " + timeout + "s");
        DebugConfig.logNetwork("=====================");
    }
    
    /**
     * Log authentication details
     */
    public static void logAuth(String token, boolean isAuthenticated) {
        if (!DebugConfig.AUTH_DEBUG_ENABLED) return;
        
        DebugConfig.logAuth("=== AUTH DEBUG ===");
        DebugConfig.logAuth("Authenticated: " + isAuthenticated);
        if (token != null) {
            DebugConfig.logAuth("Token: " + (token.length() > 20 ? token.substring(0, 20) + "..." : token));
        }
        DebugConfig.logAuth("================");
    }
    
    /**
     * Enable/disable debug logging
     */
    public static void setDebugEnabled(boolean enabled) {
        DebugConfig.logInfo(TAG, "Debug logging " + (enabled ? "enabled" : "disabled"));
    }
}
