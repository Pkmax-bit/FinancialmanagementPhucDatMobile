package com.example.financialmanagement.config;

/**
 * App Configuration - Cấu hình ứng dụng
 * Quản lý các cấu hình môi trường và settings
 */
public class AppConfig {
    
    // Environment Configuration
    public enum Environment {
        LOCAL,      // http://192.168.1.25:8000
        NETWORK,    // http://192.168.1.25:3000
        PRODUCTION  // https://financial-management-backend-3m78.onrender.com
    }
    
    // Current environment - thay đổi ở đây để chuyển môi trường
    private static final Environment CURRENT_ENVIRONMENT = Environment.PRODUCTION;
    
    /**
     * Lấy environment hiện tại
     */
    public static Environment getCurrentEnvironment() {
        return CURRENT_ENVIRONMENT;
    }
    
    /**
     * Kiểm tra có phải môi trường local không
     */
    public static boolean isLocalEnvironment() {
        return CURRENT_ENVIRONMENT == Environment.LOCAL;
    }
    
    /**
     * Kiểm tra có phải môi trường network không
     */
    public static boolean isNetworkEnvironment() {
        return CURRENT_ENVIRONMENT == Environment.NETWORK;
    }
    
    /**
     * Lấy base URL theo environment
     */
    public static String getBaseUrl() {
        switch (CURRENT_ENVIRONMENT) {
            case LOCAL:
                // 10.0.2.2 is the special alias to your host loopback interface (127.0.0.1)
                return "http://10.0.2.2:8000/api/";
            case NETWORK:
                return "http://192.168.1.25:3000/api/";
            case PRODUCTION:
                return "https://financial-management-backend-3m78.onrender.com/api/";
            default:
                return "https://financial-management-backend-3m78.onrender.com/api/";
        }
    }
    
    /**
     * Debug information
     */
    public static String getDebugInfo() {
        return "Environment: " + CURRENT_ENVIRONMENT + 
               ", Base URL: " + getBaseUrl();
    }
}
