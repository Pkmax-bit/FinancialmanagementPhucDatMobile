package com.example.financialmanagement.utils;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.io.InputStream;

/**
 * Utility class for validating user inputs and files for security
 */
public class InputValidator {
    
    // Maximum lengths
    public static final int MAX_MESSAGE_LENGTH = 10000; // 10K characters
    public static final int MAX_FILENAME_LENGTH = 255;
    
    // Maximum file sizes (in bytes)
    public static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024; // 50MB
    public static final long MAX_FILE_SIZE = 20 * 1024 * 1024;  // 20MB
    
    // Allowed file types
    private static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };
    
    private static final String[] ALLOWED_VIDEO_TYPES = {
        "video/mp4", "video/3gpp", "video/mpeg", "video/quicktime"
    };
    
    private static final String[] ALLOWED_DOCUMENT_TYPES = {
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain", "application/zip", "application/x-rar-compressed"
    };
    
    // Dangerous file extensions to block
    private static final String[] BLOCKED_EXTENSIONS = {
        ".exe", ".bat", ".cmd", ".sh", ".apk", ".jar", ".scr", ".vbs", ".js"
    };
    
    /**
     * Validate message text content
     */
    public static ValidationResult validateMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return ValidationResult.error("Tin nhắn không được để trống");
        }
        
        // Check length
        if (message.length() > MAX_MESSAGE_LENGTH) {
            return ValidationResult.error("Tin nhắn quá dài (tối đa " + MAX_MESSAGE_LENGTH + " ký tự)");
        }
        
        // Sanitize: Remove potential XSS attempts (basic check)
        String sanitized = sanitizeInput(message);
        
        return ValidationResult.success(sanitized);
    }
    
    /**
     * Sanitize user input to prevent XSS and injection attacks
     */
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        
        // Basic HTML entity encoding for display safety
        // Note: This is basic - for web display, use proper HTML encoding
        return input.replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("&", "&amp;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;")
                   .trim();
    }
    
    /**
     * Validate file before upload
     */
    public static ValidationResult validateFile(Context context, Uri fileUri, String fileName) {
        if (fileUri == null) {
            return ValidationResult.error("File không hợp lệ");
        }
        
        // Validate filename
        if (fileName == null || fileName.trim().isEmpty()) {
            return ValidationResult.error("Tên file không hợp lệ");
        }
        
        if (fileName.length() > MAX_FILENAME_LENGTH) {
            return ValidationResult.error("Tên file quá dài");
        }
        
        // Check for dangerous extensions
        String lowerFileName = fileName.toLowerCase();
        for (String ext : BLOCKED_EXTENSIONS) {
            if (lowerFileName.endsWith(ext)) {
                return ValidationResult.error("Loại file " + ext + " không được phép tải lên");
            }
        }
        
        // Get MIME type
        String mimeType = getMimeType(context, fileUri);
        if (mimeType == null) {
            return ValidationResult.error("Không xác định được loại file");
        }
        
        // Validate MIME type
        boolean isAllowed = isAllowedMimeType(mimeType);
        if (!isAllowed) {
            return ValidationResult.error("Loại file không được hỗ trợ: " + mimeType);
        }
        
        // Validate file size
        try {
            long fileSize = getFileSize(context, fileUri);
            
            if (mimeType.startsWith("image/")) {
                if (fileSize > MAX_IMAGE_SIZE) {
                    return ValidationResult.error("Hình ảnh quá lớn (tối đa " + (MAX_IMAGE_SIZE / 1024 / 1024) + "MB)");
                }
            } else if (mimeType.startsWith("video/")) {
                if (fileSize > MAX_VIDEO_SIZE) {
                    return ValidationResult.error("Video quá lớn (tối đa " + (MAX_VIDEO_SIZE / 1024 / 1024) + "MB)");
                }
            } else {
                if (fileSize > MAX_FILE_SIZE) {
                    return ValidationResult.error("File quá lớn (tối đa " + (MAX_FILE_SIZE / 1024 / 1024) + "MB)");
                }
            }
        } catch (Exception e) {
            return ValidationResult.error("Không thể kiểm tra kích thước file");
        }
        
        return ValidationResult.success(fileName);
    }
    
    /**
     * Get MIME type of file
     */
    private static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        
        // Try to get from content resolver
        try {
            mimeType = context.getContentResolver().getType(uri);
        } catch (Exception e) {
            // Ignore
        }
        
        // Fallback: get from file extension
        if (mimeType == null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            if (extension != null) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
            }
        }
        
        return mimeType;
    }
    
    /**
     * Check if MIME type is allowed
     */
    private static boolean isAllowedMimeType(String mimeType) {
        if (mimeType == null) return false;
        
        // Check images
        for (String allowed : ALLOWED_IMAGE_TYPES) {
            if (mimeType.equals(allowed)) return true;
        }
        
        // Check videos
        for (String allowed : ALLOWED_VIDEO_TYPES) {
            if (mimeType.equals(allowed)) return true;
        }
        
        // Check documents
        for (String allowed : ALLOWED_DOCUMENT_TYPES) {
            if (mimeType.equals(allowed)) return true;
        }
        
        return false;
    }
    
    /**
     * Get file size in bytes
     */
    private static long getFileSize(Context context, Uri uri) throws Exception {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                return inputStream.available();
            }
        }
        return 0;
    }
    
    /**
     * Format file size for display
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
    
    /**
     * Validation result wrapper
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final String message;
        private final String sanitizedData;
        
        private ValidationResult(boolean isValid, String message, String sanitizedData) {
            this.isValid = isValid;
            this.message = message;
            this.sanitizedData = sanitizedData;
        }
        
        public static ValidationResult success(String sanitizedData) {
            return new ValidationResult(true, null, sanitizedData);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message, null);
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getSanitizedData() {
            return sanitizedData;
        }
    }
}


