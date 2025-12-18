package com.example.financialmanagement.services;

import android.content.Context;
import android.util.Log;
import com.example.financialmanagement.config.AppConfig;
import com.example.financialmanagement.models.AppVersion;
import com.example.financialmanagement.network.ApiClient;
import com.example.financialmanagement.utils.ErrorHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Update Service - Check for app updates and download APK
 */
public class UpdateService {
    private static final String TAG = "UpdateService";
    private UpdateApi updateApi;
    private Context context;
    
    public interface UpdateCallback {
        void onUpdateAvailable(AppVersion version);
        void onNoUpdate();
        void onError(String error);
    }
    
    public interface DownloadCallback {
        void onProgress(int percent);
        void onComplete(File apkFile);
        void onError(String error);
    }
    
    private interface UpdateApi {
        @GET("app-updates/check")
        Call<AppVersion> checkVersion(
            @Query("current_version_code") int versionCode,
            @Query("current_version_name") String versionName
        );
    }
    
    public UpdateService(Context context) {
        this.context = context;
        updateApi = ApiClient.getRetrofit(context).create(UpdateApi.class);
    }
    
    /**
     * Check for app updates
     */
    public void checkForUpdate(int currentVersionCode, String currentVersionName, UpdateCallback callback) {
        Call<AppVersion> call = updateApi.checkVersion(currentVersionCode, currentVersionName);
        call.enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(Call<AppVersion> call, retrofit2.Response<AppVersion> response) {
                if (response.isSuccessful()) {
                    AppVersion version = response.body();
                    if (version != null && version.isUpdateAvailable()) {
                        Log.d(TAG, "Update available: " + version.getLatestVersionName());
                        callback.onUpdateAvailable(version);
                    } else {
                        Log.d(TAG, "No update available");
                        callback.onNoUpdate();
                    }
                } else {
                    String error = ErrorHandler.parseError(response);
                    Log.e(TAG, "Error checking update: " + error);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<AppVersion> call, Throwable t) {
                String error = ErrorHandler.parseError(t);
                Log.e(TAG, "Failed to check update: " + error);
                callback.onError(error);
            }
        });
    }
    
    /**
     * Download APK file
     */
    public void downloadApk(String downloadUrl, DownloadCallback callback) {
        new Thread(() -> {
            try {
                String baseUrl = AppConfig.getBaseUrl().replace("/api/", "");
                String fullUrl = baseUrl + downloadUrl;
                
                Log.d(TAG, "Downloading APK from: " + fullUrl);
                
                OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
                
                Request request = new Request.Builder()
                    .url(fullUrl)
                    .build();
                
                Response response = client.newCall(request).execute();
                
                if (!response.isSuccessful()) {
                    callback.onError("Download failed: " + response.code());
                    return;
                }
                
                // Get file size
                long contentLength = response.body().contentLength();
                if (contentLength == -1) {
                    contentLength = 0;
                }
                
                // Create download directory
                File downloadDir = new File(context.getExternalFilesDir(null), "updates");
                if (!downloadDir.exists()) {
                    downloadDir.mkdirs();
                }
                
                // Create APK file
                File apkFile = new File(downloadDir, "app-update.apk");
                
                // Download with progress
                InputStream inputStream = response.body().byteStream();
                FileOutputStream outputStream = new FileOutputStream(apkFile);
                
                byte[] buffer = new byte[4096];
                long totalBytes = 0;
                int bytesRead;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                    
                    if (contentLength > 0) {
                        int percent = (int) ((totalBytes * 100) / contentLength);
                        callback.onProgress(percent);
                    }
                }
                
                outputStream.close();
                inputStream.close();
                
                Log.d(TAG, "APK downloaded successfully: " + apkFile.getAbsolutePath());
                callback.onComplete(apkFile);
                
            } catch (Exception e) {
                Log.e(TAG, "Error downloading APK: " + e.getMessage(), e);
                callback.onError("Download error: " + e.getMessage());
            }
        }).start();
    }
}

