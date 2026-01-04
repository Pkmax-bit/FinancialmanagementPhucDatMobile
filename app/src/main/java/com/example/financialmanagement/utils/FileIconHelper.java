package com.example.financialmanagement.utils;

import com.example.financialmanagement.R;

/**
 * Utility class to determine appropriate file icons based on file URL/extension
 */
public class FileIconHelper {
    
    /**
     * Get drawable resource ID for file icon based on file URL
     * @param fileUrl URL or path of the file
     * @return Drawable resource ID for the appropriate icon
     */
    public static int getFileIconResource(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return R.drawable.ic_file_generic;
        }
        
        String extension = getFileExtension(fileUrl).toLowerCase();
        
        // PDF files
        if (extension.equals("pdf")) {
            return R.drawable.ic_file_pdf;
        }
        
        // Excel files
        if (extension.equals("xls") || extension.equals("xlsx") || 
            extension.equals("xlsm") || extension.equals("xlsb")) {
            return R.drawable.ic_file_excel;
        }
        
        // Word documents
        if (extension.equals("doc") || extension.equals("docx") || 
            extension.equals("docm") || extension.equals("dotx")) {
            return R.drawable.ic_file_doc;
        }
        
        // Default for unknown types
        return R.drawable.ic_file_generic;
    }
    
    /**
     * Extract file extension from URL or filename
     * @param fileUrl URL or path of the file
     * @return File extension (without dot) or empty string
     */
    public static String getFileExtension(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return "";
        }
        
        // Remove query parameters if present
        int queryIndex = fileUrl.indexOf('?');
        if (queryIndex != -1) {
            fileUrl = fileUrl.substring(0, queryIndex);
        }
        
        // Get the last segment after /
        int lastSlash = fileUrl.lastIndexOf('/');
        String filename = lastSlash != -1 ? fileUrl.substring(lastSlash + 1) : fileUrl;
        
        // Extract extension
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        
        return "";
    }
    
    /**
     * Get user-friendly file type label
     * @param fileUrl URL or path of the file
     * @return File type label (e.g., "PDF", "Excel", "Word", "File")
     */
    public static String getFileTypeLabel(String fileUrl) {
        String extension = getFileExtension(fileUrl).toLowerCase();
        
        if (extension.equals("pdf")) {
            return "PDF";
        }
        
        if (extension.equals("xls") || extension.equals("xlsx") || 
            extension.equals("xlsm") || extension.equals("xlsb")) {
            return "Excel";
        }
        
        if (extension.equals("doc") || extension.equals("docx") || 
            extension.equals("docm") || extension.equals("dotx")) {
            return "Word";
        }
        
        if (!extension.isEmpty()) {
            return extension.toUpperCase();
        }
        
        return "File";
    }
    
    /**
     * Get filename from URL (without path)
     * @param fileUrl URL or path of the file
     * @return Filename only
     */
    public static String getFileName(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return "unknown";
        }
        
        // Remove query parameters if present
        int queryIndex = fileUrl.indexOf('?');
        if (queryIndex != -1) {
            fileUrl = fileUrl.substring(0, queryIndex);
        }
        
        // Get the last segment after /
        int lastSlash = fileUrl.lastIndexOf('/');
        return lastSlash != -1 ? fileUrl.substring(lastSlash + 1) : fileUrl;
    }
}
