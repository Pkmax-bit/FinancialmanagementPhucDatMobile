package com.example.financialmanagement.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import com.example.financialmanagement.R;
import com.example.financialmanagement.models.AppVersion;
import com.example.financialmanagement.services.UpdateService;
import java.io.File;

/**
 * Update Manager - Handles update UI and installation
 */
public class UpdateManager {
    private static final String TAG = "UpdateManager";
    private Activity activity;
    private UpdateService updateService;
    private AlertDialog updateDialog;
    private AlertDialog downloadDialog;
    
    public UpdateManager(Activity activity) {
        this.activity = activity;
        this.updateService = new UpdateService(activity);
    }
    
    /**
     * Check for updates and show dialog if available
     */
    public void checkForUpdate(boolean showNoUpdateMessage) {
        try {
            int currentVersionCode = activity.getPackageManager()
                .getPackageInfo(activity.getPackageName(), 0).versionCode;
            String currentVersionName = activity.getPackageManager()
                .getPackageInfo(activity.getPackageName(), 0).versionName;
            
            updateService.checkForUpdate(currentVersionCode, currentVersionName, new UpdateService.UpdateCallback() {
                @Override
                public void onUpdateAvailable(AppVersion version) {
                    activity.runOnUiThread(() -> showUpdateDialog(version));
                }
                
                @Override
                public void onNoUpdate() {
                    if (showNoUpdateMessage) {
                        activity.runOnUiThread(() -> {
                            new AlertDialog.Builder(activity)
                                .setTitle("Cập nhật")
                                .setMessage("Bạn đang sử dụng phiên bản mới nhất!")
                                .setPositiveButton("OK", null)
                                .show();
                        });
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error checking update: " + error);
                    // Silent fail - don't show error to user
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error getting app version: " + e.getMessage(), e);
        }
    }
    
    /**
     * Show update available dialog
     */
    private void showUpdateDialog(AppVersion version) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Có bản cập nhật mới");
        builder.setMessage(
            "Phiên bản mới: " + version.getLatestVersionName() + "\n\n" +
            (version.getReleaseNotes() != null ? version.getReleaseNotes() : "Cải thiện hiệu suất và sửa lỗi") +
            (version.isUpdateRequired() ? "\n\n⚠️ Bạn cần cập nhật để tiếp tục sử dụng ứng dụng." : "")
        );
        
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            downloadAndInstall(version);
        });
        
        if (!version.isUpdateRequired()) {
            builder.setNegativeButton("Bỏ qua", (dialog, which) -> {
                dialog.dismiss();
            });
        } else {
            builder.setCancelable(false);
        }
        
        updateDialog = builder.create();
        updateDialog.show();
    }
    
    /**
     * Download and install APK
     */
    private void downloadAndInstall(AppVersion version) {
        if (version.getDownloadUrl() == null) {
            new AlertDialog.Builder(activity)
                .setTitle("Lỗi")
                .setMessage("Không tìm thấy file cập nhật. Vui lòng thử lại sau.")
                .setPositiveButton("OK", null)
                .show();
            return;
        }
        
        // Show download progress dialog
        showDownloadDialog();
        
        updateService.downloadApk(version.getDownloadUrl(), new UpdateService.DownloadCallback() {
            @Override
            public void onProgress(int percent) {
                activity.runOnUiThread(() -> {
                    if (downloadDialog != null) {
                        ProgressBar progressBar = downloadDialog.findViewById(R.id.progress_bar);
                        TextView progressText = downloadDialog.findViewById(R.id.progress_text);
                        if (progressBar != null) {
                            progressBar.setProgress(percent);
                        }
                        if (progressText != null) {
                            progressText.setText("Đang tải: " + percent + "%");
                        }
                    }
                });
            }
            
            @Override
            public void onComplete(File apkFile) {
                activity.runOnUiThread(() -> {
                    if (downloadDialog != null) {
                        downloadDialog.dismiss();
                    }
                    installApk(apkFile);
                });
            }
            
            @Override
            public void onError(String error) {
                activity.runOnUiThread(() -> {
                    if (downloadDialog != null) {
                        downloadDialog.dismiss();
                    }
                    new AlertDialog.Builder(activity)
                        .setTitle("Lỗi tải xuống")
                        .setMessage("Không thể tải xuống bản cập nhật: " + error)
                        .setPositiveButton("OK", null)
                        .show();
                });
            }
        });
    }
    
    /**
     * Show download progress dialog
     */
    private void showDownloadDialog() {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_download_progress, null);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        TextView progressText = view.findViewById(R.id.progress_text);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Đang tải cập nhật...");
        builder.setView(view);
        builder.setCancelable(false);
        
        downloadDialog = builder.create();
        downloadDialog.show();
    }
    
    /**
     * Install APK file
     */
    private void installApk(File apkFile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri;
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Use FileProvider for Android 7.0+
                String authority = activity.getPackageName() + ".fileprovider";
                apkUri = FileProvider.getUriForFile(activity, authority, apkFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                // Direct file URI for older Android
                apkUri = Uri.fromFile(apkFile);
            }
            
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error installing APK: " + e.getMessage(), e);
            new AlertDialog.Builder(activity)
                .setTitle("Lỗi cài đặt")
                .setMessage("Không thể cài đặt bản cập nhật. Vui lòng cài đặt thủ công.")
                .setPositiveButton("OK", null)
                .show();
        }
    }
}

