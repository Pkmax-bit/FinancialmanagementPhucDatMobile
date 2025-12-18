package com.example.financialmanagement.models;

import com.google.gson.annotations.SerializedName;

/**
 * App Version Model - Response from version check API
 */
public class AppVersion {
    @SerializedName("current_version_code")
    private int currentVersionCode;
    
    @SerializedName("current_version_name")
    private String currentVersionName;
    
    @SerializedName("latest_version_code")
    private int latestVersionCode;
    
    @SerializedName("latest_version_name")
    private String latestVersionName;
    
    @SerializedName("min_supported_version_code")
    private int minSupportedVersionCode;
    
    @SerializedName("update_available")
    private boolean updateAvailable;
    
    @SerializedName("update_required")
    private boolean updateRequired;
    
    @SerializedName("download_url")
    private String downloadUrl;
    
    @SerializedName("release_notes")
    private String releaseNotes;
    
    @SerializedName("file_size")
    private Long fileSize;
    
    // Getters and Setters
    public int getCurrentVersionCode() { return currentVersionCode; }
    public void setCurrentVersionCode(int currentVersionCode) { this.currentVersionCode = currentVersionCode; }
    
    public String getCurrentVersionName() { return currentVersionName; }
    public void setCurrentVersionName(String currentVersionName) { this.currentVersionName = currentVersionName; }
    
    public int getLatestVersionCode() { return latestVersionCode; }
    public void setLatestVersionCode(int latestVersionCode) { this.latestVersionCode = latestVersionCode; }
    
    public String getLatestVersionName() { return latestVersionName; }
    public void setLatestVersionName(String latestVersionName) { this.latestVersionName = latestVersionName; }
    
    public int getMinSupportedVersionCode() { return minSupportedVersionCode; }
    public void setMinSupportedVersionCode(int minSupportedVersionCode) { this.minSupportedVersionCode = minSupportedVersionCode; }
    
    public boolean isUpdateAvailable() { return updateAvailable; }
    public void setUpdateAvailable(boolean updateAvailable) { this.updateAvailable = updateAvailable; }
    
    public boolean isUpdateRequired() { return updateRequired; }
    public void setUpdateRequired(boolean updateRequired) { this.updateRequired = updateRequired; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public String getReleaseNotes() { return releaseNotes; }
    public void setReleaseNotes(String releaseNotes) { this.releaseNotes = releaseNotes; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
}

