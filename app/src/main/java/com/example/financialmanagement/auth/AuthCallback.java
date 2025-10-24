package com.example.financialmanagement.auth;

/**
 * Auth Callback Interface - Interface cho authentication callbacks
 * Được sử dụng để xử lý kết quả của các thao tác xác thực
 */
public interface AuthCallback {
    
    /**
     * Được gọi khi xác thực thành công
     */
    void onAuthSuccess();
    
    /**
     * Được gọi khi xác thực thất bại
     * @param error Thông báo lỗi
     */
    void onAuthError(String error);
}
