package com.example.financialmanagement.models;

import android.net.Uri;

public class AttachmentItem {
    public enum UploadStatus {
        PENDING, UPLOADING, SUCCESS, ERROR
    }

    private String id;
    private Uri uri;
    private String fileName;
    private String mimeType;
    private long fileSize;
    private boolean isImage;
    private String localPath;

    private UploadStatus uploadStatus = UploadStatus.PENDING;
    private String uploadError;
    private String uploadedUrl;
    private int uploadProgress = 0;

    public AttachmentItem() {
    }

    public AttachmentItem(Uri uri, String fileName, String mimeType, long fileSize) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.uri = uri;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.isImage = mimeType != null && mimeType.startsWith("image/");
        this.uploadStatus = UploadStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getUploadError() {
        return uploadError;
    }

    public void setUploadError(String uploadError) {
        this.uploadError = uploadError;
    }

    public String getUploadedUrl() {
        return uploadedUrl;
    }

    public void setUploadedUrl(String uploadedUrl) {
        this.uploadedUrl = uploadedUrl;
    }

    public int getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(int uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public boolean isUploading() {
        return uploadStatus == UploadStatus.UPLOADING;
    }

    public boolean isUploaded() {
        return uploadStatus == UploadStatus.SUCCESS;
    }

    public boolean hasError() {
        return uploadStatus == UploadStatus.ERROR;
    }

    public String getFileExtension() {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        }
        return "";
    }

    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }
}
