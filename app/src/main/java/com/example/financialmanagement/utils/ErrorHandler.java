package com.example.financialmanagement.utils;

import android.util.Log;
import org.json.JSONObject;
import retrofit2.Response;

/**
 * Error Handler - Xử lý lỗi tập trung
 * Phân tích và chuyển đổi lỗi từ backend thành thông báo tiếng Việt
 */
public class ErrorHandler {
    
    private static final String TAG = "ErrorHandler";
    
    /**
     * Parse error from Retrofit response
     * @param response Retrofit response object
     * @return User-friendly Vietnamese error message
     */
    public static String parseError(Response<?> response) {
        if (response == null) {
            return "Lỗi không xác định";
        }
        
        int code = response.code();
        String message = getErrorMessageByCode(code);
        
        // Try to get detailed error from response body
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                
                // Check if it looks like JSON
                if (errorBody != null && errorBody.trim().startsWith("{")) {
                    try {
                        JSONObject errorJson = new JSONObject(errorBody);
                        
                        // Check for detail field (FastAPI standard)
                        if (errorJson.has("detail")) {
                            String detail = errorJson.getString("detail");
                            if (detail != null && !detail.isEmpty()) {
                                return translateErrorDetail(detail, code);
                            }
                        }
                        
                        // Check for message field
                        if (errorJson.has("message")) {
                            String errorMessage = errorJson.getString("message");
                            if (errorMessage != null && !errorMessage.isEmpty()) {
                                return translateErrorDetail(errorMessage, code);
                            }
                        }
                    } catch (Exception e) {
                        // If parsing fails despite starting with {, just log and continue
                        Log.w(TAG, "Failed to parse JSON error body: " + e.getMessage());
                    }
                }
                
                // If not JSON or parsing failed, but we have an error body that is not empty
                // use it if it's short enough (likely a plain text error message)
                if (errorBody != null && !errorBody.trim().isEmpty() && errorBody.length() < 100) {
                     // For 500 errors, we might prefer "Server Error" over "Internal Server Error" raw string
                     // unless we are sure. But usually plain text 500 is "Internal Server Error"
                     // relying on getErrorMessageByCode(code) is safer for 500.
                     // But for other codes it might be useful.
                     // Let's just log it for debug and return the safe message for now, 
                     // or return specific string if we really want.
                     // Returning default message is safer for user facing string.
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading error body: " + e.getMessage());
        }
        
        return message;
    }
    
    /**
     * Parse error from Throwable
     * @param throwable Exception object
     * @return User-friendly Vietnamese error message
     */
    public static String parseError(Throwable throwable) {
        if (throwable == null) {
            return "Lỗi không xác định";
        }
        
        String message = throwable.getMessage();
        
        // Network errors
        if (throwable instanceof java.net.UnknownHostException) {
            return "Không thể kết nối đến server. Vui lòng kiểm tra kết nối internet.";
        }
        
        if (throwable instanceof java.net.SocketTimeoutException) {
            return "Kết nối quá thời gian chờ. Vui lòng thử lại.";
        }
        
        if (throwable instanceof java.net.ConnectException) {
            return "Không thể kết nối đến server. Vui lòng kiểm tra kết nối internet.";
        }
        
        if (throwable instanceof javax.net.ssl.SSLException) {
            return "Lỗi bảo mật kết nối. Vui lòng thử lại.";
        }
        
        // Generic error
        if (message != null && !message.isEmpty()) {
            return "Lỗi: " + message;
        }
        
        return "Lỗi kết nối không xác định";
    }
    
    /**
     * Get error message by HTTP status code
     */
    private static String getErrorMessageByCode(int code) {
        switch (code) {
            case 400:
                return "Dữ liệu không hợp lệ";
            case 401:
                return "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
            case 403:
                return "Bạn không có quyền truy cập tài nguyên này";
            case 404:
                return "Không tìm thấy dữ liệu";
            case 409:
                return "Dữ liệu đã tồn tại hoặc xung đột";
            case 422:
                return "Dữ liệu không hợp lệ";
            case 429:
                return "Quá nhiều yêu cầu. Vui lòng thử lại sau.";
            case 500:
                return "Lỗi server. Vui lòng thử lại sau.";
            case 502:
                return "Server tạm thời không khả dụng";
            case 503:
                return "Dịch vụ tạm thời không khả dụng";
            case 504:
                return "Server quá thời gian chờ";
            default:
                if (code >= 500) {
                    return "Lỗi server (" + code + ")";
                } else if (code >= 400) {
                    return "Lỗi yêu cầu (" + code + ")";
                }
                return "Lỗi không xác định (" + code + ")";
        }
    }
    
    /**
     * Translate English error details to Vietnamese
     */
    private static String translateErrorDetail(String detail, int code) {
        if (detail == null || detail.isEmpty()) {
            return getErrorMessageByCode(code);
        }
        
        String lowerDetail = detail.toLowerCase();
        
        // Authentication errors
        if (lowerDetail.contains("invalid credentials") || lowerDetail.contains("invalid email or password")) {
            return "Email hoặc mật khẩu không đúng";
        }
        if (lowerDetail.contains("token") && lowerDetail.contains("expired")) {
            return "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
        }
        if (lowerDetail.contains("token") && lowerDetail.contains("invalid")) {
            return "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.";
        }
        
        // Permission errors
        if (lowerDetail.contains("permission") || lowerDetail.contains("access denied")) {
            return "Bạn không có quyền thực hiện thao tác này";
        }
        if (lowerDetail.contains("don't have access") || lowerDetail.contains("not in project_team")) {
            return "Bạn không có quyền truy cập dự án này";
        }
        
        // Validation errors
        if (lowerDetail.contains("validation error") || lowerDetail.contains("validation_error")) {
            // Extract field name if available
            if (lowerDetail.contains("for quote") || lowerDetail.contains("for Quote")) {
                return "Lỗi validation: Dữ liệu báo giá không hợp lệ. Vui lòng kiểm tra lại thông tin.";
            }
            return "Lỗi validation: Dữ liệu không hợp lệ";
        }
        if (lowerDetail.contains("already exists")) {
            return "Dữ liệu đã tồn tại";
        }
        if (lowerDetail.contains("not found")) {
            return "Không tìm thấy dữ liệu";
        }
        if (lowerDetail.contains("required") || lowerDetail.contains("field required")) {
            return "Thiếu thông tin bắt buộc";
        }
        if (lowerDetail.contains("invalid")) {
            return "Dữ liệu không hợp lệ";
        }
        
        // Network errors
        if (lowerDetail.contains("network") || lowerDetail.contains("connection")) {
            return "Lỗi kết nối mạng. Vui lòng kiểm tra internet.";
        }
        if (lowerDetail.contains("timeout")) {
            return "Kết nối quá thời gian chờ. Vui lòng thử lại.";
        }
        
        // If no translation found, return original detail with code
        return detail + " (" + code + ")";
    }
    
    /**
     * Log error for debugging
     */
    public static void logError(String tag, String operation, Throwable error) {
        Log.e(tag, "Error in " + operation + ": " + error.getMessage(), error);
    }
    
    /**
     * Log error for debugging
     */
    public static void logError(String tag, String operation, Response<?> response) {
        Log.e(tag, "Error in " + operation + ": " + response.code() + " - " + response.message());
        try {
            if (response.errorBody() != null) {
                Log.e(tag, "Error body: " + response.errorBody().string());
            }
        } catch (Exception e) {
            Log.e(tag, "Error reading error body: " + e.getMessage());
        }
    }
}
